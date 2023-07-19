package com.abapblog.verticaltabs.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.RootNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.tree.nodes.TreeNode;

public class TreeContentProvider implements ITreeContentProvider, IPartListener2 {
	private RootNode invisibleRoot;
	private static TreeViewer treeViewer;
	private static Integer biggestIndex = Integer.valueOf(0);
	private static List<ITreeNode> treeNodes = new ArrayList<>();

	public TreeContentProvider(TreeViewer treeViewer) {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this);
		TreeContentProvider.treeViewer = treeViewer;
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
			invisibleRoot = new RootNode();

			IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
			createEntriesForOpenedEditors(editorReferences);
			autoResizeColumns();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createEntriesForOpenedEditors(IEditorReference[] editorReferences) throws PartInitException {
		for (int i = 0; i < editorReferences.length; i++) {
			editorReferences[i].getEditor(true);
			if (!invisibleRoot.contains(editorReferences[i]))
				invisibleRoot.addChild(new TabNode(editorReferences[i]));
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
			partInTabs = invisibleRoot.contains(er);

			if (!partInTabs) {
				try {
					invisibleRoot.addChild(new TabNode(er));
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				refreshTree();
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (partRef instanceof IEditorReference) {
			IEditorReference er = (IEditorReference) partRef;
			// System.out.println(er.getId());
			ITreeNode[] treeNodes = invisibleRoot.getChildren();
			for (int i = 0; i < treeNodes.length; i++) {
				if (treeNodes[i] instanceof TabNode) {
					TabNode tabNode = (TabNode) treeNodes[i];
					if (tabNode.getEditorReference().equals(er) && !tabNode.isPinned()) {

						tabNode.getParent().removeChild(tabNode);
						refreshTree();
						break;
					}
				}
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

	public static void autoResizeColumns() {
		Tree tree = treeViewer.getTree();
		for (int i = 0, n = tree.getColumnCount(); i < n; i++)
			tree.getColumn(i).pack();
	}

	public static void refreshTree() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					treeViewer.refresh();
					autoResizeColumns();
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
		treeNodes.forEach((treeNode) -> {
			treeNodes.remove(treeNode);
			treeNode = null;
		});
		ITreeContentProvider.super.dispose();
	}
}
