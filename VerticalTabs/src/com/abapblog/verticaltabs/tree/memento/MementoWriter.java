package com.abapblog.verticaltabs.tree.memento;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodesFactory;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class MementoWriter extends MementoOperations {
	private NodesFactory nodesFactory;
	private TreeContentProvider contentProvider;

	public MementoWriter(NodesFactory nodesFactory, TreeContentProvider contentProvider) {
		this.nodesFactory = nodesFactory;
		this.contentProvider = contentProvider;
	}

	private void saveMementoToFile(XMLMemento memento) throws FileNotFoundException, IOException {
		FileOutputStream stream = new FileOutputStream(getMementoFile());
		OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
		memento.save(writer);
		writer.close();

	}

	private void saveGroupNodes(IMemento treeNodesMememento) {
		IMemento groupNodesMememento = treeNodesMememento.createChild(MementoConstants.Types.GroupNodes,
				MementoConstants.Ids.GroupNodes);
		List<GroupNode> sortedList = new ArrayList<GroupNode>();

		for (ITreeNode gn : contentProvider.getManualRoot().getChildren()) {
			if (gn instanceof GroupNode) {
				sortedList.add((GroupNode) gn);
			}
		}
		Collections.sort(sortedList);
		int i = 0;
		for (ITreeNode gn : sortedList) {
			IMemento groupNodeMememento = groupNodesMememento.createChild(MementoConstants.Types.GroupNode,
					gn.getID().toString());
			groupNodeMememento.putString(MementoConstants.Keys.NodeName, gn.getTitle());
			groupNodeMememento.putInteger(MementoConstants.Keys.NodeSortIndex, i);
			groupNodeMememento.putBoolean(MementoConstants.Keys.NodeExpanded,
					contentProvider.getExpandedGroups().contains(gn));
			for (ITreeNode tn : gn.getChildren()) {
				IMemento groupNodeChildMememento = groupNodeMememento.createChild(MementoConstants.Types.GroupNodeChild,
						tn.getID().toString());

			}
			i++;
		}

	}

	private void saveTabNodes(IMemento treeNodesMememento) {
		IMemento tabNodesMememento = treeNodesMememento.createChild(MementoConstants.Types.TabNodes,
				MementoConstants.Ids.TabNodes);
		Collection<TabNode> tnMementos = nodesFactory.getTabNodes().values();
		List<TabNode> sortedList = new ArrayList<TabNode>(tnMementos);
		Collections.sort(sortedList);
		int i = 0;
		for (TabNode tn : sortedList) {
			IMemento tabNodeMememento = tabNodesMememento.createChild(MementoConstants.Types.TabNode,
					tn.getID().toString());
			tabNodeMememento.putString(MementoConstants.Keys.NodeName, tn.getOriginalTitle());
			tabNodeMememento.putString(MementoConstants.Keys.NodeManualName, tn.getManualTitle());
			tabNodeMememento.putString(MementoConstants.Keys.NodeEditorId, tn.getEditorReference().getId());
			tabNodeMememento.putString(MementoConstants.Keys.NodeProject, tn.getProjectName());
			tabNodeMememento.putString(MementoConstants.Keys.NodePath, tn.getPath());
			tabNodeMememento.putInteger(MementoConstants.Keys.NodeSortIndex, i);
			tabNodeMememento.putBoolean(MementoConstants.Keys.NodeExpanded, false);
			tabNodeMememento.putBoolean(MementoConstants.Keys.NodePinned, tn.isPinned());
			i++;
		}
	}

	private void saveProjectNodes(IMemento treeNodesMememento) {
		IMemento projectNodesMememento = treeNodesMememento.createChild(MementoConstants.Types.ProjectNodes,
				MementoConstants.Ids.ProjectNodes);

		Collection<ProjectNode> pnMementos = nodesFactory.getProjectNodes().values();
		List<ProjectNode> sortedList = new ArrayList<ProjectNode>(pnMementos);
		Collections.sort(sortedList);
		int i = 0;
		for (ProjectNode pn : sortedList) {
			IMemento projectNodeMememento = projectNodesMememento.createChild(MementoConstants.Types.ProjectNode,
					pn.getID().toString());
			projectNodeMememento.putString(MementoConstants.Keys.NodeName, pn.getTitle());
			projectNodeMememento.putString(MementoConstants.Keys.NodeProject, pn.getProjectName());
			projectNodeMememento.putString(MementoConstants.Keys.NodePath, pn.getPath());
			projectNodeMememento.putInteger(MementoConstants.Keys.NodeSortIndex, i);
			projectNodeMememento.putBoolean(MementoConstants.Keys.NodeExpanded,
					contentProvider.getExpandedProjects().contains(pn));
			i++;
		}
	}

	public void saveState() throws IOException {

		XMLMemento memento = XMLMemento.createWriteRoot(IWorkbenchConstants.TAG_VIEW_STATE);
		IMemento contentProviderMemento = memento.createChild(MementoConstants.Types.TreeContentProvider,
				MementoConstants.Ids.TreeContentProvider);
		IMemento treeNodesMememento = contentProviderMemento.createChild(MementoConstants.Types.TreeNodes,
				MementoConstants.Ids.TreeNodes);
		saveTabNodes(treeNodesMememento);
		saveProjectNodes(treeNodesMememento);
		saveGroupNodes(treeNodesMememento);

		saveMementoToFile(memento);

	}

}
