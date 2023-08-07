package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.State;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.TreeSorting;
import com.abapblog.verticaltabs.views.VTView;

public class SortCommand implements IHandler {
	private static final String STATE_NEXT = "Next";
	private static final String STATE_MANUAL = "Manual";
	private static final String STATE_PROJECT = "Project";
	private static final String STATE_NAME = "Name";
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private static String oldState = "";
	public static final String ID = "com.abapblog.verticaltabs.commands.sort";

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
		if (HandlerUtil.matchesRadioState(event)) {
			return null;
		}
		String currentState = event.getParameter(RadioState.PARAMETER_ID);
		switch (currentState) {
		case STATE_NAME:
			new NameSort().execute(event);
			oldState = STATE_NAME;
			HandlerUtil.updateRadioState(event.getCommand(), currentState);
			break;
		case STATE_PROJECT:
			new ProjectSort().execute(event);
			oldState = STATE_PROJECT;
			HandlerUtil.updateRadioState(event.getCommand(), currentState);
			break;
		case STATE_MANUAL:
			new ManualSort().execute(event);
			oldState = STATE_MANUAL;
			HandlerUtil.updateRadioState(event.getCommand(), currentState);
			break;
		case STATE_NEXT:

			switch (oldState) {
			case STATE_NAME:
				new ManualSort().execute(event);
				oldState = STATE_MANUAL;
				break;
			case STATE_PROJECT:
				new NameSort().execute(event);
				oldState = STATE_NAME;
				break;
			case STATE_MANUAL:
				new ProjectSort().execute(event);
				oldState = STATE_PROJECT;
				break;

			default:
				break;
			}
			HandlerUtil.updateRadioState(event.getCommand(), oldState);
			break;
		default:
			break;
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

	public static TreeSorting getSorterFromPreference() {
		String sorting = preferenceStore.getString(PreferenceConstants.TREE_SORTING);
		if (sorting.contentEquals(TreeSorting.MANUAL.name()))
			return TreeSorting.MANUAL;
		if (sorting.contentEquals(TreeSorting.PROJECT.name()))
			return TreeSorting.PROJECT;
		if (sorting.contentEquals(TreeSorting.NAME.name()))
			return TreeSorting.NAME;
		return TreeSorting.MANUAL;
	}

	public static void setSorterToPreference(TreeSorting treeSorting) {
		preferenceStore.setValue(PreferenceConstants.TREE_SORTING, treeSorting.name());
	}

	public static void setSelectedStatus(Boolean selected, String ID) {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		State state = command.getState(RadioState.STATE_ID);
		if (state == null) {
			state = new RadioState();
			command.addState(RadioState.STATE_ID, state);
		}

		switch (getSorterFromPreference()) {
		case MANUAL:
			changeState(command, state, STATE_MANUAL);
			break;
		case PROJECT:
			changeState(command, state, STATE_PROJECT);
			break;
		case NAME:
			changeState(command, state, STATE_NAME);
			break;
		default:
			break;
		}
		;

	}

	private static void changeState(Command command, State state, String value) {
		state.setValue(value);
		oldState = value;
		try {
			HandlerUtil.updateRadioState(command, value);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void sort(TreeSorting sorting) {
		VTView.sorter.setSorting(sorting);
		setSorterToPreference(sorting);
		TreeContentProvider.refreshTree();
	}

}
