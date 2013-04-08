/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import org.reactome.gwt.client.services.TutorialService;
import org.reactome.gwt.client.services.TutorialServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * Creates the panel for a Reactome tutorial.
 *
 * @author David Croft
 */
public class TutorialPanel extends HTML {
	private TutorialServiceAsync tutorialService = GWT.create(TutorialService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		this.setStyleName("textbox"); // CSS

		// Dummy initial text, so that we grab maximal page real estate
		tutorialService.getText(
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						setHTML("Remote procedure call failure: " + caught.getMessage());
					}

					public void onSuccess(String result) {
						setHTML(result);
					}
				});
	}
}
