package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.views.VTView;

public class TreeKeyListener extends KeyAdapter {

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.keyCode == SWT.CR || e.keyCode == SWT.Selection || e.keyCode == 16777296) && e.stateMask == SWT.NONE) {
			Tree tree = (Tree) e.getSource();
			if (tree.getSelectionCount() == 1) {
				TreeItem item = tree.getSelection()[0];
				try {
					ITreeNode tn = (ITreeNode) item.getData();
					if (tn.isOpenable())
						tn.open();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
		if (e.keyCode == SWT.TAB && e.stateMask == SWT.NONE) {
			VTFilteredTree ft = VTView.getFilteredTree();
			ft.setFocusOnFilter();

		}

	}

}
