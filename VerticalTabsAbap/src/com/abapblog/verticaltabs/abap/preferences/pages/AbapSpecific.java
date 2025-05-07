package com.abapblog.verticaltabs.abap.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.abap.nodes.SapGuiNode;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class AbapSpecific extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore store;
	public static final String ID = "com.abapblog.verticalTabs.abap.preferences.AbapSpecific";
	private FieldEditor updateSapGuiTabsTitleAndDescriptionFieldEditor;

	public AbapSpecific() {
		super(GRID);
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		// setDescription("Settings for ABAP specific features");
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		updateSapGuiTabsTitleAndDescriptionFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION,
				"&Update SAP GUI tabs title and description (experimental)", getFieldEditorParent());
		addField(updateSapGuiTabsTitleAndDescriptionFieldEditor);
	}

	@Override
	protected void performApply() {
		super.performApply();
		if (store.getBoolean(PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION)) {
			SapGuiNode.registerSapGuiLifecycleHandler();
			TreeContentProvider.getNodesFactory().getTabNodes().entrySet().forEach(set -> {
				if (set.getValue() instanceof SapGuiNode) {
					((SapGuiNode) set.getValue()).setShouldIgnorePropertyChange(true);
				}
			});
		} else {
			SapGuiNode.unregisterSapGuiLifecycleHandler();
			TreeContentProvider.getNodesFactory().getTabNodes().entrySet().forEach(set -> {
				if (set.getValue() instanceof SapGuiNode) {
					((SapGuiNode) set.getValue()).setShouldIgnorePropertyChange(false);
				}
			});
		}
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		store.setToDefault(PreferenceConstants.UPDATE_SAP_GUI_TABS_TITLE_AND_DESCRIPTION);
		updateSapGuiTabsTitleAndDescriptionFieldEditor.loadDefault();
	}

}
