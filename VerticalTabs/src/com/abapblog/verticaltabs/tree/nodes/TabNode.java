package com.abapblog.verticaltabs.tree.nodes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.impl.CompositePartImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartSashContainerImpl;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.WorkbenchPartReference;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.TreeContentProvider;

public class TabNode extends TreeNode implements IPropertyListener, Comparable<TabNode> {
	public static final int SPLIT_INDEX_NONE = -1;
	public static final int SPLIT_INDEX_PARENT = 0;
	public static final int SPLIT_INDEX_CLONED = 1;
	private static final String KEY_CLONED_FROM = "Cloned From";
	private static final String KEY_EDITOR_REFERENCE = "org.eclipse.ui.IWorkbenchPartReference";
	private IEditorReference editorReference;
	private IProject project;
	private boolean pinned = false;
	private static Integer biggestIndex = Integer.valueOf(9999);
	private String manualTitle = "";
	private String originalTitle = "";
	private String splitTag = "";
	private boolean archived = false;
	private int splitIndex = SPLIT_INDEX_NONE;
	private IEditorReference clonedFrom = null;
	private IMemento[] closedMementos;
	private final static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	private MElementContainer parentContainer;
	private MPart editorPart;

	public TabNode(IEditorReference editorReference) {
		super(editorReference.getTitle(), editorReference.getTitleImage(), editorReference.getTitleToolTip());
		this.setEditorReference(editorReference);
		editorReference.addPropertyListener(this);
		setProjectAndPath(editorReference);
		setSortIndex(getNextSortIndex());
		setOriginalTitle(editorReference.getTitle());
		setSplitOfEdior(editorReference);
		addUIParts();
	}

	/**
	 * Adds the UI parts to the TabNode, making it possible to restore the tab in
	 * the same position after pinning and opening it again. Works only for fully
	 * loaded editors. First try at creation of the tab, second try is done at close
	 * in method {@link #atClose(IWorkbenchPartReference)}, but may not catch all
	 * cases as the editor may be closed before or with use of Close All/Close
	 * Others actions.
	 */
	private void addUIParts() {
		try {
			editorPart = editorReference.getEditor(false).getSite().getService(MPart.class);
			parentContainer = editorPart.getParent();
		} catch (Exception e) {
			// Editor is not loaded yet. Stay calm there are other ways to get the editor
			// part
			try {
				if (editorReference instanceof WorkbenchPartReference) {
					editorPart = ((WorkbenchPartReference) editorReference).getModel();
					parentContainer = editorPart.getParent();
				}
			} catch (Exception e2) {
			}
		}
	}

	private void setSplitOfEdior(IEditorReference editorReference) {
		if (editorReference.getPart(false) instanceof IEditorPart) {
			IEditorPart ep = (IEditorPart) editorReference.getPart(false);
			MPart editorPart = ep.getSite().getService(MPart.class);
			checkSplitOfAnEditor(editorPart);
		}
		if (editorReference instanceof WorkbenchPartReference) {
			MPart editorPart = ((WorkbenchPartReference) editorReference).getModel();
			checkSplitOfAnEditor(editorPart);
		}
	}

	private void checkSplitOfAnEditor(MPart editorPart) {
		Object editorParent = editorPart.getParent();
		checkSplitOfEditorAtEclipseStart(editorPart, editorParent);
		checkSplitOfEditorAtManualAction(editorPart, editorParent);
	}

	private void checkSplitOfEditorAtManualAction(MPart editorPart, Object editorParent) {
		if (editorParent instanceof CompositePartImpl) {
			PartImpl clonedFromPart = (PartImpl) editorPart.getTransientData().get(KEY_CLONED_FROM);
			if (clonedFromPart != null) {
				if (!store.getBoolean(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS))
					throw new SplittedEditorTabNotAllowedException("Splitted Tab Not Allowed. Change configuration.");
				setNodeSplitTag(editorPart);
				setSplitIndex(SPLIT_INDEX_CLONED);
				IEditorReference clonedFromER = (IEditorReference) clonedFromPart.getTransientData()
						.get(KEY_EDITOR_REFERENCE);
				if (clonedFromER != null) {
					setClonedFrom(clonedFromER);
					updateClonedTab(clonedFromER);

				}
			}

		}
	}

