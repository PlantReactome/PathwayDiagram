/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.event.ViewChangeEventHandler;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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

    public OverviewCanvas() {
        viewRect = new Bounds();
        EventHandlers eventHandlers = new EventHandlers();
        eventHandlers.installHandlers();
    }
    
    @Override
    public void setPathway(CanvasPathway pathway) {
        super.setPathway(pathway);
        // Need to set scale automatically so that the whole pathway can be
        // drawn in this canvas.
        Bounds size = pathway.getPreferredSize();
        int width = getCoordinateSpaceWidth();
        double scale = (double) width / size.getWidth();
        // resize the height
        double height = (double) size.getHeight() / size.getWidth() * width;
        setCoordinateSpaceHeight((int)height);
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
        super.update();
        // Draw an rectangle for the view
        // Need a decent line to show the rectangle
        Context2d c2d = getContext2d();
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

    @Override
    public void onViewChange(ViewChangeEvent event) {
        if (isFromOverview) {
            isFromOverview = false;
            return;
        }
        double scale = event.getScale();
        double x = -event.getTranslateX() / scale;
        double y = -event.getTranslateY() / scale;
        double width = event.getWidth() / scale;
        double height = event.getHeight() / scale;
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
     * @param transalteY
     */
    private void fireViewChangeEvent(double translateX, double translateY) {
        if (viewEvent == null)
            viewEvent = new ViewChangeEvent();
        viewEvent.setTranslateX(translateX);
        viewEvent.setTranslateY(translateY);
        viewEvent.setScale(getScale());
        isFromOverview = true;
        handlerManager.fireEvent(viewEvent);
    }
    
    /**
     * This inner private class is used to handle dragging events for overview.
     * @author gwu
     *
     */
    private class EventHandlers {
        private boolean isDragging;
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
                    if(viewRect.contains(event.getX(), event.getY())) {
                        isDragging = true;
                        prevX = event.getX();
                        prevY = event.getY();
                        x0 = viewRect.getX();
                        y0 = viewRect.getY();
                    }
                }
            });
            OverviewCanvas.this.addMouseOutHandler(new MouseOutHandler() {
                
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    if (isDragging)
                        isDragging = false;
                }
            });
            OverviewCanvas.this.addMouseMoveHandler(new MouseMoveHandler() {
                
                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (isDragging) {
                        double dx = event.getX() - prevX;
                        double dy = event.getY() - prevY;
                        viewRect.translate(dx, dy);
                        update();
                        prevX = event.getX();
                        prevY = event.getY();
                    }
                }
            });
            OverviewCanvas.this.addMouseUpHandler(new MouseUpHandler() {
                
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    if (isDragging) {
                        isDragging = false;
                        fireViewChangeEvent(viewRect.getX() - x0,
                                            viewRect.getY() - y0);
                    }
                }
            });
        }
        
        //TODO: To implement this soon!
        private void addTouchEventHandlers() {
            
        }
        
    }
}
