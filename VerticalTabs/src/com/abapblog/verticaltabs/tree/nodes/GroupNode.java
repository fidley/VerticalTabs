package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.swt.graphics.Image;

public class GroupNode extends TreeNode {
	public GroupNode(String title, Image image, String tooltip) {
		super(title, image, tooltip);
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
}
