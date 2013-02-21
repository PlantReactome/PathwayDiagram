/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.model.GraphObject;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

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
    protected PathwayDiagramPanel diagramPane;
    protected HoverHandler hoverHandler;
    protected SelectionHandler selectionHandler;
	protected CanvasEventInstaller eventInstaller;
        
    public DiagramCanvas() {
    	scale = 1.0d;
    }
    
    public DiagramCanvas(PathwayDiagramPanel diagramPane) {
        this.diagramPane = diagramPane;
    	scale = 1.0d;
        eventInstaller = new CanvasEventInstaller(diagramPane, this);
        eventInstaller.installDiagramEventHandlers();
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
    
    public HoverHandler getHoverHandler() {
		return hoverHandler;    	
    }
    
    public SelectionHandler getSelectionHandler() {
		return selectionHandler;    	
    }
    
    public CanvasEventInstaller getEventInstaller() {
		return eventInstaller;
    	
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

    public Point getCorrectedCoordinates(Point point) {
    	return getCorrectedCoordinates(point.getX(), point.getY());
    }
    
    public Point getCorrectedCoordinates(double x, double y) {
    	double scale = getScale();
    	
    	double correctedX = x - getTranslateX();
    	correctedX /= scale;
    	
    	double correctedY = y - getTranslateY();
    	correctedY /= scale;
    	
    	return new Point(correctedX, correctedY); 
    	
    }	
    
    protected void clean(Context2d c2d) {
    	//Context2d c2d = getContext2d();
    	
    	c2d.clearRect(0, 0, getOffsetWidth(), getOffsetHeight());
    	c2d.translate(translateX, translateY);
    	c2d.scale(scale, scale);
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

	public abstract List<GraphObject> getGraphObjects();	
    
}
