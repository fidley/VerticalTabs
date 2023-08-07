package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;

import com.abapblog.verticaltabs.icons.Icons;

public class ProjectNode extends TreeNode {

	private IProject project = null;

	private static final Image getProjectImage() {
		return Icons.getIcon(Icons.ICON_PROJECT);
	}

	public ProjectNode(IProject project) {
		super(project.getName(), getProjectImage(), project.getFullPath().toString());
		this.setProject(project);
	}

	public ProjectNode() {
		super("Not linked", getProjectImage(), "");
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
