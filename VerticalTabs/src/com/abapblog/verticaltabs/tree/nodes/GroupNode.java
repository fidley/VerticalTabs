package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.swt.graphics.Image;

public class GroupNode extends TreeNode {
	private static int nextGroupNumber = 0;

	public GroupNode(String title, Image image) {
		super(title, image, title);
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.GROUP;
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
		return null;
	}

	@Override
	public boolean isPinable() {
		return false;
	}

	@Override
	public void pin() {
		super.pin();
	}

	public static String getNextGroupName() {
		return "Group " + getNextGroupNumber();
	}

	private static int getNextGroupNumber() {
		nextGroupNumber += 1;
		return nextGroupNumber;
	}

	@Override
	public String getTooltip() {
		return super.getTitle();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		super.setTooltip(title);
	}
}
