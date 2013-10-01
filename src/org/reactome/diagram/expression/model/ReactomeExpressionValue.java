/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReactomeExpressionValue {
	private String analysisId;
	private String analysisType;
	private double minExpression;
    private double maxExpression;
	private String dataType;
    private List<String> expressionColumnNames;
    private Map<Long, PathwayExpressionValue> pathwayExpressionValues;
    		
	/**
	 * Default constructor.
	 */
	public ReactomeExpressionValue() {		
		pathwayExpressionValues = new HashMap<Long, PathwayExpressionValue>();
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public double getMinExpression() {
		return minExpression;
	}

	public void setMinExpression(double minExpression) {
		this.minExpression = minExpression;
	}

	public double getMaxExpression() {
		return maxExpression;
	}

	public void setMaxExpression(double maxExpression) {
		this.maxExpression = maxExpression;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<String> getExpressionColumnNames() {
		return expressionColumnNames;
	}

	public void setExpressionColumnNames(List<String> expressionColumnNames) {
		this.expressionColumnNames = expressionColumnNames;
	}

	public Map<Long, PathwayExpressionValue> getPathwayExpressionValues() {
		return pathwayExpressionValues;
	}

	public void setPathwayExpressionValues(Map<Long, PathwayExpressionValue> pathwayExpressionValues) {
		this.pathwayExpressionValues = pathwayExpressionValues;
	}
	
	public PathwayExpressionValue getPathwayExpressionValue(Long pathwayId) {
	    if (pathwayExpressionValues == null)
	        return null;
	    return pathwayExpressionValues.get(pathwayId);
	}
	
	/**
	 * Make sure data values in each PathwayComponentValue has the same lengths as expressionColumnNames.
	 * Otherwise, an error may be generated because of mismatch.
	 */
	public boolean validateExpressionData() {
	    for (Long pathwayId : pathwayExpressionValues.keySet()) {
	        PathwayExpressionValue pathwayExp = pathwayExpressionValues.get(pathwayId);
	        Map<Long, PathwayComponentExpressionValue> compIdToValue = pathwayExp.getExpressionValues();
	        for (Long compId : compIdToValue.keySet()) {
	            PathwayComponentExpressionValue compValues = compIdToValue.get(compId);
	            if (compValues.getValues().size() != expressionColumnNames.size())
	                return false;
	        }
	    }
	    return true;
	}
}
