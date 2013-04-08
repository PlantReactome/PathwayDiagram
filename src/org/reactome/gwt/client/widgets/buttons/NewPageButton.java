/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.buttons;

import org.reactome.gwt.client.widgets.tooltip.MobileTooltip;
import org.reactome.gwt.client.widgets.tooltip.MobileTooltipMouseListener;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

/**
 * This button will launch a page.  It is abstract, because the click handler
 * needs to be implemented.  It is supposed to ensure that all page launch buttons
 * look the same, so it should be extended by any page launch button.
 * 
 * @author David Croft
 *
 */
public abstract class NewPageButton extends AdjustableWidthButton {
	private String toolTipText = null;
	
	public NewPageButton(String text) {
		super(text);
		init();
		
		addClickHandler();
	}
	
	/**
	 * Overwrite this if you want to do something special during the construction
	 * of the button
	 */
	protected void init() {
	}
	
	/**
	 * Implement a click handler
	 */
	protected abstract void addClickHandler();
	
	public void setTooltipText(String localToolTipText) {
		toolTipText = localToolTipText;
//		addMouseOverHandler(new MouseOverHandler() {
//			@Override
//			public void onMouseOver(MouseOverEvent event) {
//				String foo = toolTipText;
//			}
//		});
		
		MobileTooltip tooltip = new MobileTooltip(toolTipText);
		addMouseListener(new MobileTooltipMouseListener(tooltip));
		
	}

//	@Override
//	public void setEnabled(boolean enabled) {
//		if (!enabled) {
//			String width = DOM.getStyleAttribute(getElement(), "width");
//			String height = DOM.getStyleAttribute(getElement(), "height");
//			HorizontalPanel snozz = new HorizontalPanel();
////			snozz.add(new Label("Lotta snozz!!"));
//			DOM.setStyleAttribute(snozz.getElement(), "width", width);
//			DOM.setStyleAttribute(snozz.getElement(), "height", height);
//			Node child = snozz.getElement().getChild(0);
//			this.getElement().insertFirst(child);
//		}
//		
//		super.setEnabled(enabled);
//	}
}
