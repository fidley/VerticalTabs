package com.abapblog.verticaltabs.tree.filters;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

import com.abapblog.verticaltabs.Activator;
import com.abapblog.verticaltabs.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.nodes.TabNode;

public class TreePatternFilter extends PatternFilter implements ModifyListener, FocusListener, MouseListener {
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private static final String DEFAULT_PATTERN = "*";

	public TreePatternFilter() {
		setIncludeLeadingWildcard(true);
		setPattern(DEFAULT_PATTERN);

	}

	@Override
	protected boolean isLeafMatch(final Viewer viewer, final Object element) {

		boolean isMatch = false;
		if (element instanceof TabNode) {
			TabNode leaf = (TabNode) element;
			isMatch |= wordMatches(leaf.getTitle());
			if (!isMatch)
				return isMatch;
			if (preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_DIRTY_EDITORS)
					&& leaf.getEditorReference().isDirty()
					|| !preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_DIRTY_EDITORS)) {
				isMatch = true;
			} else {
				isMatch = false;
			}
		}

		Boolean leafMatched = callIsLeafExtension(viewer, element, isMatch);
		if (leafMatched != null) {
			isMatch = leafMatched;
		}

		return isMatch;

	}

//	public boolean isElementVisible(Viewer viewer, Object element) {
////		Boolean isVisible = super.isElementVisible(viewer, element);
////		if (!isVisible)
////			return isVisible;
////
////		if (element instanceof TabNode) {
////			TabNode leaf = (TabNode) element;
////			if (preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_DIRTY_EDITORS)
////					&& leaf.getEditorReference().isDirty()
////					|| !preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_DIRTY_EDITORS)) {
////				isVisible = true;
////			} else {
////				isVisible = false;
////			}
////		}
////
////		if (isVisible) {
////			Boolean visible = callIsElementVisibleExtension(viewer, element);
////			if (visible != null) {
////				isVisible = visible;
////			}
////		}
////		return isVisible;
//	}

	@Override
	public boolean isFilterProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return super.isFilterProperty(element, property);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Text filterText = (Text) e.widget;
		if (filterText.getText().isEmpty()) {
			setPattern(DEFAULT_PATTERN);
			filterText.setText(DEFAULT_PATTERN);
			filterText.selectAll();
		}
		;
	}

	public void widgetSelected(SelectionEvent e) {
		Text filterText = (Text) e.widget;
		if (filterText.getText().equals(DEFAULT_PATTERN)) {
			filterText.selectAll();
		}
		;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		Text filterText = (Text) e.widget;
		if (filterText.getText().isEmpty()) {
			setPattern(DEFAULT_PATTERN);
			filterText.setText(DEFAULT_PATTERN);
			filterText.selectAll();
			return;
		}
		if (filterText.getText().equals(DEFAULT_PATTERN)) {
			filterText.selectAll();
		}
		;

	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDown(MouseEvent e) {
		Text filterText = (Text) e.widget;
		if (filterText.getText().equals(DEFAULT_PATTERN)) {
			filterText.selectAll();
		}
		;
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private Boolean callIsLeafExtension(final Viewer viewer, final Object element, final Boolean orginalMatch) {

		IConfigurationElement[] config = RegistryFactory.getRegistry()
				.getConfigurationElementsFor(IPaternFilterExtension.PATTERN_FILTER_EXTENSION_ID);
		try {
			for (IConfigurationElement ce : config) {
				final Object o = ce.createExecutableExtension("class");
				if (o instanceof IPaternFilterExtension) {
					IPaternFilterExtension paternFilterExtension = (IPaternFilterExtension) o;
					return paternFilterExtension.isLeafMatch(viewer, element, orginalMatch);
//
//					ISafeRunnableWithResult runnable = new ISafeRunnableWithResult<Boolean>() {
//						@Override
//						public void handleException(Throwable er) {
//							System.out.println("Exception in client");
//						}
//
//						@Override
//						public Boolean runWithResult() throws Exception {
//
//							Boolean leafMatched = ((IPaternFilterExtension) o).isLeafMatch(viewer, element,
//									orginalMatch);
//							return leafMatched;
//
//						}
//					};
//
//					Boolean leafMatched = (Boolean) SafeRunner.run(runnable);
//					if (leafMatched != null) {
//						return leafMatched;
//					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

//	private Boolean callIsElementVisibleExtension(final Viewer viewer, final Object element) {
//
//		IConfigurationElement[] config = RegistryFactory.getRegistry()
//				.getConfigurationElementsFor(IPaternFilterExtension.PATTERN_FILTER_EXTENSION_ID);
//		try {
//			for (IConfigurationElement ce : config) {
//				final Object o = ce.createExecutableExtension("class");
//				if (o instanceof IPaternFilterExtension) {
//					ISafeRunnableWithResult runnable = new ISafeRunnableWithResult<Boolean>() {
//						@Override
//						public void handleException(Throwable er) {
//							System.out.println("Exception in client");
//						}
//
//						@Override
//						public Boolean runWithResult() throws Exception {
//
//							Boolean visible = ((IPaternFilterExtension) o).isElementVisible(viewer, element);
//							return visible;
//
//						}
//					};
//
//					Boolean visible = (Boolean) SafeRunner.run(runnable);
//					if (visible != null) {
//						return visible;
//					}
//				}
//			}
//		} catch (CoreException ex) {
//			System.out.println(ex.getMessage());
//		}
//		return null;
//	}
}