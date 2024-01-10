package com.abapblog.verticaltabs.preferences.pages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.views.VTView;

public class ColumnsSettingsPage extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String COLUMN = "column";
	private static final String VISIBLE = "visible";
	private final IPreferenceStore store;
	public static final String ID = "com.abapblog.verticalTabs.preferences.Columns";
	private Table table;
	private TableItem[] tableItems = new TableItem[Columns.getTotalNumberOfColumns()];

	public ColumnsSettingsPage() {
		super();
		this.store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.store);
		setTitle("ColumnsSettingsPage");
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = createTableViewer(parent);
		createTableEntries();
		createSortButtons(container);
		return container;
	}

	private void switchItemPlaces(int fromRow, int toRow) {
		TableItem selectedItem = tableItems[fromRow];
		TableItem replacedItem = tableItems[toRow];
		Columns replacedColumn = (Columns) replacedItem.getData(COLUMN);
		Columns currentColumn = (Columns) selectedItem.getData(COLUMN);
		String replacedItemText = replacedItem.getText();
		String currentItemText = selectedItem.getText();
		Boolean replacedItemVisible = ((Button) replacedItem.getData(VISIBLE)).getSelection();
		Boolean currentItemVisible = ((Button) selectedItem.getData(VISIBLE)).getSelection();
		tableItems[toRow].setText(currentItemText);
		tableItems[toRow].setData(COLUMN, currentColumn);
		((Button) replacedItem.getData(VISIBLE)).setSelection(currentItemVisible);
		tableItems[fromRow].setText(replacedItemText);
		tableItems[fromRow].setData(COLUMN, replacedColumn);
		((Button) selectedItem.getData(VISIBLE)).setSelection(replacedItemVisible);
		table.setSelection(toRow);
	}

	private void createSortButtons(Composite container) {
		Group grpSortColumns = new Group(container, SWT.NONE);
		GridData gd_grpSortColumns = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_grpSortColumns.heightHint = 102;
		grpSortColumns.setLayoutData(gd_grpSortColumns);
		grpSortColumns.setText("Sort columns");
		grpSortColumns.setLayout(new GridLayout(1, false));

		Button btnUp = new Button(grpSortColumns, SWT.NONE);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int currentRow = -1;
				TableItem[] selectedItems = table.getSelection();
				for (int i = 0; i < selectedItems.length; i++) {
					TableItem selectedItem = selectedItems[i];
					for (int x = 0; x < tableItems.length; x++) {
						if (tableItems[x].equals(selectedItem)) {
							currentRow = x;
							break;
						}
					}
					if (currentRow > 0) {

						switchItemPlaces(currentRow, currentRow - 1);

					}
				}

			}

		});
		GridData gd_btnUp = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnUp.widthHint = 45;
		gd_btnUp.minimumWidth = 20;
		btnUp.setLayoutData(gd_btnUp);
		btnUp.setText("Up");

		Button btnDown = new Button(grpSortColumns, SWT.NONE);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				int currentRow = -1;
				TableItem[] selectedItems = table.getSelection();
				for (int i = 0; i < selectedItems.length; i++) {
					TableItem selectedItem = selectedItems[i];
					for (int x = 0; x < tableItems.length; x++) {
						if (tableItems[x].equals(selectedItem)) {
							currentRow = x;
							break;
						}
					}
					if (currentRow < Columns.getTotalNumberOfColumns() - 1) {

						switchItemPlaces(currentRow, currentRow + 1);
					}
				}

			}
		});
		GridData gd_btnDown = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnDown.widthHint = 45;
		btnDown.setLayoutData(gd_btnDown);
		btnDown.setText("Down");
	}

	private Composite createTableViewer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		GridData gd_container = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gd_container.widthHint = 554;
		gd_container.heightHint = 310;
		container.setLayoutData(gd_container);
		TableViewer tv = new TableViewer(container, SWT.SINGLE + SWT.FULL_SELECTION);
		table = tv.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn tblTVclmnName = new TableViewerColumn(tv, SWT.NONE);
		final TableColumn tblclmnName = tblTVclmnName.getColumn();
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableViewerColumn tblTVclmnVisible = new TableViewerColumn(tv, SWT.CHECK);
		final TableColumn tblclmnVisible = tblTVclmnVisible.getColumn();
		tblclmnVisible.setWidth(102);
		tblclmnVisible.setText("Visible");
		return container;
	}

	private void createTableEntries() {

		for (int i = 0; i < Columns.getTotalNumberOfColumns(); i++) {
			createTableItem(i);
		}

		addCheckboxes();
	}

	private void addCheckboxes() {
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {

			final TableEditor editor = new TableEditor(table);
			Button checkBox = new Button(table, SWT.CHECK);
			checkBox.setBackground(table.getBackground());
			checkBox.setText("");
			if (items[i].getText(1).toString().equals("true"))
				checkBox.setSelection(true);
			items[i].setText(1, "");
			editor.grabHorizontal = true;
			items[i].setData(VISIBLE, checkBox);
			editor.setEditor(checkBox, items[i], 1);
		}
	}

	private void createTableItem(int i) {

		TableItem item = new TableItem(table, SWT.NONE);
		tableItems[i] = item;
		Columns c = Columns.getBySequence(i);
		item.setText(new String[] { c.getColumnHeaderText(), c.isVisible().toString() });
		item.setData(COLUMN, c);
	}

	@Override
	protected void performApply() {
		// super.performApply();

		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			Columns c = (Columns) item.getData(COLUMN);
			Button b = (Button) item.getData(VISIBLE);
			c.setVisible(b.getSelection());
			c.setSequence(i);
		}
		VTView.changeColumnsVisibility();
		VTView.changeColumnSequence();
	}

