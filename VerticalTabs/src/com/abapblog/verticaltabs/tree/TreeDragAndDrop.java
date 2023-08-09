package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeItem;

import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodeType;
import com.abapblog.verticaltabs.tree.nodes.NodesFactory;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreeDragAndDrop implements DragSourceListener, DropTargetListener {

	protected IStructuredSelection dndSourceSelection;
	private final TreeViewer treeViewer;
	private TreeContentProvider contentProvider;

	public TreeDragAndDrop(TreeViewer treeViewer) {
		final Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		final int operations = DND.DROP_MOVE | DND.DROP_COPY;
		this.treeViewer = treeViewer;
		contentProvider = (TreeContentProvider) treeViewer.getContentProvider();
		treeViewer.addDragSupport(operations, types, this);
		treeViewer.addDropSupport(operations, types, this);
	}

	@Override
	public void dragEnter(final DropTargetEvent event) {
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}

	@Override
	public void dragOver(final DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT | DND.FEEDBACK_EXPAND;
	}

	@Override
	public void dragOperationChanged(final DropTargetEvent event) {

	}

	@Override
	public void dragLeave(final DropTargetEvent event) {

	}

	@Override
	public void dropAccept(final DropTargetEvent event) {
	}

	@Override
	public void drop(final DropTargetEvent event) {
		switch (event.detail) {
		case DND.DROP_MOVE:
			resortNodes(event);
			break;
		case DND.DROP_COPY:
			groupNodes(event);
			break;
		default:
			break;
		}
	}

	private void groupNodes(DropTargetEvent event) {
		GroupNode groupNode = null;
		NodesFactory nodesFactory = contentProvider.getNodesFactory();
		if (!contentProvider.getInvisibleRoot().equals(contentProvider.getManualRoot())) {
			return;
		}
		final TreeItem targetItem = (TreeItem) event.item;
		try {
			ITreeNode targetNode = (ITreeNode) targetItem.getData();
			for (Object selectedNode : dndSourceSelection) {
				ITreeNode sourceNode = (ITreeNode) selectedNode;
				if (targetNode.equals(selectedNode))
					continue;
				if (targetNode.getNodeType().equals(sourceNode.getNodeType())
						&& targetNode.getNodeType().equals(NodeType.TAB)
						&& !(targetNode.getParent() instanceof GroupNode)) {

					if (groupNode == null) {
						groupNode = nodesFactory.createGroupNode((TabNode) targetNode);
					}
					nodesFactory.moveTabNodeToGroup((TabNode) sourceNode, groupNode);
				}
				if (targetNode.getNodeType().equals(NodeType.GROUP) && sourceNode.getNodeType().equals(NodeType.TAB)) {
					nodesFactory.moveTabNodeToGroup((TabNode) sourceNode, (GroupNode) targetNode);
				}
				if (targetNode.getNodeType().equals(sourceNode.getNodeType())
						&& targetNode.getNodeType().equals(NodeType.TAB)
						&& (targetNode.getParent() instanceof GroupNode)) {
					nodesFactory.moveTabNodeToGroup((TabNode) sourceNode, (GroupNode) targetNode.getParent());
				}

			}
			TreeContentProvider.refreshTree();
		} catch (Exception e) {
			for (Object selectedNode : dndSourceSelection) {
				ITreeNode sourceNode = (ITreeNode) selectedNode;
				if (sourceNode.getNodeType().equals(NodeType.TAB)) {
					nodesFactory.moveTabNodeFromGroupToRoot(sourceNode);
				}

			}
			TreeContentProvider.refreshTree();
		}
	}

	private void resortNodes(final DropTargetEvent event) {
		final TreeItem targetItem = (TreeItem) event.item;
		try {
			ITreeNode targetNode = (ITreeNode) targetItem.getData();
			for (Object selectedNode : dndSourceSelection) {
				ITreeNode sourceNode = (ITreeNode) selectedNode;
				if (targetNode.getNodeType().equals(sourceNode.getNodeType())) {
					if (contentProvider.getInvisibleRoot().equals(contentProvider.getProjectsRoot())
							&& targetNode.getProjectName().equals(sourceNode.getProjectName())) {
						manualSort(targetNode, sourceNode);
					} else if (contentProvider.getInvisibleRoot().equals(contentProvider.getManualRoot())) {
						manualSort(targetNode, sourceNode);
					}
				} else if (contentProvider.getInvisibleRoot().equals(contentProvider.getManualRoot())) {
					manualSort(targetNode, sourceNode);
				}
			}
		} catch (Exception e) {
		}
	}

	private void manualSort(ITreeNode targetNode, ITreeNode sourceNode) {
		Integer targetIndex = targetNode.getSortIndex();
		for (ITreeNode tn : getNodesColection(targetNode.getNodeType())) {
			if (!tn.equals(sourceNode)) {
				if (tn.getSortIndex() >= targetIndex) {
					tn.setSortIndex(tn.getSortIndex() + 1);
				}
			} else {
				tn.setSortIndex(targetIndex);
			}
		}
		TreeContentProvider.refreshTree();
	}

	private ITreeNode[] getNodesColection(NodeType nodeType) {
		NodesFactory nodesFactory = contentProvider.getNodesFactory();
		switch (nodeType) {
		case PROJECT:
			return nodesFactory.getProjectNodes().values().toArray(new ITreeNode[0]);
		case TAB:
			return nodesFactory.getTabNodes().values().toArray(new ITreeNode[0]);
		case GROOT:
			break;
		case GROUP:
			return nodesFactory.getGroupNodes().values().toArray(new ITreeNode[0]);
		default:
			break;
		}
		return null;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		final TreeItem[] selection = treeViewer.getTree().getSelection();
		if (selection.length > 0) {
			event.doit = true;
		} else {
			event.doit = false;
		}

	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		dndSourceSelection = (IStructuredSelection) treeViewer.getSelection();
		event.data = "Data";

	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// TODO Auto-generated method stub

	}
}