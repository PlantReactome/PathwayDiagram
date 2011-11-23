/*
 * Created on Oct 25, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.touch.client.Point;

/**
 * This class is used to handle selection related stuff for PathwayDiagramPanel.
 * @author gwu
 *
 */
public class SelectionHandler {
    private PathwayDiagramPanel diagramPanel;
    private List<GraphObject> selectedObjects;
    
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
        }
    }
    
    public void select(double x, double y) {
        if (diagramPanel.getPathway() == null ||
            diagramPanel.getPathway().getGraphObjects() == null)
            return;
        Point point = new Point(x, y);
        // Three layers: last for compartment, second to last complexes, and others
        // Only one object should be selected
        GraphObject selected = null;
        List<GraphObject> objects = diagramPanel.getPathway().getGraphObjects();
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
        // Compartment cannot be selected
        // A special case to gain some performance: this should be common during selection.
        if (selected != null) {
            if (selectedObjects.size() == 1 &&
                selected == selectedObjects.get(0)) {
                return; // Don't redraw
            }
        }
        for (GraphObject obj : selectedObjects) {
            if (obj != selected)
                obj.setIsSelected(false);
        }
        selectedObjects.clear();
        if (selected != null)
            selectedObjects.add(selected);
        diagramPanel.update();
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
        diagramPanel.update();
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
        diagramPanel.update();
    }
    
}
