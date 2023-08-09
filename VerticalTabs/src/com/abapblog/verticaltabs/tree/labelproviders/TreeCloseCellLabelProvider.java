package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.icons.Icons;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreeCloseCellLabelProvider extends StyledCellLabelProvider {

	public TreeCloseCellLabelProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void update(ViewerCell cell) {
		if (!(cell.getElement() instanceof TabNode)) {
			cell.setImage(null);
			super.update(cell);
			return;
		}

		cell.setImage(Icons.getIcon(Icons.ICON_CLOSE_TAB));
		super.update(cell);
	}

	@Override
	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof TabNode) {
			return "Close";
		}
		return "";
	}
}
