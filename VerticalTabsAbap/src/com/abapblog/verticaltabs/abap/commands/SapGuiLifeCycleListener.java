package com.abapblog.verticaltabs.abap.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.abap.nodes.SapGuiNode;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
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
	private boolean isProcessing = false;
	private long delay = 1000;
	private Map<String, String> eventProperties = new HashMap<String, String>();
	private IEditorPart editorPart = null;
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	@Override
	public void handleLifeCycleEvent(ISapGuiLifeCycleEvent event) {
		if (store.getBoolean(PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION) == false) {
			return;
		}
//		System.out.println("Event: " + event.getType() + " " + event.getProperties().toString());
		if (event == null || !event.getType().equals(ISapGuiLifeCycleEvent.EventType.GUI_REQUEST_ENDED)) {
			return;
		}
		eventProperties = event.getProperties();
		if (eventProperties.get(EventPropertyIsModal) == null
				|| eventProperties.get(EventPropertyIsModal).equals("1")) {
			return;
		}
		editorPart = (IEditorPart) event.getEditor();
		if (!isProcessing) {
			isProcessing = true;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					processEvent(editorPart, eventProperties);
					isProcessing = false;
				}
			}, delay);
		} else {
			timer.cancel();
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					processEvent(editorPart, eventProperties);
					isProcessing = false;
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
				titleUpdated = updateTitle(node, newTitle);
			}

			else if (transaction.equals(TransactionS000)) {
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), node.getProjectName());
				titleUpdated = updateTitle(node, newTitle);
			} else {
				newTitle = updateProjectNameInTitle(node.getOriginalTitle(), transaction);
				titleUpdated = updateTitle(node, newTitle);
			}
		}
		descriptionUpdated = updateDescription(description, node);
		return titleUpdated || descriptionUpdated;
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
}