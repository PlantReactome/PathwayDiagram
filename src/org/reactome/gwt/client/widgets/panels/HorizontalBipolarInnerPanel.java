/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.panels;

import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Inherits from HorizontalInnerPanel, implements two special methods:
 * addLeft and addRight, which allow you to add widgets on the far left
 * and the far right respectively.
 * 
 * If you are using this panel to create a simple form, you can use the
 * "addHidden" method to add hidden parameters.
 * 
 * @author David Croft
 *
 */
public class HorizontalBipolarInnerPanel extends HorizontalInnerPanel {
	public void addLeft(Widget w) {
		// No real magic here, this relies on the default ALIGN_LEFT
		// behavior of HorizontalPanel
		super.add(w);
	}
	
	public void addRight(Widget w) {
		HorizontalPanel widgetPanel = new HorizontalPanel();
		widgetPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		widgetPanel.setWidth("100%");
		widgetPanel.add(w);
		super.add(widgetPanel);
	}

	public void addHidden(String name, String value) {
		super.add(new Hidden(name, value));
	}

	/**
	 * Has no effect in this class.
	 */
	@Override
	public void add(Widget w) {
	}
}
