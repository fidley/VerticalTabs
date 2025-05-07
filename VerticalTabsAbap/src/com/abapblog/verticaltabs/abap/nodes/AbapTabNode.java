package com.abapblog.verticaltabs.abap.nodes;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;

import com.abapblog.verticaltabs.tree.nodes.INodeWithDescription;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.tools.core.model.adtcore.AdtVersionEnum;
import com.sap.adt.tools.core.sfs.AdtRequestDataProviderFactory;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

public class AbapTabNode extends TabNode implements INodeWithDescription {
	// to be used with Memento
	private String Description = "";

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

	@Override
	public String getObjectDescription() {
		String description = "";
		IEditorPart editor;
		editor = getEditorReference().getEditor(false);
		if (editor instanceof IAdtFormEditor) {
			try {
				IAdtFormEditor adtFE = (IAdtFormEditor) editor;
				description = adtFE.getModel().getDescription() != null ? adtFE.getModel().getDescription() : "";
			} catch (Exception e) {
				// throw new Error(e.getMessage());
			}
		}

		if (description == null || description.isEmpty()) {
			description = Description;
		}
		return description;

	}

	@Override
	public void setObjectDescription(String description) {
		Description = description;
	}
}
