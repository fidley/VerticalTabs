package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreePathCellLabelProvider extends StyledCellLabelProvider {

	public TreePathCellLabelProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof ITreeNode) {
			ITreeNode node = (ITreeNode) element;
			StyledString styledString = new StyledString();
			styledString.append(node.getPath(), StyledString.DECORATIONS_STYLER);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
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
			return "Path";
		}
		return "";
	}
}
