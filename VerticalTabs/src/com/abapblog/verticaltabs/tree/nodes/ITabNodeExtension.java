package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.ui.IEditorReference;

public interface ITabNodeExtension {

	public static final String TAB_NODE_EXTENSION_ID = "com.abapblog.verticaltabs.extensions.TabNode";

	public TabNode createExtendedTabNode(IEditorReference editorReference);
}
