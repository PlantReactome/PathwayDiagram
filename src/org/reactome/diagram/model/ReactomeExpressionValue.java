/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.List;

public class ReactomeExpressionValue {
	private String analysisId;
	private double minExpression;		
    private double maxExpression;
	private List<String> expressionColumnNames;
    private List<PathwayExpressionValue> pathwayExpressionValues; 
    		
	/**
	 * Default constructor.
	 */
	public ReactomeExpressionValue() {		
		pathwayExpressionValues = new ArrayList<PathwayExpressionValue>();
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

	public List<PathwayExpressionValue> getPathwayExpressionValues() {
		return pathwayExpressionValues;
	}

	public void setPathwayExpressionValues(List<PathwayExpressionValue> pathwayExpressionValues) {
		this.pathwayExpressionValues = pathwayExpressionValues;
	}
	
}
