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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.handlers.OnlyDirtyEditorsHandler;
import com.abapblog.verticaltabs.handlers.SortCommand;
import com.abapblog.verticaltabs.icons.Icons;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.ColumnControlListener;
import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.tree.FolderDoubleClickListener;
import com.abapblog.verticaltabs.tree.Sorter;
import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.abapblog.verticaltabs.tree.TreeDragAndDrop;
import com.abapblog.verticaltabs.tree.TreeKeyListener;
import com.abapblog.verticaltabs.tree.TreeMouseHandler;
import com.abapblog.verticaltabs.tree.TreeSorting;
import com.abapblog.verticaltabs.tree.VTFilteredTree;
import com.abapblog.verticaltabs.tree.filters.LinkedWithProjectFilter;
import com.abapblog.verticaltabs.tree.filters.TreePatternFilter;
import com.abapblog.verticaltabs.tree.labelproviders.TreeCloseCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeNameCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreePathCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreePinCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeProjectCellLabelProvider;

public class VTView extends ViewPart {
	private static VTFilteredTree filteredTree;
	public static final Sorter sorter = new Sorter();
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final ColumnControlListener columnListener = new ColumnControlListener();
	public static final String ID = "com.abapblog.verticaltabs.view";

	@Override
	public void createPartControl(Composite parent) {
		new Icons();
		createTreeViewer(parent);
		IContextService contextService = getSite().getService(IContextService.class);
		contextService.activateContext("com.abapblog.verticaltabs.view.context");
	}

	private void createTreeViewer(Composite parent) {
		createFilteredTreeViewer(parent);
		TreeViewer viewer = filteredTree.getViewer();
		TreeContentProvider contentProvider = TreeContentProvider.getTreeContentProvider(filteredTree.getViewer());
		viewer.setContentProvider(contentProvider);
		setTreeProperties(viewer.getTree());
		createColumns(viewer);
		changeColumnsVisibility();
		createGridData(viewer);
		createSorter(viewer);
		new TreeDragAndDrop(viewer);
		createMenuManager(viewer);
		addDoubleClick(viewer);
		addLinkedWithProjectFilter(viewer);

		contentProvider.setExpandedElementsForTreeViewer();
		contentProvider.setInitialRootNode();

	}

	private void addLinkedWithProjectFilter(TreeViewer viewer) {
		viewer.addFilter(new LinkedWithProjectFilter());

	}

	private void addDoubleClick(TreeViewer viewer) {
		viewer.addDoubleClickListener(new FolderDoubleClickListener());

	}

	private static void createFilteredTreeViewer(Composite parent) {
		if (filteredTree == null) {
			TreePatternFilter filter = new TreePatternFilter();
			filteredTree = new VTFilteredTree(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL,
					filter, true, true);
			addFilterListeners(filter);
		}
	}

	private static void addFilterListeners(TreePatternFilter filter) {
		Text filterText = filteredTree.getFilterControl();
		filterText.addModifyListener(filter);
		filterText.addFocusListener(filter);
		filterText.addMouseListener(filter);
		OnlyDirtyEditorsHandler.setDefaultToggleStatus();
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

		viewer.setComparator(sorter);
		TreeSorting treeSorter = SortCommand.getSorterFromPreference();
		sorter.setSorting(treeSorter);
		markSortRadiobutton(treeSorter);
		SortCommand.sort(treeSorter);
	}

	private void markSortRadiobutton(TreeSorting treeSorter) {

		SortCommand.setSelectedStatus(treeSorter);

	}

	public static TreeViewer getTreeViewer() {
		return filteredTree.getViewer();
	}

	public static VTFilteredTree getFilteredTree() {
		return filteredTree;
	}

	@Override
	public void setFocus() {

		filteredTree.getViewer().getTree().setFocus();
	}

	private void setTreeProperties(Tree tree) {
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		TreeMouseHandler treeMouseHandler = new TreeMouseHandler();
		tree.addMouseListener(treeMouseHandler);
		tree.addKeyListener(new TreeKeyListener());
	}

