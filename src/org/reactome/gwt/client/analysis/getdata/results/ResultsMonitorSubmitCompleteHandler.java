/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import org.reactome.gwt.client.SpringUtils;
import org.reactome.gwt.client.analysis.AnalysisUtils;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

/**
 * Monitors the results of a completed analysis.
 *
 * @author David Croft
 */
public class ResultsMonitorSubmitCompleteHandler extends MonitorSubmitCompleteHandler {
	private ResultsDisplayHandler resultsDisplayHandler;

	public ResultsMonitorSubmitCompleteHandler(Monitor resultsMonitor, ResultsDisplayHandler resultsDisplayHandler) {
		super(resultsMonitor);
		this.resultsDisplayHandler = resultsDisplayHandler;
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		if (event == null) {
			handleWarning("ResultsMonitorSubmitCompleteHandler.onSubmitComplete: event is null!!");
			return;
		}

		String output = AnalysisUtils.extractResultsFromSubmitCompleteEvent(event);
		if (output == null || output.length() == 0)
			handleWarning("ResultsMonitorSubmitCompleteHandler.onSubmitComplete: null or empty output, aborting!");
		else if (output.startsWith("<h2>HTTP ERROR"))
			handleWarning("ResultsMonitorSubmitCompleteHandler.onSubmitComplete: output is an error message: " + output);
		else {
			// Pass an object rather than a string, because Javascript
			// will use an object reference, whereas a string will be
			// duplicated.
			try {
				JSONValue jsonValue = JSONParser.parseStrict(output);
				JSONObject jsonObject = jsonValue.isObject();
				if (jsonObject == null)
					handleWarning("ResultsMonitorSubmitCompleteHandler.onSubmitComplete: could not extract JSONObject from output=" + output);
				else {
					jsonObject = SpringUtils.unpackFromSpring(jsonObject);
					resultsDisplayHandler.broadcastResults(jsonObject);
				}
			} catch (Exception e) {
				handleWarning("ResultsMonitorSubmitCompleteHandler.onSubmitComplete: problem with JSON, exception=" + e.getMessage() + ", output=" + output);
				e.printStackTrace(System.err);
			}
		}
	}

	public void handleWarning(String warning) {
		resultsDisplayHandler.showWarningInResultsDisplayPanel(warning);
	}
}
