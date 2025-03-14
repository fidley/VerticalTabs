package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodeType;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class Sorter extends ViewerComparator {
	private TreeSorting treeSorting;
	private static final int DESCENDING = 1;
	private static final int ASCENDING = -1;
	private int direction = ASCENDING;

	public Sorter() {
		this.treeSorting = TreeSorting.MANUAL;
		direction = ASCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setSorting(TreeSorting sorting) {
		this.treeSorting = sorting;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ITreeNode p1 = (ITreeNode) e1;
		ITreeNode p2 = (ITreeNode) e2;
		String p1Title = cleanDirtyTitle(p1.getTitle());
		String p2Title = cleanDirtyTitle(p2.getTitle());
		int rc = 0;
		switch (treeSorting) {
		case MANUAL:
			if (p1.getNodeType().equals(p2.getNodeType())) {
				rc = p1.getSortIndex().compareTo(p2.getSortIndex());
				break;
			}

			else {
				rc = sortWhenOneIsGroupNode(p1, p2, rc);
			}

			break;
		case NAME:
			if (p1.getNodeType().equals(p2.getNodeType())) {
				rc = (p1Title + "_" + p1.getProjectName()).toUpperCase()
						.compareTo((p2Title + "_" + p2.getProjectName()).toUpperCase());
				break;
			} else {
				rc = sortWhenOneIsGroupNode(p1, p2, rc);
			}

			break;

		case PROJECT:
			if (p1.getNodeType().equals(p2.getNodeType())) {
				rc = (p1.getProjectName() + "_" + p1Title).toUpperCase()
						.compareTo((p2.getProjectName() + "_" + p2Title).toUpperCase());
				break;
			} else {
				rc = sortWhenOneIsGroupNode(p1, p2, rc);
			}

			break;
		case EDITOR:
			if (p1.getNodeType().equals(p2.getNodeType()) && p1.getNodeType().equals(NodeType.TAB)) {
				TabNode t1 = (TabNode) p1;
				TabNode t2 = (TabNode) p2;
				rc = (t1.getEditorReference().getId() + "_" + t1.getProjectName() + "_" + p1Title).toUpperCase()
						.compareTo((t2.getEditorReference().getId() + "_" + t2.getProjectName() + "_" + p2Title)
								.toUpperCase());
				break;
			} else {
				rc = sortWhenOneIsGroupNode(p1, p2, rc);
			}

			break;

		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

	private String cleanDirtyTitle(String title) {
		if (title.startsWith("*")) {
			title = title.substring(1);
		}
		return title;
	}

	private int sortWhenOneIsGroupNode(ITreeNode p1, ITreeNode p2, int rc) {
		if (p1.getNodeType().equals(NodeType.GROUP)) {
			rc = -1;
		} else if (p2.getNodeType().equals(NodeType.GROUP)) {
			rc = 1;
		}
		return rc;
	}

}
