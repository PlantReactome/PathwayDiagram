/* Copyright (c) 2013 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis.getdata.species;


/**
 * 
 * Implement this if you want to handle the display of species information in a uniform way.
 *
 * @author David Croft
 */
public interface SpeciesDisplayHandler {
	public void broadcastSpecies(String[][] speciesList, int defaultSpeciesNum);
}
