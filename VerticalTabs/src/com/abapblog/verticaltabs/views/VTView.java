package com.abapblog.verticaltabs.views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.handlers.SortCommand;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.ColumnControlListener;
import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.tree.Sorter;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.TreeDragAndDrop;
import com.abapblog.verticaltabs.tree.TreeMouseHandler;
import com.abapblog.verticaltabs.tree.TreePatternFilter;
import com.abapblog.verticaltabs.tree.TreeSorting;
import com.abapblog.verticaltabs.tree.VTFilteredTree;
import com.abapblog.verticaltabs.tree.labelproviders.TreeCloseCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeNameCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreePinCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeProjectCellLabelProvider;

public class VTView extends ViewPart {
	private IMemento memento;
	private Composite parent;
	private static VTFilteredTree filteredTree;
	public static Sorter sorter;
	private final static IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final ColumnControlListener columnListener = new ColumnControlListener();

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createTreeViewer(parent);
		IContextService contextService = getSite().getService(IContextService.class);
		contextService.activateContext("com.abapblog.verticaltabs.view.context");
	}

	private void createTreeViewer(Composite parent) {
		filteredTree = new VTFilteredTree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL,
				new TreePatternFilter(), true, true);
		TreeViewer viewer = filteredTree.getViewer();
		TreeContentProvider contentProvider = TreeContentProvider.getTreeContentProvider(filteredTree.getViewer());
		viewer.setContentProvider(contentProvider);
		setTreeProperties(viewer.getTree());
		createColumns(viewer);
		createGridData(viewer);
		createSorter(viewer);
		new TreeDragAndDrop(viewer);
		createMenuManager(viewer);

		contentProvider.setExpandedElementsForTreeViewer();
		contentProvider.setInitialRootNode();
	}

	private void createMenuManager(TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		getSite().setSelectionProvider(viewer);
	}

	private void createSorter(TreeViewer viewer) {
		sorter = new Sorter();
		viewer.setComparator(sorter);
		TreeSorting treeSorter = SortCommand.getSorterFromPreference();
		sorter.setSorting(treeSorter);
		markSortRadiobutton(treeSorter);
	}

	private void markSortRadiobutton(TreeSorting treeSorter) {

		SortCommand.setSelectedStatus(treeSorter);

	}

	public static TreeViewer getTreeViewer() {
		return filteredTree.getViewer();
	}

	@Override
	public void setFocus() {

		filteredTree.getViewer().getTree().setFocus();
//		this.parent.setFocus();

	}

	private void setTreeProperties(Tree tree) {
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		TreeMouseHandler treeMouseHandler = new TreeMouseHandler();
		tree.addMouseListener(treeMouseHandler);
//		tree.addListener(SWT.Expand, new TreeExpandListener());
	}

	private void createColumns(TreeViewer viewer) {

		createColumnName(Columns.fromInteger(0), viewer);
		createColumnName(Columns.fromInteger(1), viewer);
		createColumnName(Columns.fromInteger(2), viewer);
		createColumnName(Columns.fromInteger(3), viewer);
	}

	private void createColumnName(Columns column, TreeViewer viewer) {
		switch (column) {
		case NAME:
			createNAMEColumn(viewer);
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

	private static int getColumnWidth(Columns column) {
		switch (column) {
		case CLOSE:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_CLOSE);
		case PIN:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_PIN);
		case NAME:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_NAME);
		case PROJECT:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_PROJECT);
		}
		return 40;
	}

	private void createCLOSEColumn(TreeViewer viewer) {
		TreeViewerColumn closeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		closeColumn.getColumn().setWidth(getColumnWidth(Columns.CLOSE));
		closeColumn.getColumn().setResizable(false);
		closeColumn.getColumn().setText("Close");
		closeColumn.setLabelProvider(new TreeCloseCellLabelProvider());
		closeColumn.getColumn().addControlListener(columnListener);
	}

	private void createProjectColumn(TreeViewer viewer) {
		TreeViewerColumn projectColumn = new TreeViewerColumn(viewer, SWT.NONE);
		if (isRootNodeManualNode(viewer)) {
			projectColumn.getColumn().setWidth(getColumnWidth(Columns.PROJECT));
		} else {
			projectColumn.getColumn().setWidth(0);
		}
		projectColumn.getColumn().setResizable(true);
		projectColumn.getColumn().setText("Project");
		projectColumn.setLabelProvider(new TreeProjectCellLabelProvider());
		projectColumn.getColumn().addControlListener(columnListener);
	}

	private boolean isRootNodeManualNode(TreeViewer viewer) {
		return ((TreeContentProvider) viewer.getContentProvider()).getInvisibleRoot()
				.equals(((TreeContentProvider) viewer.getContentProvider()).getManualRoot());
	}

	private void createNAMEColumn(TreeViewer viewer) {
		TreeViewerColumn tabColumn = new TreeViewerColumn(viewer, SWT.NONE);
		tabColumn.getColumn().setWidth(getColumnWidth(Columns.NAME));
		tabColumn.getColumn().setText("Name");
		tabColumn.getColumn().setResizable(true);
		tabColumn.setLabelProvider(new TreeNameCellLabelProvider());
		ColumnViewerToolTipSupport.enableFor(viewer);
		tabColumn.getColumn().addControlListener(columnListener);
	}

	private void createPINColumn(TreeViewer viewer) {
		TreeViewerColumn pinColumn = new TreeViewerColumn(viewer, SWT.NONE);
		pinColumn.getColumn().setWidth(getColumnWidth(Columns.PIN));
		pinColumn.getColumn().setText("Pin");
		pinColumn.getColumn().setResizable(true);
		pinColumn.setLabelProvider(new TreePinCellLabelProvider());
		pinColumn.getColumn().addControlListener(columnListener);
	}

	private void createGridData(TreeViewer viewer) {
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		viewer.getControl().setLayoutData(data);
		try {
			viewer.setInput(getViewSite());
		} catch (Exception e) {

		}
	}

	public static void hideProjectColumn() {
		TreeColumn projectColumn = filteredTree.getViewer().getTree().getColumn(Columns.getInteger(Columns.PROJECT));
		projectColumn.setWidth(0);
	}

	public static void showProjectColumn() {
		TreeColumn projectColumn = filteredTree.getViewer().getTree().getColumn(Columns.getInteger(Columns.PROJECT));
		projectColumn.setWidth(getColumnWidth(Columns.PROJECT));
	}

	@Override
	public void dispose() {
		filteredTree = null;
		sorter = null;
		super.dispose();
	}

}
