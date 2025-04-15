package com.abapblog.verticaltabs.handlers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class OpenContainingFolderInExplorer implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof TabNode) {
				TabNode tn = (TabNode) selectedNode;
				IResource res = getResourceFromEditorReference(tn.getEditorReference());
				if (res != null) {
					try {
						File file = res.getRawLocation().toFile();
						if (file.exists()) {
							Desktop.getDesktop().open(file.getParentFile());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (tn.getEditorReference().getEditorInput() instanceof IURIEditorInput) {
							IURIEditorInput uriEditorInput = (IURIEditorInput) tn.getEditorReference().getEditorInput();
							File file = new File(uriEditorInput.getURI());
							if (file.exists()) {
								try {
									Desktop.getDesktop().open(file.getParentFile());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return null;

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	private IResource getResourceFromEditorReference(IEditorReference editorReference) {
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
