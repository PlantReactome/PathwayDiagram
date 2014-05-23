/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.analysis.model.IdentifierMap;
import org.reactome.diagram.analysis.model.PathwayIdentifier;
import org.reactome.diagram.analysis.model.PathwayIdentifiers;
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
    	this.expressionIdToValue = new HashMap<String, List<Double>>();
    	this.componentReferenceIdToExpressionId = new HashMap<String, List<String>>();
    	
    	if (pathwayIdentifiers != null) {
    		setExpressionIdToValue(pathwayIdentifiers);
    		setReferenceIdToExpressionIds(pathwayIdentifiers);
    	}
    	
    }

    public CanvasPathway getPathway() {
    	return pathway;
    }
    
    public Map<Long, List<String>> getDbIdToExpressionId(String resourceName) {
    	Map<Long, List<String>> dbIdToExpressionId = new HashMap<Long, List<String>>();
    	
    	for (Long dbId : getPathway().getDbIdToRefEntity().keySet()) {
    		for (ReferenceEntity refEntity : getReferenceEntities(dbId)) {
    			if (selectedResourceExcludesReferenceEntity(resourceName, refEntity))
    				continue;
    		
    			dbIdToExpressionId.put(refEntity.getDbId(), getExpressionIds(refEntity));
    		}
    	}
    	
    	return dbIdToExpressionId;
    }
    
    public Map<Long, Double> getExpressionValuesForDataPoint(int dataIndex, String resourceName) {
    	Map<Long, Double> dbIdToExpressionValue = new HashMap<Long, Double>();

    	for (Long dbId : getPathway().getDbIdToRefEntity().keySet()) {
    		for (ReferenceEntity refEntity : getReferenceEntities(dbId)) {
    			if (selectedResourceExcludesReferenceEntity(resourceName, refEntity) || 
    				getExpressionIds(refEntity) == null)
    				continue;
    	
    			PathwayComponentExpressionValue component = new PathwayComponentExpressionValue();
    			for (String expressionId : getExpressionIds(refEntity)) {
    				component.addExpressionValues(expressionId, expressionIdToValue.get(expressionId));
    			}
    		
    			dbIdToExpressionValue.put(refEntity.getDbId(), getExpressionValueAtDataPoint(component, dataIndex));
    		}
    	}
    	
    	return dbIdToExpressionValue;
    }
    
    private boolean selectedResourceExcludesReferenceEntity(String resourceName, ReferenceEntity refEntity) {
    	if (resourceName.equalsIgnoreCase("TOTAL"))
    		return false;
    	
    	return !resourceName.equals(refEntity.getResource());
    }
    
    private List<String> getExpressionIds(ReferenceEntity refEntity) {
		return componentReferenceIdToExpressionId.get(refEntity.getReferenceIdentifier());
    }
    
    private Double getExpressionValueAtDataPoint(PathwayComponentExpressionValue component, int dataIndex) {
    	if (component.getValues().size() <= dataIndex)
    		return null;
    	
    	return component.getValues().get(dataIndex);
    }
    
    private List<ReferenceEntity> getReferenceEntities(Long dbId) {
    	List<ReferenceEntity> refEntities = getPathway().getDbIdToRefEntity().get(dbId);
    	
    	if (refEntities == null)
    		return new ArrayList<ReferenceEntity>();
    	
    	return refEntities;
    }
    
    private void setExpressionIdToValue(PathwayIdentifiers pathwayIdentifiers) {
    	expressionIdToValue.clear();
    	
    	for (PathwayIdentifier pathwayIdentifier : pathwayIdentifiers.getIdentifiers()) {
    		expressionIdToValue.put(pathwayIdentifier.getIdentifier(), pathwayIdentifier.getExp());
    	}
    }
       
    private void setReferenceIdToExpressionIds(PathwayIdentifiers pathwayIdentifiers) {
    	componentReferenceIdToExpressionId.clear();
    	
    	for (PathwayIdentifier pathwayIdentifier : pathwayIdentifiers.getIdentifiers()) {
    		String expressionId = pathwayIdentifier.getIdentifier();
    		for (IdentifierMap identifierMap : pathwayIdentifier.getMapsTo()) {
    			String resource = identifierMap.getResource();
    			for (String referenceId : identifierMap.getIds()) {
    				String id = resource + ":" + referenceId;
    				
    				if (componentReferenceIdToExpressionId.get(id) == null ) {
    					componentReferenceIdToExpressionId.put(id, new ArrayList<String>());
    				}
    				
    				componentReferenceIdToExpressionId.get(id).add(expressionId);
    			}
    		}
    	}
    }
}
