/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * Monitors a running analysis.
 *
 * @author David Croft
 */
public abstract class MonitorSubmitCompleteHandler implements FormPanel.SubmitCompleteHandler {
	protected PollTimer pollTimer;

	public MonitorSubmitCompleteHandler(Monitor monitor) {
		this.pollTimer = new PollTimer(monitor, 5000);
	}

	public abstract void handleWarning(String warning);
}
