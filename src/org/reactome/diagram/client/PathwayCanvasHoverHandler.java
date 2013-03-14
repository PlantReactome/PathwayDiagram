/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Label;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class PathwayCanvasHoverHandler extends HoverHandler {
    private PathwayCanvas pc;
	
    public PathwayCanvasHoverHandler(PathwayDiagramPanel diagramPanel, PathwayCanvas pathwayCanvas) {
        super(diagramPanel, pathwayCanvas);
        pc = pathwayCanvas; 
    }

    public GraphObject hover(Point hoverPoint) {
        this.hoverPoint = hoverPoint;
    	
    	if (pc.getPathway() == null || pc.getPathway().getGraphObjects() == null)
            return null;
                
        List<GraphObject> objects = pc.getPathway().getGraphObjects();
        super.hover(objects);        
        
        if (hoveredObject != null && (!(isOverSameObject && timeElapsed)))
        	showTooltip();
        
        return hoveredObject;        
    }
        
    protected void showTooltip() {
    	String label = super.getLabel();
    	 	
    	tooltip.setWidget(new Label(label));
    
    	super.showTooltip();
    }
}
