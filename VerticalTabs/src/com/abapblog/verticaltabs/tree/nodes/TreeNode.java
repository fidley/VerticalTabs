package com.abapblog.verticaltabs.tree.nodes;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorReference;

public abstract class TreeNode implements IAdaptable, ITreeNode {
	public ITreeNode parent;
	protected Image image;
	protected String title;
	protected String tooltip;
	private String projectName = "";
	private String path = "";
	private Integer sortIndex = Integer.valueOf(0);
	private ArrayList<ITreeNode> children = new ArrayList<ITreeNode>();

	public TreeNode(String title, Image image, String tooltip) {
		this.setTitle(title);
		this.setImage(image);
		this.setTooltip(tooltip);
	}

	@Override
	public ITreeNode getParent() {
		return parent;
	}

	@Override
	public void setParent(ITreeNode parent) {
		this.parent = parent;

	}

	@Override
	public void addChild(ITreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	@Override
	public void removeChild(ITreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	@Override
	public ITreeNode[] getChildren() {
		return children.toArray(new ITreeNode[children.size()]);
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public String toString() {
		return getTitle() + " " + getTooltip() + " ";

	}

	@Override
	public Image getImage() {
		return image;
	}

	protected void setImage(Image image) {
		this.image = image;
	}

	@Override
	public String getTitle() {
		return title;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	protected void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public void open() {

	}

	@Override
	public void pin() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPinned() {
		return false;
	}

	@Override
	public void unpin() {
	}

	@Override
	public boolean contains(IEditorReference er) {
		ITreeNode[] treeNodes = getChildren();
		for (int i = 0; i < treeNodes.length; i++) {
			if (treeNodes[i] instanceof TabNode) {
				TabNode tabNode = (TabNode) treeNodes[i];
				if (tabNode.getEditorReference().equals(er)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean contains(ITreeNode tn) {
		ITreeNode[] treeNodes = getChildren();
		for (int i = 0; i < treeNodes.length; i++) {
			if (treeNodes[i] instanceof ITreeNode) {
				ITreeNode treeNode = treeNodes[i];
				if (treeNode.equals(tn)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Integer getSortIndex() {
		return sortIndex;
	}

	@Override
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;

	}
}
