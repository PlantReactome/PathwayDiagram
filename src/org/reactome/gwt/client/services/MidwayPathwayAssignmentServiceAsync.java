/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.PathwayInfoPerIdentifier;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>MidwayPathwayAssignmentService</code>.
 * 
 * @author David Croft
 */
public interface MidwayPathwayAssignmentServiceAsync extends ProgressMonitorAsync { 
	void mapToPathways(boolean qualifyNumericalIdentifiers, String numericalIdentifierType, AsyncCallback<PathwayInfoPerIdentifier> callback);
}
