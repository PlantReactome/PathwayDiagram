/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public abstract class HoverHandler {
    protected PathwayDiagramPanel diagramPanel;
    protected GraphObject hoveredObject;
    protected PopupPanel tooltip;
    protected Timer timer;
    
    public HoverHandler(PathwayDiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
        this.tooltip = new PopupPanel();
        tooltip.setStyleName(diagramPanel.getStyle().tooltip());
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
    
    public void hover(Point hoverPoint, List<GraphObject> objects) {
        // Only one object should be hovered over
        GraphObject hovered = null;

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
        	   	diagramPanel.getElement().getStyle().setCursor(Cursor.DEFAULT);
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
        
        	//fireHoverEvent();
        }	
    }
        
    protected void showTooltip() {
       	if (hoveredObject.getType() == GraphObjectType.FlowLine)
    		return;
    	
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
    	
    	// Position label 
    	tooltip.setPopupPosition( (int) x, (int) y);
    	
    	// Label must appear above the canvas
    	tooltip.getElement().getStyle().setZIndex(canvasZIndex + 1); 
    	
    	// Show the label
    	tooltip.show();
    	
    	// Hide after 2 seconds
    	timer.schedule(2000);
    }
    
    protected String getLabel() {
    	return getObjType() + ": " + hoveredObject.getDisplayName();  
    }	
    	
    private String getObjType() {    	
    	String type = hoveredObject.getType().toString();
    	if (type.startsWith("Renderable")) {
    		return type.substring("Renderable".length());
    	}
    	return type;
    }
    
    protected void fireHoverEvent() {
        HoverEvent event = new HoverEvent();
        event.setHoveredObject(hoveredObject);
        diagramPanel.getCanvas().fireEvent(event);
    }

    
	public abstract GraphObject hover(Point hoveredPoint);
	
}
