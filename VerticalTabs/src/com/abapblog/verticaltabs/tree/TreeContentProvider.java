package com.abapblog.verticaltabs.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.handlers.GroupByProject;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.NodesFactory;
import com.abapblog.verticaltabs.tree.nodes.RootNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.tree.nodes.TreeNode;

public class TreeContentProvider implements ITreeContentProvider, IPartListener2 {
	private static RootNode invisibleRoot;
	private static RootNode projectsRoot;
	private static RootNode manualRoot;
	private static Object[] expandedProjects;
	private static TreeViewer treeViewer;
	private static Integer biggestIndex = Integer.valueOf(0);
	private static List<ITreeNode> groupNodes = new ArrayList<>();
	private NodesFactory nodesFactory = new NodesFactory(this);

	public TreeContentProvider(TreeViewer treeViewer) {
		createPartListener();
		TreeContentProvider.treeViewer = treeViewer;
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

	private void setInitialRootNode() {
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
//			if (!invisibleRoot.contains(editorReferences[i]))
//				invisibleRoot.addChild(new TabNode(editorReferences[i]));
		}
	}

	private void addEditorReferenceToNodesAndGroups(IEditorReference editorReference) {
		try {
			TabNode tabNode = getNodesFactory().getTabNode(editorReference);
			if (!manualRoot.contains(tabNode))
				manualRoot.addChild(tabNode);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
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
			partInTabs = manualRoot.contains(er);
			if (!partInTabs) {
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
				tn.getParent().removeChild(tn);
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
						refreshTree();
						break;
					}
				}
			}
		}
	}

	public static void refreshTree() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					treeViewer.refresh();
					try {
						if (invisibleRoot.equals(projectsRoot) && expandedProjects != null)
							treeViewer.setExpandedElements(expandedProjects);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
				}

			}

		});
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
		nodesFactory = null;
		removePartListener();
		ITreeContentProvider.super.dispose();
	}

	private void removePartListener() {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().removePartListener(this);
	}

	private void setInvisibleRoot(RootNode root) {
		if (invisibleRoot.equals(projectsRoot))
			expandedProjects = treeViewer.getExpandedElements();

		if (root.equals(projectsRoot) && expandedProjects != null)
			treeViewer.setExpandedElements(expandedProjects);
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
				setInvisibleRoot(projectsRoot);
				refreshTree();
			}
		} else {
			if (invisibleRoot.equals(projectsRoot)) {
				setInvisibleRoot(manualRoot);
				refreshTree();
			}
		}
	}
}
