package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class CloseSelectedTabNodes implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof TabNode) {
				TabNode tabNode = (TabNode) selectedNode;
				if (tabNode.getParent() == null)
					continue;
				IEditorPart editor = tabNode.getEditorReference().getEditor(true);
				page.closeEditor(editor, true);
			}
			if (selectedNode instanceof GroupNode || selectedNode instanceof ProjectNode) {
				ITreeNode tn = (ITreeNode) selectedNode;
				for (ITreeNode treeNode : tn.getChildren()) {
					TabNode tabNode = (TabNode) treeNode;
					IEditorPart editor = tabNode.getEditorReference().getEditor(true);
					page.closeEditor(editor, true);
				}
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
