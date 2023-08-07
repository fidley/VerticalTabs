package com.abapblog.verticaltabs.icons;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class Icons {
	public static final String ICON_PROJECT = "project_open.gif";
	public static final String ICON_FOLDER_OPEN = "folder_open.gif";
	public static final String ICON_FOLDER_CLOSED = "folder_closed.gif";
	private static final ImageRegistry registry = new ImageRegistry();

	public static Image getIcon(String iconName) {
		return registry.get(iconName);
	}

	public Icons() {
		registry.put(ICON_PROJECT, getImageDescriptor(ICON_PROJECT));
		registry.put(ICON_FOLDER_OPEN, getImageDescriptor(ICON_FOLDER_OPEN));
		registry.put(ICON_FOLDER_CLOSED, getImageDescriptor(ICON_FOLDER_CLOSED));
	}

	public ImageDescriptor getImageDescriptor(String iconName) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + iconName), null);
		return ImageDescriptor.createFromURL(url);
	}

}
