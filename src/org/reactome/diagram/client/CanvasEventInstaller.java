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
                int[] position = getPositionInTouch(event);
                if (position == null)
                    return;
                previousX = position[0];
                previousY = position[1];
                isDragging = true;
                event.stopPropagation();
            }
        };
        diagramPane.getCanvas().addTouchStartHandler(touchStartHandler);
        
        TouchMoveHandler touchMoveHandler = new TouchMoveHandler() {
            
            @Override
            public void onTouchMove(TouchMoveEvent event) {
                if (isDragging) {
                    event.preventDefault();
                    int[] position = getPositionInTouch(event);
                    int dx = position[0] - previousX;
                    int dy = position[1] - previousY;
                    diagramPane.translate(dx, dy);
                    diagramPane.update();
                    previousX = position[0];
                    previousY = position[1];
                    event.stopPropagation();
                }
            }
        };
        diagramPane.getCanvas().addTouchMoveHandler(touchMoveHandler);
        
        TouchEndHandler touchEndHandler = new TouchEndHandler() {
            
            @Override
            public void onTouchEnd(TouchEndEvent event) {
                isDragging = false;
                event.stopPropagation();
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
                previousX = event.getX();
                previousY = event.getY();
                isDragging = true;
                event.stopPropagation();
//                System.out.println(previousX + ", " + prevuousY);
            }
        };
        MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (isDragging) {
                    // Do panning
                    int x = event.getX();
                    int y = event.getY();
                    int dx = x - previousX;
                    int dy = y - previousY;
                    diagramPane.translate(dx, dy);
                    diagramPane.update();
                    previousX = x;
                    previousY = y;
                    event.stopPropagation();
                }
            }
        };
        MouseUpHandler mouseUpHandler = new MouseUpHandler() {
            
            @Override
            public void onMouseUp(MouseUpEvent event) {
                isDragging = false;
                event.stopPropagation();
            }
        };
        PlugInSupportCanvas canvas = diagramPane.getCanvas();
        canvas.addMouseDownHandler(mouseDownHandler);
        canvas.addMouseUpHandler(mouseUpHandler);
        canvas.addMouseMoveHandler(mouseMoveHandler);
    }
    
    
}
