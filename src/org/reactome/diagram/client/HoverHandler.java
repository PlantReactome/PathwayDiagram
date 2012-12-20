/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.InteractorNode;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class HoverHandler {
    private PathwayDiagramPanel diagramPanel;
    private GraphObject hoveredObject;
    private PopupPanel tooltip;
    private Timer timer;
    
    public HoverHandler(PathwayDiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
        this.tooltip = new PopupPanel();
        this.timer = new Timer() {

			@Override
			public void run() {
				tooltip.hide();				
			}
        	
        };
    }
    
    /**
     * Get the hovered object.
     * @return
     */
    public GraphObject getHoveredObject() {
        return hoveredObject;
    }
 
    public PopupPanel getTooltip() {
    	return tooltip;
    }
    
    public void hover(Point hoverPoint) {
        if (diagramPanel.getPathway() == null || diagramPanel.getPathway().getGraphObjects() == null)
            return;
        
        // Only one object should be hovered over
        GraphObject hovered = null;

        List<GraphObject> objects = diagramPanel.getInteractorCanvas().getGraphObjects();
        objects.addAll(diagramPanel.getPathway().getGraphObjects());
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
        		tooltip.hide();
        	   	timer.cancel();
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
        		tooltip.hide();
        		timer.cancel();
        	} 
        	
        	// Set new hovered object
        	hoveredObject = hovered;
        	showTooltip();
        
        	diagramPanel.update();
        	fireHoverEvent();
        }	
    }
        
    private void showTooltip() {
    	String displayName = hoveredObject.getDisplayName();
    	String objType = getObjType();
    	Point objPos = hoveredObject.getPosition();
    	
    	double scale = diagramPanel.getCanvas().getScale();
    	double translateX = diagramPanel.getCanvas().getTranslateX();
    	double translateY = diagramPanel.getCanvas().getTranslateY();
    	
    	// Compensate for canvas translation and scale
    	double x = (objPos.getX() * scale) + translateX;
    	double y = (objPos.getY() * scale) + translateY;
    	
    	int canvasZIndex;
    	    	
    	try {
    		canvasZIndex = Integer.parseInt(diagramPanel.getElement().getStyle().getZIndex()); 
    	} catch (NumberFormatException nfe) {
    		canvasZIndex = 0;
    	}
    	// Set tooltip text
    	String label = objType + ": " + displayName;    	
    	if (hoveredObject instanceof InteractorNode) 
    		label = label + "\nUniprot Accession:" + ((InteractorNode) hoveredObject).getRefId();
    	
    	tooltip.setWidget(new Label(label));
    	
    	// Position label 
    	tooltip.setPopupPosition( (int) x, (int) y);
    	
    	// Label must appear above the canvas
    	tooltip.getElement().getStyle().setZIndex(canvasZIndex + 1); 
    	
    	// Show the label
    	tooltip.show();
    	
    	// Hide after 2 seconds
    	timer.schedule(2000);
    }
    
    private String getObjType() {
    	String type = hoveredObject.getType().toString();
    	if (type.startsWith("Renderable")) {
    		return type.substring("Renderable".length());
    	}
    	return type;
    }
    
    private void fireHoverEvent() {
        HoverEvent event = new HoverEvent();
        event.setHoveredObject(hoveredObject);
        diagramPanel.getCanvas().fireEvent(event);
    }
}
