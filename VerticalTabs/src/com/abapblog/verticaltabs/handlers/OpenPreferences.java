package com.abapblog.verticaltabs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.abapblog.verticaltabs.preferences.pages.MainPage;

public class OpenPreferences implements IHandler {

	private static final String ORG_ECLIPSE_UI_PREFERENCE_PAGES_KEYS = "org.eclipse.ui.preferencePages.Keys";

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
		PreferencesUtil.createPreferenceDialogOn(null, MainPage.ID,
				new String[] { MainPage.ID, ORG_ECLIPSE_UI_PREFERENCE_PAGES_KEYS }, null).open();
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
