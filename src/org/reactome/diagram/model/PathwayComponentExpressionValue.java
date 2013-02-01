/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.List;

public class PathwayComponentExpressionValue {
	// Physical Entity id
	private Long dbId;
	// 
	private String expressionId;
	
    private List<Double> values; 
    //  
    private String dataType;
		
	/**
	 * Default constructor.
	 */
	public PathwayComponentExpressionValue() {		
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public String getExpressionId() {
		return expressionId;
	}

	public void setExpressionId(String expressionId) {
		this.expressionId = expressionId;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}	
}
