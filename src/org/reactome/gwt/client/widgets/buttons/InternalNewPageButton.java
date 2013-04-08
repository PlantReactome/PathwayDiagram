/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.buttons;

import org.reactome.gwt.client.ReactomeGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * This button will launch a new internal (Reactome GWT) page.  It needs
 * to know the controller to do the actual launching, the name of the
 * class which generates the page, and a snappy little piece of text to
 * display in the button, so that the user knows what it does.
 * 
 * @author David Croft
 *
 */
public class InternalNewPageButton extends NewPageButton {
	private ReactomeGWT controller = null;
	private String pageName = null;
	
	public InternalNewPageButton(ReactomeGWT controller, String pgeName, String text, String title) {
		super(text);
		this.pageName = pgeName;
		this.controller = controller;
		if(title!=null) setTitle(title);
	}
	
	/**
	 * When a user clicks this button, a new Reactome GWT page should be displayed
	 */
	protected void addClickHandler() {
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.createPage(pageName, null);
			}
		});
	}
}
