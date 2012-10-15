/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class HoverHandler {
    private PathwayDiagramPanel diagramPanel;
    private GraphObject hoveredObject;
    
    public HoverHandler(PathwayDiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
    }
    
    /**
     * Get the hovered object.
     * @return
     */
    public GraphObject getHoveredObject() {
        return hoveredObject;
    }
        
    public void hover(Point hoverPoint) {
        if (diagramPanel.getPathway() == null || diagramPanel.getPathway().getGraphObjects() == null)
            return;
        
        // Only one object should be hovered over
        GraphObject hovered = null;
        List<GraphObject> objects = diagramPanel.getPathway().getGraphObjects();
        for (GraphObject obj : objects) {
            if (hovered != null) 
                break;
            GraphObjectType type = obj.getType();
            if (type == GraphObjectType.RenderableCompartment) 
                continue;
            if (obj.isPicked(hoverPoint)) {
                obj.setIsHovered(true);
                hovered = obj;
            }
        }

        // Remove displayed label if just hovering over empty space
        if (hovered == null) {
        	if (hoveredObject != null) {
        		hoveredObject.getLabel().hide();
        	   	hoveredObject = null;
        	}
        	return;
        } else {
        	// If there is a previously hovered object
        	if (hoveredObject != null) {
        		// Do nothing if the new object is the same as the old        		
        		if (hovered == hoveredObject) {
        			return;
        		}
        		hoveredObject.setIsHovered(false);
        		hoveredObject.getLabel().hide();
        	} 
        	
        	// Set new hovered object
        	hoveredObject = hovered;
        	showLabel();
        
        	diagramPanel.update();
        	fireHoverEvent();
        }	
    }
        
    private void showLabel() {
    	Point objPos = hoveredObject.getPosition();
    	PopupPanel label = hoveredObject.getLabel();
    	double scale = diagramPanel.getCanvas().getScale();
    	int canvasZIndex;
    	    	
    	try {
    		canvasZIndex = Integer.parseInt(diagramPanel.getElement().getStyle().getZIndex()); 
    	} catch (NumberFormatException nfe) {
    		canvasZIndex = 0;
    	}
    	// Position label 
    	label.setPopupPosition( (int) (objPos.getX() * scale), (int) (objPos.getY() * scale));
    	
    	// Label must appear above the canvas
    	label.getElement().getStyle().setZIndex(canvasZIndex + 1); 
    	
    	// Show the label
    	label.show();
    }
    
    private void fireHoverEvent() {
        HoverEvent event = new HoverEvent();
        event.setHoveredObject(hoveredObject);
        diagramPanel.getCanvas().fireEvent(event);
    }
}
