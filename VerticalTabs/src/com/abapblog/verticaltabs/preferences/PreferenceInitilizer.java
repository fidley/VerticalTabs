package com.abapblog.verticaltabs.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.tree.Columns;
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
		store.setDefault(PreferenceConstants.COLUMN_WIDTH_PATH, 300);
		store.setDefault(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS, true);
		store.setDefault(PreferenceConstants.TAB_NAVIGATION, TabNavigation.AT_SELECTION.name());
		store.setDefault(PreferenceConstants.SELECT_ACTIVE_TAB_IN_TREE, true);
		store.setDefault(PreferenceConstants.SHOW_ONLY_DIRTY_EDITORS, false);
		initializeColumnVisibilityAndSorting(store);
	}

	public void initializeColumnVisibilityAndSorting(final IPreferenceStore store) {
		store.setDefault(PreferenceConstants.COLUMN_VISIBILITY_CLOSE, Columns.VISIBLE);
		store.setDefault(PreferenceConstants.COLUMN_VISIBILITY_PIN, Columns.VISIBLE);
		store.setDefault(PreferenceConstants.COLUMN_VISIBILITY_PROJECT, Columns.VISIBLE);
		store.setDefault(PreferenceConstants.COLUMN_VISIBILITY_NAME, Columns.VISIBLE);
		store.setDefault(PreferenceConstants.COLUMN_VISIBILITY_PATH, Columns.VISIBLE);
		store.setDefault(PreferenceConstants.COLUMN_INDEX_CLOSE, 1);
		store.setDefault(PreferenceConstants.COLUMN_INDEX_PIN, 2);
		store.setDefault(PreferenceConstants.COLUMN_INDEX_PROJECT, 3);
		store.setDefault(PreferenceConstants.COLUMN_INDEX_NAME, 0);
		store.setDefault(PreferenceConstants.COLUMN_INDEX_PATH, 4);
	}

}
