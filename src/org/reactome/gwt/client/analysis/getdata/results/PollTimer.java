/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;


import com.google.gwt.user.client.Timer;

/**
 * Monitors a server side process and reacts appropriately to changes in the
 * process' state.
 *
 * @author David Croft
 */
public class PollTimer extends Timer {
	private int pollCount = 0; // Could be used to limit the number of times polling happens
	private int pollingInterval;
	protected Monitor monitor;

	public PollTimer(Monitor monitor, int pollingInterval) {
		this.pollingInterval = pollingInterval;
		this.monitor = monitor;
	}

	public void run() {
		monitor.poll();
	}

	public void schedule() {
		pollCount++;
		schedule(pollingInterval);
	}
}
