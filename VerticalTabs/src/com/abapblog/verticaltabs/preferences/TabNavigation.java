package com.abapblog.verticaltabs.preferences;

public enum TabNavigation {
	AT_SELECTION("Selection"), AT_DOUBLE_CLICK("Double-Click");

	private String description;

	private TabNavigation(final String description) {
		this.description = description;
	}

	/**
	 * Creates key/value pair array from enum name and description
	 *
	 * @return key/value pair array from enum name and description
	 */
	public static String[][] toNamesAndKeys() {
		final TabNavigation[] types = TabNavigation.values();
		final String[][] keyValue = new String[types.length][2];
		for (int i = 0; i < types.length; i++) {
			keyValue[i][0] = types[i].description;
			keyValue[i][1] = types[i].name();
		}
		return keyValue;
	}
}
