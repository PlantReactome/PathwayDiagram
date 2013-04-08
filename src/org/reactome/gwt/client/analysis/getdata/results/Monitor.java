/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Monitors a server side process and reacts appropriately to changes in the
 * process' state.
 *
 * @author David Croft
 */
public abstract class Monitor {
	private Panel hostPanel;
	private String command;
	private String analysisId;
	private String analysisName;
	private MonitorSubmitCompleteHandler monitorSubmitCompleteHandler;
	protected boolean slashSeparatedParams = true; // use Spring MVC server
	protected String moduleBaseUrl = null;
	
	public Monitor(String moduleBaseUrl, String analysisId, String analysisName, Panel hostPanel, String command) {
		this.moduleBaseUrl = moduleBaseUrl;
		this.analysisId = analysisId;
		this.analysisName = analysisName;
		this.hostPanel = hostPanel;
		this.command = command;
	}

	public void setMonitorSubmitCompleteHandler(MonitorSubmitCompleteHandler monitorSubmitCompleteHandler) {
		this.monitorSubmitCompleteHandler = monitorSubmitCompleteHandler;
	}

	public void setSlashSeparatedParams(boolean slashSeparatedParams) {
		this.slashSeparatedParams = slashSeparatedParams;
	}

	/**
	 * Poll the server for results or status information.
	 * Sends a name/value pair to the servlet specified by the URL in 'action'.
	 * The hostPanel is used as an attachment point for the form, but the form
	 * itself will be invisible.  You must also supply a handler that will deal
	 * with the results of the command.
	 */
	public void poll() {
		if (!hostPanel.isAttached())
			System.err.println("Monitor.sendCommandToServlet: WARNING - hostPanel is not attached to anything, the form will probably not be able to connect to the server");
		FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		String action = null;
		if (slashSeparatedParams) {
			form.setMethod(FormPanel.METHOD_GET);
			action = moduleBaseUrl + "service/analysis/" + command.toLowerCase() + "/" + analysisId + "/" + analysisName;
			form.setAction(action);
		} else {
			form.setMethod(FormPanel.METHOD_POST);
			SimplePanel hiddenPanel = new SimplePanel();
			String value = analysisId;
			if (analysisName != null)
				value += "__" + analysisName;
			hiddenPanel.add(new Hidden(command, value));
			form.add(hiddenPanel);
			action = moduleBaseUrl + "analysis";
			form.setAction(action);
		}
		hostPanel.add(form);
		form.addSubmitCompleteHandler(monitorSubmitCompleteHandler);
		form.submit();
	}

	public void handleWarning(String warning) {
		monitorSubmitCompleteHandler.handleWarning(warning);
	}
}
