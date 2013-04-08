/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import org.reactome.gwt.client.services.PathwayOfTheMonthService;
import org.reactome.gwt.client.services.PathwayOfTheMonthServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * Creates the panel for Reactome's pathway of the month.
 * 
 * @author David Croft
 */
public class PathwayOfTheMonthPanel extends HTML {
	/**
	 * @uml.property  name="pathwayOfTheMonthService"
	 * @uml.associationEnd  
	 */
	private final PathwayOfTheMonthServiceAsync pathwayOfTheMonthService = GWT.create(PathwayOfTheMonthService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		this.setStyleName("textbox"); // CSS

		// Dummy initial text, so that we grab maximal page real estate
		pathwayOfTheMonthService.getText(
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
