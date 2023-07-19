package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreePatternFilter extends PatternFilter {

	public TreePatternFilter() {
		setIncludeLeadingWildcard(true);
	}

	@Override
	protected boolean isLeafMatch(final Viewer viewer, final Object element) {

		boolean isMatch = false;
		if (element instanceof TabNode) {
			TabNode leaf = (TabNode) element;
			isMatch |= wordMatches(leaf.getTitle());
		}

		return isMatch;
	}

}