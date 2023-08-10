package com.abapblog.verticaltabs.tree.memento;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;

public class MementoReader extends MementoOperations {

	private XMLMemento readMementoFromFile() throws IOException, WorkbenchException {
		FileInputStream stream = new FileInputStream(getMementoFile());
		InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		int bufferSize = 1024;
		char[] buffer = new char[bufferSize];
		StringBuilder out = new StringBuilder();

		for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0;) {
			out.append(buffer, 0, numRead);
		}
		StringReader sr = new StringReader(out.toString());
		return XMLMemento.createReadRoot(sr);
	}

	public IMemento loadMemento() {
		try {
			return readMementoFromFile();
		} catch (WorkbenchException | IOException e) {
			e.printStackTrace();
			return XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_VIEW_STATE);

		}

	}

}
