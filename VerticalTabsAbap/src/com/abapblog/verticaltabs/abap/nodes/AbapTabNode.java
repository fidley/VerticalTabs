package com.abapblog.verticaltabs.abap.nodes;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;

import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.tools.core.model.adtcore.AdtVersionEnum;
import com.sap.adt.tools.core.sfs.AdtRequestDataProviderFactory;

public class AbapTabNode extends TabNode {

	public AbapTabNode(IEditorReference editorReference) {
		super(editorReference);

	}

	public AdtVersionEnum getActivationState() {

		try {
			IFile file = ((IFileEditorInput) getEditorReference().getEditorInput()).getFile();
			return AdtRequestDataProviderFactory.createAdtRequestDataProvider(file).getCurrentVersion();

		} catch (Exception e) {
			return null;
		}

	}

	public Boolean isLocked() {
		try {
			IFile file = ((IFileEditorInput) getEditorReference().getEditorInput()).getFile();
			return AdtRequestDataProviderFactory.createAdtRequestDataProvider(file).isLocked();
		} catch (Exception e) {
			return false;
		}

	}
}
