package com.abapblog.verticaltabs.tree.nodes;

public class RootNode extends TreeNode {

	public RootNode() {
		super("root", null, "");
		// TODO Auto-generated constructor stub
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.GROOT;
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
	public boolean isPinable() {
		return false;
	}

}
