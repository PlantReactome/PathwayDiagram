/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * This button will launch a new external (linked-to URL) page.  It needs
 * to know the URL of the page to be opened, and some text to
 * display in the button, to tell the user knows what it does.
 * 
 * @author David Croft
 *
 */
public class ExternalNewPageButton extends NewPageButton {
	protected String url = null;
	protected String target = "_self"; // use current page by default, for new page set to "_blank"
	
	public ExternalNewPageButton(String url, String text) {
		super(text);
		this.url = url;
	}
	
	public ExternalNewPageButton(String url, String text, String target) {
		this(url, text);
		this.target = target;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * When a user clicks this button, a new URL should be displayed
	 */
	protected void addClickHandler() {
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(url, target, ""); 
			}
		});
	}
}
