package com.abapblog.verticaltabs.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;

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

public class CopyPath implements IHandler {

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
		IStructuredSelection selection;
		selection = (IStructuredSelection) VTView.getTreeViewer().getSelection();
		for (Object selectedNode : selection) {
			if (selectedNode instanceof TabNode) {
				TabNode tn = (TabNode) selectedNode;

				IResource res = getResourceFromEditorReference(tn.getEditorReference());
				if (res != null) {
					File file = res.getRawLocation().toFile();
					if (file.exists()) {
						final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(new StringSelection(file.getAbsolutePath()), null);
					}
				} else {
					try {
						if (tn.getEditorReference().getEditorInput() instanceof IURIEditorInput) {
							IURIEditorInput uriEditorInput = (IURIEditorInput) tn.getEditorReference().getEditorInput();
							File file = new File(uriEditorInput.getURI());
							if (file.exists()) {
								final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(new StringSelection(file.getAbsolutePath()), null);
							}
						}
					} catch (PartInitException e) {

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
