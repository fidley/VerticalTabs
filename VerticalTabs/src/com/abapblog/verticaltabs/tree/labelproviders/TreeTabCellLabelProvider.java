package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreeTabCellLabelProvider extends StyledCellLabelProvider {

	public TreeTabCellLabelProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void erase(Event event, Object element) {
		super.erase(event, element);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof ITreeNode) {
			ITreeNode node = (ITreeNode) element;
			StyledString styledString = new StyledString(node.getTitle());
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
			try {
				cell.setImage(node.getImage());
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
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
			TabNode node = (TabNode) element;
			return node.getEditorReference().getTitleToolTip();
		}
		return "";
	}

}
