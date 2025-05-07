package com.abapblog.verticaltabs.tree.labelproviders;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;

import com.abapblog.verticaltabs.views.VTView;

public class ItalicStyler extends Styler {
	private final String fForegroundColorName = JFacePreferences.DECORATIONS_COLOR;
	private final String fBackgroundColorName;
	private static Font fFont = null;

	static {
		Font standardFont = VTView.getFilteredTree().getFont();
		fFont = new Font(standardFont.getDevice(), standardFont.getFontData()[0].getName(),
				standardFont.getFontData()[0].getHeight(), SWT.ITALIC);
	}

	public ItalicStyler() {
		this.fBackgroundColorName = "";

	}

	@Override
	public void applyStyles(TextStyle textStyle) {
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (fForegroundColorName != null) {
			textStyle.foreground = colorRegistry.get(fForegroundColorName);
		}
		if (fBackgroundColorName != null) {
			textStyle.background = colorRegistry.get(fBackgroundColorName);
		}
		textStyle.font = fFont;
	}

}
