package com.abapblog.verticaltabs.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.tree.TreeSorting;

public class PreferenceInitilizer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.GROUP_BY_PROJECT, false);
		store.setDefault(PreferenceConstants.TREE_SORTING, TreeSorting.MANUAL.name());
		store.setDefault(PreferenceConstants.COLUMN_WIDTH_CLOSE, 20);
		store.setDefault(PreferenceConstants.COLUMN_WIDTH_PIN, 30);
		store.setDefault(PreferenceConstants.COLUMN_WIDTH_PROJECT, 120);
		store.setDefault(PreferenceConstants.COLUMN_WIDTH_NAME, 300);
	}

}
