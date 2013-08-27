/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathwayComponentExpressionValue {
	// Physical Entity id
	private Long dbId;
	
    private Map<String, List<Double>> expressionIdToValues; 
    
    //  
    private List<Double> medianExpressionValues;
    
    private String dataType;
		
	/**
	 * Default constructor.
	 */
	public PathwayComponentExpressionValue() {
		expressionIdToValues = new HashMap<String, List<Double>>();
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public void addExpressionValues(String expressionId, List<Double> values) {
		expressionIdToValues.put(expressionId, values);
	}

	public List<String> getExpressionIds() {
		List<String> expressionIds = new ArrayList<String>();
		expressionIds.addAll(expressionIdToValues.keySet());
		
		return expressionIds;
	}
	
	/**
	 * 
	 * @return Median expression values at each time point for the pathway component 
	 */
	public List<Double> getValues() {
		if (medianExpressionValues == null) {
		
			List<Double> medianValues = new ArrayList<Double>();
				
			for (Integer timePointIndex = 0; timePointIndex < numberOfTimePoints(); timePointIndex++) {
				List<Double> timePointValues = new ArrayList<Double>();
				for (String expressionId : expressionIdToValues.keySet()) {
					timePointValues.add(expressionIdToValues.get(expressionId).get(timePointIndex));
				}
								
				medianValues.add(getMedian(timePointValues));
			}
	
			medianExpressionValues = medianValues;
		}
		
		return medianExpressionValues;
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	private Integer numberOfTimePoints() {
		Integer numberOfTimePoints = 0;
		
		for (String expressionId : expressionIdToValues.keySet()) {
			numberOfTimePoints = Math.max(numberOfTimePoints, expressionIdToValues.get(expressionId).size());
		}
		
		return numberOfTimePoints;
	}
	
	private Double getMedian(List<Double> values) {
		if (values.isEmpty())
			return null;
		
		Collections.sort(values);
		
		Integer medianIndex = (int) Math.ceil(values.size() / 2.0) - 1;
		
		if (values.size() % 2 == 0) {
			return ((values.get(medianIndex) + values.get(medianIndex + 1)) / 2.0);
		} else {
			return values.get(medianIndex);
		}
	}
}
