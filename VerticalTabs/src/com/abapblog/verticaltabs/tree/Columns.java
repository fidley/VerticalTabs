package com.abapblog.verticaltabs.tree;

public enum Columns {
	PIN, TAB, CLOSE, PROJECT;

	public static int getInteger(Columns c) {
		switch (c) {
		case PIN:
			return 2;
		case TAB:
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
		case 0:
			return TAB;
		case 1:
			return CLOSE;
		case 3:
			return PROJECT;
		}
		return null;
	}

}
