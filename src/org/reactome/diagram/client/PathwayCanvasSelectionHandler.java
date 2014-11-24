/*
 * Created on Oct 25, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;import java.util.Collections;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.touch.client.Point;

/**
 * This class is used to handle selection related stuff for PathwayDiagramPanel.
 * @author gwu
 *
 */
public class PathwayCanvasSelectionHandler extends SelectionHandler {
	private PathwayCanvas pc;
	//private InfoPopup infoPopup;
	
    public PathwayCanvasSelectionHandler(PathwayDiagramPanel diagramPanel, PathwayCanvas pathwayCanvas) {
        super(diagramPanel);
    	
        this.diagramPanel = diagramPanel;
        this.pc = pathwayCanvas;
       // this.infoPopup = new InfoPopup(diagramPanel);
        
        selectedObjects = new ArrayList<GraphObject>();
    }
        
    public GraphObject select(GwtEvent<? extends EventHandler> event, Point point) {
        if (pc.getPathway() == null || getObjectsForRendering() == null)
        	return null;
        	
    	canvasObjects = getObjectsForRendering();
        gwtEvent = event;
        
        return super.select(event, point);
    }
    
    public void setSelectionObjects(List<GraphObject> objects) {
        CanvasPathway pathway = pc.getPathway();
        if (pathway == null)
            return;
        
        canvasObjects = getObjectsForRendering();
        super.setSelectionObjects(objects);       
    }
    
    /**
     * Set selections using a list of DB_IDs from Reactome.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
        CanvasPathway pathway = pc.getPathway();
        if (pathway == null)
            return;
        
        canvasObjects = getObjectsForRendering();
        
        super.setSelectionIds(dbIds);
      
    }
    
    private List<GraphObject> getObjectsForRendering() {
    	List<GraphObject> objects = new ArrayList<GraphObject>(pc.getPathway().getObjectsForRendering());
    	Collections.reverse(objects);
		return objects;
    }

    private boolean isPathwayDoubleClicked(GraphObject selected) {
    	return (gwtEvent instanceof DoubleClickEvent && selected.getType() == GraphObjectType.ProcessNode);
    }

	@Override
	protected void doAdditionalActions() {
		GraphObject selected = getSelectedObject();
		
		if (selected != null && isPathwayDoubleClicked(selected))
			diagramPanel.setPathway(selected.getReactomeId());
	}
}
