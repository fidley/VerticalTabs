package com.abapblog.verticaltabs.tree.nodes;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ProjectNode extends TreeNode {

	private static Image ImageToDispose = null;

	private IProject project = null;

	private static final Image getProjectImage() {
		try {
			ImageToDispose = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/org.eclipse.wb.core/icons/project_open.gif"))
					.createImage();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ImageToDispose;
	}

	public ProjectNode(IProject project) {
		super(project.getName(), getProjectImage(), project.getFullPath().toString());
		this.setProject(project);
	}

	public ProjectNode() {
		super("Not linked", ImageToDispose, "");
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
