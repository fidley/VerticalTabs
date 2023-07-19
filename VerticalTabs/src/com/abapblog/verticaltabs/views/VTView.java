package com.abapblog.verticaltabs.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.TreeMouseHandler;
import com.abapblog.verticaltabs.tree.TreePatternFilter;
import com.abapblog.verticaltabs.tree.VTFilteredTree;
import com.abapblog.verticaltabs.tree.labelproviders.TreeCloseCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreePinCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeProjectCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeTabCellLabelProvider;

public class VTView extends ViewPart implements ILinkedWithEditorView {
	private Composite parent;
	private static VTFilteredTree filteredTree;
	public static Sorter sorter;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		filteredTree = new VTFilteredTree(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL,
				new TreePatternFilter(), true, true);
		TreeViewer viewer = filteredTree.getViewer();
		TreeContentProvider contentProvider = new TreeContentProvider(filteredTree.getViewer());
		contentProvider.initialize();
		viewer.setContentProvider(contentProvider);
		setTreeProperties(viewer.getTree());
		createColumns(viewer);
		createGridData(viewer);
		viewer.expandToLevel(2);
		TreeMouseHandler treeMouseHandler = new TreeMouseHandler();
		viewer.getTree().addMouseListener(treeMouseHandler);
//		viewer.getTree().addMouseMoveListener(treeMouseHandler);
//		contentProvider.autoResizeColumns();
		sorter = new Sorter();
		viewer.setComparator(sorter);
		new TreeDragAndDrop(viewer);
	}

	public static TreeViewer getTreeViewer() {
		return filteredTree.getViewer();
	}

	@Override
	public void setFocus() {
		this.parent.setFocus();

	}

	private void setTreeProperties(Tree tree) {
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);
	}

	private void createColumns(TreeViewer viewer) {
		createColumnName(Columns.fromInteger(0), viewer);
		createColumnName(Columns.fromInteger(1), viewer);
		createColumnName(Columns.fromInteger(2), viewer);
		createColumnName(Columns.fromInteger(3), viewer);
	}

	private void createColumnName(Columns column, TreeViewer viewer) {
		switch (column) {
		case TAB:
			createTABColumn(viewer);
			break;
		case PIN:
			createPINColumn(viewer);
			break;
		case CLOSE:
			createCLOSEColumn(viewer);
			break;
		case PROJECT:
			createProjectColumn(viewer);
			break;
		}
	}

	private void createCLOSEColumn(TreeViewer viewer) {
		TreeViewerColumn closeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		closeColumn.getColumn().setWidth(40);
		closeColumn.getColumn().setResizable(false);
		closeColumn.getColumn().setText("");
		closeColumn.setLabelProvider(new TreeCloseCellLabelProvider());
	}

	private void createProjectColumn(TreeViewer viewer) {
		TreeViewerColumn projectColumn = new TreeViewerColumn(viewer, SWT.NONE);
		projectColumn.getColumn().setWidth(300);
		projectColumn.getColumn().setResizable(true);
		projectColumn.getColumn().setText("");
		projectColumn.setLabelProvider(new TreeProjectCellLabelProvider());
	}

	private void createTABColumn(TreeViewer viewer) {
		TreeViewerColumn tabColumn = new TreeViewerColumn(viewer, SWT.NONE);
		tabColumn.getColumn().setWidth(300);
		tabColumn.getColumn().setText("Tab");
		tabColumn.getColumn().setResizable(true);
		tabColumn.setLabelProvider(new TreeTabCellLabelProvider());
		ColumnViewerToolTipSupport.enableFor(viewer);
	}

	private void createPINColumn(TreeViewer viewer) {
		TreeViewerColumn pinColumn = new TreeViewerColumn(viewer, SWT.NONE);
		pinColumn.getColumn().setWidth(40);
		pinColumn.getColumn().setText("");
		pinColumn.getColumn().setResizable(false);
		pinColumn.setLabelProvider(new TreePinCellLabelProvider());
	}

	private void createGridData(TreeViewer viewer) {
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		viewer.getControl().setLayoutData(data);
		try {
			viewer.setInput(getViewSite());
		} catch (Exception e) {

		}
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		IProject project = activeEditor.getEditorInput().getAdapter(IProject.class);
		if (project != null) {

		}

	}

}
