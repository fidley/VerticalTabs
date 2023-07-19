package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorReference;

public interface ITreeNode {

	public NodeType getNodeType();

	public boolean isOpenable();

	public boolean isExpandable();

	public boolean isPinable();

	public ITreeNode getParent();

	public void setParent(ITreeNode parent);

	public ITreeNode[] getChildren();

	public boolean hasChildren();

	public boolean contains(IEditorReference er);

	public boolean contains(ITreeNode tn);

	public void removeChild(ITreeNode child);

	public void addChild(ITreeNode child);

	public String getTitle();

	public String getTooltip();

	public Image getImage();

	public void open();

	public void pin();

	public void unpin();

	public boolean isPinned();

	public String getProjectName();

	public void setProjectName(String projectName);

	public String getPath();

	public void setPath(String path);

	public Integer getSortIndex();

	public void setSortIndex(Integer sortIndex);

}
