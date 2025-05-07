package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.abapblog.verticaltabs.tree.CellEditingSupport;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class RenameGroup implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		return;

	}

	@Override
	public void dispose() {
		return;

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof GroupNode || selectedNode instanceof TabNode) {
				CellEditingSupport.setSelectedNodeNameEditable((ITreeNode) selectedNode);
			}
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
		return;

	}

}
