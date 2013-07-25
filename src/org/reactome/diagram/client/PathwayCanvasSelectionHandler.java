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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
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
	private InfoPopup infoPopup;
	
    public PathwayCanvasSelectionHandler(PathwayDiagramPanel diagramPanel, PathwayCanvas pathwayCanvas) {
        super(diagramPanel);
    	
        this.diagramPanel = diagramPanel;
        this.pc = pathwayCanvas;
        this.infoPopup = new InfoPopup(diagramPanel);
        
        selectedObjects = new ArrayList<GraphObject>();
    }
        
    public GraphObject select(GwtEvent<? extends EventHandler> event, Point point) {
        if (pc.getPathway() == null || pc.getPathway().getGraphObjects() == null)
        	return null;
        	
    	canvasObjects = pc.getPathway().getGraphObjects();
        gwtEvent = event;
        
        return super.select(event, point);
    }
    
    public void setSelectionObjects(List<GraphObject> objects) {
        CanvasPathway pathway = pc.getPathway();
        if (pathway == null)
            return;
        
        canvasObjects = pathway.getGraphObjects();
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
        
        canvasObjects = pathway.getGraphObjects();
        
        super.setSelectionIds(dbIds);
      
    }
                
    private boolean isPathwayDoubleClicked(GraphObject selected) {
    	return (gwtEvent instanceof DoubleClickEvent && selected.getType() == GraphObjectType.ProcessNode);
    }
    
    private boolean selectedObjectLeftClicked() {
    	return gwtEvent instanceof MouseEvent && ((MouseEvent<? extends EventHandler>) gwtEvent).getNativeButton() == NativeEvent.BUTTON_LEFT;
    }

	@Override
	protected void doAdditionalActions(GraphObject selected) {
		if (isPathwayDoubleClicked(selected))
			diagramPanel.setPathway(selected.getReactomeId());
		else if (selectedObjectLeftClicked())
			infoPopup.showPopup(selected);		
	}
}
