package com.abapblog.verticaltabs.tree;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnableWithResult;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.preferences.TabNavigation;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class RowClickHandler {
	private EModelService modelService;
	private IWorkbenchWindow window;
	private final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public void handleClick(MouseEvent e, ITreeNode treeNode, int columnIndex) {
		IConfigurationElement[] config = RegistryFactory.getRegistry()
				.getConfigurationElementsFor(IRowClickExtension.ROW_CLICK_EXTENSION_ID);
		try {
			for (IConfigurationElement ce : config) {
				final Object o = ce.createExecutableExtension("class");
				if (o instanceof IRowClickExtension) {
					ISafeRunnableWithResult runnable = new ISafeRunnableWithResult<Boolean>() {
						@Override
						public void handleException(Throwable er) {
							System.out.println("Exception in client");
						}

						@Override
						public Boolean runWithResult() throws Exception {

							return ((IRowClickExtension) o).handleClick(e, treeNode, columnIndex);
						}
					};

					Boolean handled = (Boolean) SafeRunner.run(runnable);
					if (handled)
						return;
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}

		if (e.button == 3) {
//			VTView.getTreeViewer().getTree().setFocus();
			return;
		}

		switch (Columns.fromInteger(columnIndex)) {
		case NAME:
			if (e.count < 2
					&& store.getString(PreferenceConstants.TAB_NAVIGATION).equals(TabNavigation.AT_DOUBLE_CLICK.name()))
				break;

			if (treeNode.isOpenable() && !((e.stateMask & SWT.CTRL) == SWT.CTRL)
					&& !((e.stateMask & SWT.SHIFT) == SWT.SHIFT))
				treeNode.open();
			break;
		case PIN:
			if (treeNode.isPinable()) {
				if (treeNode.isPinned()) {
					treeNode.unpin();
				} else {
					treeNode.pin();
				}
			}
			TreeContentProvider.refreshTree();
			break;
		case CLOSE:
			if (treeNode instanceof TabNode) {
				TabNode tabNode = (TabNode) treeNode;
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IEditorPart editor = tabNode.getEditorReference().getEditor(true);
				if (tabNode.getSplitTag().equals("")) {
					page.closeEditor(editor, true);
				} else {
					closeSplittedEditor(tabNode);
					if (!store.getBoolean(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS))
						page.closeEditor(editor, true);
				}

			}

		}
	}

	private void closeSplittedEditor(TabNode tabNode) {
		if (tabNode.getEditorReference().getPart(false) instanceof IEditorPart) {
			IEditorPart ep = (IEditorPart) tabNode.getEditorReference().getPart(true);
			MPart editorPart = ep.getSite().getService(MPart.class);
			if (editorPart == null)
				return;

			window = tabNode.getEditorReference().getPage().getWorkbenchWindow();

			// Get services
			modelService = editorPart.getContext().get(EModelService.class);

			MPartStack stack = getStackFor(editorPart);
			if (stack == null)
				return;
			Boolean horizontal = false;
			if (tabNode.getSplitTag().equals(IPresentationEngine.SPLIT_HORIZONTAL))
				horizontal = true;
			window.getShell().setRedraw(false);
			try {
				// Determine which part has the tags
				MStackElement stackSelElement = stack.getSelectedElement();
				MPart taggedEditor = editorPart;
				if (stackSelElement instanceof MCompositePart) {
					List<MPart> innerElements = modelService.findElements(stackSelElement, null, MPart.class);
					taggedEditor = innerElements.get(1); // '0' is the composite part
				}
				if (store.getBoolean(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS))

					if (Boolean.FALSE.equals(horizontal)) {
						if (taggedEditor.getTags().contains(IPresentationEngine.SPLIT_VERTICAL)) {
							taggedEditor.getTags().remove(IPresentationEngine.SPLIT_VERTICAL);
						} else {
							editorPart.getTags().remove(IPresentationEngine.SPLIT_VERTICAL);
						}
					} else if (taggedEditor.getTags().contains(IPresentationEngine.SPLIT_HORIZONTAL)) {
						taggedEditor.getTags().remove(IPresentationEngine.SPLIT_HORIZONTAL);
					} else {
						editorPart.getTags().remove(IPresentationEngine.SPLIT_HORIZONTAL);
					}

			} finally {
				window.getShell().setRedraw(true);
			}
		}
	}

	private MPartStack getStackFor(MPart part) {
		MUIElement presentationElement = part.getCurSharedRef() == null ? part : part.getCurSharedRef();
		MUIElement parent = presentationElement.getParent();
		while (parent != null && !(parent instanceof MPartStack))
			parent = parent.getParent();

		return (MPartStack) parent;
	}

}
