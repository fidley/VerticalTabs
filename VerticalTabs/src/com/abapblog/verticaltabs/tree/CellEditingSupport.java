package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;

import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;

public class CellEditingSupport extends EditingSupport {
	private static ITreeNode nodeSelectedForEditing = null;
	private TreeViewer viewer;

	public CellEditingSupport(TreeViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	public CellEditor getCellEditor() {
		return getCellEditor(null);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(viewer.getTree());
	}

	@Override
	protected boolean canEdit(Object element) {
		return (element.equals(nodeSelectedForEditing));
	}

	@Override
	protected Object getValue(Object element) {
		return ((ITreeNode) element).getTitle();
	}

	@Override
	protected void setValue(Object element, Object value) {

		if (element instanceof TabNode)
			((TabNode) element).setManualTitle((String) value);
		else if (element instanceof GroupNode)
			((GroupNode) element).setTitle((String) value);
		else {
			return;
		}
		setNodeSelectedForEditing(null);
		TreeContentProvider.refreshTree();
	}

	public static void setSelectedNodeNameEditable(ITreeNode node) {
		if (node != null) {
			nodeSelectedForEditing = node;
		}
		setNodeSelectedForEditing(node);
		VTView.getTreeViewer().editElement(node, Columns.getInteger(Columns.NAME));
		clearNodeSelectedForEditing();

	}

	/**
	 * Set which node is currently selected for editing. It is used to allow editing
	 * only after the F2 key is pressed or when proper menu item is selected.
	 */
	private static void setNodeSelectedForEditing(ITreeNode nodeSelectedForEditing) {
		CellEditingSupport.nodeSelectedForEditing = nodeSelectedForEditing;
	}

	/**
	 * In case user press Escape key, we need to clear the nodeSelectedForEditing so
	 * the cell will not become editable at next selection
	 */
	private static void clearNodeSelectedForEditing() {
		nodeSelectedForEditing = null;

	}

}