//Apply&Close
	@Override
	public boolean performOk() {
		final Boolean ApplyClose = super.performOk();
		performApply();
		return ApplyClose;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		resetColumnsSettingsToDefaultInPreferencesStore();
		Columns.resetSequenceToDefault();
		moveItemsToDefaultSequence();
		VTView.changeColumnsVisibility();
		VTView.changeColumnSequence();
	}

	private void moveItemsToDefaultSequence() {
		int[] defaultSequence = Columns.getSortOrderForTreeViewer();
		for (int z = 0; z < 5; z++) {
			TableItem[] tempItems = tableItems.clone();
			for (int i = 0; i < tempItems.length; i++) {
				Columns currentColumn = (Columns) tempItems[i].getData(COLUMN);
				for (int x = 0; x < defaultSequence.length; x++) {
					if (Columns.getInteger(currentColumn) == defaultSequence[x]) {
						if (i != x)
							((Button) tempItems[i].getData(VISIBLE)).setSelection(true);
						switchItemPlaces(i, x);
						break;
					}
				}

			}
		}

		for (int i = 0; i < tableItems.length - 1; i++) {
			Button b = (Button) tableItems[i].getData(VISIBLE);
			b.setSelection(true);
		}

	}

	private void resetColumnsSettingsToDefaultInPreferencesStore() {
		store.setValue(PreferenceConstants.COLUMN_WIDTH_CLOSE,
				store.getDefaultInt(PreferenceConstants.COLUMN_WIDTH_CLOSE));
		store.setValue(PreferenceConstants.COLUMN_WIDTH_PIN, store.getDefaultInt(PreferenceConstants.COLUMN_WIDTH_PIN));
		store.setValue(PreferenceConstants.COLUMN_WIDTH_PROJECT,
				store.getDefaultInt(PreferenceConstants.COLUMN_WIDTH_PROJECT));
		store.setValue(PreferenceConstants.COLUMN_WIDTH_NAME,
				store.getDefaultInt(PreferenceConstants.COLUMN_WIDTH_NAME));
		store.setValue(PreferenceConstants.COLUMN_WIDTH_PATH,
				store.getDefaultInt(PreferenceConstants.COLUMN_WIDTH_PATH));
		store.setValue(PreferenceConstants.COLUMN_VISIBILITY_CLOSE,
				store.getDefaultString(PreferenceConstants.COLUMN_VISIBILITY_CLOSE));
		store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PIN,
				store.getDefaultString(PreferenceConstants.COLUMN_VISIBILITY_PIN));
		store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PROJECT,
				store.getDefaultString(PreferenceConstants.COLUMN_VISIBILITY_PROJECT));
		store.setValue(PreferenceConstants.COLUMN_VISIBILITY_NAME,
				store.getDefaultString(PreferenceConstants.COLUMN_VISIBILITY_NAME));
		store.setValue(PreferenceConstants.COLUMN_VISIBILITY_PATH,
				store.getDefaultString(PreferenceConstants.COLUMN_VISIBILITY_PATH));
		store.setValue(PreferenceConstants.COLUMN_INDEX_CLOSE,
				store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_CLOSE));
		store.setValue(PreferenceConstants.COLUMN_INDEX_PIN, store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PIN));
		store.setValue(PreferenceConstants.COLUMN_INDEX_PROJECT,
				store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PROJECT));
		store.setValue(PreferenceConstants.COLUMN_INDEX_NAME,
				store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_NAME));
		store.setValue(PreferenceConstants.COLUMN_INDEX_PATH,
				store.getDefaultInt(PreferenceConstants.COLUMN_INDEX_PATH));
	}

}
