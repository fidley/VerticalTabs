package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class RowClickHandler {

	public void handleClick(MouseEvent e, ITreeNode treeNode, int columnIndex) {

		switch (Columns.fromInteger(columnIndex)) {
		case TAB:
			if (treeNode.isOpenable())
				treeNode.open();
			break;
		case PIN:
			if (treeNode.isPinable()) {
				if (treeNode.isPinned()) {
					treeNode.unpin();
				} else {
					treeNode.pin();
				}
			}
			TreeContentProvider.refreshTree();
			break;
		case CLOSE:
			if (treeNode instanceof TabNode) {
				TabNode tabNode = (TabNode) treeNode;
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = workbenchWindow.getActivePage();
				IEditorPart editor = tabNode.getEditorReference().getEditor(true);
				page.closeEditor(editor, true);
			}
			break;
		}

	}

}
