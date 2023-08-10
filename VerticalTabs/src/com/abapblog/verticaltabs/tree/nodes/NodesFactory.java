package com.abapblog.verticaltabs.tree.nodes;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

import com.abapblog.verticaltabs.icons.Icons;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.memento.MementoConverter;
import com.abapblog.verticaltabs.tree.memento.MementoWriter;

public class NodesFactory {

	HashMap<IEditorReference, TabNode> tabNodes = new HashMap<IEditorReference, TabNode>();
	HashMap<IProject, ProjectNode> projectNodes = new HashMap<IProject, ProjectNode>();
	HashMap<TabNode, GroupNode> groupNodes = new HashMap<TabNode, GroupNode>();
	private TreeContentProvider contentProvider;
	private MementoConverter memento;

	public NodesFactory(TreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		memento = new MementoConverter(contentProvider);
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
				getGroupNodes().remove(tabNode);
				if (!groupNode.hasChildren()) {
					contentProvider.getManualRoot().removeChild(groupNode);
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
		updateTabNodeFromMemento(tabNode);
		getTabNodes().put(editorReference, tabNode);
		ProjectNode projectNode = getProjectNode(tabNode.getProject());
		projectNode.addChild(tabNode);
		if (!contentProvider.getProjectsRoot().contains(projectNode)) {
			contentProvider.getProjectsRoot().addChild(projectNode);
		}
		return tabNode;
	}

	private void updateTabNodeFromMemento(TabNode tabNode) {
		GroupNode groupNode;
		if (memento.isTabInMemento(tabNode)) {
			memento.updateTabFromMemento(tabNode);
			if (memento.isTabInGroup(tabNode)) {
				groupNode = memento.getGroupNodeForTabNode(tabNode);
				if (groupNode == null) {
					groupNode = createGroupNode(tabNode);
					memento.updateGroupNodeFromMemento(groupNode, tabNode.getID().toString());
				} else {
					moveTabNodeToGroup(tabNode, groupNode);
				}

			}
		}

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
		if (memento.isProjectInMemento(project)) {
			memento.updateProjectFromMemento(projectNode);
		} else {
			contentProvider.getExpandedProjects().add(projectNode);
		}
		getProjectNodes().put(project, projectNode);
		contentProvider.getProjectsRoot().addChild(projectNode);

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

	public GroupNode createGroupNode(String name, Integer sortIndex, String id) {
		GroupNode groupNode = new GroupNode(name, Icons.getIcon(Icons.ICON_FOLDER_OPEN));
		contentProvider.getManualRoot().addChild(groupNode);
		groupNode.setID(UUID.fromString(id));
		groupNode.setSortIndex(sortIndex);
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

	public void dispose() {
		try {
			new MementoWriter(this, contentProvider).saveState();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
