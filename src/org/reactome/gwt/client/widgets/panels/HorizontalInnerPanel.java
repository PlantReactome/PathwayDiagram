/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.panels;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A horizontal panel that is designed to be used within other panels.
 * Behaves just like a regular HorizontalPanel, but provides a little
 * bit of padding above and below.
 * 
 * The width is also set by default to 100%.
 * 
 * Horizontal alignment is ALIGN_MIDDLE
 * 
 * @author David Croft
 *
 */
public class HorizontalInnerPanel extends HorizontalPanel {
	public HorizontalInnerPanel() {
		super();
		this.setStyleName("horizontal_inner_panel"); // CSS
		this.setWidth("100%");
		this.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	}
}

