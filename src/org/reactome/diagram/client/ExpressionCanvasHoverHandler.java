/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.client.ExpressionCanvas.ProcessNodeExpression;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel.ExpressionInfo;
import org.reactome.diagram.model.CanvasPathway.ReferenceEntity;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.HTML;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class ExpressionCanvasHoverHandler extends HoverHandler {
    private ExpressionCanvas expressionCanvas;
	
    public ExpressionCanvasHoverHandler(PathwayDiagramPanel diagramPanel, ExpressionCanvas expressionCanvas) {
        super(diagramPanel, expressionCanvas);
        this.expressionCanvas = expressionCanvas; 
    }

    public GraphObject hover(Point hoverPoint) {
        this.hoverPoint = hoverPoint;
        
    	if (expressionCanvas.getObjectsForRendering() == null)
            return null;
    	
        List<GraphObject> objects = expressionCanvas.getObjectsForRendering();
        super.hover(objects);
                
        if (hoveredObject != null) {
        	//if (!(isOverSameObject && timeElapsed)) {
        		showTooltip();
        	//}
        	
        	return hoveredObject;
        } else {
        	return null;
        }	
    }

    protected void showTooltip() {
    	if (showTooltipForHoveredObjectType() == false)
    		return;
    	
    	tooltip.setWidget(new HTML(getTooltipText()));
    	super.showTooltip();
    }
    
    private String getTooltipText() {
    	if (hoveredObjectIsProcessNode())
    		return getTextForProcessNode();
    	else if (hoveredObjectIsSingleEntity())
    		return getTextForSingleEntity();
		return null;
    }
    
    private String getTextForProcessNode() {
    	ProcessNodeExpression processNodeExpression = expressionCanvas
    			.getCurrentPathwayExpressionForDataPoint()
    			.getProcessNodeExpressionObject(hoveredObject.getReactomeId());
    	
    	if (processNodeExpression == null)
    		return "No entities found";
    	
    	String text = "";
    	if (expressionCanvas.getAnalysisType() == AnalysisType.Expression)
    		text += "Average expression: " + processNodeExpression.getExpValue() + "<br />";
    	
    	return text += "Entities found: " + processNodeExpression.getFound() + "/" + processNodeExpression.getTotal();
    }
    
    private String getTextForSingleEntity() {
    	String text = "Expression Identifiers: " + getExpressionIdForHoveredObject();
    	
    	if (expressionCanvas.getAnalysisType() == AnalysisType.Expression)
    		text += "<br /> Median Expression Level: " + getExpressionLevelForHoveredObject();
		
    	return text;
    }

    private boolean showTooltipForHoveredObjectType() {
		return hoveredObjectIsProcessNode() || hoveredObjectIsSingleEntity();
	}

	private boolean hoveredObjectIsSingleEntity() {
    	return 	hoveredObject.getType() == GraphObjectType.RenderableGene ||
    			hoveredObject.getType() == GraphObjectType.RenderableRNA ||
    			hoveredObject.getType() == GraphObjectType.RenderableProtein ||
    			hoveredObject.getType() == GraphObjectType.RenderableChemical;
    }
    
    private boolean hoveredObjectIsProcessNode() {
    	return hoveredObject.getType() == GraphObjectType.ProcessNode;
    }
    
    private String getExpressionIdForHoveredObject() {
    	final ExpressionInfo expressionInfo = getExpressionInfoForHoveredObject();
    	
    	if (expressionInfo == null || expressionInfo.getIdentifiers() == null || expressionInfo.getIdentifiers().isEmpty())
    		return "N/A";
    	
    	return expressionInfo.getIdentifiersAsString();
    }
    
    private String getExpressionLevelForHoveredObject() {
    	final ExpressionInfo expressionInfo = getExpressionInfoForHoveredObject();
    	
    	if (expressionInfo == null || expressionInfo.getLevel() == null)
    		return "N/A";
    	
    	return expressionInfo.getLevel().toString();
    }
    
	private ExpressionInfo getExpressionInfoForHoveredObject() {
		final List<Long> refIdsForHoveredObject = getReferenceIdsForHoveredObject();
		final Map<Long, ExpressionInfo> entityToExpressionInfoMap = getEntityToExpressionInfoMap();
		
		if (refIdsForHoveredObject.isEmpty() || entityToExpressionInfoMap == null) 
			return null;
		
		return entityToExpressionInfoMap.get(refIdsForHoveredObject.get(0));
	}

    private List<Long> getReferenceIdsForHoveredObject() {
    	List<Long> refIdsForHoveredObject = new ArrayList<Long>();
    	
    	for (ReferenceEntity refEntity : getReferenceEntitiesForHoveredObject()) {
    		refIdsForHoveredObject.add(refEntity.getDbId());
    	}
    	
    	return refIdsForHoveredObject;
    }
    
    private List<ReferenceEntity> getReferenceEntitiesForHoveredObject() {
    	List<ReferenceEntity> refEntities = expressionCanvas.getPathway().getDbIdToRefEntity().get(hoveredObject.getReactomeId());
    	
    	if (refEntities == null)
    		return new ArrayList<ReferenceEntity>();
		
    	return refEntities;
    }

    private Map<Long, ExpressionInfo> getEntityToExpressionInfoMap() {
    	return expressionCanvas.getExpressionCanvasModel().getEntityExpressionInfoMap();
    }
}
