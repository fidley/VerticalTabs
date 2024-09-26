package com.abapblog.verticaltabs.preferences.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.preferences.TabNavigation;
import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class MainPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore store;
	public static final String ID = "com.abapblog.verticalTabs.preferences.MainPage";

	public MainPage() {
		super(GRID);
		this.store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.store);
		setDescription("Settings for Vertical Tabs plugin");
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS,
				"&Show separate tabs for splitted editors", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceConstants.TAB_NAVIGATION, "Navigation to linked TAB by", 1,
				TabNavigation.toNamesAndKeys(), getFieldEditorParent(), true));

		addField(new BooleanFieldEditor(PreferenceConstants.SELECT_ACTIVE_TAB_IN_TREE,
				"&Select active tab in tree and expand its parent", getFieldEditorParent()));

	}

	@Override
	protected void performApply() {
		super.performApply();
		TreeContentProvider.getNodesFactory().removeSplittedEditorNodes();
		TreeContentProvider.getNodesFactory().AddSplittedEditorNodes();
	}

//Apply&Close
	@Override
	public boolean performOk() {
		final Boolean ApplyClose = super.performOk();
		TreeContentProvider.getNodesFactory().removeSplittedEditorNodes();
		TreeContentProvider.getNodesFactory().AddSplittedEditorNodes();
		return ApplyClose;
	}
}
