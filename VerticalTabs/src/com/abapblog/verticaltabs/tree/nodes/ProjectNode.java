package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.abapblog.verticaltabs.icons.Icons;

public class ProjectNode extends TreeNode implements Comparable<ProjectNode> {
	private static Integer biggestIndex = Integer.valueOf(9999);
	private IProject project = null;

	private static final Image getProjectImage() {
		return Icons.getIcon(Icons.ICON_PROJECT);
	}

	public ProjectNode(IProject project) {
		super(project.getName(), getProjectImage(), project.getFullPath().toString());
		this.setProject(project);
		setSortIndex(getNextSortIndex());
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

	public static Integer getNextSortIndex() {
		biggestIndex += 1;
		return biggestIndex;
	}

	@Override
	public int compareTo(ProjectNode o) {
		if (o == null)
			return 0;
		return getSortIndex().compareTo(o.getSortIndex());
	}

	@Override
	public String getProjectName() {
		try {
			if (project != null) {
				return project.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Image getImage() {
		if (project == null) {
			return super.getImage();
		}
		ILabelProvider baseProvider = new WorkbenchLabelProvider();
		DecoratingLabelProvider decoratingProvider = new DecoratingLabelProvider(baseProvider,
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
		Image decoratedImage = decoratingProvider.getImage(project);
		if (decoratedImage != null) {
			return decoratedImage;
		} else {
			return super.getImage();
		}
	}
}
