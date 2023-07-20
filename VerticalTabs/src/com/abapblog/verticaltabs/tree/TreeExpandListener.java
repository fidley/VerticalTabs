package com.abapblog.verticaltabs.tree;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TreeExpandListener implements Listener {

	@Override
	public void handleEvent(Event event) {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);
					TreeContentProvider.autoResizeColumns();
				} catch (Exception e) {
				}

			}

		});

	}

}
