/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.event.ViewChangeEvent.ResizeEvent;
import org.reactome.diagram.event.ViewChangeEvent.TranslationEvent;
import org.reactome.diagram.event.ViewChangeEvent.ZoomEvent;
import org.reactome.diagram.event.ViewChangeEventHandler;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This customized Canvas is used as an overview.
 * @author gwu
 *
 */
public class OverviewCanvas extends PathwayCanvas implements ViewChangeEventHandler {
	private Bounds viewRect;
	// A flag to block an event bouncing back
    private boolean isFromOverview;
    // To re-draw selected objects so that they can be shown more apparently
    // Otherwise, it is difficult to see if an object is selected in an overview
    private List<GraphObject> selectedObjects;

    public OverviewCanvas(PathwayDiagramPanel diagramPanel) {
        super(diagramPanel, false);
    	
    	viewRect = new Bounds();
    	canvasTransformation = new OverviewCanvasTransformation();
        
    	EventHandlers eventHandlers = new EventHandlers();
        eventHandlers.installHandlers();
    }
    
    public void setIsFromOverview(boolean isFromOverview) {
    	this.isFromOverview = isFromOverview;
    }
    
    public boolean isFromOverview() {
    	return isFromOverview;
    }
    
    @Override
    public void setPathway(CanvasPathway pathway) {
        super.setPathway(pathway);
        // Re-set selected objects
        if (selectedObjects != null)
            selectedObjects.clear();
        if (pathway == null)
            return;
        // Need to set scale automatically so that the whole pathway can be
        // drawn in this canvas.
        Bounds size = pathway.getPreferredSize();
        // The following statements are based on the original JavaScript implementation
        // based on images
        double larger = Math.max(size.getWidth(), size.getHeight());
        double scale = org.reactome.diagram.view.Parameters.OVERVIEW_SIZE / larger;
        setCoordinateSpaceWidth((int)(size.getWidth() * scale));
        setCoordinateSpaceHeight((int)(size.getHeight() * scale));
        
//        int width = getCoordinateSpaceWidth();
//        double scale = (double) width / size.getWidth();
//        // resize the height
//        double height = (double) size.getHeight() / size.getWidth() * width;
//        setCoordinateSpaceHeight((int)height);
        
        reset();
        scale(scale);
        updatePosition();
    }
    
	public void updatePosition() {
        // Need to make sure it is placed at the correct position
        //TODO: This is hard-coded and should be changed soon
        AbsolutePanel container = (AbsolutePanel) getParent();
        int height = getCoordinateSpaceHeight();
        container.setWidgetPosition(this, 
                                    4, 
                                    container.getOffsetHeight() - height - 7);
    }

    @Override
    public void update() {
    	Context2d c2d = getContext2d();
    	
        super.update();
        
        // Draw an rectangle for the view
        // Need a decent line to show the rectangle
        c2d.setLineWidth(1.0d); // After the super.update call, all states should be reset to the original.
        // Use blue line
        CssColor blue = CssColor.make(0, 0, 255);
        c2d.setStrokeStyle(blue);
        c2d.beginPath();
        c2d.rect(viewRect.getX(),
                 viewRect.getY(),
                 viewRect.getWidth(),
                 viewRect.getHeight());
        c2d.closePath();
        c2d.stroke();
    }

    /**
     * Update drawing of selected objects so that they can be seen easily.
     */
    @Override
    protected void updateOthers(Context2d c2d) {
    	if (selectedObjects == null || selectedObjects.size() == 0)
            return;
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        for (GraphObject obj :  selectedObjects) {
            if (obj instanceof Node) {
                NodeRenderer nodeRenderer = viewFactory.getNodeRenderer((Node)obj);
                nodeRenderer.setAbsoluteLineWidth(1.0d / getScale());
                nodeRenderer.render(c2d, (Node)obj);
                nodeRenderer.setAbsoluteLineWidth(null);
            }
            else if (obj instanceof HyperEdge) {
                HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere((HyperEdge)obj);
                edgeRenderer.setAbsoluteLineWidth(1.0d / getScale());
                edgeRenderer.render(c2d, (HyperEdge)obj);
                edgeRenderer.setAbsoluteLineWidth(null);
            }
        }
    }

    @Override
    public void onViewChange(ViewChangeEvent event) {
        if (isFromOverview()) {
            return;
        }
        
        double scale = event.getZoomEvent().getScale();
        double x = -event.getTranslationEvent().getTranslateX() / scale;
        double y = -event.getTranslationEvent().getTranslateY() / scale;
        double width = event.getResizeEvent().getWidth() / scale;
        double height = event.getResizeEvent().getHeight() / scale;
        double currentScale = getScale();
        viewRect.setX((int)(x * currentScale));
        viewRect.setY((int)(y * currentScale));
        viewRect.setWidth((int)(width * currentScale));
        viewRect.setHeight((int)(height * currentScale));
//        System.out.println("ViewRect: " + viewRect.toString());
        update();
    }
    
