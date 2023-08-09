package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.icons.Icons;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreePinCellLabelProvider extends StyledCellLabelProvider {

	public TreePinCellLabelProvider() {

	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	public void update(ViewerCell cell) {
		ITreeNode treeNode = (ITreeNode) cell.getElement();

		if (treeNode.isPinned()) {
			cell.setImage(Icons.getIcon(Icons.ICON_PIN_TAB));
		} else {
			cell.setImage(null);
		}

		super.update(cell);
	}

	@Override
	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof TabNode) {
			return "Pin";
		}
		return "";
	}
}
