/*
 * Created on Oct 25, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.InteractorNode;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;

/**
 * This class is used to handle selection related stuff for PathwayDiagramPanel.
 * @author gwu
 *
 */
public class InteractorCanvasSelectionHandler extends SelectionHandler {
    private InteractorCanvas ic;
	
    public InteractorCanvasSelectionHandler(PathwayDiagramPanel diagramPanel, InteractorCanvas interactorCanvas) {
        super(diagramPanel);
    	this.diagramPanel = diagramPanel;
        this.ic = interactorCanvas;
        selectedObjects = new ArrayList<GraphObject>();
    }
   
    public GraphObject select(GwtEvent<? extends EventHandler> event, Point point) {
        if (ic.getGraphObjects() == null)
            return null;                

        canvasObjects = ic.getGraphObjects();
        gwtEvent = event;

        return super.select(event, point);
    }
    
    public void setSelectionObjects(List<GraphObject> objects) {
        canvasObjects = ic.getGraphObjects();
        
        super.setSelectionObjects(objects);
    }
    
    /**
     * Set selections using a list of DB_IDs from Reactome.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
    	canvasObjects = ic.getGraphObjects();
    	
    	super.setSelectionIds(dbIds);
    }

	@Override
	protected void doAdditionalActions(GraphObject selected) {
		if (selected instanceof InteractorNode) {
			Window.open(((InteractorNode) selected).getUrl(), "_blank", "");
		} else if (selected instanceof InteractorEdge) {
			diagramPanel.getController().openInteractionPage((InteractorEdge) selected);
		}
		
	}
        
 }
