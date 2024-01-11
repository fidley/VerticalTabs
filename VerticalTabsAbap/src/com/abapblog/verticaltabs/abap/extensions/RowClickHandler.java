package com.abapblog.verticaltabs.abap.extensions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.tree.Columns;
import com.abapblog.verticaltabs.tree.IRowClickExtension;
import com.abapblog.verticaltabs.tree.nodes.ITreeNode;
import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;

public class RowClickHandler implements IRowClickExtension {
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public RowClickHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean handleClick(MouseEvent e, ITreeNode treeNode, int columnIndex) {
		if (!(e.stateMask == SWT.CTRL) || e.button != 1 || !Columns.fromInteger(columnIndex).equals(Columns.NAME)
				|| e.count < 2)
			return false;
		if (!(treeNode instanceof TabNode))
			return false;
		final TabNode treeObject = (TabNode) treeNode;
		IEditorPart editor = treeObject.getEditorReference().getEditor(true);
		if (editor instanceof IAdtFormEditor) {
			IProject ABAPProject = getProjectFromProjectChooserDialog();
			if (ABAPProject == null
					|| !AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(ABAPProject).isOK()) {
				return true;

			}

			IAdtFormEditor formEditor = (IAdtFormEditor) editor;
			IFile file = formEditor.getModelFile();
			IAdtObjectReference ref = Adapters.adapt((Object) file, IAdtObjectReference.class);
			AdtNavigationServiceFactory.createNavigationService().navigate(ABAPProject, ref, true);
			return true;
		}
		return false;
	}

	public static IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}
}
