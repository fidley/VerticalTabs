package com.abapblog.verticaltabs.abap.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.WorkbenchPart;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.abap.nodes.SapGuiNode;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.sapgui.ui.internal.editors.GuiEditorInput;
import com.sap.adt.sapgui.ui.internal.editors.SapGuiStartupData;
import com.sap.adt.sapgui.ui.internal.handlers.ISapGuiLifeCycleEvent;
import com.sap.adt.sapgui.ui.internal.handlers.ISapGuiLifeCycleListener;

@SuppressWarnings("restriction")
public class SapGuiLifeCycleListener implements ISapGuiLifeCycleListener {
	private static final String ProgramSAPLSLVC_FULLSCREEN = "SAPLSLVC_FULLSCREEN";
	private static final String ProgramSAPLZFALV = "SAPLZFALV";
	private static final String EventPropertyTransaction = "transaction";
	private static final String EventPropertyProgram = "program";
	private static final String EventPropertyTitle = "title";
	private static final String TransactionSE38 = "SE38";
	private static final String TransactionSA38 = "SA38";
	private static final String TransactionS000 = "S000";
	private static final String TransactionSADT_START_WB_URI = "SADT_START_WB_URI";
	private static final String ProgramSAPMS38M = "SAPMS38M";
	private static final String ProgramSAPLWBABAP = "SAPLWBABAP";
	private static final String EventPropertyIsModal = "isModal";
	private Timer timer = new Timer();
	private long delay = 1000;
	private Map<String, String> eventProperties = new HashMap<String, String>();
	private IEditorPart editorPart = null;
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	private static final ConcurrentHashMap<IEditorPart, Timer> editorLocks = new ConcurrentHashMap<>();

