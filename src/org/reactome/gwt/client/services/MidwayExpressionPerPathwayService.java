/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.ExpressionDataPerPathway;
import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("midwayExpressionPerPathway")
public interface MidwayExpressionPerPathwayService extends RemoteService {
	ExpressionDataPerPathway mapToEntities(String numericalIdentifierType);
	void setPathwayDbIdInSession(String pathwayDbId);
	void launchPathwayAnalysisForPathway(String pathwayDbId);
	ProgressInfo getProgressInfo();
}
