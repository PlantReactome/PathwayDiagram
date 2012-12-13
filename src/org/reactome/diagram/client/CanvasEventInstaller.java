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
import org.reactome.diagram.view.Parameters;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.touch.client.Point;

/**
 * This helper class is used to set up event handler for the canvas used in PathwayDiagramPanel.
 * @author gwu
 *
 */
public class CanvasEventInstaller {
    private PathwayDiagramPanel diagramPane;
    private DiagramCanvas canvas;
    // The following properties are used for panning
    private boolean isDragging;
    private boolean isMouseDown;
    private int previousX;
    private int previousY;
    
    
    public CanvasEventInstaller(PathwayDiagramPanel diagramPanel, DiagramCanvas canvas) {
        this.diagramPane = diagramPanel;
        this.canvas = canvas;
    }
    
    public void installHandlers() {
        addMouseHandlers();
        addTouchHandlers();
        addKeyHandlers();
        
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
                diagramPane.translate(-dx / scale * canvasScale, 
                                 -dy / scale * canvasScale);
                diagramPane.update();
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
            
            if (e.isDoCentring()) {
            	centreObject(obj);
            }	
        }
        diagramPane.update();
    }
    
    private void centreObject(GraphObject obj) {
            // Centre selected object
            PathwayCanvas c = diagramPane.getCanvas();
                    
            double scale = c.getScale();
            
            Point objPos = obj.getPosition();
            double objX = objPos.getX();
            double objY = objPos.getY();
            
            double x = (objX * -1.0 * scale) + (c.getCoordinateSpaceWidth() / 2);
            double y = (objY * -1.0 * scale) + (c.getCoordinateSpaceHeight() / 2);
            
            diagramPane.translate(-objX + x, -objY + y);
            diagramPane.hideTooltip();
    }    
    	
    
    
    private void addTouchHandlers() {
        TouchStartHandler touchStartHandler = new TouchStartHandler() {
            
            @Override
            public void onTouchStart(TouchStartEvent event) {
                event.stopPropagation();
                event.preventDefault();
                mouseDown(event);
            }
        };
        canvas.addTouchStartHandler(touchStartHandler);
        
        TouchMoveHandler touchMoveHandler = new TouchMoveHandler() {
            
            @Override
            public void onTouchMove(TouchMoveEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    event.preventDefault();
                    mouseMove(event);
                }
            }
        };
        canvas.addTouchMoveHandler(touchMoveHandler);
        
        TouchEndHandler touchEndHandler = new TouchEndHandler() {
            
            @Override
            public void onTouchEnd(TouchEndEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    event.preventDefault();
                    mouseUp(event);
                }
            }
        };
        canvas.addTouchEndHandler(touchEndHandler);
    }
    
    private int[] getCoordinates(GwtEvent<? extends EventHandler> event) {
    	if (event instanceof TouchEvent) {
    		return getPositionInTouch((TouchEvent<? extends EventHandler>) event);
    	} else if (event instanceof MouseEvent) {
    		MouseEvent<? extends EventHandler> me = (MouseEvent<? extends EventHandler>) event;
    		return new int[] { 
    				me.getRelativeX(canvas.getElement()), 
    				me.getRelativeY(canvas.getElement())
    		};
    	}
    	return null;
    }
    
    private int[] getPositionInTouch(TouchEvent<? extends EventHandler> event) {
        JsArray<Touch> touches = event.getTouches();
        if (touches == null || touches.length() == 0)
            return null;
        // Get the first touch
        Touch touch = touches.get(0);
        int x = touch.getRelativeX(canvas.getElement());
        int y = touch.getRelativeY(canvas.getElement());
        return new int[]{x, y};
    }

    private void addMouseHandlers() {
        // To record the original position
        MouseDownHandler mouseDownHandler = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
                mouseDown(event);
            }
        };
        MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.stopPropagation();
                mouseMove(event);
            }
        };
        MouseUpHandler mouseUpHandler = new MouseUpHandler() {
            
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (isMouseDown) {      
                	event.stopPropagation();
                    mouseUp(event);
                }
            }
        };
        MouseOutHandler mouseOutHandler = new MouseOutHandler() {
            
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (isMouseDown) {
                    event.stopPropagation();
                    mouseOut(event);
                }
            }
        };
        
        MouseWheelHandler mouseWheelHandler = new MouseWheelHandler() {
        	
        	@Override
        	public void onMouseWheel(MouseWheelEvent event) {
        		event.stopPropagation();
        		mouseWheel(event);
        	}

        };

        DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
				doubleClick(event);
			}
        
        };	
                
        canvas.addMouseDownHandler(mouseDownHandler);
        canvas.addMouseUpHandler(mouseUpHandler);
        canvas.addMouseMoveHandler(mouseMoveHandler);
        canvas.addMouseOutHandler(mouseOutHandler);
        canvas.addMouseWheelHandler(mouseWheelHandler);
        canvas.addDoubleClickHandler(doubleClickHandler);        
    }
    
    private void addKeyHandlers() {
    	KeyUpHandler keyUpHandler = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				event.stopPropagation();
				
				if (KeyCodeEvent.isArrow(event.getNativeKeyCode())) {
					arrowKeyUp(event);
				}
			}
    		
    	};
    	
    	canvas.addKeyUpHandler(keyUpHandler);
    }
    
    
    private void mouseDown(GwtEvent<? extends EventHandler> event) {
    	int [] coord = getCoordinates(event);
    	previousX = coord[0];
        previousY = coord[1];
        isMouseDown = true;        	
        diagramPane.hideTooltip();
    }
    
    private void mouseMove(GwtEvent<? extends EventHandler> event) {
        int [] coord = getCoordinates(event);
        int x = coord[0];
        int y = coord[1];
        
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
    
    private void mouseUp(GwtEvent<? extends EventHandler> event) {
        int [] coord = getCoordinates(event);
    	int x = coord[0];
    	int y = coord[1];
        
    	if (isMouseDown) {
            isMouseDown = false;            
    	}
            
       	if (isDragging) {
       		isDragging = false;
       	} else { // Do click selection
       		//TODO: selection cannot work under iPad. Need to check touchEnd event.
       		diagramPane.select(event, x, y);
       	}
       	
       	
    }
    
    private void mouseOut(GwtEvent<? extends EventHandler> event) {
        if (isMouseDown) {
            isMouseDown = false;
        }
        if (isDragging)
            isDragging = false;
    }
    
    private void mouseWheel(MouseWheelEvent event) {
    	if (!diagramPane.getPopupMenu().isShowing()) {
    		if (event.isNorth()) {
    			diagramPane.scale(Parameters.ZOOMIN);
    		} else {
    			diagramPane.scale(Parameters.ZOOMOUT);
    		}
    	
    		diagramPane.update();
    	}	
    }

    private void doubleClick(DoubleClickEvent event) {
    	mouseUp(event);
    }
    
    private void arrowKeyUp(KeyUpEvent event) {
    	int x = 0;
    	int y = 0; 
    	
    	if (event.isLeftArrow()) {
    		x = Parameters.MOVEX;
    	} else if (event.isRightArrow()) {
    		x = -Parameters.MOVEX;
    	} else if (event.isUpArrow()) {
    		y = Parameters.MOVEY;
    	} else if (event.isDownArrow()) {
    		y = -Parameters.MOVEY;
    	}
    	
    	diagramPane.translate(x, y);
    	diagramPane.update();
    }
    
}


