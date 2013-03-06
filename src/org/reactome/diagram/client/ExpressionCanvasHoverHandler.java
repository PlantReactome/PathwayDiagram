/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

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
        
    	if (ec.getGraphObjects() == null)
            return null;
        
    	
        List<GraphObject> objects = ec.getGraphObjects();
        super.hover(objects);
        
        return hoveredObject;
    }
        
    protected void showTooltip() {
    	String label;
    	
       	GraphObjectType type =  hoveredObject.getType();
    	
    	if (type != null && type == GraphObjectType.RenderableProtein) {
    		Long refId = ec.getPhysicalToReferenceEntityMap().get(hoveredObject.getReactomeId()).get(0);  	
    		
    		String expressionId = ec.getEntityExpressionIdMap().get(refId);
    		Double expressionLevel = ec.getEntityExpressionLevelMap().get(refId);
    		
    		label = "Id: " + expressionId + "<br/> Level: " + expressionLevel;
    	} else {	
    		return;
    	}    		
      	 	
    	tooltip.setWidget(new HTML(label));
    
    	super.showTooltip();
    }
}
