package com.abapblog.verticaltabs.abap.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.State;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.abapblog.verticaltabs.abap.Activator;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class OnlyInactiveEditorsHandler implements IHandler {

	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	public static final String ID = "com.abapblog.verticaltabs.commands.onlyInactiveEditors";

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
		Event e = (Event) event.getTrigger();
		Boolean showOnlyInactiveEditors = (((ToolItem) e.widget).getSelection());
		if (showOnlyInactiveEditors) {
			preferenceStore.setValue(PreferenceConstants.SHOW_ONLY_INACTIVE_EDITORS, true);
		} else {
			preferenceStore.setValue(PreferenceConstants.SHOW_ONLY_INACTIVE_EDITORS, false);
		}
		setToggleStatus(showOnlyInactiveEditors);
		TreeContentProvider.refreshTree();
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

	public static void setDefaultToggleStatus() {
		if (preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_INACTIVE_EDITORS)) {
			setToggleStatus(true);
		} else {
			setToggleStatus(false);
		}
	}

	public static Boolean getToggleStatus() {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		State state = command.getState(RegistryToggleState.STATE_ID);
		return (Boolean) state.getValue();
	}

	public static void setToggleStatus(Boolean toggled) {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		State state = command.getState(RegistryToggleState.STATE_ID);
		state.setValue(toggled);
	}

}
