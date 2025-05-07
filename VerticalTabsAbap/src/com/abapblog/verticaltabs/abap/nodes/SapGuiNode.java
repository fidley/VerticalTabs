package com.abapblog.verticaltabs.abap.nodes;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorReference;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.abap.commands.SapGuiLifeCycleListener;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.nodes.INodeWithDescription;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.sapgui.ui.SapGuiPlugin;

public class SapGuiNode extends TabNode implements INodeWithDescription {
	// to be used with Memento
	private String Description = "";
	private boolean shouldIgnorePropertyChange = false;
	public static SapGuiLifeCycleListener sapGuiLifeCycleListener = new SapGuiLifeCycleListener();
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	static {
		registerSapGuiLifecycleHandler();
	}

	public SapGuiNode(IEditorReference editorReference) {
		super(editorReference);

	}

	public static void registerSapGuiLifecycleHandler() {
		if (store.getBoolean(PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION) == false) {
			return;
		}
		SapGuiPlugin.getDefault().registerLifeCycleListener(sapGuiLifeCycleListener);
	}

	public static void unregisterSapGuiLifecycleHandler() {
		if (sapGuiLifeCycleListener == null) {
			return;
		}
		SapGuiPlugin.getDefault().unregisterLifeCycleListener(sapGuiLifeCycleListener);
	}

	@Override
	public String getObjectDescription() {
		return Description;
	}

	@Override
	public void setObjectDescription(String description) {
		Description = description;

	}

	@Override
	public void propertyChanged(Object source, int propId) {
		if (shouldIgnorePropertyChange) {
			return;
		}
		super.propertyChanged(source, propId);
	}

	@Override
	public void updateFromEditorReferenece() {
		if (shouldIgnorePropertyChange) {
			return;
		}
		super.updateFromEditorReferenece();
	}

	public void setShouldIgnorePropertyChange(boolean shouldIgnorePropertyChange) {
		this.shouldIgnorePropertyChange = shouldIgnorePropertyChange;
	}
}
