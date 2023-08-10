package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.views.VTView;

public class Ungroup implements IHandler {

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
		TreeContentProvider tcp = (TreeContentProvider) VTView.getTreeViewer().getContentProvider();
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof GroupNode) {
				GroupNode gn = (GroupNode) selectedNode;
				for (ITreeNode itn : gn.getChildren()) {
					tcp.getNodesFactory().moveTabNodeFromGroupToRoot(itn);
				}
			}
			tcp.refreshTree();
		}
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
