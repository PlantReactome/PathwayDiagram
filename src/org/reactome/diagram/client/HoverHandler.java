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
    public GraphObject getHoveredObjects() {
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

        // Don't do anything if just hovering over empty space
        if (hovered == null)
            return;
        
        // A special case to gain some performance: moving to a new point on the previously hovered object.
        if (hovered == hoveredObject) {
            return; // Don't redraw
        } 
        
        // Set the previously hovered object's flag to false
        if (hoveredObject != null)
        	hoveredObject.setIsHovered(false);
        
        hoveredObject = hovered;
                
        diagramPanel.update();
        fireHoverEvent();
    }
        
    private void fireHoverEvent() {
        HoverEvent event = new HoverEvent();
        event.setHoveredObject(hoveredObject);
        diagramPanel.getCanvas().fireEvent(event);
    }
}
