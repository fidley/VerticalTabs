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
	private FieldEditor putDescriptionIntoSapGuiEditorTabsTitleFieldEditor;
	private FieldEditor putTabnameIntoSapGuiEditorTooltipFieldEditor;

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
		putDescriptionIntoSapGuiEditorTabsTitleFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.PUT_DESCRIPTION_INTO_SAP_GUI_EDITOR_TABS_TITLE,
				"&Put description into SAP GUI Editor Tab title - works only with above setting on (experimental)",
				getFieldEditorParent());
		addField(putDescriptionIntoSapGuiEditorTabsTitleFieldEditor);
		putTabnameIntoSapGuiEditorTooltipFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.PUT_TABNAME_INTO_SAP_GUI_EDITOR_TOOLTIP,
				"&Put tab name into SAP GUI Editor Tooltip (instead of description)", getFieldEditorParent());
		addField(putTabnameIntoSapGuiEditorTooltipFieldEditor);
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
		store.setToDefault(PreferenceConstants.PUT_DESCRIPTION_INTO_SAP_GUI_EDITOR_TABS_TITLE);
		updateSapGuiTabsTitleAndDescriptionFieldEditor.loadDefault();
		putDescriptionIntoSapGuiEditorTabsTitleFieldEditor.loadDefault();
		putTabnameIntoSapGuiEditorTooltipFieldEditor.loadDefault();
	}

}
