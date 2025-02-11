package com.abapblog.verticaltabs.tree.filters;

import org.eclipse.jface.viewers.Viewer;

public interface IPaternFilterExtension {

	String PATTERN_FILTER_EXTENSION_ID = "com.abapblog.verticaltabs.extensions.PatternFilter";

	public Boolean isLeafMatch(final Viewer viewer, final Object element, final Boolean originalMatch);

	public Boolean isElementVisible(Viewer viewer, Object element, Boolean originalVisible);
}
