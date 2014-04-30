/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.analysis.model.IdentifierMap;
import org.reactome.analysis.model.PathwayIdentifier;
import org.reactome.analysis.model.PathwayIdentifiers;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.CanvasPathway.ReferenceEntity;

/**
 * @author weiserj
 *
 */
public class PathwayOverlay {
    private CanvasPathway pathway;
    private Map<String, List<Double>> expressionIdToValue;
    private Map<String, List<String>> componentReferenceIdToExpressionId;
	
    public PathwayOverlay(CanvasPathway pathway, PathwayIdentifiers pathwayIdentifiers) {
    	this.pathway = pathway;
    	setExpressionIdToValue(pathwayIdentifiers);
    	setReferenceIdToExpressionIds(pathwayIdentifiers);
    }

    public CanvasPathway getPathway() {
    	return pathway;
    }
    
    public Map<Long, List<String>> getDbIdToExpressionId() {
    	Map<Long, List<String>> dbIdToExpressionId = new HashMap<Long, List<String>>();
    	
    	for (Long dbId : getPathway().getDbIdToRefEntity().keySet()) {
    		
    		ReferenceEntity refEntity = getReferenceEntity(dbId);
    		if (refEntity == null)
    			continue;
    		
    		List<String> expressionIds = componentReferenceIdToExpressionId.get(refEntity.getReferenceIdentifier());
    		dbIdToExpressionId.put(refEntity.getDbId(), expressionIds);
    	}
    	
    	return dbIdToExpressionId;
    }
    
    public Map<Long, Double> getExpressionValuesForDataPoint(int dataIndex) {
    	Map<Long, Double> dbIdToExpressionValue = new HashMap<Long, Double>();

    	for (Long dbId : getPathway().getDbIdToRefEntity().keySet()) {
    		ReferenceEntity refEntity = getReferenceEntity(dbId);
    		if (refEntity == null)
    			continue;
    			
    		PathwayComponentExpressionValue component = new PathwayComponentExpressionValue();
    		for (String expressionId : componentReferenceIdToExpressionId.get(refEntity.getReferenceIdentifier())) {
    			component.addExpressionValues(expressionId, expressionIdToValue.get(expressionId));
    		}
    		
    		
    		dbIdToExpressionValue.put(refEntity.getDbId(), component.getValues().get(dataIndex));
    	}
    	
    	return dbIdToExpressionValue;
    }
    
    private ReferenceEntity getReferenceEntity(Long dbId) {
    	List<ReferenceEntity> refEntities = getPathway().getDbIdToRefEntity().get(dbId);
    	
    	if (refEntities == null || refEntities.size() != 1)
    		return null;
    	
    	return refEntities.get(0);
    }
    
    private void setExpressionIdToValue(PathwayIdentifiers pathwayIdentifiers) {
    	Map<String, List<Double>> expressionValueMap = new HashMap<String, List<Double>>();
    	
    	for (PathwayIdentifier pathwayIdentifier : pathwayIdentifiers.getIdentifiers()) {
    		expressionValueMap.put(pathwayIdentifier.getIdentifier(), pathwayIdentifier.getExp());
    	}
    	
    	this.expressionIdToValue = expressionValueMap;
    }
       
    private Map<String, List<String>> setReferenceIdToExpressionIds(PathwayIdentifiers pathwayIdentifiers) {
    	Map<String, List<String>> expressionIdMap = new HashMap<String, List<String>>();
    	
    	for (PathwayIdentifier pathwayIdentifier : pathwayIdentifiers.getIdentifiers()) {
    		String expressionId = pathwayIdentifier.getIdentifier();
    		for (IdentifierMap identifierMap : pathwayIdentifier.getMapsTo()) {
    			String resource = identifierMap.getResource();
    			for (String referenceId : identifierMap.getIds()) {
    				String id = resource + ":" + referenceId;
    				
    				if (expressionIdMap.get(id) == null ) {
    					expressionIdMap.put(id, new ArrayList<String>());
    				}
    				
    				expressionIdMap.get(id).add(expressionId);
    			}
    		}
    	}
    	
    	return expressionIdMap;
    }
}
