package com.abapblog.verticaltabs.abap.extensions;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;

import com.abapblog.verticaltabs.abap.nodes.AbapTabNode;
import com.abapblog.verticaltabs.tree.nodes.ITabNodeExtension;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

public class AbapTabNodeCreationHandler implements ITabNodeExtension {

	public AbapTabNodeCreationHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TabNode createExtendedTabNode(IEditorReference editorReference) {
		try {
			IAdtFormEditor editor = (IAdtFormEditor) editorReference.getEditor(false);
			if (editor != null) {
				return getTabNode(editorReference);

			} else {
				return getTabNodeFromFile(editorReference);
			}

		} catch (Exception e) {

			return getTabNodeFromFile(editorReference);
		}

	}

	private TabNode getTabNodeFromFile(IEditorReference editorReference) {
		IFileEditorInput fei;
		try {
			fei = (IFileEditorInput) editorReference.getEditorInput();
			IFile file = fei.getFile();
			if (file.getFullPath().toString().contains("/.adt/")) {
				return getTabNode(editorReference);
			}
		} catch (PartInitException e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}

	public TabNode getTabNode(IEditorReference editorReference) {
		TabNode extendedTabNode = new AbapTabNode(editorReference);
		return extendedTabNode;
	}

}