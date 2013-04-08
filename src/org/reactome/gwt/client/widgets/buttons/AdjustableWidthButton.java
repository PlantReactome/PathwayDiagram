/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.buttons;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

/**
 * A button whose exact width can be externally specified.  Normally, buttons
 * will stretch or shrink, according to what they contain.
 * 
 * @author David Croft
 *
 */
public class AdjustableWidthButton extends Button {
	public AdjustableWidthButton(String text) {
		setText(text);
		DOM.setStyleAttribute(getElement(), "height", "25px");
		DOM.setStyleAttribute(getElement(), "cursor", "hand");
		DOM.setStyleAttribute(getElement(), "fontSize", "smaller");
		DOM.setStyleAttribute(getElement(), "color", "black");
	}

	@Override
	public void setWidth(String width) {
		// Set button width - setWidth doesn't work in the current version
		// of GWT, hence this strange hack
		// TODO: switch to setWidth when this is corrected.
		DOM.setStyleAttribute(getElement(), "width", width);
	}
	
	/**
	 * Forces text to be bold.
	 */
	public void setText(String text) {
		setHTML("<B>" + text + "</B>"); // Make button text bold
	}
}
