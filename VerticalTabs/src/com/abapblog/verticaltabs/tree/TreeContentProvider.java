package com.abapblog.verticaltabs.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.handlers.GroupByProject;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodesFactory;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.RootNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.tree.nodes.TreeNode;

public class TreeContentProvider implements ITreeContentProvider, IPartListener2, IWorkbenchListener {
	private static RootNode invisibleRoot;
	private static RootNode projectsRoot;
	private static RootNode manualRoot;
	private static List<Object> expandedProjects = new ArrayList<Object>();
	private static List<Object> expandedGroups = new ArrayList<Object>();
	private static TreeViewer treeViewer;
	private static Integer biggestIndex = Integer.valueOf(0);
	private NodesFactory nodesFactory = new NodesFactory(this);
	private static TreeContentProvider contentProvider;

	private TreeContentProvider(TreeViewer treeViewer) {
		createPartListener();
		TreeContentProvider.treeViewer = treeViewer;
		PlatformUI.getWorkbench().addWorkbenchListener(this);
		addExpandCollapseListeners(treeViewer);
	}

	public static TreeContentProvider getTreeContentProvider(TreeViewer treeViewer) {
		if (contentProvider == null) {
			contentProvider = new TreeContentProvider(treeViewer);
			contentProvider.initialize();
		} else {
			TreeContentProvider.treeViewer = treeViewer;
			addExpandCollapseListeners(treeViewer);
		}
		return contentProvider;

	}

	private static void addExpandCollapseListeners(TreeViewer treeViewer) {
		treeViewer.getTree().addListener(SWT.Expand, new Listener() {
			@Override
			public void handleEvent(Event event) {
				final TreeItem item = (TreeItem) event.item;
				ITreeNode treeNode = (ITreeNode) item.getData();
				if (treeNode instanceof GroupNode)
					expandedGroups.add(treeNode);
				if (treeNode instanceof ProjectNode)
					expandedProjects.add(treeNode);

			}
		});
		treeViewer.getTree().addListener(SWT.Collapse, new Listener() {
			@Override
			public void handleEvent(Event event) {
				final TreeItem item = (TreeItem) event.item;
				ITreeNode treeNode = (ITreeNode) item.getData();
				if (treeNode instanceof GroupNode)
					expandedGroups.remove(treeNode);
				if (treeNode instanceof ProjectNode)
					expandedProjects.remove(treeNode);

			}
		});
	}

