package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.RootNode;
import com.abapblog.verticaltabs.views.VTView;

public class GroupByProject implements IHandler {

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
		RootNode project = tcp.getProjectsRoot();
		RootNode current = (RootNode) tcp.getInvisibleRoot();
		Event e = (Event) event.getTrigger();
		if (e.widget instanceof ToolItem) {
			if (((ToolItem) e.widget).getSelection()) {
				if (!current.equals(project)) {
					tcp.setInvisibleRoot(project);
					tcp.refreshTree();
				}
			} else {
				if (current.equals(project)) {
					tcp.setInvisibleRoot(tcp.getManualRoot());
					tcp.refreshTree();
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
