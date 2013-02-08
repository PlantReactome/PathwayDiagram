/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression.model;

import java.util.HashMap;
import java.util.Map;


public class PathwayExpressionValue {
	private String species;
	private Long speciesId;		
    private String pathway;
    private Long pathwayId;
	private Map<Long, PathwayComponentExpressionValue> expressionValues; 
    		
	/**
	 * Default constructor.
	 */
	public PathwayExpressionValue() {		
		expressionValues = new HashMap<Long, PathwayComponentExpressionValue>();
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

	public Map<Long, PathwayComponentExpressionValue> getExpressionValues() {
		return expressionValues;
	}

	public void setExpressionValues(Map<Long, PathwayComponentExpressionValue> expressionValues) {
		this.expressionValues = expressionValues;
	}
	
}
