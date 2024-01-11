package com.abapblog.verticaltabs.abap.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.verticaltabs.tree.nodes.TabNode;
import com.abapblog.verticaltabs.views.VTView;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;

public class OpenInProjectDynamic extends ContributionItem {
	private Boolean menuAdded = false;

	public OpenInProjectDynamic() {
		// TODO Auto-generated constructor stub
	}

	public OpenInProjectDynamic(String id) {
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
				IEditorPart editor = treeObject.getEditorReference().getEditor(true);
				if (editor instanceof IAdtFormEditor) {
					IAdtFormEditor formEditor = (IAdtFormEditor) editor;
					IFile file = formEditor.getModelFile();
					IAdtObjectReference ref = Adapters.adapt((Object) file, IAdtObjectReference.class);
					isABAPTabNode = true;
				}
			}
		}

		if (isABAPTabNode == false) {
			return;
		}

		if (getParent() instanceof MenuManager && menuAdded == false) {

			MenuManager mm = ((MenuManager) getParent());

			final MenuManager subMenu = new MenuManager("Open in project", null);

			for (final IProject ABAPProject : getABAPProjects()) {

				try {
					final Action projectAction = new Action() {
						@Override
						public void run() {

							if (!AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(ABAPProject).isOK()) {
								return;
							}

							final ISelection selection = VTView.getTreeViewer().getSelection();
							final Iterator<?> selIter = ((IStructuredSelection) selection).iterator();
							while (selIter.hasNext()) {
								final Object selObj = selIter.next();
								if (selObj instanceof TabNode) {
									final TabNode treeObject = (TabNode) selObj;
									IEditorPart editor = treeObject.getEditorReference().getEditor(true);
									if (editor instanceof IAdtFormEditor) {
										IAdtFormEditor formEditor = (IAdtFormEditor) editor;
										IFile file = formEditor.getModelFile();
										IAdtObjectReference ref = Adapters.adapt((Object) file,
												IAdtObjectReference.class);
										AdtNavigationServiceFactory.createNavigationService().navigate(ABAPProject, ref,
												true);
									}
								}
							}
						}
					};
					projectAction.setText(ABAPProject.getName());
					projectAction.setToolTipText(ABAPProject.getName());
					projectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
							.getImageDescriptor(ISharedImages.IMG_OBJ_PROJECT));

					subMenu.add(projectAction);
				} catch (final Exception e1) {
					e1.printStackTrace();

				}
				mm.add(subMenu);
				menuAdded = true;
			}
		}
	}

	public static List<IProject> getABAPProjects() {
		List<IProject> projectList = new LinkedList<IProject>();

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			try {
				if (project.hasNature(IAdtCoreProject.ABAP_PROJECT_NATURE)) {
					projectList.add(project);
				}
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}

		return projectList;
	}

}
