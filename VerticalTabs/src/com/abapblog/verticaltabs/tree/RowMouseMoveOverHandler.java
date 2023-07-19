package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.events.MouseEvent;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class RowMouseMoveOverHandler {

	public void handleClick(MouseEvent e, ITreeNode treeNode, int columnIndex) {

		switch (Columns.fromInteger(columnIndex)) {
		case CLOSE:
			if (treeNode instanceof TabNode) {
				TabNode tabNode = (TabNode) treeNode;
				System.out.println(e.toString());
			}
			break;
		}

	}

}
