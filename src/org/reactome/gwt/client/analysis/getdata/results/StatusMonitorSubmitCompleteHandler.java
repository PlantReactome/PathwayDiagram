/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;


import org.reactome.gwt.client.SpringUtils;
import org.reactome.gwt.client.analysis.AnalysisUtils;
import org.reactome.gwt.client.analysis.ClientAnalysisStatus;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

/**
 * Monitors the status of a running analysis.
 *
 * @author David Croft
 */
public class StatusMonitorSubmitCompleteHandler extends MonitorSubmitCompleteHandler {
	public final static String FINISHED_ANALYSIS_NAME = "Finished";
	public final static String WARNING_ANALYSIS_NAME = "Warning";
	private StatusDisplayHandler statusDisplayHandler;
	private Monitor resultsMonitor;

	public StatusMonitorSubmitCompleteHandler(Monitor statusMonitor, Monitor resultsMonitor, StatusDisplayHandler statusDisplayHandler) {
		super(statusMonitor);
		this.resultsMonitor = resultsMonitor;
		this.statusDisplayHandler = statusDisplayHandler;
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		if (event == null) {
			handleWarning("onSubmitComplete: event is null!!");
			return;
		}

		String output = AnalysisUtils.extractResultsFromSubmitCompleteEvent(event);
		if (statusDisplayHandler != null)
			statusDisplayHandler.showStatus(output);
		if (output == null || output.length() == 0)
			pollTimer.schedule();
		else if (isStatusWarning(output))
			handleWarning("Server has returned a warning message, giving up!  Warning=" + output);
		else if (isStatusFinished(output))
			resultsMonitor.poll();
		else
			pollTimer.schedule();
	}

	@Override
	public void handleWarning(String warning) {
		// Pass the warning on to the results handler to be dealt with
		resultsMonitor.handleWarning(warning);
	}
	
	private boolean isStatusFinished(String jsonStatusPacketString) {
		return isStatus(jsonStatusPacketString, FINISHED_ANALYSIS_NAME);
	}
	
	private boolean isStatusWarning(String jsonStatusPacketString) {
		return isStatus(jsonStatusPacketString, WARNING_ANALYSIS_NAME);
	}
	
	private boolean isStatus(String jsonStatusString, String statusName) {
		try {
			JSONObject jsonStatus = SpringUtils.unpackFromSpring((JSONObject)JSONParser.parseStrict(jsonStatusString));
			if (jsonStatus != null && jsonStatus.containsKey("name")) {
				String name = AnalysisUtils.stripQuotes(jsonStatus.get("name").toString());
				if (name.equals(statusName))
					return true;
			}
		} catch (Exception e) {
		}

		return false;
	}
}
