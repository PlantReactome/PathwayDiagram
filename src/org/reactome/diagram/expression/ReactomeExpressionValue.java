/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReactomeExpressionValue {
	private String analysisId;
	private double minExpression;		
    private double maxExpression;
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
	
}
