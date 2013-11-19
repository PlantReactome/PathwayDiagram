/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.DataType;
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
        
    	if (expressionCanvas.getObjectsForRendering() == null || expressionCanvas.getAnalysisType() == AnalysisType.SpeciesComparison)
            return null;
    	
        List<GraphObject> objects = expressionCanvas.getObjectsForRendering();
        super.hover(objects);
                
        if (hoveredObject != null && hoveredObjectTypeMatchesDataType()) {
        	if (!(isOverSameObject && timeElapsed)) {
        		showTooltip();
        	}
        	
        	return hoveredObject;
        } else {
        	return null;
        }	
    }
        
    private boolean hoveredObjectTypeMatchesDataType() {
    	return (hoveredObject.getType() == GraphObjectType.RenderableProtein && getDataType() == DataType.Protein) ||
    	(hoveredObject.getType() == GraphObjectType.RenderableChemical && getDataType() == DataType.SmallCompound);
    }
    
    private DataType getDataType() {
    	return expressionCanvas.getDataType();
    }
    
    protected void showTooltip() {
    	String text = "Expression Identifiers: " + getExpressionIdForHoveredObject() + "<br />" +
    				  "Median Expression Level: " + getExpressionLevelForHoveredObject();
   
    	tooltip.setWidget(new HTML(text));
    
    	super.showTooltip();
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
    	return expressionCanvas.getPathway().getDbIdToRefEntity().get(hoveredObject.getReactomeId());
    }

    private Map<Long, ExpressionInfo> getEntityToExpressionInfoMap() {
    	return expressionCanvas.getExpressionCanvasModel().getEntityExpressionInfoMap();
    }
}
