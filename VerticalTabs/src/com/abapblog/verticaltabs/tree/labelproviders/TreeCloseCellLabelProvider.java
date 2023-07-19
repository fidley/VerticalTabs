package com.abapblog.verticaltabs.tree.labelproviders;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreeCloseCellLabelProvider extends StyledCellLabelProvider {
	private Image ImageToDispose;

	public TreeCloseCellLabelProvider() {
	}

	@Override
	public void dispose() {
		super.dispose();
		ImageToDispose.dispose();
	}

	@Override
	public void update(ViewerCell cell) {
		if (ImageToDispose == null) {
			try {
				ImageToDispose = ImageDescriptor
						.createFromURL(new URL("platform:/plugin/org.eclipse.ui/icons/full/elcl16/close_view.png"))
						.createImage();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		cell.setImage(ImageToDispose);
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
