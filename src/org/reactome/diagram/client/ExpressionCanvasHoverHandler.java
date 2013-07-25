/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.expression.model.ExpressionCanvasModel.ExpressionInfo;
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
    private ExpressionCanvas ec;
	
    public ExpressionCanvasHoverHandler(PathwayDiagramPanel diagramPanel, ExpressionCanvas expressionCanvas) {
        super(diagramPanel, expressionCanvas);
        ec = expressionCanvas; 
    }

    public GraphObject hover(Point hoverPoint) {
        this.hoverPoint = hoverPoint;
        
    	if (ec.getGraphObjects() == null || ec.getAnalysisType() == AnalysisType.SpeciesComparison)
            return null;
        
    	
        List<GraphObject> objects = ec.getGraphObjects();
        super.hover(objects);
                
        if (hoveredObject != null && hoveredObject.getType() == GraphObjectType.RenderableProtein) {        	
        	if (!(isOverSameObject && timeElapsed)) {       	
        		showTooltip();
        	}
        	
        	return hoveredObject;
        } else {
        	return null;
        }	
    }
        
    protected void showTooltip() {
    	
    	ExpressionCanvasModel ecm = ec.getExpressionCanvasModel();
    	
    	Long refId = ec.getPathway().getDbIdToRefEntityId().get(hoveredObject.getReactomeId()).get(0);  	
    	
    	String expressionId = "N/A";    	
    	String expressionLevel = "N/A";
    	
    	if (ecm.getEntityExpressionInfoMap() != null) {
    		ExpressionInfo expressionInfo = ecm.getEntityExpressionInfoMap().get(refId);
    		
    		if (expressionInfo != null && expressionInfo.getId() != null)
    			expressionId = expressionInfo.getId();
    	
    		if (expressionInfo != null && expressionInfo.getLevel() != null)
    			expressionLevel = expressionInfo.getLevel().toString();
    	}
    		
    	String label = "Expression Id: " + expressionId + "<br/> Expression Level: " + expressionLevel;
   
    	tooltip.setWidget(new HTML(label));
    
    	super.showTooltip();
    }
}
