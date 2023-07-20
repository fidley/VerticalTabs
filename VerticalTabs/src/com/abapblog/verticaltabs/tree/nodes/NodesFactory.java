package com.abapblog.verticaltabs.tree.nodes;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class NodesFactory {
	// private static List<TabNode> tabNodes = new ArrayList<>();
	HashMap<IEditorReference, TabNode> tabNodes = new HashMap<IEditorReference, TabNode>();
	HashMap<IProject, ProjectNode> projectNodes = new HashMap<IProject, ProjectNode>();
	private TreeContentProvider contentProvider;

	public NodesFactory(TreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void removeTabNode(IEditorReference editorReference) throws PartInitException {
		if (getTabNodes().containsKey(editorReference)) {
			TabNode tabNode = getTabNodes().get(editorReference);
			ProjectNode projectNode = getProjectNodes().get(tabNode.getProject());
			projectNode.removeChild(tabNode);
			if (!projectNode.hasChildren()) {
				contentProvider.getProjectsRoot().removeChild(projectNode);
			}
		}
	}

	public TabNode getTabNode(IEditorReference editorReference) throws PartInitException {
		if (getTabNodes().containsKey(editorReference))
			return getTabNodes().get(editorReference);

		TabNode tabNode = createTabNode(editorReference);
		return tabNode;
	}

	private TabNode createTabNode(IEditorReference editorReference) throws PartInitException {
		TabNode tabNode = new TabNode(editorReference);
		getTabNodes().put(editorReference, tabNode);
		getProjectNode(tabNode.getProject()).addChild(tabNode);
		return tabNode;
	}

	public ProjectNode getProjectNode(IProject project) {
		if (getProjectNodes().containsKey(project))
			return getProjectNodes().get(project);

		ProjectNode projectNode = createProjectNode(project);
		return projectNode;

	}

	private ProjectNode createProjectNode(IProject project) {
		ProjectNode projectNode;
		try {
			projectNode = new ProjectNode(project);
		} catch (Exception e) {
			projectNode = new ProjectNode();
		}
		getProjectNodes().put(project, projectNode);
		contentProvider.getProjectsRoot().addChild(projectNode);
		return projectNode;
	}

	public HashMap<IEditorReference, TabNode> getTabNodes() {
		return tabNodes;
	}

	public HashMap<IProject, ProjectNode> getProjectNodes() {
		return projectNodes;
	}

}
