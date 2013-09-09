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
										   Map<Long, Double> entityExpressionLevelMap,
										   Map<Long, String> entityExpressionColorMap) {
		
		if (id == null) {
			entityExpressionInfoMap = null;
			return;
		}
		
		entityExpressionInfoMap = new HashMap<Long, ExpressionInfo>();
		
		for (Long entityId : id.keySet()) {
			List<String> expressionId = id.get(entityId);
			Double expressionLevel = entityExpressionLevelMap.get(entityId);
			String expressionColor = entityExpressionColorMap.get(entityId);
			
			entityExpressionInfoMap.put(entityId, 
										new ExpressionInfo(expressionId, expressionLevel, expressionColor)
									   );
			
		}		
	}
	
	public List<String> getColorList(List<Long> refGeneIds, Map<Long, ExpressionInfo> pathwayExpression) {
		Set<Long> expressionEntityIds = pathwayExpression.keySet();

		//System.out.println(pathway.getDisplayName() + "Pathway Proteins");
		//for (Long proteinId : proteinReferenceIds) {
		//	System.out.println(proteinId);
		//}
		
		//System.out.println(pathway.getDisplayName() + "Expression proteins");
		//for (Long expressionId : expressionEntityIds) {
		//	System.out.println(expressionId);
		//}
		
		refGeneIds.removeAll(expressionEntityIds);
		
		List<ExpressionInfo> expressionInfoOfEntities = new ArrayList<ExpressionInfo>(pathwayExpression.values());
		Collections.sort(expressionInfoOfEntities);
		
		List<String> colorList = new ArrayList<String>();
		
		Iterator<Long> proteinsWithoutExpressionColors = refGeneIds.iterator();
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
