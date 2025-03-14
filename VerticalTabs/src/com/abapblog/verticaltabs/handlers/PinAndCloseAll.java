package com.abapblog.verticaltabs.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class PinAndCloseAll implements IHandler {

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
		List<IEditorReference> editorReferencesToClose = new ArrayList<>();
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof GroupNode || selectedNode instanceof ProjectNode) {
				ITreeNode tn = (ITreeNode) selectedNode;
				for (ITreeNode child : tn.getChildren()) {
					TabNode tabNode = (TabNode) child;
					tabNode.pin();
					editorReferencesToClose.add(tabNode.getEditorReference());
				}

			}
		}
		if (editorReferencesToClose.size() > 0)
			CloseEditor.closeEditors(
					editorReferencesToClose.toArray(new IEditorReference[editorReferencesToClose.size()]));
		TreeContentProvider.refreshTree();
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
