package com.abapblog.verticaltabs.tree;

import java.util.EnumMap;

import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;

public enum Columns {
	PIN, NAME, CLOSE, PROJECT, PATH;

	public static final String VISIBLE = "VISIBLE";
	public static final String HIDDEN = "HIDDEN";
	private static EnumMap<Columns, Integer> columnSequence = new EnumMap<>(Columns.class);
	private static EnumMap<Columns, Integer> columnTableViewerSequence = new EnumMap<>(Columns.class);
	private static final EnumMap<Columns, Integer> defaultColumnSequence = new EnumMap<>(Columns.class);
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	static {
		setDefaultColumnSequence();
		setColumnsSequenceFromPreferences();
		setColumnsTableViewerSequence();

	}

	private static void setDefaultColumnSequence() {
		defaultColumnSequence.put(NAME, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_NAME));
		defaultColumnSequence.put(CLOSE, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_CLOSE));
		defaultColumnSequence.put(PIN, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PIN));
		defaultColumnSequence.put(PROJECT, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PROJECT));
		defaultColumnSequence.put(PATH, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PATH));
	}

	private static void setColumnsTableViewerSequence() {
		columnTableViewerSequence = columnSequence.clone();
	}

	public static int getInteger(Columns c) {
		return columnSequence.getOrDefault(c, defaultColumnSequence.get(c));
	}

	public static Columns fromInteger(int x) {

		for (EnumMap.Entry<Columns, Integer> entry : columnSequence.entrySet()) {
			if (entry.getValue().equals(x))
				return entry.getKey();
		}

		for (EnumMap.Entry<Columns, Integer> entry : defaultColumnSequence.entrySet()) {
			if (entry.getValue().equals(x))
				return entry.getKey();
		}

		return null;
	}

	private static void setColumnsSequenceFromPreferences() {
		columnSequence.put(CLOSE, store.getInt(PreferenceConstants.COLUMN_INDEX_CLOSE));
		columnSequence.put(NAME, store.getInt(PreferenceConstants.COLUMN_INDEX_NAME));
		columnSequence.put(PIN, store.getInt(PreferenceConstants.COLUMN_INDEX_PIN));
		columnSequence.put(PROJECT, store.getInt(PreferenceConstants.COLUMN_INDEX_PROJECT));
		columnSequence.put(PATH, store.getInt(PreferenceConstants.COLUMN_INDEX_PATH));
	}

	public static void resetSequenceToDefault() {
		columnTableViewerSequence = defaultColumnSequence.clone();
	}

	public Boolean isVisible() {
		switch (this) {
		case CLOSE:
			return store.getString(PreferenceConstants.COLUMN_VISIBILITY_CLOSE).equals(Columns.VISIBLE);
		case NAME:
			return store.getString(PreferenceConstants.COLUMN_VISIBILITY_NAME).equals(Columns.VISIBLE);
		case PATH:
			return store.getString(PreferenceConstants.COLUMN_VISIBILITY_PATH).equals(Columns.VISIBLE);
		case PIN:
			return store.getString(PreferenceConstants.COLUMN_VISIBILITY_PIN).equals(Columns.VISIBLE);
		case PROJECT:
			return store.getString(PreferenceConstants.COLUMN_VISIBILITY_PROJECT).equals(Columns.VISIBLE);
		default:
			return false;

		}
	}

	public String getColumnHeaderText() {
		switch (this) {
		case CLOSE:
			return "Close";
		case NAME:
			return "Name";
		case PATH:
			return "Path";
		case PIN:
			return "Pin";
		case PROJECT:
			return "Project";
		default:
			return "";
		}
	}

	public void setSequence(int i) {
		switch (this) {
		case CLOSE:
			store.setValue(PreferenceConstants.COLUMN_INDEX_CLOSE, i);
			break;
		case NAME:
			store.setValue(PreferenceConstants.COLUMN_INDEX_NAME, i);
			break;
		case PATH:
			store.setValue(PreferenceConstants.COLUMN_INDEX_PATH, i);
			break;
		case PIN:
			store.setValue(PreferenceConstants.COLUMN_INDEX_PIN, i);
			break;
		case PROJECT:
			store.setValue(PreferenceConstants.COLUMN_INDEX_PROJECT, i);
			break;
		}
		columnTableViewerSequence.remove(this);
		columnTableViewerSequence.put(this, i);
	}

	public static Columns getBySequence(int i) {
		for (EnumMap.Entry<Columns, Integer> entry : columnTableViewerSequence.entrySet()) {
			if (entry.getValue().equals(i))
				return entry.getKey();
		}
		return null;
	}

	public void setVisible(Boolean visible) {
		String visibilityState = "";
		if (visible) {
			visibilityState = Columns.VISIBLE;
		} else {
			visibilityState = Columns.HIDDEN;
		}

		switch (this) {
		case CLOSE:
			store.setValue(PreferenceConstants.COLUMN_VISIBILITY_CLOSE, visibilityState);
			break;
		case NAME:
			store.setValue(PreferenceConstants.COLUMN_VISIBILITY_NAME, visibilityState);
			break;
		case PATH:
			store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PATH, visibilityState);
			break;
		case PIN:
			store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PIN, visibilityState);
			break;
		case PROJECT:
			store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PROJECT, visibilityState);
			break;

		}

	}

	public static int[] getSortOrderForTreeViewer() {
		int[] sortOrder = new int[5];
		for (EnumMap.Entry<Columns, Integer> entry : columnTableViewerSequence.entrySet()) {
			sortOrder[entry.getValue()] = getInteger(entry.getKey());
		}
		return sortOrder;
	}

	public static int getTotalNumberOfColumns() {
		return defaultColumnSequence.size();
	}

}