	private void updateClonedTab(IEditorReference clonedFromER) {
		NodesFactory nf = TreeContentProvider.getNodesFactory();
		try {
			TabNode tn = nf.getTabNode(clonedFromER);
			tn.setSplitTag(getSplitTag());
			tn.setSplitIndex(SPLIT_INDEX_PARENT);
			nf.getSplitTabNodes().put(this, tn);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void setNodeSplitTag(MPart editorPart) {
		if (editorPart.getTags().contains(IPresentationEngine.SPLIT_VERTICAL)) {
			setSplitTag(IPresentationEngine.SPLIT_VERTICAL);
		}
		if (editorPart.getTags().contains(IPresentationEngine.SPLIT_HORIZONTAL)) {
			setSplitTag(IPresentationEngine.SPLIT_HORIZONTAL);
		}
	}

	private void checkSplitOfEditorAtEclipseStart(MPart editorPart, Object editorParent) {
		int index = SPLIT_INDEX_NONE;
		if (editorParent instanceof PartSashContainerImpl) {
			PartSashContainerImpl partSashContainer = (PartSashContainerImpl) editorParent;
			index = partSashContainer.getChildren().indexOf(editorPart);
			setSplitIndex(index);
			setNodeSplitTag(editorPart);
			if (index == SPLIT_INDEX_CLONED) {
				if (!store.getBoolean(PreferenceConstants.SEPARATE_TABS_FOR_SPLITTED_EDITORS))
					throw new SplittedEditorTabNotAllowedException("Splitted Tab Not Allowed. Change configuration.");
				IEditorReference clonedFromER = (IEditorReference) partSashContainer.getChildren().get(0)
						.getTransientData().get(KEY_EDITOR_REFERENCE);
				if (clonedFromER != null) {
					setClonedFrom(clonedFromER);
					updateClonedTab(clonedFromER);
				}
			}

		}
	}

	private void setProjectAndPath(IEditorReference editorReference) {

		IEditorInput editorInput;
		try {
			editorInput = editorReference.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				setProjectAndPathForIFileEditorInput(editorInput);

			} else {
				setProject(editorInput.getAdapter(IProject.class));
				if (getProject() != null) {
					setProjectName(getProject().getName());
				}
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	private void setProjectAndPathForIFileEditorInput(IEditorInput editorInput) {
		try {

			IFileEditorInput input = (IFileEditorInput) editorInput;
			IFile file = input.getFile();
			setProject(file.getProject());
			setProjectName(getProject().getName());
			file.getFullPath();
			if (file.getFullPath() != null)
				setPath(file.getFullPath().toString());
		} catch (Exception e) {

			try {
				FileStoreEditorInput uriEditor = (FileStoreEditorInput) editorInput;

				setPath((new java.io.File(uriEditor.getURI()).getAbsolutePath()));
			} catch (Exception e1) {
				// e1.printStackTrace();
			}

		}
	}

	@Override
	public String getPath() {
		if (super.getPath() == "") {
			try {
				setProjectAndPathForIFileEditorInput(editorReference.getEditorInput());
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return super.getPath();
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.TAB;
	}

	@Override
	public boolean isOpenable() {
		return true;
	}

	public boolean isClosed() {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			try {
				if (editorReference == editorReferences[i])
					return false;
			} catch (Exception e) {

			}
		}
		return true;

	}

	@Override
	public boolean isExpandable() {
		return false;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	public IEditorReference getEditorReference() {
		return editorReference;
	}

	private void setEditorReference(IEditorReference editorReference) {
		this.editorReference = editorReference;
	}

	@Override
	public void open() {
		IWorkbenchPart part = getEditorReference().getPart(true);

		if (part != null) {
			try {
				IEditorInput[] ei = { editorReference.getEditorInput() };
				String[] editorIDs = { editorReference.getId() };
				if (splitTag.isEmpty()) {
					addToOldUIPart();
					part.getSite().getPage().openEditors(ei, editorIDs, closedMementos, IWorkbenchPage.MATCH_INPUT, 0);
				} else {
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					activePage.activate(part);
				}
			} catch (Exception e) {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (activePage != null) {
					activePage.activate(part);
				}

			}
		}
	}

	private void addToOldUIPart() {

		if (parentContainer != null && editorPart != null && !parentContainer.getChildren().contains(editorPart)
				&& !parentContainer.getChildren().isEmpty()) {
			if (splitTag.isEmpty()) {
				editorPart.getParent().getChildren().remove(editorPart);
				parentContainer.getChildren().add(editorPart);
			}
		}
	}

	@Override
	public boolean isPinable() {
		return true;
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		updateTitleAtChange(source, propId);
		updateTilteWhenDirty(source, propId);
	}

	private void updateTilteWhenDirty(Object source, int propId) {
		if (propId == IWorkbenchPartConstants.PROP_DIRTY) {
			IWorkbenchPart part = (IWorkbenchPart) source;
			if (part instanceof IEditorPart) {
				if (getEditorReference().isDirty()) {
					setOriginalTitle("*" + part.getTitle());
					if (!getManualTitle().equals("") && !getManualTitle().substring(0, 1).equals("*"))
						setManualTitle("*" + getManualTitle());
				} else {
					setOriginalTitle(part.getTitle());

					if (getManualTitle().length() > 0 && getManualTitle().substring(0, 1).equals("*"))
						setManualTitle(getManualTitle().substring(1));

				}
				TreeContentProvider.refreshTree();
			}

		}
	}

	private void updateTitleAtChange(Object source, int propId) {
		if (propId == IWorkbenchPartConstants.PROP_TITLE) {
			IWorkbenchPart part = (IWorkbenchPart) source;
			if (part instanceof IEditorPart) {
				getEditorReference().getTitleToolTip();
				if (getEditorReference().isDirty()) {
					setOriginalTitle("*" + part.getTitle());
					if (!getManualTitle().equals("") && !getManualTitle().substring(0, 1).equals("*"))
						setManualTitle("*" + getManualTitle());
				} else {
					setOriginalTitle(part.getTitle());
					if (getManualTitle().length() > 0 && getManualTitle().substring(0, 1).equals("*"))
						setManualTitle(getManualTitle().substring(1));

				}

				Display.getCurrent().asyncExec(() -> {
					try {
						Thread.sleep(200);
						setImage(getEditorReference().getTitleImage());
						TreeContentProvider.refreshTree();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

			}

		}
	}

	@Override
	public boolean isPinned() {
		return pinned;
	}

	@Override
	public void pin() {
		pinned = true;
	}

	@Override
	public void unpin() {
		pinned = false;
	}

	public void updateFromEditorReferenece() {
		setOriginalTitle(editorReference.getTitle());
		setImage(editorReference.getTitleImage());
		setProjectAndPath(editorReference);
		setSplitOfEdior(editorReference);
	}

	public IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	public static Integer getNextSortIndex() {
		biggestIndex += 1;
		return biggestIndex;
	}

	@Override
	public int compareTo(TabNode o) {
		if (o == null)
			return 0;
		return getSortIndex().compareTo(o.getSortIndex());
	}

	public String getManualTitle() {
		if (manualTitle == null)
			manualTitle = "";
		return manualTitle;
	}

	public void setManualTitle(String manualTitle) {
		this.manualTitle = manualTitle;
	}

	@Override
	public String getTitle() {
		if (getManualTitle().equals(""))
			return getOriginalTitle();
		return getManualTitle();
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		if (editorReference.isDirty() && originalTitle.length() > 0 && !originalTitle.substring(0, 1).equals("*")) {
			originalTitle = "*" + originalTitle;
		} else {
			this.originalTitle = originalTitle;
		}
	}

	public String getSplitTag() {
		return splitTag;
	}

	public void setSplitTag(String splitTag) {
		this.splitTag = splitTag;
	}

	public int getSplitIndex() {
		return splitIndex;
	}

	public void setSplitIndex(int splitIndex) {
		this.splitIndex = splitIndex;
	}

	public String getSplitTagDisplayName() {
		switch (splitIndex) {
		case SPLIT_INDEX_CLONED:
			switch (splitTag) {
			case IPresentationEngine.SPLIT_HORIZONTAL:
				return "Bottom";
			case IPresentationEngine.SPLIT_VERTICAL:
				return "Right";
			default:
				return "";
			}
		case SPLIT_INDEX_PARENT:
			switch (splitTag) {
			case IPresentationEngine.SPLIT_HORIZONTAL:
				return "Top";
			case IPresentationEngine.SPLIT_VERTICAL:
				return "Left";
			default:
				return "";
			}
		case SPLIT_INDEX_NONE:
			return "";
		}

		return "";
	}

	public IEditorReference getClonedFrom() {
		return clonedFrom;
	}

	private void setClonedFrom(IEditorReference clonedFrom) {
		this.clonedFrom = clonedFrom;
	}

	private void SetClosedMementos(IMemento[] memento) {
		closedMementos = memento;
	}

	public IMemento[] getClosedMementos() {
		return closedMementos;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public void atClose(IWorkbenchPartReference partRef) {
		addUIParts();
		addClosedMemento(partRef);
	}

	private void addClosedMemento(IWorkbenchPartReference partRef) {
		IEditorReference[] editors = { editorReference };
		IMemento[] memento = partRef.getPage().getEditorState(editors, true);
		SetClosedMementos(memento);
	}

}
