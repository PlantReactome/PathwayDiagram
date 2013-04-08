/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;

import com.google.gwt.json.client.JSONObject;


/**
 * 
 * Implement this if you want to handle the display of results information in a uniform way.
 *
 * @author David Croft
 */
public interface ResultsDisplayHandler {
	public void broadcastResults(JSONObject jsonObject); // returns true if load succeeded
	public void showWarningInResultsDisplayPanel(String message);
}
