/*
 * Created on Oct 11, 2011
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventHandler;

/**
 * This helper class is used to set up event handler for the canvas used in PathwayDiagramPanel.
 * @author gwu
 *
 */
public class CanvasEventInstaller {
    private PathwayDiagramPanel diagramPane;
    // The following properties are used for panning
    private boolean isDragging;
    private boolean isMouseDown;
    private int previousX;
    private int previousY;
    
    public CanvasEventInstaller(PathwayDiagramPanel diagramPanel) {
        this.diagramPane = diagramPanel;
    }
    
    public void installHandlers() {
        addMouseHandlers();
        addTouchHandlers();
    }
    
    private void addTouchHandlers() {
        TouchStartHandler touchStartHandler = new TouchStartHandler() {
            
            @Override
            public void onTouchStart(TouchStartEvent event) {
                event.stopPropagation();
                event.preventDefault();
                int[] position = getPositionInTouch(event);
                if (position == null)
                    return;
                mouseDown(position[0], position[1]);
            }
        };
        diagramPane.getCanvas().addTouchStartHandler(touchStartHandler);
        
        TouchMoveHandler touchMoveHandler = new TouchMoveHandler() {
            
            @Override
            public void onTouchMove(TouchMoveEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    event.preventDefault();
                    int[] position = getPositionInTouch(event);
                    mouseMove(position[0], position[1]);
                }
            }
        };
        diagramPane.getCanvas().addTouchMoveHandler(touchMoveHandler);
        
        TouchEndHandler touchEndHandler = new TouchEndHandler() {
            
            @Override
            public void onTouchEnd(TouchEndEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    event.preventDefault();
                    int[] pos = getPositionInTouch(event);
                    mouseUp(pos[0], pos[1]);
                }
            }
        };
        diagramPane.getCanvas().addTouchEndHandler(touchEndHandler);
    }
    
    private int[] getPositionInTouch(TouchEvent<? extends EventHandler> event) {
        JsArray<Touch> touches = event.getTouches();
        if (touches == null || touches.length() == 0)
            return null;
        // Get the first touch
        Touch touch = touches.get(0);
        int x = touch.getRelativeX(diagramPane.getCanvas().getElement());
        int y = touch.getRelativeY(diagramPane.getCanvas().getElement());
        return new int[]{x, y};
    }

    private void addMouseHandlers() {
        // To record the original position
        MouseDownHandler mouseDownHandler = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                isMouseDown = true;
                event.stopPropagation();
                mouseDown(event.getX(), event.getY());
            }
        };
        MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    mouseMove(event.getX(), event.getY());
                }
            }
        };
        MouseUpHandler mouseUpHandler = new MouseUpHandler() {
            
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    mouseUp(event.getX(), event.getY());
                }
            }
        };
        MouseOutHandler mouseOutHandler = new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    mouseOut(event.getX(), event.getY());
                }
            }
        };
        PlugInSupportCanvas canvas = diagramPane.getCanvas();
        canvas.addMouseDownHandler(mouseDownHandler);
        canvas.addMouseUpHandler(mouseUpHandler);
        canvas.addMouseMoveHandler(mouseMoveHandler);
        canvas.addMouseOutHandler(mouseOutHandler);
    }
    
    private void mouseDown(int x, int y) {
        previousX = x;
        previousY = y;
        isMouseDown = true;
    }
    
    private void mouseMove(int x, int y) {
        if (isMouseDown) {
            // Do panning
            int dx = x - previousX;
            int dy = y - previousY;
            diagramPane.translate(dx, dy);
            diagramPane.update();
            previousX = x;
            previousY = y;
            isDragging = true;
        }
    }
    
    private void mouseUp(int x, int y) {
        if (isMouseDown)
            isMouseDown = false;
        if (isDragging) {
            isDragging = false;
        }
        else { // Do click selection
            //TODO: selection cannot work under iPad. Need to check touchEnd event.
            diagramPane.select(x, y);
        }
    }
    
    private void mouseOut(int x, int y) {
        if (isMouseDown)
            isMouseDown = false;
        if (isDragging)
            isDragging = false;
    }
    
}
