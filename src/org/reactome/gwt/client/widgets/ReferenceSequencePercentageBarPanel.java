/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import java.util.HashMap; 
import java.util.List;
import java.util.Map;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.analysis.AnalysisUtils;
import org.reactome.gwt.client.analysis.Constants;
import org.reactome.gwt.client.analysis.PathwayFormPage;
import org.reactome.gwt.client.widgets.sortableTable.CellClickCatcher;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel; 


/**
 * Graphical bar for displaying percentage values.
 * 
 * Implements Comparable, so that it can be incorporated into SortableTable.
 * 
 * The ProgressBar is wrapped in a VerticalPanel, because a naked ProgressBar
 * does not display properly in a table cell.
 * 
 * @author David Croft
 */
public class ReferenceSequencePercentageBarPanel extends HorizontalPanel implements Comparable,CellClickCatcher { 
	private ProgressBar progressBar;
	private double percent = 0.0;
	private List<String> referenceSequenceDbIds = null;
	private ReactomeGWT controller;
	private Panel basePanel;
	private String pathwayFormPageAction = null;
	private String pathwayFormPageNextPage = null;
	
	public ReferenceSequencePercentageBarPanel(Panel basePanel, double percent, List<String> referenceSequenceDbIds, ReactomeGWT controller) {
		super();
		if (percent < 0.0)
			percent = 0.0;
		if (percent > 100.0)
			percent = 100.0;
		this.percent = percent;
		this.referenceSequenceDbIds = referenceSequenceDbIds;
		this.controller = controller;
		this.basePanel = basePanel;
		
		PathwayFormPage pathwayFormPage = new PathwayFormPage(controller);
		pathwayFormPageAction = pathwayFormPage.getAction();
		pathwayFormPageNextPage = pathwayFormPage.getNextPage();

		progressBar = new ProgressBar(0.0, 100.0, percent);
		progressBar.setStyleName("comparison-ProgressBar-shell");
		progressBar.setWidth("100px");
		
		add(progressBar);
	}
	
	/**
	 * Does something when a user clicks on the percentage.
	 */
	protected void percentageBarAction() {
		String query = "";
		for (String referenceSequenceDbId: referenceSequenceDbIds) {
			if (!query.isEmpty())
				query += "\n";
			query += referenceSequenceDbId;
		}
			
		Map<String,String> params = new HashMap<String,String>();
		params.put("QUERY", query);
		
		sendCommandToServlet(pathwayFormPageAction, params, formSubmitCompleteHandler());
	}

	/**
	 * Run a command on the servlet specified by the action.  Use params
	 * to specify the command.  You must also supply a handler that will deal
	 * with the results of the command.
	 * 
	 * @param action
	 * @param params
	 * @param monitorSubmitCompleteHandler
	 */
	public void sendCommandToServlet(String action, Map<String,String> params, FormPanel.SubmitCompleteHandler monitorSubmitCompleteHandler) {
		// Create a form with a name/value pair, supplied via the params argument.
		FormPanel formPanel = FormUtils.formCreator(basePanel, action, params, null, "invisible");
		formPanel.addSubmitCompleteHandler(monitorSubmitCompleteHandler);
		
		// Execute the command.  monitorSubmitCompleteHandler should take
		// care of dealing with the results that are returned.
		formPanel.submit();
	}
	
	/**
	 *  Deal with the event generated when form submission is complete.
	 *  This gets the ID for the analysis from the server and passes it
	 *  on to the results page.  It also switches to the next page.
	 */
	protected FormPanel.SubmitCompleteHandler formSubmitCompleteHandler() {
		return new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (event == null) {
					ReactomePopup popup = new ReactomePopup();
					popup.setText("event == null, this indicates a problem with the connection to the server");
					popup.setWidth("500px");
					popup.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);

					return;
				}
				HashMap<String,Object> state = new HashMap<String,Object>();
				String results = AnalysisUtils.extractResultsFromSubmitCompleteEvent(event);
				if (results == null) {
					ReactomePopup popup = new ReactomePopup();
					popup.setText("results == null, this indicates a problem on the server");
					popup.setWidth("500px");
					popup.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
				} else if (results.length() == 0) {
					ReactomePopup popup = new ReactomePopup();
					popup.setText("results.length() == 0, this indicates a problem on the server");
					popup.setWidth("500px");
					popup.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
				} else {
					String analysisId = AnalysisUtils.stripQuotes(results);
					
					state.put("fadeDbIds", "1");
					state.put("qualifyNumericalIdentifiers", "0");
//					state.put("title", "Which IDs from expression analysis are in \"" + pathwayName + "\"?");
					state.put("title", "Which IDs from expression analysis are in your pathway?");
					state.put(Constants.ANALYSIS_ID_KEY, analysisId);
					state.put(Constants.ANALYSIS_ACTION_URL_KEY, pathwayFormPageAction);
					controller.createPage(pathwayFormPageNextPage, state);
				}
			}
		};
	}
	
	@Override
	public int compareTo(Object arg0) {
		ReferenceSequencePercentageBarPanel otherPercentageBar = (ReferenceSequencePercentageBarPanel)arg0;
		double otherPercent = otherPercentageBar.getPercent();
		if (otherPercent < percent)
			return (-1);
		else if (otherPercent > percent)
			return 1;
		else
			return 0;
	}
	
	public double getPercent() {
		return percent;
	}

	public String getHTML() {
		return getElement().getInnerHTML();
	}

	@Override
	public void click() {
		percentageBarAction();
	}
}
