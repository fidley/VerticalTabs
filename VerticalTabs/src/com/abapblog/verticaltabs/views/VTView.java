package com.abapblog.verticaltabs.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
import com.abapblog.verticaltabs.tree.labelproviders.TreePinCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeProjectCellLabelProvider;
import com.abapblog.verticaltabs.tree.labelproviders.TreeTabCellLabelProvider;

public class VTView extends ViewPart implements ILinkedWithEditorView {
	private Composite parent;
	private static VTFilteredTree filteredTree;
	public static Sorter sorter;
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	protected Action linkWithEditorAction;
	private boolean linkingActive = true;
	protected String LinkedEditorProject = "";
	protected IProject LinkedProject;
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final ColumnControlListener columnListener = new ColumnControlListener();

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		createTreeViewer(parent);
	}

	private void createTreeViewer(Composite parent) {
		filteredTree = new VTFilteredTree(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL,
				new TreePatternFilter(), true, true);
		TreeViewer viewer = filteredTree.getViewer();
		TreeContentProvider contentProvider = new TreeContentProvider(filteredTree.getViewer());
		contentProvider.initialize();
		viewer.setContentProvider(contentProvider);
		setTreeProperties(viewer.getTree());
		createColumns(viewer);
		createGridData(viewer);
		createSorter(viewer);
		new TreeDragAndDrop(viewer);
//		setLinkingWithEditor();
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

	private int getColumnWidth(Columns column) {
		switch (column) {
		case CLOSE:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_CLOSE);
		case PIN:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_PIN);
		case TAB:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_NAME);
		case PROJECT:
			return preferenceStore.getInt(PreferenceConstants.COLUMN_WIDTH_PROJECT);
		}
		return 40;
	}

	private void createCLOSEColumn(TreeViewer viewer) {
		TreeViewerColumn closeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		closeColumn.getColumn().setWidth(getColumnWidth(Columns.CLOSE));
		closeColumn.getColumn().setResizable(true);
		closeColumn.getColumn().setText("Close");
		closeColumn.setLabelProvider(new TreeCloseCellLabelProvider());
		closeColumn.getColumn().addControlListener(columnListener);
	}

	private void createProjectColumn(TreeViewer viewer) {
		TreeViewerColumn projectColumn = new TreeViewerColumn(viewer, SWT.NONE);
		projectColumn.getColumn().setWidth(getColumnWidth(Columns.PROJECT));
		projectColumn.getColumn().setResizable(true);
		projectColumn.getColumn().setText("Project");
		projectColumn.setLabelProvider(new TreeProjectCellLabelProvider());
		projectColumn.getColumn().addControlListener(columnListener);
	}

	private void createTABColumn(TreeViewer viewer) {
		TreeViewerColumn tabColumn = new TreeViewerColumn(viewer, SWT.NONE);
		tabColumn.getColumn().setWidth(getColumnWidth(Columns.TAB));
		tabColumn.getColumn().setText("Name");
		tabColumn.getColumn().setResizable(true);
		tabColumn.setLabelProvider(new TreeTabCellLabelProvider());
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

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		IProject project = activeEditor.getEditorInput().getAdapter(IProject.class);
		if (project != null) {

		}

	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		filteredTree = null;
		sorter = null;
		super.dispose();
	}

	public void enableLinkingOfEditor() {
		if (this.linkWithEditorAction != null) {
			this.linkWithEditorAction.setEnabled(true);
		}
	}

	private void setLinkingWithEditor() {
		// Linking with editor
		this.linkWithEditorAction = new Action("Link with Editor", SWT.TOGGLE) {
			@Override
			public void run() {
				toggleLinking();
			}

		};
		this.linkWithEditorAction.setText("Link with Editor");
		this.linkWithEditorAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		getViewSite().getActionBars().getToolBarManager().add(this.linkWithEditorAction);
		getSite().getPage().addPartListener(this.linkWithEditorPartListener);
		this.linkWithEditorAction.setChecked(isLinkingActive());
		enableLinkingOfEditor();
	}

	protected void toggleLinking() {
		if (isLinkingActive()) {
			setLinkingActive(false);

		} else {
			setLinkingActive(true);
			editorActivated(getSite().getPage().getActiveEditor());
		}

	}

	public boolean isLinkingActive() {
		return this.linkingActive;
	}

	public void setLinkingActive(final boolean linkingActive) {
		this.linkingActive = linkingActive;
	}

	public void setLinkedEditorProject(final String linkedEditorProject) {
		this.LinkedEditorProject = linkedEditorProject;
	}

}
