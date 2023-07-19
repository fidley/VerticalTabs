package com.abapblog.verticaltabs.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

@SuppressWarnings("restriction")
public class LinkWithEditorPartListener implements IPartListener2 {
	private final ILinkedWithEditorView view;

	public LinkWithEditorPartListener(ILinkedWithEditorView view) {
		this.view = view;
	}

	@Override
	public void partActivated(IWorkbenchPartReference ref) {
		if (ref.getPart(true) instanceof IEditorPart) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	@Override
	public void partOpened(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	@Override
	public void partVisible(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			IEditorPart editor = view.getViewSite().getPage().getActiveEditor();
			if (editor != null) {
				view.editorActivated(editor);
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference ref) {

	}

	@Override
	public void partDeactivated(IWorkbenchPartReference ref) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference ref) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference ref) {
	}
}
