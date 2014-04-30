/*
 * Created on Mar 14, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.view.Parameters;

/**
 * Mappings for physical entities on the expression canvas
 * @author weiserj
 *
 */
public class ExpressionCanvasModel {
	private Map<Long, ExpressionInfo> entityExpressionInfoMap;
    	
    public ExpressionCanvasModel() {
    }
   
	public Map<Long, ExpressionInfo> getEntityExpressionInfoMap() {
		return entityExpressionInfoMap;
	}

	public void setEntityExpressionInfoMap(Map<Long, List<String>> id,
										   Map<Long, Double> level,
										   Map<Long, String> color) {
		
		if (id == null) {
			entityExpressionInfoMap = null;
			return;
		}
		
		entityExpressionInfoMap = new HashMap<Long, ExpressionInfo>();
		
		for (Long dbId : id.keySet()) {
			List<String> expressionId = id.get(dbId);
			Double expressionLevel = level.get(dbId);
			String expressionColor = color.get(dbId);
			
			entityExpressionInfoMap.put(dbId, 
										new ExpressionInfo(expressionId, expressionLevel, expressionColor)
									   );
			
		}		
	}
	
	public List<String> getColorList(List<Long> refIds, Map<Long, ExpressionInfo> pathwayExpression) {
		List<String> colorList = new ArrayList<String>();
		
		colorList.addAll(getEntityColorsFromExpressionInfo(new ArrayList<ExpressionInfo>(pathwayExpression.values())));
		colorList.addAll(getDefaultColorList(refIds, pathwayExpression));
		
		return colorList;
	}
	
	
	private List<String> getDefaultColorList(List<Long> refIds, Map<Long, ExpressionInfo> pathwayExpression) {	
		refIds.removeAll(pathwayExpression.keySet());
		
		List<String> defaultColorList = new ArrayList<String>();
		Iterator<Long> entitiesWithoutExpressionColors = refIds.iterator();
		while (entitiesWithoutExpressionColors.hasNext()) {
			defaultColorList.add(getDefaultColor());
			entitiesWithoutExpressionColors.next();
		}
		
		return defaultColorList;
	}

	private List<String> getEntityColorsFromExpressionInfo(List<ExpressionInfo> expressionInfoOfEntities) {
		Collections.sort(expressionInfoOfEntities);
		Collections.reverse(expressionInfoOfEntities);
		
		List<String> entityColors = new ArrayList<String>();
		
		for (ExpressionInfo entityExpressionInfo : expressionInfoOfEntities) {
			entityColors.add(entityExpressionInfo.getColor());
		}
		
		return entityColors;
	}
	
	public String getDefaultColor() {
		return Parameters.defaultExpressionColor.value();
	}
	
	public class ExpressionInfo implements Comparable<ExpressionInfo> {
		private List<String> identifiers;
		private Double level;
		private String color;
				
		public ExpressionInfo(List<String> expressionIds, Double level, String color) {
			this.identifiers = expressionIds;
			this.level = level;
			this.color = color;
		}
		
		public void setIdentifiers(List<String> identifiers) {
			this.identifiers = identifiers;
		}
		
		public List<String> getIdentifiers() {
			return identifiers;
		}
		
		public String getIdentifiersAsString() {
			String delimiter = ", ";
			
			String identifierString = "";
			
			
			for (String identifier : getIdentifiers()) {
				identifierString = identifierString.concat(identifier).concat(delimiter);
			}
			
			identifierString = identifierString.substring(0, identifierString.length() - delimiter.length());
			
			return identifierString;
		}
		
		public void setLevel(Double level) {
			this.level = level;
		}
		
		public Double getLevel() {
			return level;
		}
		
		public void setColor(String color) {
			this.color = color;
		}
		
		public String getColor() {
			return color;
		}
		
		public int compareTo(ExpressionInfo expressionInfoObject) {
			if (level < expressionInfoObject.getLevel())
				return -1;
			else if (level > expressionInfoObject.getLevel())
				return 1;
			else
				return 0;			
		}
	}
}
