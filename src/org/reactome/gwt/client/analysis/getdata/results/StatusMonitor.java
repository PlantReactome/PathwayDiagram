/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;


import com.google.gwt.user.client.ui.Panel;

/**
 * Monitors the status of a running analysis.
 *
 * @author David Croft
 */
public class StatusMonitor extends Monitor {
	public StatusMonitor(String moduleBaseUrl, String analysisId, String analysisName, Panel basePanel, Monitor resultsMonitor, StatusDisplayHandler statusDisplayHandler) {
		super(moduleBaseUrl, analysisId, analysisName, basePanel, "STATUS");
		setMonitorSubmitCompleteHandler(new StatusMonitorSubmitCompleteHandler(this, resultsMonitor, statusDisplayHandler));
	}
}
