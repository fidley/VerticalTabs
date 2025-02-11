package com.abapblog.verticaltabs.abap.extensions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;

import com.abapblog.verticaltabs.abap.Activator;
import com.abapblog.verticaltabs.abap.nodes.AbapTabNode;
import com.abapblog.verticaltabs.abap.preferences.PreferenceConstants;
import com.abapblog.verticaltabs.tree.filters.IPaternFilterExtension;
import com.sap.adt.tools.core.model.adtcore.AdtVersionEnum;

public class PatternFilterExtensionHandler implements IPaternFilterExtension {
	private static final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public PatternFilterExtensionHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean isLeafMatch(Viewer viewer, Object element, Boolean originalMatch) {
		Boolean isInactive = false;
		Boolean isLocked = false;
		try {
			if (!preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_INACTIVE_EDITORS)
					&& !preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_LOCKED_EDITORS))
				return originalMatch;
			if (!(element instanceof AbapTabNode)) {
				return false;
			}
			AbapTabNode abapTabNode = (AbapTabNode) element;
			if (preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_INACTIVE_EDITORS)) {
				AdtVersionEnum activationState = abapTabNode.getActivationState();
				isInactive = isInactive(activationState);

			}
			if (preferenceStore.getBoolean(PreferenceConstants.SHOW_ONLY_LOCKED_EDITORS)) {
				isLocked = abapTabNode.isLocked();
			}
			if (isInactive || isLocked) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return originalMatch;
		}
	}

	@Override
	public Boolean isElementVisible(Viewer viewer, Object element, Boolean originalVisible) {
		return isLeafMatch(viewer, element, originalVisible);
	}

	private boolean isInactive(AdtVersionEnum activationState) {
		if (activationState != null && (activationState.equals(AdtVersionEnum.INACTIVE)
				|| activationState.equals(AdtVersionEnum.INITIAL) || activationState.equals(AdtVersionEnum.NEW)))
			return true;
		return false;
	}

}
