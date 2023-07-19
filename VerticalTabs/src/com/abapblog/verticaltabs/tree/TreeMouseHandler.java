package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;

public class TreeMouseHandler implements MouseListener, MouseMoveListener {
	private final RowClickHandler rowClickHandler = new RowClickHandler();
	private final RowMouseMoveOverHandler rowMouseMoveOverHandler = new RowMouseMoveOverHandler();

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent e) {
		Tree tree = (Tree) e.getSource();
		Point coords = new Point(e.x, e.y);
		TreeItem item = tree.getItem(coords);

		if (item != null) {
			int columns = tree.getColumnCount();
			for (int i = 0; i < columns; i++) {
				if (item.getBounds(i).contains(coords)) {
					rowClickHandler.handleClick(e, (ITreeNode) item.getData(), i);
				}
			}
		}

	}

	@Override
	public void mouseUp(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMove(MouseEvent e) {
		Tree tree = (Tree) e.getSource();
		Point coords = new Point(e.x, e.y);
		TreeItem item = tree.getItem(coords);

		if (item != null) {
			int columns = tree.getColumnCount();
			for (int i = 0; i < columns; i++) {
				if (item.getBounds(i).contains(coords)) {
					rowMouseMoveOverHandler.handleClick(e, (ITreeNode) item.getData(), i);
				}
			}
		}

	}

}
