/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("speciesComparison")
public interface SpeciesComparisonService extends RemoteService {
	String[][] getSpeciesList();
	String[][] getSpeciesTable(String speciesDbIDString);
	String getDataTableName();
}
