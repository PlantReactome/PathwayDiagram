/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import org.reactome.gwt.client.services.NewsService;
import org.reactome.gwt.client.services.NewsServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates the panel for Reactome news and notes.
 *
 * @author David Croft
 */
public class NewsPanel extends VerticalPanel { 
	private HTML text = new HTML();
	private final NewsServiceAsync newsService = GWT.create(NewsService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		this.setStyleName("textbox"); // CSS

		this.add(text);
		
		// Dummy initial text, so that we grab maximal page real estate
		newsService.getText(
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						text.setHTML("Remote procedure call failure: " + caught.getMessage());
					}

					public void onSuccess(String result) {
						text.setHTML(result);
					}
				});
	}
}
