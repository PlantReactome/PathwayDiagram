/*
 * Created on Mar 13, 2013
 *
 */
package org.reactome.diagram.expression.model;

/**
 * This enum lists analysis types: e.g. gene expression, species comparison 
 * @author weiserj
 *
 */
public enum AnalysisType { 
	Expression, 
	SpeciesComparison; 

	public static boolean contains(String test) {
		for (AnalysisType analysis : AnalysisType.values()) {
			if (analysis.name().equals(test)) {
				return true;
			}
		}
	
		return false;
	}
}