/*
 * Created on Oct 25, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.model.CompositionalNode;
import org.reactome.diagram.model.CompositionalNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.touch.client.Point;

/**
 * This class is used to handle selection related stuff for PathwayDiagramPanel.
 * @author gwu
 *
 */
public abstract class SelectionHandler {
    protected PathwayDiagramPanel diagramPanel;
    protected List<GraphObject> selectedObjects;
    protected List<GraphObject> canvasObjects;
    protected SelectionEvent selectionEvent;
    protected GwtEvent<? extends EventHandler> gwtEvent;
    protected boolean objectReselected;
    
    public SelectionHandler(PathwayDiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
        selectedObjects = new ArrayList<GraphObject>();
    }
    
    /**
     * Get the selected objects.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        return selectedObjects;
    }
    
    protected GraphObject getSelectedObject() {
    	if (getSelectedObjects().isEmpty())
    		return null;
    	
    	return getSelectedObjects().get(0);
    }
    
    private void addSelection(GraphObject obj) {
        if (!selectedObjects.contains(obj)) {
            selectedObjects.add(obj);
            obj.setIsSelected(true);
        }
    }
    
    public GraphObject select(GwtEvent<? extends EventHandler> event, Point point) {
              
        // Three layers: last for compartment, second to last complexes, and others
        // Only one object should be selected
        GraphObject selected = null;

        for (GraphObject obj : canvasObjects) {
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
            for (GraphObject obj : canvasObjects) {
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
        } else {        
        	// If previous object was reselected
        	// A special case to gain some performance: this should be common during selection.
            if (selectedObjects.size() == 1 && selected == selectedObjects.get(0)) {
            	//objectReselected = true;
            }        

            deSelectAllExcept(selected);
            
            selectedObjects.add(selected);
          
            //if (objectReselected) { 
            //	objectReselected = false;
            	//return selected;            
            //}	                                             
        }

        doAdditionalActions();
        
        return selected;
    }

    public void setSelectionObjects(List<GraphObject> objects) {        
        if (selectedObjects != objects) {
    		selectedObjects.clear();
        	selectedObjects.addAll(objects);
        }
        	
        for (GraphObject obj : canvasObjects) {
            obj.setIsSelected(objects.contains(obj));
        }
        fireSelectionEvent();
    }
    
    /**
     * Set selections using a list of DB_IDs from Reactome.
     * @param dbIds
     */
    public void setSelectionIds(final List<Long> dbIds) {
        selectedObjects.clear();
        
        final List<CompositionalNode> canvasSetsAndComplexes = new ArrayList<CompositionalNode>();
        for (GraphObject obj : canvasObjects) {
            Long dbId = obj.getReactomeId();
            if (dbId == null)
                continue;
            if (dbIds.contains(dbId)) {
                addSelection(obj);
            } else {
                obj.setIsSelected(false);
                obj.setHighlighted(false);
            }
            
            if (obj.isSetOrComplex())
            	canvasSetsAndComplexes.add((CompositionalNode) obj);
        }
        
        // also select complexes and sets which contain entities matching
        // the given database identifiers
        ComplexComponentFetcher componentFetcher = new ComplexComponentFetcher() {
        
        	@Override
        	public void performActionAfterComponentsObtained() {
        		
        		for (CompositionalNode composedNode : canvasSetsAndComplexes) {
        			for (Component component : composedNode.getComponents()) {
        				if (dbIds.contains(component.getReactomeId())) {
        					addSelection(composedNode);
        					break;
        				}
        			}
        		}
        		
        		//DO NOT FIRE EVENT HERE, just update the diagram
        		OverviewCanvas overview = diagramPanel.getOverview();
        		overview.setSelectedObjects(selectedObjects);
        		overview.update();
        		diagramPanel.getPathwayCanvas().update();
        	}
        };
        componentFetcher.getComplexNodeComponentData(canvasSetsAndComplexes);
    }
    
    /**
     * Clear any selection.
     */
    public void clearSelection() {
        setSelectionIds(new ArrayList<Long>());
    }
    
    protected abstract void doAdditionalActions(); 
    
    protected void fireSelectionEvent() {
        selectionEvent = new SelectionEvent();
        selectionEvent.setDoCentring(doCentring());
        selectionEvent.setSelectedObjects(selectedObjects);
        //OverviewCanvas overview = diagramPanel.getOverview();
        //overview.setSelectedObjects(selectedObjects);
        //overview.update();
        diagramPanel.fireSelectionEvent(selectionEvent);        
    }
    
    private boolean doCentring() {
    	if (selectedObjects.isEmpty())
    		return false;
    	
		return !diagramPanel.getPathwayCanvas().currentViewContainsAtLeastOneGraphObject(selectedObjects);		
	}

	private void deSelectAllExcept(GraphObject selected) {
    	for (GraphObject obj : canvasObjects) {
    		if (obj != selected) {
    			obj.setIsSelected(false);
    		}
    	}
    	selectedObjects.clear();
    }
}
