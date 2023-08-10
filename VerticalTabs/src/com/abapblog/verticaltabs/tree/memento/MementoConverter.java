package com.abapblog.verticaltabs.tree.memento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IMemento;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class MementoConverter {
	private IMemento memento = new MementoReader().loadMemento();
	private ArrayList<IMemento> tabsMemento = new ArrayList<IMemento>();
	private ArrayList<IMemento> groupsMemento = new ArrayList<IMemento>();;
	private ArrayList<IMemento> projectsMemento = new ArrayList<IMemento>();;
	HashMap<IMemento, TabNode> mementoTabNodes = new HashMap<IMemento, TabNode>();
	HashMap<String, String> tabIDGroupIDMap = new HashMap<String, String>();
	HashMap<IProject, IMemento> projectMementoMap = new HashMap<IProject, IMemento>();
	HashMap<TabNode, IMemento> tabsMementoMap = new HashMap<TabNode, IMemento>();
	HashMap<String, GroupNode> GroupIDGroupNodeMap = new HashMap<String, GroupNode>();
	HashMap<String, IMemento> GroupIDGroupMementoMap = new HashMap<String, IMemento>();
	private TreeContentProvider contentProvider;

	public MementoConverter(TreeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		getNodesMemento();
		mapTabsWithGroup();
		getAndMapProjects();
		MapGroupIDMemento();
	}

	private void MapGroupIDMemento() {
		for (IMemento m : groupsMemento) {
			GroupIDGroupMementoMap.put(m.getID(), m);
		}

	}

	private void getNodesMemento() {
		try {
			IMemento mtcp = memento.getChild(MementoConstants.Types.TreeContentProvider);
			IMemento mtreeNodes = mtcp.getChild(MementoConstants.Types.TreeNodes);
			try {
				tabsMemento = new ArrayList<>(Arrays.asList(mtreeNodes.getChild(MementoConstants.Types.TabNodes)
						.getChildren(MementoConstants.Types.TabNode)));
			} catch (Exception e) {

			}
			try {
				groupsMemento = new ArrayList<>(Arrays.asList(mtreeNodes.getChild(MementoConstants.Types.GroupNodes)
						.getChildren(MementoConstants.Types.GroupNode)));
			} catch (Exception e) {

			}
			try {
				projectsMemento = new ArrayList<>(Arrays.asList(mtreeNodes.getChild(MementoConstants.Types.ProjectNodes)
						.getChildren(MementoConstants.Types.ProjectNode)));
			} catch (Exception e) {

			}
		} catch (Exception e) {

		}
	}

	private void mapTabsWithGroup() {
		for (IMemento gm : groupsMemento) {
			for (IMemento tab : gm.getChildren()) {
				tabIDGroupIDMap.put(tab.getID(), gm.getID());
			}
		}
	}

	private void getAndMapProjects() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			if (project.isOpen()) {
				for (IMemento pm : projectsMemento) {
					if (pm.getString(MementoConstants.Keys.NodeName).equals("Not linked")) {
						projectMementoMap.put(null, pm);
					}
					if (pm.getString(MementoConstants.Keys.NodeName).equals(project.getName())) {
						projectMementoMap.put(project, pm);
						continue;
					}
				}
			}
		}
	}

	public void updateProjectFromMemento(ProjectNode pn) {
		IMemento pm = projectMementoMap.get(pn.getProject());
		if (pm != null) {
			try {
				Integer sortIndex = pm.getInteger(MementoConstants.Keys.NodeSortIndex);
				pn.setSortIndex(sortIndex);
			} catch (Exception e) {

			}
			try {
				Boolean expanded = pm.getBoolean(MementoConstants.Keys.NodeExpanded);
				if (expanded)
					contentProvider.getExpandedProjects().add(pn);
			} catch (Exception e) {

			}

		}
	}

	public Boolean isProjectInMemento(IProject project) {
		return projectMementoMap.containsKey(project);

	}

	public Boolean isTabInMemento(TabNode tabNode) {
		Boolean found = false;
		IMemento foundMemento = null;
		for (IMemento tm : tabsMemento) {
			if (tm.getString(MementoConstants.Keys.NodeName).equals(tabNode.getTitle())
					&& tm.getString(MementoConstants.Keys.NodePath).equals(tabNode.getPath())
					&& tm.getString(MementoConstants.Keys.NodeProject).equals(tabNode.getProjectName())
					&& tm.getString(MementoConstants.Keys.NodeEditorId).equals(tabNode.getEditorReference().getId())) {
				tabsMementoMap.put(tabNode, tm);
				foundMemento = tm;
				found = true;
				break;
			}
		}
		if (found)
			tabsMemento.remove(foundMemento);
		return found;
	}

	public void updateTabFromMemento(TabNode tabNode) {
		IMemento tm = tabsMementoMap.get(tabNode);

		if (tm != null) {
			try {
				Integer sortIndex = tm.getInteger(MementoConstants.Keys.NodeSortIndex);
				tabNode.setSortIndex(sortIndex);
			} catch (Exception e) {

			}
			try {
				tabNode.setID(UUID.fromString(tm.getID()));
			} catch (Exception e) {

			}
			try {
				Boolean pinned = tm.getBoolean(MementoConstants.Keys.NodePinned);
				if (pinned != null && pinned == true)
					tabNode.pin();
			} catch (Exception e) {

			}

		}

	}

	public Boolean isTabInGroup(TabNode tabNode) {
		String groupID = tabIDGroupIDMap.get(tabNode.getID().toString());
		if (groupID != null && !groupID.isEmpty())
			return true;
		return false;
	}

	public void updateGroupNodeFromMemento(GroupNode groupNode, String tabId) {
		String groupID = tabIDGroupIDMap.get(tabId);
		if (groupID != null && !groupID.isEmpty()) {
			IMemento gm = GroupIDGroupMementoMap.get(groupID);
			if (gm != null) {
				GroupIDGroupNodeMap.put(groupID, groupNode);
				try {
					Integer sortIndex = gm.getInteger(MementoConstants.Keys.NodeSortIndex);
					groupNode.setSortIndex(sortIndex);
				} catch (Exception e) {
				}
				try {
					groupNode.setID(UUID.fromString(gm.getID()));
				} catch (Exception e) {
				}

				try {
					groupNode.setTitle(gm.getString(MementoConstants.Keys.NodeName));
				} catch (Exception e) {
				}
				try {
					Boolean expanded = gm.getBoolean(MementoConstants.Keys.NodeExpanded);
					if (expanded)
						contentProvider.getExpandedGroups().add(groupNode);
				} catch (Exception e) {

				}

			}
		}
	}

	public GroupNode getGroupNodeForTabNode(TabNode tabNode) {
		String groupID = tabIDGroupIDMap.get(tabNode.getID().toString());
		return GroupIDGroupNodeMap.get(groupID);
	}

}
