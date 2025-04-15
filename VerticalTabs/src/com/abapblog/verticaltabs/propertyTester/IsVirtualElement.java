package com.abapblog.verticaltabs.propertyTester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class IsVirtualElement extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof TabNode) {
				TabNode tn = (TabNode) selectedNode;
				IFile res = getResourceFromEditorReference(tn.getEditorReference());
				if (res != null) {
					return !res.isVirtual() && !(res.getRawLocation() == null);
				} else {

					try {
						if (tn.getEditorReference().getEditorInput() instanceof IURIEditorInput) {
							return true;
						}
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}
		return false;
	}

	private IFile getResourceFromEditorReference(IEditorReference editorReference) {
		try {
			IEditorInput editorInput = editorReference.getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) editorInput).getFile();
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
