package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.events.MouseEvent;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;

public interface IRowClickExtension {

	public static final String ROW_CLICK_EXTENSION_ID = "com.abapblog.verticaltabs.extensions.RowClick";

	public Boolean handleClick(MouseEvent e, ITreeNode treeNode, int columnIndex);

}
