package com.abapblog.verticaltabs.tree;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.TreeColumn;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.views.VTView;

public class ColumnControlListener implements ControlListener {
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Override
	public void controlResized(ControlEvent arg0) {
		TreeColumn column = (TreeColumn) arg0.getSource();
		if (column.getWidth() != 0) {
			TreeColumn[] allColumns = VTView.getTreeViewer().getTree().getColumns();
			for (int i = 0; i < allColumns.length; i++) {
				TreeColumn treeColumn = allColumns[i];
				if (treeColumn.equals(column)) {
					switch (Columns.fromInteger(i)) {
					case CLOSE:
						preferenceStore.setValue(PreferenceConstants.COLUMN_WIDTH_CLOSE, column.getWidth());
						break;
					case PIN:
						preferenceStore.setValue(PreferenceConstants.COLUMN_WIDTH_PIN, column.getWidth());
						break;
					case PROJECT:
						preferenceStore.setValue(PreferenceConstants.COLUMN_WIDTH_PROJECT, column.getWidth());
						break;
					case NAME:
						preferenceStore.setValue(PreferenceConstants.COLUMN_WIDTH_NAME, column.getWidth());
						break;
					case PATH:
						preferenceStore.setValue(PreferenceConstants.COLUMN_WIDTH_PATH, column.getWidth());
						break;
					default:
						break;

					}
				}

			}
		}
	}

	@Override
	public void controlMoved(ControlEvent arg0) {
		// Not needed at the moment
	}

}