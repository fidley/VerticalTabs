package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TreeNode;
import com.abapblog.verticaltabs.views.VTView;

public class FolderDoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		try {
			TreeSelection ts = (TreeSelection) event.getSelection();
			TreeNode node = (TreeNode) ts.getFirstElement();
			if (node instanceof GroupNode || node instanceof ProjectNode) {
				TreeViewer tree = VTView.getFilteredTree().getViewer();
				tree.setExpandedState(node, !tree.getExpandedState(node));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
