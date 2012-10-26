/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A specialized PlugInSupportCanvas that is used to draw CanvasPathway only.
 * @author gwu
 *
 */
public class PathwayCanvas extends PlugInSupportCanvas {
	public final int MOVEX = 100;
	public final int MOVEY = 100;
	public final double ZOOMOUT = 0.8d;
	public final double ZOOMIN = 1.25d;
	
	
    // Pathway to be displayed
    private CanvasPathway pathway;
    // These are used for translate
    private double translateX;
    private double translateY;
    // This is for scale
    private double scale;
    // For view change
    protected ViewChangeEvent viewEvent;
    // Used to draw pathway
    private PathwayCanvasDrawer drawer;
    
    public PathwayCanvas() {
        scale = 1.0d;
        drawer = new PathwayCanvasDrawer();
    }
    
    public void setPathway(CanvasPathway pathway) {
        this.pathway = pathway;
        fireViewChangeEvent();
    }
    
    public CanvasPathway getPathway() {
        return this.pathway;
    }
    
    public void translate(double dx, double dy) {
        this.translateX += dx;
        this.translateY += dy;
        fireViewChangeEvent();
    }
    
    private void fireViewChangeEvent() {
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
    public void update() {
        if (pathway == null)
            return;
        Context2d c2d = getContext2d();
        c2d.save();
        c2d.clearRect(0.0d, 
                      0.0d, 
                      getOffsetWidth(),
                      getOffsetHeight());
        c2d.translate(translateX, translateY);
        c2d.scale(scale, scale);
        drawer.drawPathway(pathway, 
                           this,
                           c2d);
        updateOthers(c2d);
        c2d.restore();
    }

    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
