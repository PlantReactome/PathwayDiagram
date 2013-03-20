/*
 * Created on Mar 14, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.client.ExpressionCanvas;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Mappings for physical entities on the expression canvas
 * @author weiserj
 *
 */
public class ExpressionCanvasModel {
	private ExpressionCanvas expressionCanvas;
	private Map<Long, String> entityColorMap;
    private Map<Long, Double> entityExpressionLevelMap;
    private Map<Long, String> entityExpressionIdMap;
    private Map<Long, List<Long>> physicalToReferenceEntityMap;
    
    public ExpressionCanvasModel(ExpressionCanvas expressionCanvas) {
    		this.expressionCanvas = expressionCanvas;
    }
   
	public Map<Long, String> getEntityColorMap() {
		return entityColorMap;
	}

	public void setEntityColorMap(Map<Long, String> entityColorMap) {
		this.entityColorMap = entityColorMap;
	}

	public Map<Long, Double> getEntityExpressionLevelMap() {
		return entityExpressionLevelMap;
	}

	public void setEntityExpressionLevelMap(Map<Long, Double> entityExpressionLevelMap) {
		this.entityExpressionLevelMap = entityExpressionLevelMap;
	}

	public Map<Long, String> getEntityExpressionIdMap() {
		return entityExpressionIdMap;
	}

	public void setEntityExpressionIdMap(Map<Long, String> entityExpressionIdMap) {
		this.entityExpressionIdMap = entityExpressionIdMap;
	}

	public Map<Long, List<Long>> getPhysicalToReferenceEntityMap() {
		return physicalToReferenceEntityMap;
	}

	public void setPhysicalToReferenceEntityMap(
			Map<Long, List<Long>> physicalToReferenceEntityMap) {
		this.physicalToReferenceEntityMap = physicalToReferenceEntityMap;
	}

	public void setPhysicalToReferenceEntityMap(String mapObjectJSON, boolean updateCanvas) {
		JSONArray mapObjects = (JSONArray) JSONParser.parseStrict(mapObjectJSON);
		
		Map<Long, List<Long>> physicalToReferenceEntityMap = new HashMap<Long, List<Long>>();
		for (int i = 0; i < mapObjects.size(); i++) {
			JSONObject entityMap = mapObjects.get(i).isObject();
			Long physicalEntityId = new Long((long) entityMap.get("peDbId").isNumber().doubleValue());
			JSONArray referenceEntityArray = entityMap.get("refDbIds").isArray();
			
			ArrayList<Long> referenceEntityIds = new ArrayList<Long>();
			for (int j = 0; j < referenceEntityArray.size(); j++) {
				Long referenceEntityId = new Long((long) referenceEntityArray.get(j).isNumber().doubleValue());
				referenceEntityIds.add(referenceEntityId);
			}
			
			physicalToReferenceEntityMap.put(physicalEntityId, referenceEntityIds);
		}
		setPhysicalToReferenceEntityMap(physicalToReferenceEntityMap);
		
		if (updateCanvas)
			expressionCanvas.update();
	}
}
