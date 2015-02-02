/*

 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.event.ViewChangeEvent.ResizeEvent;
import org.reactome.diagram.event.ViewChangeEvent.TranslationEvent;
import org.reactome.diagram.event.ViewChangeEvent.ZoomEvent;
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
    
    public DiagramCanvas(PathwayDiagramPanel diagramPane, boolean installEventHandlers) {
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
    
    public void resize(int width, int height) {
    	setSize(width + "px", height + "px");
    	setCoordinateSpaceWidth(width);
    	setCoordinateSpaceHeight(height);
    }
    
    public CanvasTransformation getCanvasTransformation() {
    	return canvasTransformation;
    }
    
    public void translate(double dx, double dy, boolean gradual) {
        canvasTransformation.translate(dx, dy, gradual);
    }
        
    protected void fireViewChangeEvent() {
        viewEvent = new ViewChangeEvent(
        	new ZoomEvent(getScale()),
        	new TranslationEvent(getTranslateX(), getTranslateY()),
        	new ResizeEvent(getCoordinateSpaceWidth(), getCoordinateSpaceHeight())
        );
        fireEvent(viewEvent);
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
    }
    
    public void scale(double scaleFactor, Point point) {
        canvasTransformation.scale(scaleFactor, point);
    }
        
    public void center(Point point, boolean entityCoordinates) {
    	canvasTransformation.center(point, entityCoordinates);
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

    public boolean currentViewContainsAtLeastOneGraphObject(List<GraphObject> objects) {
		for (GraphObject object : objects) {
			if (objectWithinDiagramBounds(object))
				return true;
		}
		
		return false;
	}

	public Point getAbsoluteCoordinates(double diagramXCoordinate, double diagramYCoordinate) {
    	final double x = getAbsoluteXCoordinate(diagramXCoordinate);
    	final double y = getAbsoluteYCoordinate(diagramYCoordinate);
    	
    	return new Point(x, y);
    }
    
    public double getAbsoluteXCoordinate(double diagramCoordinate) {
    	return (diagramCoordinate * getScale()) + getTranslateX() + getAbsoluteLeft();
    }
    
    public double getAbsoluteYCoordinate(double diagramCoordinate) {
    	return (diagramCoordinate * getScale()) + getTranslateY() + getAbsoluteTop();
    }
    
    protected void clean(Context2d c2d) {    	
    	c2d.setTransform(1, 0, 0, 1, 0, 0); // Remove all transforms
    	c2d.clearRect(0, 0, getOffsetWidth() , getOffsetHeight()); // Clear the canvas
    	c2d.setTransform(getScale(), 0, 0, getScale(), getTranslateX(), getTranslateY()); // Set new scale and translations
    	//System.out.println("Clean - " + getClass() + " Scale " + getScale() + " TranslateX - " + getTranslateX() + " TranslateY - " + getTranslateY());
    }

    public Bounds getViewBounds() {
    	int x = (int) (-getTranslateX() / getScale());
    	int y = (int) (-getTranslateY() / getScale());
    	int width = (int) (getCoordinateSpaceWidth() / getScale());
    	int height = (int) (getCoordinateSpaceHeight() / getScale());
    	
    	return new Bounds(x, y, width, height);
    }
    
    private Boolean objectWithinDiagramBounds(GraphObject object) {
    	if (object instanceof Node)
    		return getViewBounds().isColliding(((Node) object).getBounds());
    	else
    		return getViewBounds().contains(object.getPosition());
    }
    
    /**
     * Update drawing.
     */
    public abstract void update(); 

    /**
     * Does the actual drawing to a specified 2D context
     */
    public abstract void drawCanvasLayer(Context2d c2d);
    
    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected abstract void updateOthers(Context2d c2d);

	public abstract List<GraphObject> getObjectsForRendering();	
    
	public class CanvasTransformation {
		protected double scale;
		protected double translateX;
		protected double translateY;
		
		public CanvasTransformation() {
			this(1.0, 0.0, 0.0);
		}
		
		public CanvasTransformation(double scale, double translateX, double translateY) {
			init(scale, translateX, translateY);
		}
		
		private void init(double scale, double translateX, double translateY) {
			this.scale = scale;
			this.translateX = translateX;
			this.translateY = translateY;
		}
		
		public double getScale() {
			return scale;
		}
		
		private double applyScaleFactor(double scaleFactor) {
			final double newScale = getScale() * scaleFactor;
			
			if (newScale > Parameters.ZOOMMAX)
				return Parameters.ZOOMMAX;
				
			//if (newScale < Parameters.ZOOMMIN)
				//return Parameters.ZOOMMIN;
		
			return newScale;
		}
			
		public void scale(double scaleFactor) {	
			scale(scaleFactor, new Point(getCoordinateSpaceWidth() / 2, getCoordinateSpaceHeight() / 2));
		}
		
		public void scale(double scaleFactor, Point point) {
			zoomToPoint(scaleFactor, point);
		}
		
		private void zoomToPoint(double scaleFactor, Point point) {
			Point zoomPoint = getCorrectedCoordinates(point.getX(), point.getY());

			this.scale = applyScaleFactor(scaleFactor);

			double deltaX = getAbsoluteXCoordinate(zoomPoint.getX()) - 
							DiagramCanvas.this.getAbsoluteLeft() - 
							point.getX();
			double deltaY = getAbsoluteYCoordinate(zoomPoint.getY()) - 
							DiagramCanvas.this.getAbsoluteTop() - 
							point.getY();
						
			translate(-deltaX, -deltaY, false);
		}		
		
		public double getTranslateX() {
			return translateX;
		}
		
		public double getTranslateY() {
			return translateY;
		}
		
		public void translate(double deltaX, double deltaY, boolean gradual) {
			translateX += deltaX;
			translateY += deltaY;			
		}
		
		public void center(Point point, boolean entityCoordinates) {
			double deltaX;
			double deltaY;
			if (entityCoordinates) {
				Point currentCentre = getViewBounds().getCentre();
				deltaX = (currentCentre.getX() - point.getX()) * getScale();
				deltaY = (currentCentre.getY() - point.getY()) * getScale();
			} else {
				Point correctedPoint = getCorrectedCoordinates(point);
				deltaX = (centerX(getScale()) - correctedPoint.getX()) * getScale();
				deltaY = (centerY(getScale()) - correctedPoint.getY()) * getScale();
			}	
			translate(deltaX, deltaY, true);
		}
		
		private double centerX(Double scale) {
			return ((-getTranslateX() / scale) + (getCoordinateSpaceWidth() / scale / 2));
		}
	
		private double centerY(Double scale) {
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
