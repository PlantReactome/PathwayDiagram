/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.results;


/**
 * 
 * Implement this if you want to handle the display of status information in a uniform way.
 *
 * @author David Croft
 */
public interface StatusDisplayHandler {
	public void showStatus(String serializedJsonStatus);
}
