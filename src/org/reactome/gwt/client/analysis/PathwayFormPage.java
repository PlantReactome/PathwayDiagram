/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.services.PathwayAnalysisDataExampleService;

import com.google.gwt.core.client.GWT;

/**
 * Creates page for uploading pathway analysis data.
 * 
 * The state hash for this page contains the following items:
 * 
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class PathwayFormPage extends FormPage {
	public PathwayFormPage(ReactomeGWT controller) {
    	super(controller);
		setTitle("Upload your identifiers for analysis");
		setDataExampleService(GWT.create(PathwayAnalysisDataExampleService.class));
		setAnalysisName(Constants.PATHWAY_ANALYSIS_SET_NAME);
		setAction(GWT.getModuleBaseURL() + Constants.ANALYSIS_FORM_ACTION);
		setNextPage("PathwayResultsPage");
	}
}
