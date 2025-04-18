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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.filters.LinkedWithProjectFilter;

public class LinkWithProject implements IHandler {

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
		Boolean linkWithProject = (((ToolItem) e.widget).getSelection());
		if (linkWithProject) {
			preferenceStore.setValue(PreferenceConstants.LING_WITH_PROJECT, true);
		} else {
			preferenceStore.setValue(PreferenceConstants.LING_WITH_PROJECT, false);
		}
		setToggleStatus(linkWithProject);
		try {
			LinkedWithProjectFilter.setLinkedProjectName(getActiveEditorReference());
		} catch (Exception ex) {
			LinkedWithProjectFilter.setLinkedProjectName(null);
		}
		TreeContentProvider.refreshTree();
		return null;
	}

	private IEditorReference getActiveEditorReference() {
		try {
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();

			if (activeEditor != null) {
				IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getEditorReferences();

				for (IEditorReference editorReference : editorReferences) {
					if (editorReference.getEditor(false) == activeEditor) {
						return editorReference;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	public static final String ID = "com.abapblog.verticaltabs.commands.linkWithProject";

	public static void setDefaultToggleStatus() {
		if (preferenceStore.getBoolean(PreferenceConstants.LING_WITH_PROJECT)) {
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
