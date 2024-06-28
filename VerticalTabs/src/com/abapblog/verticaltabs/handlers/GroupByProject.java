package com.abapblog.verticaltabs.handlers;

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

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.views.VTView;

public class GroupByProject implements IHandler {
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	public static final String ID = "com.abapblog.verticaltabs.commands.groupbyproject";

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

	public static void setGroupByProjectPreference(Boolean groupByProject) {
		preferenceStore.setValue(PreferenceConstants.GROUP_BY_PROJECT, groupByProject);
	}

	public static Boolean getGroupByProjectPreference() {
		return preferenceStore.getBoolean(PreferenceConstants.GROUP_BY_PROJECT);
	}

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
		if (e.widget instanceof ToolItem) {
			TreeContentProvider tcp = (TreeContentProvider) VTView.getTreeViewer().getContentProvider();
			Boolean groupByProject = (((ToolItem) e.widget).getSelection());
			tcp.toggleGrouping(groupByProject);
			setGroupByProjectPreference(groupByProject);
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
