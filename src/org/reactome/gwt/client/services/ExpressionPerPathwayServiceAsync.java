/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.ExpressionDataPerPathway;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ExpressionPerPathwayService</code>.
 * 
 * @author David Croft
 */
public interface ExpressionPerPathwayServiceAsync extends ProgressMonitorAsync { 
	void mapToEntities(String numericalIdentifierType, AsyncCallback<ExpressionDataPerPathway> callback);
	void setPathwayDbIdInSession(String pathwayDbId, AsyncCallback<Void> callback);
	void launchPathwayAnalysisForPathway(String pathwayDbId, AsyncCallback<Void> callback);
}
