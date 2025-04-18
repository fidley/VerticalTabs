package com.abapblog.verticaltabs.tree.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;

public class LinkedWithProjectFilter extends ViewerFilter {
	private static String linkedProjectName = "";
	private final static IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!store.getBoolean(PreferenceConstants.LING_WITH_PROJECT)) {
			return true;
		}

		if (element instanceof ITreeNode && !(element instanceof GroupNode)) {
			ITreeNode node = (ITreeNode) element;
			return node.getProjectName() != null && node.getProjectName().equals(linkedProjectName);
		}

		if (element instanceof GroupNode) {
			return hasVisibleChildren((TreeViewer) viewer, (GroupNode) element);
		}

		return true;
	}

	public static Boolean setLinkedProjectName(IEditorReference editorReference) {
		String currentLinkedProjectName = linkedProjectName;
		IEditorInput editorInput;
		try {
			editorInput = editorReference.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IFileEditorInput input = (IFileEditorInput) editorInput;
				IFile file = input.getFile();
				LinkedWithProjectFilter.linkedProjectName = file.getProject().getName();
				return !currentLinkedProjectName.equals(linkedProjectName);
			} else {
				LinkedWithProjectFilter.linkedProjectName = editorInput.getAdapter(IProject.class).getName();
				return !currentLinkedProjectName.equals(linkedProjectName);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		LinkedWithProjectFilter.linkedProjectName = "";
		return !currentLinkedProjectName.equals(linkedProjectName);
	}

	private boolean hasVisibleChildren(TreeViewer treeViewer, GroupNode parent) {
		Object[] children = parent.getChildren();
		if (children == null || children.length == 0) {
			return false;
		}

		ViewerFilter[] filters = treeViewer.getFilters();
		for (Object child : children) {
			boolean isVisible = true;
			for (ViewerFilter filter : filters) {
				if (!filter.select(treeViewer, parent, child)) {
					isVisible = false;
					break;
				}
			}
			if (isVisible) {
				return true; // At least one visible child
			}
		}
		return false; // No visible children
	}
}
