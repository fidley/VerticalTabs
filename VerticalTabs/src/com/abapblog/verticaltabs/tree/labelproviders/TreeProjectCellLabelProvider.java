package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;

public class TreeProjectCellLabelProvider extends StyledCellLabelProvider {

	public TreeProjectCellLabelProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void erase(Event event, Object element) {
		// TODO Auto-generated method stub
		super.erase(event, element);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof ITreeNode) {
			ITreeNode node = (ITreeNode) element;
			StyledString styledString = new StyledString();
			styledString.append(" " + node.getProjectName(), StyledString.DECORATIONS_STYLER);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());

		}

		super.update(cell);

	}

	@Override
	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}
}
