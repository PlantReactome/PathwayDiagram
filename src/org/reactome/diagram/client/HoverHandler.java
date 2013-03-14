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
    protected DiagramCanvas canvas;
    protected Point hoverPoint;
    protected GraphObject hoveredObject;
    protected boolean isOverSameObject;    
    protected PopupPanel tooltip;
    protected Timer timer;
    Boolean timeElapsed;
    
    public HoverHandler(PathwayDiagramPanel diagramPanel, DiagramCanvas canvas) {
        this.diagramPanel = diagramPanel;
        this.canvas = canvas;
        
        this.tooltip = new PopupPanel();
        tooltip.setStyleName(diagramPanel.getStyle().tooltip());

        this.timer = new Timer() {

			@Override
			public void run() {
				tooltip.hide();				
				timeElapsed = true;
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
    
    public void clearHoveredObject() {
    	hoveredObject = null;
    }
    
    public void hover(List<GraphObject> objects) {
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

        isOverSameObject = false;
        // Remove displayed label if just hovering over empty space
        if (hovered == null) {
        	if (hoveredObject != null) {
        		tooltip.hide();
        	   	timer.cancel();
        	   	diagramPanel.getElement().getStyle().setCursor(Cursor.DEFAULT);
        	   	hoveredObject.setIsHovered(false);
        	   	hoveredObject = null;
        	}
        	return;
        } else {
        	// If there is a previously hovered object
        	if (hoveredObject != null) {
        		// Do nothing if the new object is the same as the old        		
        		if (hovered == hoveredObject) {
        			isOverSameObject = true; 
        			return;
        		}
        		hoveredObject.setIsHovered(false);
        		tooltip.hide();
        		timer.cancel();
        	} 
        	
        	// Set new hovered object
        	hoveredObject = hovered;
        	        
        	//fireHoverEvent();
        }	
    }
        
    protected void showTooltip() {
       	if (hoveredObject.getType() == GraphObjectType.FlowLine)
    		return;

    	//Point objPos = hoveredObject.getPosition();
    	
    	double scale = canvas.getScale();
    	double translateX = canvas.getTranslateX();
    	double translateY = canvas.getTranslateY();
    	
    	// Compensate for canvas translation, scale and container position
    	double x = (hoverPoint.getX() * scale) + translateX + this.diagramPanel.getPathwayCanvas().getAbsoluteLeft();
    	double y = (hoverPoint.getY() * scale) + translateY + this.diagramPanel.getPathwayCanvas().getAbsoluteTop();
    	int canvasZIndex;
    	    	
    	try {
    		canvasZIndex = Integer.parseInt(diagramPanel.getElement().getStyle().getZIndex()); 
    	} catch (NumberFormatException nfe) {
    		canvasZIndex = 0;
    	}
    	
    	// Position label 
    	tooltip.setPopupPosition( (int) x + 2, (int) y + 2);
    	
    	// Label must appear above the canvas
    	tooltip.getElement().getStyle().setZIndex(canvasZIndex + 1); 
    	
    	// Show the label
    	tooltip.show();
    	
    	// Hide after 2 seconds
    	timeElapsed = false;
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
        diagramPanel.fireEvent(event);
    }

    
	public abstract GraphObject hover(Point hoveredPoint);
	
}