	private void createPartListener() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (getInvisibleRoot() == null)
			initialize();
		return getChildren(getInvisibleRoot());
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof ITreeNode) {
			return ((ITreeNode) parent).getChildren();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof ITreeNode) {
			return ((ITreeNode) child).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof ITreeNode)
			return ((ITreeNode) parent).hasChildren();
		return false;
	}

	public void initialize() {

		try {
			createRootNodes();
			createTabNodes();
			setInitialRootNode();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setInitialRootNode() {
		if (GroupByProject.getGroupByProjectPreference()) {
			invisibleRoot = getProjectsRoot();
			GroupByProject.setToggleStatus(true);
			return;
		}
		GroupByProject.setToggleStatus(false);
		invisibleRoot = getManualRoot();
	}

	private void createTabNodes() throws PartInitException {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		IMemento[] mementos = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getEditorState(editorReferences, true);
		createEntriesForOpenedEditors(editorReferences);
	}

	private void createRootNodes() {
		invisibleRoot = new RootNode();
		setProjectsRoot(new RootNode());
		setManualRoot(new RootNode());
	}

	private void createEntriesForOpenedEditors(IEditorReference[] editorReferences) throws PartInitException {
		for (int i = 0; i < editorReferences.length; i++) {
			editorReferences[i].getEditor(true);
			addEditorReferenceToNodesAndGroups(editorReferences[i]);
		}
	}

	private void addEditorReferenceToNodesAndGroups(IEditorReference editorReference) {
		try {
			TabNode tabNode = getNodesFactory().getTabNode(editorReference);
			if (!manualRoot.contains(tabNode))
				manualRoot.addChild(tabNode);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	public TreeNode getInvisibleRoot() {
		return invisibleRoot;
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		boolean partInTabs = false;
		if (partRef instanceof IEditorReference) {
			IEditorReference er = (IEditorReference) partRef;
			partInTabs = nodesFactory.getTabNodes().containsKey(er);
			if (!partInTabs) {
//				try {
//					er.getEditorInput().getPersistable();
//					er.getEditor(false);
//					if (er.getEditor(false) instanceof CompatibilityPart) {
//						er.getClass();
//					}
//					er.getPage();
//
//					IWorkbenchPart part = er.getPart(false);
//					MPart modelPart = part.getSite().getService(MPart.class);
//					modelPart.getContext();
//					CompatibilityEditor view = (CompatibilityEditor) modelPart.getObject();
//					if (modelPart instanceof CompatibilityPart) {
//						CompatibilityPart cp = (CompatibilityPart) modelPart;
//					}
//					modelService.getContainer(modelPart);
//					modelPart.getParent();
////					view.getReference().get;
//					modelPart.getVariables();
//					modelPart.getBindingContexts();
//					modelPart.getTransientData();
//
////					try {
//////						part.getAdapter(Class.forName("org.eclipse.e4.ui.model.application.MPart"));
////
////					} catch (ClassNotFoundException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//					IEditorRegistry registry = er.getPage().getWorkbenchWindow().getWorkbench().getEditorRegistry();
//					IEditorPart ed = er.getEditor(false);
//
//					EditorDescriptor descriptor = (EditorDescriptor) registry.findEditor(ed.getSite().getId());
//
//				} catch (PartInitException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				addEditorReferenceToNodesAndGroups(er);
				refreshTree();
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (nodesFactory == null)
			return;
		if (partRef instanceof IEditorReference) {
			IEditorReference er = (IEditorReference) partRef;
			try {
				TabNode tn = nodesFactory.getTabNode(er);
				if (tn.isPinned())
					return;
				nodesFactory.removeTabNode(er);
				refreshTree();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		if (partRef instanceof IEditorReference) {
			IEditorReference er = (IEditorReference) partRef;
			ITreeNode[] treeNodes = invisibleRoot.getChildren();
			for (int i = 0; i < treeNodes.length; i++) {
				if (treeNodes[i] instanceof TabNode) {
					TabNode tabNode = (TabNode) treeNodes[i];
					if (tabNode.getEditorReference().equals(er)) {
						tabNode.updateFromEditorReferenece();
//						refreshTree();
						break;
					}
				}
			}
		}
	}

	private static void getExpandedElementsIntoList(List<Object> list) {
		list.clear();
		for (Object expanded : treeViewer.getExpandedElements()) {
			list.add(expanded);
		}
	}

	public static void refreshTree() {
		if (treeViewer == null)
			return;
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					treeViewer.refresh();
					try {
						contentProvider.setExpandedElementsForTreeViewer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
	}

	public void setExpandedElementsForTreeViewer() {
		if (invisibleRoot.equals(projectsRoot) && getExpandedProjects() != null)
			treeViewer.setExpandedElements(getExpandedProjects().toArray());
		if (invisibleRoot.equals(manualRoot) && expandedGroups != null)
			treeViewer.setExpandedElements(expandedGroups.toArray());
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//https://stackoverflow.com/questions/12480402/adding-a-remove-button-to-a-column-in-a-table
		TreeViewer tv = (TreeViewer) viewer;
		// This will dispose of all the control button that were created previously
		if (tv.getTree() != null && tv.getTree().getChildren() != null) {
			for (Control item : tv.getTree().getChildren()) {
				// at this point there are no other controls embedded in the viewer, however
				// different instances may require more checking of the controls here.
				if ((item != null) && (!item.isDisposed())) {
					item.dispose();
				}

			}
		}
	}

	public static Integer getNextSortIndex() {
		biggestIndex += 1;
		return biggestIndex;
	}

	@Override
	public void dispose() {
//		nodesFactory = null;
//		removePartListener();
//		ITreeContentProvider.super.dispose();
		treeViewer = null;
	}

	private void removePartListener() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().removePartListener(this);
	}

	private void setInvisibleRoot(RootNode root) {
		if (invisibleRoot.equals(projectsRoot))
			getExpandedElementsIntoList(expandedProjects);
		if (invisibleRoot.equals(manualRoot))
			getExpandedElementsIntoList(expandedGroups);

		if (root.equals(projectsRoot) && getExpandedProjects() != null)
			treeViewer.setExpandedElements(getExpandedProjects());
		if (root.equals(manualRoot) && expandedGroups != null)
			treeViewer.setExpandedElements(expandedGroups);
		invisibleRoot = root;
	}

	public RootNode getProjectsRoot() {
		return projectsRoot;
	}

	private void setProjectsRoot(RootNode projectsRoot) {
		this.projectsRoot = projectsRoot;
	}

	public RootNode getManualRoot() {
		return manualRoot;
	}

	private void setManualRoot(RootNode manualRoot) {
		this.manualRoot = manualRoot;
	}

	public NodesFactory getNodesFactory() {
		return nodesFactory;
	}

	public void toggleGrouping(Boolean groupByProject) {
		if (groupByProject) {
			if (!invisibleRoot.equals(projectsRoot)) {
//				getExpandedElementsIntoList(expandedGroups);
				setInvisibleRoot(projectsRoot);
				refreshTree();
			}
		} else {
			if (invisibleRoot.equals(projectsRoot)) {
//				getExpandedElementsIntoList(expandedProjects);
				setInvisibleRoot(manualRoot);
				refreshTree();
			}
		}
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(this);
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	public static List<Object> getExpandedProjects() {
		return expandedProjects;
	}
}
