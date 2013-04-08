/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ExpressionPerPathwayService</code>.
 * 
 * @author David Croft
 */
public interface SpeciesComparisonServiceAsync {
	void getSpeciesList(AsyncCallback<String[][]> callback);
	void getSpeciesTable(String speciesDbIDString, AsyncCallback<String[][]> callback);
	void getDataTableName(AsyncCallback<String> callback);
}
