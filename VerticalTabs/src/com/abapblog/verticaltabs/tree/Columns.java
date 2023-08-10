package com.abapblog.verticaltabs.tree;

public enum Columns {
	PIN, NAME, CLOSE, PROJECT;

	public static int getInteger(Columns c) {
		switch (c) {
		case PIN:
			return 2;
		case NAME:
			return 0;
		case CLOSE:
			return 1;
		case PROJECT:
			return 3;
		}
		return -1;
	}

	public static Columns fromInteger(int x) {
		switch (x) {
		case 2:
			return PIN;
		case 1:
			return CLOSE;
		case 0:
			return NAME;
		case 3:
			return PROJECT;
		}
		return null;
	}

}
