/*
 * Created on Mar 13, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum lists analysis types: e.g. gene expression, species comparison 
 * @author weiserj
 *
 */
public enum AnalysisType { 
	Expression, 
	SpeciesComparison,
	Overrepresentation; 

	private static final Map<String, AnalysisType> analysisTypeMap;
	static {
		Map<String, AnalysisType> analysisTypeStringToEnum = new HashMap<String, AnalysisType>();
		analysisTypeStringToEnum.put("EXPRESSION", Expression);
		analysisTypeStringToEnum.put("SPECIES_COMPARISON", SpeciesComparison);
		analysisTypeStringToEnum.put("OVERREPRESENTATION", Overrepresentation);
	
		analysisTypeMap = Collections.unmodifiableMap(analysisTypeStringToEnum);
	}
	
	
	public static boolean contains(String test) {
		for (AnalysisType analysis : AnalysisType.values()) {
			if (analysis.name().equals(test)) {
				return true;
			}
		}
	
		return false;
	}
	
	public static AnalysisType getAnalysisType(String analysisType) {
		return analysisTypeMap.get(analysisType);
	}
}