	@Override
	public void handleLifeCycleEvent(ISapGuiLifeCycleEvent event) {
		if (store.getBoolean(PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION) == false) {
			return;
		}
		// System.out.println("Event: " + event.getType() + " " +
		// event.getProperties().toString());
		if (event == null || !event.getType().equals(ISapGuiLifeCycleEvent.EventType.GUI_REQUEST_ENDED)) {
			return;
		}
		eventProperties = event.getProperties();
		if (eventProperties.get(EventPropertyIsModal) == null || (eventProperties.get(EventPropertyIsModal).equals("1")
				&& !eventProperties.get(EventPropertyTransaction).equals(TransactionSADT_START_WB_URI))) {
			return;
		}
		editorPart = (IEditorPart) event.getEditor();
		if (timer == null) {
			timer = new Timer();
		}
		if (editorLocks.putIfAbsent(editorPart, timer) == null) {

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					processEvent(editorPart, eventProperties);
					editorLocks.remove(editorPart);

				}
			}, delay);
		} else {
			timer = editorLocks.get(editorPart);
			if (timer != null) {
				timer.cancel();
			}

			timer = new Timer();
			editorLocks.put(editorPart, timer);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					processEvent(editorPart, eventProperties);
					editorLocks.remove(editorPart);
				}
			}, delay);
		}
	}

	private void processEvent(IEditorPart ep, Map<String, String> eventProperties) {
		if (ep == null || eventProperties == null) {
			return;
		}

		for (Map.Entry<IEditorReference, TabNode> set : TreeContentProvider.getNodesFactory().getTabNodes()
				.entrySet()) {
			IEditorPart editorPart = set.getKey().getEditor(false);
			if (editorPart != null && editorPart.equals(ep)) {
				String transaction = eventProperties.get(EventPropertyTransaction);
				String program = eventProperties.get(EventPropertyProgram);
				String description = eventProperties.get(EventPropertyTitle);
				SapGuiNode node = (SapGuiNode) set.getValue();
				node.setShouldIgnorePropertyChange(true);
				if (updateTitleAndDescription(editorPart, transaction, program, description, node)) {
					updateTabName(node, editorPart);
					TreeContentProvider.refreshTree();

				}
				return;
			}

		}
	}

	private boolean updateTitleAndDescription(IEditorPart editorPart, String transaction, String program,
			String description, SapGuiNode node) {
		String newTitle;
		boolean titleUpdated = false;
		boolean descriptionUpdated = false;
		if (notFullScreenALV(transaction, program)) {
			if ((transaction.equals(TransactionSA38) || transaction.equals(TransactionSE38))
					&& !(program.equals(ProgramSAPMS38M) || program.equals(ProgramSAPLWBABAP))) {
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), program);
				titleUpdated = updateTitle(node, newTitle);
			} else if (transaction.equals(TransactionSADT_START_WB_URI)) {
				newTitle = editorPart.getTitle();
				newTitle = getTitleFromGuiLinkedObject(editorPart, newTitle, transaction);
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), newTitle);
				titleUpdated = updateTitle(node, newTitle);
			}

			else if (transaction.equals(TransactionS000)) {

				newTitle = getTitleFromGuiLinkedObject(editorPart, node.getProjectName(), transaction);
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), newTitle);
				titleUpdated = updateTitle(node, newTitle);
			} else {
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), transaction);
				titleUpdated = updateTitle(node, newTitle);
			}
		}
		descriptionUpdated = updateDescription(description, node);
		return titleUpdated || descriptionUpdated;
	}

	private String getTitleFromGuiLinkedObject(IEditorPart editorPart, String newTitle, String transaction) {
		if (editorPart.getEditorInput() instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
			newTitle = file.getName();// 170
		} else if (editorPart.getEditorInput() instanceof GuiEditorInput) {// 179
			SapGuiStartupData startupInfo = ((GuiEditorInput) editorPart.getEditorInput()).getStartupInfo();
			if (startupInfo != null) {
				String parameters = startupInfo.getParameters(true);
				Pattern pattern = Pattern.compile("tran=\\**(\\w*)\\s*");
				Matcher matcher = pattern.matcher(parameters);
				String startupTransaction = "";
				if (matcher.find()) {
					startupTransaction = matcher.group(1);
				}
				if (startupTransaction.equals(transaction)) {
					newTitle = startupInfo.getTitle();

				}
			}
		}
		return newTitle;
	}

	private boolean updateDescription(String description, SapGuiNode node) {
		if (node.getObjectDescription() != null && !node.getObjectDescription().equals(description)) {
			node.setObjectDescription(description);
			return true;
		}
		return false;
	}

	private boolean updateTitle(SapGuiNode node, String newTitle) {
		if (!newTitle.equals(node.getOriginalTitle())) {
			node.setOriginalTitle(newTitle);
			return true;
		}
		return false;
	}

	/**
	 * This method checks if the program is not a full screen ALV like SALF,
	 * REUSE_ALV or FALV. In this case if they are and the transaction is SE38 or
	 * SA38, we do not want to update the title with program name, as it would be
	 * then one of the Full Screen Function Group name like SAPLZFALV or
	 * SAPLSLVC_FULLSCREEN.
	 */
	private boolean notFullScreenALV(String transaction, String program) {
		return !((transaction.equals(TransactionSA38) || transaction.equals(TransactionSE38))
				&& (program.equals(ProgramSAPLZFALV) || program.equals(ProgramSAPLSLVC_FULLSCREEN)));
	}

	/**
	 * If in the previous title with have a project name contained, we add this
	 * project name also to then new title, keeping consistency.
	 */
	private String updateProjectNameInTitle(String previousTitle, String newTitle) {
		if (previousTitle != null && previousTitle.contains("[")) {
			int start = previousTitle.indexOf("[");
			int end = previousTitle.indexOf("]");
			String projectName = previousTitle.substring(start, end + 1);
			if (start == 0) {
				return projectName + " " + newTitle;
			} else {
				return newTitle + " " + projectName;
			}
		} else {
			return newTitle;
		}
	}

	private void updateTabName(SapGuiNode node, IEditorPart editorPart) {
		Display.getDefault().asyncExec(() -> {
			updateGUITabName(node, editorPart);
			updateGUIToolTip(node, editorPart);
		});
	}

	private void updateGUIToolTip(SapGuiNode node, IEditorPart editorPart) {
		try {
			Method method = WorkbenchPart.class.getDeclaredMethod("setTitleToolTip", String.class);
			method.setAccessible(true);

			String tooltip = "";

			if (store.getBoolean(PreferenceConstants.PUT_TABNAME_INTO_SAP_GUI_EDITOR_TOOLTIP)) {
				tooltip = node.getTitle() + " " + node.getObjectDescription();
			} else {
				tooltip = node.getObjectDescription();
			}
			method.invoke(editorPart, tooltip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateGUITabName(SapGuiNode node, IEditorPart editorPart) {
		try {
			Method method = WorkbenchPart.class.getDeclaredMethod("setPartName", String.class);
			method.setAccessible(true);
			String title = node.getTitle();
			if (store.getBoolean(PreferenceConstants.PUT_DESCRIPTION_INTO_SAP_GUI_EDITOR_TABS_TITLE)) {
				title = title + " " + node.getObjectDescription();
			}
			method.invoke(editorPart, title); // pass arguments as needed
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}