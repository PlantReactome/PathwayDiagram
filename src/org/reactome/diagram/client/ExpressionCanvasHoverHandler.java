/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
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
    	String label;
    	
    	ExpressionCanvasModel ecm = ec.getExpressionCanvasModel();
    	
    	Long refId = ecm.getPhysicalToReferenceEntityMap().get(hoveredObject.getReactomeId()).get(0);  	
    	
    	String expressionId = "N/A";    	
    	String expressionLevel = "N/A";
    	
    	if (ecm.getEntityExpressionIdMap() != null)
    		expressionId = ecm.getEntityExpressionIdMap().get(refId);
    	
    	if (ecm.getEntityExpressionLevelMap() != null)
    		expressionLevel = ecm.getEntityExpressionLevelMap().get(refId).toString();
    	
    	label = "Id: " + expressionId + "<br/> Level: " + expressionLevel;
   
    	tooltip.setWidget(new HTML(label));
    
    	super.showTooltip();
    }
}