	private void createColumns(TreeViewer viewer) {

		createColumnName(Columns.getBySequence(0), viewer);
		createColumnName(Columns.getBySequence(1), viewer);
		createColumnName(Columns.getBySequence(2), viewer);
		createColumnName(Columns.getBySequence(3), viewer);
		createColumnName(Columns.getBySequence(4), viewer);
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
		case PATH:
			createPathColumn(viewer);
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
		case PATH:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_PATH);
		}
		return 40;
	}

	private void createCLOSEColumn(TreeViewer viewer) {
		TreeViewerColumn closeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		closeColumn.getColumn().setWidth(getColumnWidth(Columns.CLOSE));
		closeColumn.getColumn().setResizable(false);
		closeColumn.getColumn().setText(Columns.CLOSE.getColumnHeaderText());
		closeColumn.setLabelProvider(new TreeCloseCellLabelProvider());
		closeColumn.getColumn().addControlListener(columnListener);
	}

	private void createPathColumn(TreeViewer viewer) {
		TreeViewerColumn pathColumn = new TreeViewerColumn(viewer, SWT.NONE);
		pathColumn.getColumn().setWidth(getColumnWidth(Columns.PATH));
		pathColumn.getColumn().setResizable(true);
		pathColumn.getColumn().setText(Columns.PATH.getColumnHeaderText());
		pathColumn.setLabelProvider(new TreePathCellLabelProvider());
		pathColumn.getColumn().addControlListener(columnListener);
	}

	private void createProjectColumn(TreeViewer viewer) {
		TreeViewerColumn projectColumn = new TreeViewerColumn(viewer, SWT.NONE);
		if (isRootNodeManualNode(viewer)) {
			projectColumn.getColumn().setWidth(getColumnWidth(Columns.PROJECT));
		} else {
			projectColumn.getColumn().setWidth(0);
		}
		projectColumn.getColumn().setResizable(true);
		projectColumn.getColumn().setText(Columns.PROJECT.getColumnHeaderText());
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
		tabColumn.getColumn().setText(Columns.NAME.getColumnHeaderText());
		tabColumn.getColumn().setResizable(true);
		tabColumn.setLabelProvider(new TreeNameCellLabelProvider());
		ColumnViewerToolTipSupport.enableFor(viewer);
		tabColumn.getColumn().addControlListener(columnListener);

	}

	private void createPINColumn(TreeViewer viewer) {
		TreeViewerColumn pinColumn = new TreeViewerColumn(viewer, SWT.NONE);
		pinColumn.getColumn().setWidth(getColumnWidth(Columns.PIN));
		pinColumn.getColumn().setText(Columns.PIN.getColumnHeaderText());
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
			e.printStackTrace();
		}
	}

	public static void hideProjectColumn() {
		hideColumn(Columns.PROJECT);
	}

	public static void showProjectColumn() {
		showColumn(Columns.PROJECT);
	}

	public static void hideColumn(Columns column) {
		TreeColumn columnToHide = filteredTree.getViewer().getTree().getColumn(Columns.getInteger(column));
		columnToHide.setWidth(0);
		columnToHide.setResizable(false);
	}

	public static void showColumn(Columns column) {
		TreeColumn columnToShow = filteredTree.getViewer().getTree().getColumn(Columns.getInteger(column));
		columnToShow.setWidth(getColumnWidth(column));
		columnToShow.setResizable(true);
	}

	public static void changeColumnsVisibility() {
		if (preferenceStore.getString(PreferenceConstants.COLUMN_VISIBILITY_CLOSE).equals(Columns.VISIBLE)) {
			showColumn(Columns.CLOSE);
		} else {
			hideColumn(Columns.CLOSE);
		}
		if (preferenceStore.getString(PreferenceConstants.COLUMN_VISIBILITY_NAME).equals(Columns.VISIBLE)) {
			showColumn(Columns.NAME);
		} else {
			hideColumn(Columns.NAME);
		}
		if (preferenceStore.getString(PreferenceConstants.COLUMN_VISIBILITY_PATH).equals(Columns.VISIBLE)) {
			showColumn(Columns.PATH);
		} else {
			hideColumn(Columns.PATH);
		}

		if (preferenceStore.getString(PreferenceConstants.COLUMN_VISIBILITY_PIN).equals(Columns.VISIBLE)) {
			showColumn(Columns.PIN);
		} else {
			hideColumn(Columns.PIN);
		}

		if (preferenceStore.getString(PreferenceConstants.COLUMN_VISIBILITY_PROJECT).equals(Columns.VISIBLE)) {
			showColumn(Columns.PROJECT);
		} else {
			hideColumn(Columns.PROJECT);
		}
		try {
			TreeContentProvider tcp = (TreeContentProvider) VTView.getTreeViewer().getContentProvider();
			if (tcp.getInvisibleRoot().equals(tcp.getProjectsRoot())) {
				VTView.hideProjectColumn();
			}
		} catch (Exception e) {

		}
	}

	public static void changeColumnSequence() {
		filteredTree.getViewer().getTree().setColumnOrder(Columns.getSortOrderForTreeViewer());
	}

	@Override
	public void dispose() {
		VTView.filteredTree = null;
		super.dispose();
	}

	public static String getID() {
		return ID;
	}
}
