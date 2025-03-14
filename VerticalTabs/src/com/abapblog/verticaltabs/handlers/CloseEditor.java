package com.abapblog.verticaltabs.handlers;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.TreeContentProvider;

public final class CloseEditor {

	public static void closeEditor(IEditorReference editorReference) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		page.closeEditors(new IEditorReference[] { editorReference }, true);
		TreeContentProvider.removeClosedTab(editorReference);
	}

	public static void closeEditors(IEditorReference[] editorReferences) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		page.closeEditors(editorReferences, true);
		TreeContentProvider.removeClosedTabs();
	}

}
