/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.event.ViewChangeEvent;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A specialized PlugInSupportCanvas that is used to draw CanvasPathway only.
 * @author gwu
 *
 */
public abstract class DiagramCanvas extends PlugInSupportCanvas {
    // These are used for translate
    protected double translateX;
    protected double translateY;
    // This is for scale
    protected double scale;
    // For view change
    protected ViewChangeEvent viewEvent;
        
    public DiagramCanvas() {
        scale = 1.0d;
    }
        
    public void translate(double dx, double dy) {
        this.translateX += dx;
        this.translateY += dy;
        fireViewChangeEvent();
    }
    
    protected void fireViewChangeEvent() {
        if (viewEvent == null)
            viewEvent = new ViewChangeEvent();
        viewEvent.setScale(scale);
        viewEvent.setTranslateX(translateX);
        viewEvent.setTranslateY(translateY);
        viewEvent.setWidth(getCoordinateSpaceWidth());
        viewEvent.setHeight(getCoordinateSpaceHeight());
        super.fireEvent(viewEvent);
    }
    
    public double getTranslateX() {
        return translateX;
    }
    
    public double getTranslateY() {
        return translateY;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public void scale(double scale) {
        this.scale *= scale;
        fireViewChangeEvent();
    }
    
    public void reset() {
        resetTranslate();
        scale = 1.0d;
        fireViewChangeEvent();
    }

    public void resetTranslate() {
    	translateX = 0.0d;
    	translateY = 0.0d;
    }
    
    /**
     * Update drawing.
     */
    public abstract void update(); 

    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected abstract void updateOthers(Context2d c2d);
    
}
