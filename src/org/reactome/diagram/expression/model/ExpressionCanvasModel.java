/*
 * Created on Mar 14, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.List;
import java.util.Map;

/**
 * Mappings for physical entities on the expression canvas
 * @author weiserj
 *
 */
public class ExpressionCanvasModel {
	private Map<Long, String> entityColorMap;
    private Map<Long, Double> entityExpressionLevelMap;
    private Map<Long, String> entityExpressionIdMap;
    private Map<Long, List<Long>> physicalToReferenceEntityMap;
    
    public ExpressionCanvasModel() {

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
}
