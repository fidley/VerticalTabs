package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ProjectNode extends TreeNode {
	private IProject project = null;

	public ProjectNode(IProject project) {
		super(project.getName(), PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER),
				project.getFullPath().toString());
		this.setProject(project);
	}

	public ProjectNode() {
		super("Not linked", PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), "");
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.PROJECT;
	}

	@Override
	public boolean isOpenable() {
		return false;
	}

	@Override
	public boolean isExpandable() {
		return true;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPinable() {
		return true;
	}

	@Override
	public void pin() {
		super.pin();
	}

	public IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	@Override
	public void addChild(ITreeNode child) {
		children.add(child);
	}

	@Override
	public void removeChild(ITreeNode child) {
		children.remove(child);
	}

}
