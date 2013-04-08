/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.PathwayInfoPerIdentifier;
import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("pathwayAssignment")
public interface PathwayAssignmentService extends RemoteService { 
	PathwayInfoPerIdentifier mapToPathways(boolean qualifyNumericalIdentifiers, String numericalIdentifierType);
	ProgressInfo getProgressInfo();
}
