/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.SurveyResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DescriptionService</code>.
 * 
 * @author David Croft
 */
public interface SurveyServiceAsync {
	void setSurveyResult(SurveyResult surveyResult, AsyncCallback<Void> callback);
}
