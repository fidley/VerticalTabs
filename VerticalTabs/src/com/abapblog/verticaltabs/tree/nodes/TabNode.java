package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class TabNode extends TreeNode implements IPropertyListener {
	private IEditorReference editorReference;
	private IProject project;
	private boolean pinned = false;

	public TabNode(IEditorReference editorReference) throws PartInitException {
		super(editorReference.getTitle(), editorReference.getTitleImage(), editorReference.getTitleToolTip());
		this.setEditorReference(editorReference);
		editorReference.addPropertyListener(this);
		setProjectAndPath(editorReference);
		setSortIndex(TreeContentProvider.getNextSortIndex());

	}

	private void setProjectAndPath(IEditorReference editorReference) {

		IEditorInput editorInput;
		try {
			editorInput = editorReference.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				try {

					IFileEditorInput input = (IFileEditorInput) editorInput;
					IFile file = input.getFile();
					setProject(file.getProject());
					setProjectName(getProject().getName());
					file.getFullPath();
					if (file.getFullPath() != null)
						setPath(file.getFullPath().toString());
				} catch (Exception e) {

				}

			} else {
				setProject(editorInput.getAdapter(IProject.class));
				if (getProject() != null) {
					setProjectName(getProject().getName());
				}
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public NodeType getNodeType() {
		return NodeType.TAB;
	}

	@Override
	public boolean isOpenable() {
		return true;
	}

	@Override
	public boolean isExpandable() {
		return false;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public IEditorReference getEditorReference() {
		return editorReference;
	}

	private void setEditorReference(IEditorReference editorReference) {
		this.editorReference = editorReference;
	}

	@Override
	public void open() {
		IWorkbenchPart part = getEditorReference().getPart(true);
		if (part != null) {
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null) {
				activePage.activate(part);
			}
		}
	}

	@Override
	public boolean isPinable() {
		return true;
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		updateTitleAtChange(source, propId);
		updateTilteWhenDirty(source, propId);
	}

	private void updateTilteWhenDirty(Object source, int propId) {
		if (propId == IWorkbenchPartConstants.PROP_DIRTY) {
			IWorkbenchPart part = (IWorkbenchPart) source;
			if (part instanceof IEditorPart) {
				if (getEditorReference().isDirty()) {
					setTitle("*" + part.getTitle());
				} else {
					setTitle(part.getTitle());
				}
				TreeContentProvider.refreshTree();
			}

		}
	}

	private void updateTitleAtChange(Object source, int propId) {
		if (propId == IWorkbenchPartConstants.PROP_TITLE) {
			IWorkbenchPart part = (IWorkbenchPart) source;
			if (part instanceof IEditorPart) {
				if (getEditorReference().isDirty()) {
					setTitle("*" + part.getTitle());
				} else {
					setTitle(part.getTitle());
				}
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500);
							setImage(getEditorReference().getTitleImage());
							TreeContentProvider.refreshTree();
						} catch (Exception e) {
						}

					}

				});

			}

		}
	}

	@Override
	public boolean isPinned() {
		return pinned;
	}

	@Override
	public void pin() {
		pinned = true;
	}

	@Override
	public void unpin() {
		pinned = false;
	}

	public void updateFromEditorReferenece() {
		setTitle(editorReference.getTitle());
		setImage(editorReference.getTitleImage());
		setProjectAndPath(editorReference);
	}

	public IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

}
