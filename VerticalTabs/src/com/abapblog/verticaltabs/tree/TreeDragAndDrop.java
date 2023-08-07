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

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodeType;
import com.abapblog.verticaltabs.tree.nodes.NodesFactory;

public class TreeDragAndDrop implements DragSourceListener, DropTargetListener {

	protected IStructuredSelection dndSourceSelection;
	private final TreeViewer treeViewer;

	public TreeDragAndDrop(TreeViewer treeViewer) {
		final Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		final int operations = DND.DROP_MOVE;
		this.treeViewer = treeViewer;
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
		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
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
		if (event.detail == DND.DROP_MOVE) {
			final TreeItem targetItem = (TreeItem) event.item;
			try {
				ITreeNode targetNode = (ITreeNode) targetItem.getData();
				ITreeNode sourceNode = (ITreeNode) dndSourceSelection.getFirstElement();
				if (targetNode.getNodeType().equals(sourceNode.getNodeType()))
					manualSort(targetNode, sourceNode);
			} catch (Exception e) {
			}
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
		TreeContentProvider tcp = (TreeContentProvider) treeViewer.getContentProvider();
		NodesFactory nodesFactory = tcp.getNodesFactory();
		switch (nodeType) {
		case PROJECT:
			return nodesFactory.getProjectNodes().values().toArray(new ITreeNode[0]);
		case TAB:
			return nodesFactory.getTabNodes().values().toArray(new ITreeNode[0]);
		case GROOT:
			break;
		case GROUP:
			break;
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