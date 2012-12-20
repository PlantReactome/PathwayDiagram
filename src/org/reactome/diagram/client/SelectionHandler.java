/*
 * Created on Oct 25, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.InteractorNode;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;

/**
 * This class is used to handle selection related stuff for PathwayDiagramPanel.
 * @author gwu
 *
 */
public class SelectionHandler {
    private PathwayDiagramPanel diagramPanel;
    private List<GraphObject> selectedObjects;
    private SelectionEvent selectionEvent;
        
    public SelectionHandler(PathwayDiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
        selectedObjects = new ArrayList<GraphObject>();
    }
    
    /**
     * Get the selected objects.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        return new ArrayList<GraphObject>(selectedObjects);
    }
    
    public void addSelection(Long dbId) {
    }
    
    public void addSelection(GraphObject obj) {
        if (!selectedObjects.contains(obj)) {
            selectedObjects.add(obj);
            obj.setIsSelected(true);
            diagramPanel.update();
            fireSelectionEvent();
        }
    }
    
    public void select(GwtEvent<? extends EventHandler> event, double x, double y) {
        if (diagramPanel.getPathway() == null || diagramPanel.getPathway().getGraphObjects() == null)
            return;
                
        Point point = new Point(x, y);
        // Three layers: last for compartment, second to last complexes, and others
        // Only one object should be selected
        GraphObject selected = null;
        List<GraphObject> objects = diagramPanel.getInteractorCanvas().getGraphObjects();
        objects.addAll(diagramPanel.getPathway().getGraphObjects());

        for (GraphObject obj : objects) {
            if (selected != null) 
                break;
            GraphObjectType type = obj.getType();
            if (type == GraphObjectType.RenderableCompartment ||
                type == GraphObjectType.RenderableComplex) 
                continue;
            if (obj.isPicked(point)) {
                obj.setIsSelected(true);
                selected = obj;
            }
        }
        if (selected == null) {
            // Check complex
            for (GraphObject obj : objects) {
                if (selected != null)
                    break;
                GraphObjectType type = obj.getType();
                if (type != GraphObjectType.RenderableComplex) 
                    continue;
                if (obj.isPicked(point)) {
                    obj.setIsSelected(true);
                    selected = obj;
                }
            }
        }
        
        // Don't do anything if just empty click
        if (selected == null) {
        	deSelectAllExcept(selected);
        	return;
        } else {        
        	boolean pathwayDoubleClicked = isPathwayDoubleClicked(selected, event);
                    	
        	// If previous object was reselected
        	// A special case to gain some performance: this should be common during selection.
            if (selectedObjects.size() == 1 && selected == selectedObjects.get(0) && !pathwayDoubleClicked) {
            	showPopupIfRightClick(event);
            	return; // Don't redraw
            }        

            // De-select previous object
            deSelectAllExcept(selected);
        
            // Go to selected pathway if process node double clicked
            if (pathwayDoubleClicked) {
            	diagramPanel.setPathway(selected.getReactomeId());
            	return;
            } else if (selected instanceof InteractorNode) {
            	Window.open(((InteractorNode) selected).getUrl(), "_blank", "");
            	return;
            } else if (selected instanceof InteractorEdge) {
            	Window.open(((InteractorEdge) selected).getUrl(), null, null);
            	return;
            }
        
            // Add selected object to class variable            
            selectedObjects.add(selected);          
                        
            showPopupIfRightClick(event);
            
            fireSelectionEvent();
            
        }
    }
    public void setSelectionObjects(List<GraphObject> objects) {
        CanvasPathway pathway = diagramPanel.getPathway();
        if (pathway == null)
            return;
        selectedObjects.clear();
        selectedObjects.addAll(objects);
        for (GraphObject obj : pathway.getGraphObjects()) {
            obj.setIsSelected(objects.contains(obj));
        }
        fireSelectionEvent();
    }
    
    /**
     * Set selections using a list of DB_IDs from Reactome.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
        CanvasPathway pathway = diagramPanel.getPathway();
        if (pathway == null)
            return;
        selectedObjects.clear();
        List<GraphObject> objects = pathway.getGraphObjects();
        for (GraphObject obj : objects) {
            Long dbId = obj.getReactomeId();
            if (dbId == null)
                continue;
            if (dbIds.contains(dbId)) {
                selectedObjects.add(obj);
                obj.setIsSelected(true);
            }
            else
                obj.setIsSelected(false);
        }
        fireSelectionEvent();
    }
    
    /**
     * Clear any selection.
     */
    public void clearSelection() {
        setSelectionIds(new ArrayList<Long>());
    }
        
    private void fireSelectionEvent() {
        selectionEvent = new SelectionEvent();
        selectionEvent.setDoCentring(false);
        selectionEvent.setSelectedObjects(selectedObjects);
        diagramPanel.fireSelectionEvent(selectionEvent);        
    }
    
    private void deSelectAllExcept(GraphObject selected) {
    	for (GraphObject obj : selectedObjects) {
    		if (obj != selected) {
    			obj.setIsSelected(false);
    		}
    	}
    	selectedObjects.clear();
    }
        
    private boolean isPathwayDoubleClicked(GraphObject selected, GwtEvent<? extends EventHandler> event) {
    	return (event instanceof DoubleClickEvent && selected.getType() == GraphObjectType.ProcessNode);
    }
    
    private void showPopupIfRightClick(GwtEvent<? extends EventHandler> event) {
    	if (event instanceof MouseEvent && ((MouseEvent <? extends EventHandler>) event).getNativeButton() == NativeEvent.BUTTON_RIGHT) {
    		diagramPanel.getPopupMenu().showPopupMenu((MouseEvent <? extends EventHandler>) event);
    	}
    }
}
