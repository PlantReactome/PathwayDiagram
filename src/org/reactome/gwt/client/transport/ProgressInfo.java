/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.transport;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transport class for passing progress data from server to client.
 * Also provides a few utility methods.
 * 
 * @author David Croft
 *
 */

public class ProgressInfo implements IsSerializable {
	public Float progress = new Float(0.0);
	public String comment = null;
	private Long timer = null;
	
	public ProgressInfo() {
		initialize();
	}

	public void setProgress(float value) {
		progress = new Float(value);
	}

	public void incrementProgress(float value) {
		if (((Float)progress).floatValue() < 100.0)
			progress = new Float(((Float)progress).floatValue() + value);
	}

	private void initialize() {
		setProgress(0);
		comment = "Initializing...";
		timer = new Long(System.currentTimeMillis());
	}

	public void finalizeProgress() {
		progress = new Float(100.0);
	}
	
	public Long getElapsedTime() {
		return new Long(System.currentTimeMillis() - timer.longValue());
	}
	
	public String getReport() {
		String report = "";
		if (isFinished())
			report += " finished";
		else
			report += " incomplete";
		report +=  ", (" + progress + "%)";
		report +=  ", execution time: " + getElapsedTime() + " milliseconds";
		
		return report;
	}
	
	public boolean isFinished() {
		if (progress.floatValue() == 100.0)
			return true;
		
		return false;
	}
}
