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
    // This is used for transformation
    protected CanvasTransformation canvasTransformation;
      
    // For view change
    protected ViewChangeEvent viewEvent;
    protected PathwayDiagramPanel diagramPane;
    protected HoverHandler hoverHandler;
    protected SelectionHandler selectionHandler;
	protected CanvasEventInstaller eventInstaller;
	protected boolean greyOutCanvas;
	
    public DiagramCanvas() {
    	canvasTransformation = new CanvasTransformation();
    }
    
    public DiagramCanvas(PathwayDiagramPanel diagramPane) {
    	this(diagramPane, true);
    }
    
    public DiagramCanvas(PathwayDiagramPanel diagramPane, Boolean installEventHandlers) {
        this();
    	
    	this.diagramPane = diagramPane;
    	
    	if (installEventHandlers) {
        	eventInstaller = new CanvasEventInstaller(diagramPane, this);
        	eventInstaller.installEventHandlersForCanvas();
    	}
    }

    public DiagramCanvas(PathwayDiagramPanel diagramPane, CanvasTransformation transformation) {
    	this(diagramPane);
    	
    	canvasTransformation = transformation;
    }
    
    public void resize(Integer width, Integer height) {
    	final Integer BUFFER = 8;
    	
    	Integer adjustedWidth = width - BUFFER;
    	Integer adjustedHeight = height - BUFFER;
    	
    	setSize(adjustedWidth + "px", adjustedHeight + "px");
    	setCoordinateSpaceWidth(adjustedWidth);
    	setCoordinateSpaceHeight(adjustedHeight);
    }
    
    public CanvasTransformation getCanvasTransformation() {
    	return canvasTransformation;
    }
    
    public void translate(double dx, double dy) {
        canvasTransformation.translate(dx, dy);
    	
        fireViewChangeEvent();
    }
        
    protected void fireViewChangeEvent() {
        if (viewEvent == null)
            viewEvent = new ViewChangeEvent();
        
        viewEvent.setScale(canvasTransformation.getScale());
        viewEvent.setTranslateX(canvasTransformation.getTranslateX());
        viewEvent.setTranslateY(canvasTransformation.getTranslateY());
        viewEvent.setWidth(getCoordinateSpaceWidth());
        viewEvent.setHeight(getCoordinateSpaceHeight());
        super.fireEvent(viewEvent);
    }
    
    public double getTranslateX() {
        return canvasTransformation.getTranslateX();
    }
    
    public double getTranslateY() {
        return canvasTransformation.getTranslateY();
    }
    
    public double getScale() {
        return canvasTransformation.getScale();
    }
    
    public void scale(double scaleFactor) {
        canvasTransformation.scale(scaleFactor);
        //Double translateXToKeepOldCentre = -(centreX(previousScale) - (0.5 * newWidth())) * newScale;
    	//Double translateYToKeepOldCentre = -(centreY(previousScale) - (0.5 * newHeight())) * newScale;
    	    	
        fireViewChangeEvent();
    }
        
    public void center(Point point) {
    	canvasTransformation.center(point);
    }
      
    private Double newWidth() {
    	return getCoordinateSpaceWidth() / getScale();
    }
    
    private Double newHeight() {
    	return getCoordinateSpaceHeight() / getScale();
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
        canvasTransformation.reset();
		
        fireViewChangeEvent();
    }

    public void resetTranslate() {
    	canvasTransformation.resetTranslate();
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

    public Point getAbsoluteCoordinates(Integer diagramXCoordinate, Integer diagramYCoordinate) {
    	final Integer x = getAbsoluteXCoordinate(diagramXCoordinate);
    	final Integer y = getAbsoluteYCoordinate(diagramYCoordinate);
    	
    	return new Point(x, y);
    }
    
    public Integer getAbsoluteXCoordinate(Integer diagramCoordinate) {
    	return (int) ((diagramCoordinate * getScale()) + getTranslateX() + getAbsoluteLeft());
    }
    
    public Integer getAbsoluteYCoordinate(Integer diagramCoordinate) {
    	return (int) ((diagramCoordinate * getScale()) + getTranslateY() + getAbsoluteTop());
    }
    
    protected void clean(Context2d c2d) {    	
    	c2d.setTransform(1, 0, 0, 1, 0, 0); // Remove all transforms
    	c2d.clearRect(0, 0, getOffsetWidth() , getOffsetHeight()); // Clear the canvas
    	c2d.setTransform(getScale(), 0, 0, getScale(), getTranslateX(), getTranslateY()); // Set new scale and translations    	
    }

    public Boolean currentViewContainsAtLeastOneGraphObject(List<GraphObject> objects) {		
		Integer x = (int) (-getTranslateX() / getScale());
    	Integer y = (int) (-getTranslateY() / getScale());
    	Integer width = (int) (getCoordinateSpaceWidth() / getScale()); 
    	Integer height = (int) (getCoordinateSpaceHeight() / getScale());
		
    	Bounds diagramBounds = new Bounds(x, y, width, height);
    	
    	for (GraphObject object : objects) {
    		if (objectWithinDiagramBounds(object, diagramBounds))
    			return true;    			
    	}
    	
    	return false;    	
    }
    
    private Boolean objectWithinDiagramBounds(GraphObject object, Bounds diagramBounds) {
    	if (object instanceof Node)
    		return diagramBounds.isColliding(((Node) object).getBounds());
    	else
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
    
	protected class CanvasTransformation {
		protected Double scale;
		protected Double translateX;
		protected Double translateY;
		
		public CanvasTransformation() {
			this(1.0, 0.0, 0.0);
		}
		
		public CanvasTransformation(Double scale, Double translateX, Double translateY) {
			init(scale, translateX, translateY);
		}
		
		private void init(Double scale, Double translateX, Double translateY) {
			this.scale = scale;
			this.translateX = translateX;
			this.translateY = translateY;
		}
		
		public Double getScale() {
			return scale;
		}
		
		public void scale(Double scaleFactor) {
			final Double previousScale = getScale();
			final Double newScale = previousScale * scaleFactor;
			
			if (newScale > Parameters.ZOOMMAX || newScale < Parameters.ZOOMMIN) {
				return;
			}
			
			this.scale = newScale;
		}
		
		public Double getTranslateX() {
			return translateX;
		}
		
		public Double getTranslateY() {
			return translateY;
		}
		
		public void translate(Double deltaX, Double deltaY) {
			translateX += deltaX;
			translateY += deltaY;
		}
		
		public void center(Point point) {
			Point correctedPoint = getCorrectedCoordinates(point);
			
			translate(
				-(correctedPoint.getX() - centerX(getScale())),
				-(correctedPoint.getY() - centerY(getScale()))
			);			
		}
		
		private Double centerX(Double scale) {
			return ((-getTranslateX() / scale) + (getCoordinateSpaceWidth() / scale / 2));
		}
	
		private Double centerY(Double scale) {
			return ((-getTranslateY() / scale) + (getCoordinateSpaceHeight() / scale / 2));
		}
		
		public void resetTranslate() {
			translateX = 0.0;
			translateY = 0.0;
		}
		
		public void reset() {
			init(1.0, 0.0, 0.0);
		}				
	}	
}
