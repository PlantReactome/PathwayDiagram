/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.List;

public class PathwayExpressionValue {
	private String species;
	private Long speciesId;		
    private String pathway;
    private Long pathwayId;
	private List<PathwayComponentExpressionValue> expressionValues; 
    		
	/**
	 * Default constructor.
	 */
	public PathwayExpressionValue() {		
		expressionValues = new ArrayList<PathwayComponentExpressionValue>();
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	public String getPathway() {
		return pathway;
	}

	public void setPathway(String pathway) {
		this.pathway = pathway;
	}

	public Long getPathwayId() {
		return pathwayId;
	}

	public void setPathwayId(Long pathwayId) {
		this.pathwayId = pathwayId;
	}

	public List<PathwayComponentExpressionValue> getExpressionValues() {
		return expressionValues;
	}

	public void setExpressionValues(List<PathwayComponentExpressionValue> expressionValues) {
		this.expressionValues = expressionValues;
	}
	
}
