package com.abapblog.verticaltabs.tree.labelproviders;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreePinCellLabelProvider extends StyledCellLabelProvider {
	private Image ImageToDispose;

	public TreePinCellLabelProvider() {

	}

	@Override
	public void dispose() {
		super.dispose();
		if (ImageToDispose != null)
			ImageToDispose.dispose();
	}

	@Override
	public void update(ViewerCell cell) {
		ITreeNode treeNode = (ITreeNode) cell.getElement();

		if (treeNode.isPinned()) {
			try {
				if (ImageToDispose == null) {
					ImageToDispose = ImageDescriptor
							.createFromURL(new URL("platform:/plugin/org.eclipse.ui/icons/full/elcl16/pin_view.png"))
							.createImage();
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cell.setImage(ImageToDispose);
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