    /**
     * In the overview, the view can be translated only. It cannot be scaled.
     * @param translateX
     * @param translateY
     */
    private void fireViewChangeEvent(double translateX, double translateY) {
        setIsFromOverview(true);
        viewEvent = new ViewChangeEvent(
        	new ZoomEvent(0, getScale()),
        	new TranslationEvent(translateX, translateY),
        	new ResizeEvent(getCoordinateSpaceWidth(), getCoordinateSpaceHeight())
        );

        //isFromOverview = true;
        fireEvent(viewEvent);
    }
    
    public void setSelectedObjects(List<GraphObject> objects) {
        if (selectedObjects == null)
            selectedObjects = new ArrayList<GraphObject>();
        else
            selectedObjects.clear();
        if (objects != null)
            selectedObjects.addAll(objects);
    }
    
    /**
     * This inner private class is used to handle dragging events for overview.
     * @author gwu
     *
     */
    private class EventHandlers {
        private boolean isDragging;
        private boolean isMouseDown;
        private boolean isInViewRect;
        
        // The position when mouse is down
        private double x0;
        private double y0;
        private double prevX;
        private double prevY;
        
        public EventHandlers() {
        }
        
        void installHandlers() {
            addMouseEventHandlers();
            addTouchEventHandlers();
        }
        
        private void addMouseEventHandlers() {
            OverviewCanvas.this.addMouseDownHandler(new MouseDownHandler() {
                
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    pressDown(event.getX(), event.getY());
                }
            });
            OverviewCanvas.this.addMouseOutHandler(new MouseOutHandler() {
                
                @Override
                public void onMouseOut(MouseOutEvent event) {
                	outOfBounds(event.getX(), event.getY());
                }
            });
            
            OverviewCanvas.this.addMouseMoveHandler(new MouseMoveHandler() {
                
                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (isMouseDown && isInViewRect) {
                    	move(event.getX(), event.getY());
                    }
                }
            });
            
            OverviewCanvas.this.addMouseUpHandler(new MouseUpHandler() {
                
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    onRelease(event.getX(), event.getY());
                }
            });
        }
        
        private void stopDragging() {
        	isDragging = false;
        	fireViewChangeEvent(viewRect.getX() - x0,
        					viewRect.getY() - y0); 
        	
        }
        
        //TODO: To implement this soon!
        private void addTouchEventHandlers() {
        	OverviewCanvas.this.addTouchStartHandler(new TouchStartHandler() {

				@Override
				public void onTouchStart(TouchStartEvent event) {
					JsArray<Touch> touches = event.getTouches();
					
					if (touches == null || touches.length() == 0)
						return;
					
					Touch touch = touches.get(0);
					
					pressDown(touch.getRelativeX(OverviewCanvas.this.getElement()),
							  touch.getRelativeY(OverviewCanvas.this.getElement()));
				}
        		
        	});
        	
        	OverviewCanvas.this.addTouchMoveHandler(new TouchMoveHandler() {

				@Override
				public void onTouchMove(TouchMoveEvent event) {
					JsArray<Touch> touches = event.getTouches();
					
					if (touches == null || touches.length() == 0)
						return;
						
					Touch touch = touches.get(0);
					
					move(touch.getRelativeX(OverviewCanvas.this.getElement()),
						 touch.getRelativeY(OverviewCanvas.this.getElement()));
				}
        		
        	});
        	
        	OverviewCanvas.this.addTouchEndHandler(new TouchEndHandler() {

				@Override
				public void onTouchEnd(TouchEndEvent event) {
					onRelease(prevX, prevY);
				}
        		
        	});
        	
        }
        
        private void pressDown(int x, int y) {
            isMouseDown = true;
        
            if(viewRect.contains(x, y)) 
            	isInViewRect = true;
                    	
            prevX = x;
            prevY = y;
            x0 = viewRect.getX();
            y0 = viewRect.getY();     
        }
        
        private void outOfBounds(int x, int y) {    
        	isMouseDown = false;
            isInViewRect = false;
            
            if (isDragging) {
            	stopDragging();
            }	
        }
        
        private void move(int x, int y) {
        	if (isMouseDown && isInViewRect) {
        		// Do dragging
                isDragging = true;
                double dx = x - prevX;
                double dy = y - prevY;
                viewRect.translate(dx, dy);
                update();
                fireViewChangeEvent(viewRect.getX() - x0, viewRect.getY() - y0);
                        
                prevX = x;
                prevY = y;
                x0 = viewRect.getX();
                y0 = viewRect.getY();
            }
        }
        
        private void onRelease(double x, double y) {
        	isMouseDown = false;
            isInViewRect = false;

            if (isDragging) {
               stopDragging();
            } else {
               double dx = x - x0;
               double dy = y - y0;
               viewRect.translate(dx, dy);
               viewRect.translate(-viewRect.getWidth() / 2.0, -viewRect.getHeight() / 2.0);
               update();
               fireViewChangeEvent(viewRect.getX() - x0, viewRect.getY() - y0);
            }
        }
    }
    
    private class OverviewCanvasTransformation extends CanvasTransformation {
    	
    	public OverviewCanvasTransformation() {
    		super();
    	}
    	
    	@Override
    	public void scale(double scaleFactor) {
    		this.scale *= scaleFactor; 
    	}
    }
}
