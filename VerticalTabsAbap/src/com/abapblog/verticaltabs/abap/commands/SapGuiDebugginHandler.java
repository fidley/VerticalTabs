package com.abapblog.verticaltabs.abap.commands;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;

import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;
import com.sap.adt.sapgui.ui.ISapGui;
import com.sap.adt.sapgui.ui.internal.views.AbstractSapGuiWorkbenchPart;

public class SapGuiDebugginHandler extends ContributionItem {
	private Boolean menuAdded = false;

	public SapGuiDebugginHandler() {
		// TODO Auto-generated constructor stub
	}

	public SapGuiDebugginHandler(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fill(Menu menu, int index) {
		Boolean isABAPTabNode = false;
		final ISelection selection = VTView.getTreeViewer().getSelection();
		final Iterator<?> selIter = ((IStructuredSelection) selection).iterator();
		while (selIter.hasNext()) {
			final Object selObj = selIter.next();
			if (selObj instanceof TabNode) {
				final TabNode treeObject = (TabNode) selObj;
				final ISapGui sapGuiEditorPart = getGuiEditorPart(treeObject.getEditorReference());
				if (sapGuiEditorPart == null) {
					continue;
				}

				if (getParent() instanceof MenuManager && menuAdded == false) {

					MenuManager mm = ((MenuManager) getParent());

					try {
						final Action projectAction = new Action() {
							@Override
							public void run() {
								if (sapGuiEditorPart instanceof AbstractSapGuiWorkbenchPart) {
									AbstractSapGuiWorkbenchPart sapGuiWorkbenchPart = (AbstractSapGuiWorkbenchPart) sapGuiEditorPart;
									sapGuiWorkbenchPart.sendOkCode("/H");
								}
							}
						};

						projectAction.setText("Set breakpoint at next statement");
						mm.add(projectAction);
						menuAdded = true;
					} catch (final Exception e1) {
						e1.printStackTrace();

					}
				}

			}
		}

	}

	public ISapGui getGuiEditorPart(IEditorReference editorReference) {
		IEditorPart editor = editorReference.getEditor(false);
		if (editor instanceof ISapGui) {
			return (ISapGui) editor;
		}
		return null;
	}

}
