package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;

import com.abapblog.verticaltabs.dialogs.GroupRenameDialog;
import com.abapblog.verticaltabs.dialogs.TabRenameDialog;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class RenameGroup implements IHandler {

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
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof GroupNode) {
				GroupNode gn = (GroupNode) selectedNode;
				final GroupRenameDialog dialog = new GroupRenameDialog(VTView.getTreeViewer().getControl().getShell(),
						gn.getTitle());
				dialog.create();
				if (dialog.open() == Window.OK) {
					gn.setTitle(dialog.getName());
				}
			} else if (selectedNode instanceof TabNode) {
				TabNode tn = (TabNode) selectedNode;
				final TabRenameDialog dialog = new TabRenameDialog(VTView.getTreeViewer().getControl().getShell(),
						tn.getTitle());
				dialog.create();
				if (dialog.open() == Window.OK) {
					tn.setManualTitle(dialog.getName());
				}
			}
			VTView.getTreeViewer().refresh();
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
