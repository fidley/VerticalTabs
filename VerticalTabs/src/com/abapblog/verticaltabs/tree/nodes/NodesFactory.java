package com.abapblog.verticaltabs.tree.nodes;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

import com.abapblog.verticaltabs.icons.Icons;
import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class NodesFactory {

	HashMap<IEditorReference, TabNode> tabNodes = new HashMap<IEditorReference, TabNode>();
	HashMap<IProject, ProjectNode> projectNodes = new HashMap<IProject, ProjectNode>();
	HashMap<TabNode, GroupNode> groupNodes = new HashMap<TabNode, GroupNode>();
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
			GroupNode groupNode = getGroupNodes().get(tabNode);
			if (groupNode != null) {
				groupNode.removeChild(tabNode);
				if (!groupNode.hasChildren()) {
					contentProvider.getManualRoot().removeChild(groupNode);
					getGroupNodes().remove(tabNode);
				}
			}
			if (tabNode.getParent() != null)
				tabNode.getParent().removeChild(tabNode);
			contentProvider.getManualRoot().removeChild(tabNode);
		}
	}

	public void moveTabNodeToGroup(TabNode tabNode, GroupNode groupNode) {
		ITreeNode oldParent = tabNode.getParent();
		if (oldParent != null)
			oldParent.removeChild(tabNode);

		if (oldParent instanceof GroupNode) {
			getGroupNodes().remove(tabNode);
			if (!oldParent.hasChildren()) {
				contentProvider.getManualRoot().removeChild(oldParent);
			}
		}
		getGroupNodes().put(tabNode, groupNode);
		groupNode.addChild(tabNode);
	}

	public void moveTabNodeFromGroupToRoot(ITreeNode tabNode) {
		GroupNode groupNode = getGroupNodes().get(tabNode);
		if (groupNode == null)
			return;
		getGroupNodes().remove(tabNode);
		groupNode.removeChild(tabNode);
		contentProvider.getManualRoot().addChild(tabNode);
		if (!groupNode.hasChildren()) {
			contentProvider.getManualRoot().removeChild(groupNode);
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
		ProjectNode projectNode = getProjectNode(tabNode.getProject());
		projectNode.addChild(tabNode);
		if (!contentProvider.getProjectsRoot().contains(projectNode)) {
			contentProvider.getProjectsRoot().addChild(projectNode);
			if (!contentProvider.getExpandedProjects().contains(projectNode))
				contentProvider.getExpandedProjects().add(projectNode);
		}
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
		contentProvider.getExpandedProjects().add(projectNode);
		return projectNode;
	}

	public GroupNode createGroupNode(TabNode tabNode) {
		GroupNode groupNode = new GroupNode(GroupNode.getNextGroupName(), Icons.getIcon(Icons.ICON_FOLDER_OPEN));
		contentProvider.getManualRoot().addChild(groupNode);
		contentProvider.getManualRoot().removeChild(tabNode);
		groupNode.addChild(tabNode);

		getGroupNodes().put(tabNode, groupNode);
		return groupNode;

	}

	public HashMap<IEditorReference, TabNode> getTabNodes() {
		return tabNodes;
	}

	public HashMap<IProject, ProjectNode> getProjectNodes() {
		return projectNodes;
	}

	public HashMap<TabNode, GroupNode> getGroupNodes() {
		return groupNodes;
	}

}
