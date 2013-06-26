/*

 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.Parameters;

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
    protected double previousScale;
    // For view change
    protected ViewChangeEvent viewEvent;
    protected PathwayDiagramPanel diagramPane;
    protected HoverHandler hoverHandler;
    protected SelectionHandler selectionHandler;
	protected CanvasEventInstaller eventInstaller;
	protected boolean greyOutCanvas;
	
    public DiagramCanvas() {
    	scale = 1.0d;
    }
    
    public DiagramCanvas(PathwayDiagramPanel diagramPane) {
        this.diagramPane = diagramPane;
    	scale = 1.0d;
        eventInstaller = new CanvasEventInstaller(diagramPane, this);
        eventInstaller.installEventHandlersForCanvas();
    }

    public void resize(Integer width, Integer height) {
    	final Integer BUFFER = 8;
    	
    	Integer adjustedWidth = width - BUFFER;
    	Integer adjustedHeight = height - BUFFER;
    	
    	setSize(adjustedWidth + "px", adjustedHeight + "px");
    	setCoordinateSpaceWidth(adjustedWidth);
    	setCoordinateSpaceHeight(adjustedHeight);
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
        Double previousScale = this.scale;
        Double newScale = this.scale * scale;
        
        if (newScale > Parameters.ZOOMMAX || newScale < Parameters.ZOOMMIN)
        	return;
    	
        this.scale = newScale;
        
    	translateX = -(centreX(previousScale) - (0.5 * newWidth())) * newScale;
    	translateY = -(centreY(previousScale) - (0.5 * newHeight())) * newScale;
    	
        fireViewChangeEvent();
    }
    
    private Double centreX(Double previousScale) {
    	return ((-translateX / previousScale) + (getCoordinateSpaceWidth() / previousScale / 2));
    }
    
    private Double centreY(Double previousScale) {
    	return ((-translateY / previousScale) + (getCoordinateSpaceHeight() / previousScale / 2));
    }
    
    private Double newWidth() {
    	return getCoordinateSpaceWidth() / scale;
    }
    
    private Double newHeight() {
    	return getCoordinateSpaceHeight() / scale;
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
    
    public boolean isGreyOutCanvas() {
		return greyOutCanvas;
	}

	public void setGreyOutCanvas(boolean greyOutCanvas) {
		this.greyOutCanvas = greyOutCanvas;
				 
		String greyOut = this.diagramPane.getStyle().greyOutCanvas();
		if (greyOutCanvas) {
			setStyleName(greyOut);
		} else {
			removeStyleName(greyOut);
		}			
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

    public Boolean currentViewContainsGraphObject(GraphObject object) {		
		Integer x = (int) (-translateX / scale); 
    	Integer y = (int) (-translateY / scale);
    	Integer width = (int) (getCoordinateSpaceWidth() / scale); 
    	Integer height = (int) (getCoordinateSpaceHeight() / scale);
		
    	Bounds diagramBounds = new Bounds(x, y, width, height);
    	
    	
    	if (object instanceof Node)
    		return diagramBounds.isColliding(((Node) object).getBounds());
    	
    	return diagramBounds.contains(object.getPosition());    	
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
