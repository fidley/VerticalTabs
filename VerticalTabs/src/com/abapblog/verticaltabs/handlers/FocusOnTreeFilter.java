package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.views.VTView;

public class FocusOnTreeFilter implements IHandler {

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
		if (VTView.getFilteredTree() == null || !VTView.getFilteredTree().getVisible())
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VTView.getID());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		VTView.getFilteredTree().setFocus();
		VTView.getFilteredTree().setFocusOnFilter();
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
