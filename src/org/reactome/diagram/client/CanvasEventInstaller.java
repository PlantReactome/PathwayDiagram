/*
 * Created on Oct 11, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.event.ViewChangeEventHandler;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.touch.client.Point;

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
//        Window.addResizeHandler(new ResizeHandler() {
//            
//            @Override
//            public void onResize(ResizeEvent event) {
//                diagramPane.setSize(event.getWidth(), 
//                                    event.getHeight());
////                diagramPane.update();
//            }
//        });

        final OverviewCanvas overview = diagramPane.getOverview();
        final PathwayCanvas canvas = diagramPane.getCanvas();
        canvas.addHandler(overview, 
                          ViewChangeEvent.TYPE);
        // To catch overview dragging
        ViewChangeEventHandler overviewEventHandler = new ViewChangeEventHandler() {
            @Override
            public void onViewChange(ViewChangeEvent event) {
                double dx = event.getTranslateX();
                double dy = event.getTranslateY();
                double scale = event.getScale();
                double canvasScale = canvas.getScale();
                canvas.translate(-dx / scale * canvasScale, 
                                 -dy / scale * canvasScale);
                canvas.update();
            }
        };
        overview.addHandler(overviewEventHandler,
                            ViewChangeEvent.TYPE);
        // The following is used to hilight linked objects
        // Test selections
        SelectionEventHandler selectionHandler = new SelectionEventHandler() {
            
            @Override
            public void onSelectionChanged(SelectionEvent e) {
                hiliteAndCentreObjects(e);
                overview.setSelectedObjects(e.getSelectedObjects());
                overview.update();
            }
        };
        diagramPane.addSelectionEventHandler(selectionHandler);
    }
    
    private void hiliteAndCentreObjects(SelectionEvent e) {
        if (diagramPane.getPathway() == null ||
            diagramPane.getPathway().getGraphObjects() == null)
            return;
        // Un-hilight objects first
        for (GraphObject obj : diagramPane.getPathway().getGraphObjects()) {
            obj.setHighlighted(false);
        }
        
        // Highlight the selected object and connected objects
        List<GraphObject> selectedObjects = e.getSelectedObjects();
        for (GraphObject obj : selectedObjects) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                List<HyperEdge> reactions = node.getConnectedReactions();
                for (HyperEdge edge : reactions)
                    edge.setHighlighted(true);
            }
            else if (obj instanceof HyperEdge) {
                HyperEdge edge = (HyperEdge) obj;
                List<Node> nodes = edge.getConnectedNodes();
                for (Node node : nodes)
                    node.setHighlighted(true);
            }
                
            // Centre selected object
            PathwayCanvas c = diagramPane.getCanvas();
            c.resetTranslate();
            
            double scale = c.getScale();
            
            Point objPos = obj.getPosition();
            double objX = objPos.getX();
            double objY = objPos.getY();
            
            double x = (objX * -1.0 * scale) + (c.getCoordinateSpaceWidth() / 2);
            double y = (objY * -1.0 * scale) + (c.getCoordinateSpaceHeight() / 2);
            
            c.translate(x,y);                        
        }    
    	diagramPane.update();
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
                event.stopPropagation();
                mouseDown(event.getX(), event.getY());
            }
        };
        MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.stopPropagation();
                mouseMove(event.getX(), event.getY());
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
        } else {
        	diagramPane.hover(x, y);
        }
    }
    
    private void mouseUp(int x, int y) {
        if (isMouseDown)
            isMouseDown = false;
        
       	if (isDragging) {
       		isDragging = false;
       	} else { // Do click selection
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
