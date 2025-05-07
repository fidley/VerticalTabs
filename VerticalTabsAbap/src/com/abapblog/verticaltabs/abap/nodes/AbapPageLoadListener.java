package com.abapblog.verticaltabs.abap.nodes;

import com.abapblog.verticaltabs.tree.TreeContentProvider;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbstractAbapSourcePageExtensionProcessor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

public class AbapPageLoadListener extends AbstractAbapSourcePageExtensionProcessor {

	@Override
	public void processOnDocumentLoaded(IAbapSourcePage sourcePage) {

		TreeContentProvider.refreshTree();

	}

}
