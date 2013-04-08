/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;



import com.google.gwt.user.client.ui.Panel;

/**
 * Retrieves the results of a completed analysis.
 *
 * @author David Croft
 */
public class ResultsMonitor extends Monitor {
	public ResultsMonitor(String analysisId, String analysisName, Panel basePanel, ResultsDisplayHandler resultsDisplayHandler) {
		super(analysisId, analysisName, basePanel, "RESULTS");
		setMonitorSubmitCompleteHandler(new ResultsMonitorSubmitCompleteHandler(this, resultsDisplayHandler));
		
		slashSeparatedParams = true; // use Spring MVC server
	}
}
