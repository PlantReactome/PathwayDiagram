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
import java.util.Set;

import org.reactome.diagram.model.CanvasPathway;
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

	public void setEntityExpressionInfoMap(Map<Long, String> entityExpressionIdMap,
										   Map<Long, Double> entityExpressionLevelMap,
										   Map<Long, String> entityExpressionColorMap) {
		
		if (entityExpressionIdMap == null) {
			entityExpressionInfoMap = null;
			return;
		}
		
		entityExpressionInfoMap = new HashMap<Long, ExpressionInfo>();
		
		for (Long entityId : entityExpressionIdMap.keySet()) {
			String expressionId = entityExpressionIdMap.get(entityId);
			Double expressionLevel = entityExpressionLevelMap.get(entityId);
			String expressionColor = entityExpressionColorMap.get(entityId);
			
			entityExpressionInfoMap.put(entityId, 
										new ExpressionInfo(expressionId, expressionLevel, expressionColor)
									   );
			
		}		
	}
	
	public List<String> getColorList(CanvasPathway pathway, Map<Long, ExpressionInfo> pathwayExpression) {
		List<Long> proteinReferenceIds = pathway.getReferenceIds(pathway.getProteins());
		
		Set<Long> expressionEntityIds = pathwayExpression.keySet();
		
		proteinReferenceIds.removeAll(expressionEntityIds);
		
		List<ExpressionInfo> expressionInfoOfEntities = new ArrayList<ExpressionInfo>(pathwayExpression.values());
		Collections.sort(expressionInfoOfEntities);
		
		List<String> colorList = new ArrayList<String>();
		
		Iterator<Long> proteinsWithoutExpressionColors = proteinReferenceIds.iterator();
		while (proteinsWithoutExpressionColors.hasNext()) {
			colorList.add(getDefaultColor());
			proteinsWithoutExpressionColors.next();
		}
		
		for (ExpressionInfo entityExpressionInfo : expressionInfoOfEntities) {
			colorList.add(entityExpressionInfo.getColor());
		}
		
		return colorList;
	}

	public String getDefaultColor() {
		return Parameters.defaultExpressionColor.value();
	}
	
	public class ExpressionInfo implements Comparable<ExpressionInfo> {
		private String id;
		private Double level;
		private String color;
				
		public ExpressionInfo(String id, Double level, String color) {
			this.id = id;
			this.level = level;
			this.color = color;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
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
