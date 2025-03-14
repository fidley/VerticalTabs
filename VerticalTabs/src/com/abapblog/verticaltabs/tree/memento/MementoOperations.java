package com.abapblog.verticaltabs.tree.memento;

import java.io.File;

import com.abapblog.verticaltabs.Activator;

public abstract class MementoOperations {

	protected File getMementoFile() {
		File stateFile = new File(Activator.getDefault().getStateLocation().toString(), "contentProviderMemento.xml");
		return stateFile;
	}

}
