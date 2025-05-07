package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.tree.nodes.GroupNode;
import com.abapblog.verticaltabs.tree.nodes.INodeWithDescription;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.ProjectNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreeNameCellLabelProvider extends StyledCellLabelProvider {

	public TreeNameCellLabelProvider() {
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
			addCounters(node, styledString);
			addSplitInfo(node, styledString);
			addDescription(node, styledString);
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

	private void addSplitInfo(ITreeNode node, StyledString styledString) {
		if (node instanceof TabNode) {
			TabNode tn = (TabNode) node;
			if (!tn.getSplitTag().equals(""))
				styledString.append(" (" + tn.getSplitTagDisplayName() + ")", StyledString.QUALIFIER_STYLER);
		}

	}

	private void addCounters(ITreeNode node, StyledString styledString) {
		if (node instanceof GroupNode || node instanceof ProjectNode)
			styledString.append(" (" + node.getChildren().length + ")", StyledString.DECORATIONS_STYLER);
	}

	private void addDescription(ITreeNode node, StyledString styledString) {
		if (node instanceof INodeWithDescription && Columns.isNameDescriptionVisible()) {
			INodeWithDescription tn = (INodeWithDescription) node;
			if (!tn.getObjectDescription().equals("")) {
				Styler styler = new ItalicStyler();
				styledString.append(" " + tn.getObjectDescription(), styler);
			}
		}
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
		if (element instanceof ITreeNode) {
			ITreeNode node = (ITreeNode) element;
			return node.getTooltip();
		}
		return "";
	}

}
