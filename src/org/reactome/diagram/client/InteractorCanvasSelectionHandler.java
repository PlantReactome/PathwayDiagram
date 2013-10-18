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
import org.reactome.diagram.model.ProteinNode;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
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
        if (ic.getObjectsForRendering() == null)
            return null;                

        canvasObjects = ic.getObjectsForRendering();
        gwtEvent = event;

        return super.select(event, point);
    }
    
    public void setSelectionObjects(List<GraphObject> objects) {
        canvasObjects = ic.getObjectsForRendering();
        
        super.setSelectionObjects(objects);
    }
    
    /**
     * Set selections using a list of DB_IDs from Reactome.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
    	canvasObjects = ic.getObjectsForRendering();
    	
    	super.setSelectionIds(dbIds);
    }

    @Override
    protected void fireSelectionEvent() {
    	// Clicking interactor nodes or edges should not trigger a selection event
    }

	@Override
	protected void doAdditionalActions() {
		GraphObject selected = getSelectedObject();
		
		if (selected == null) 
			return;
		
		if (selected instanceof InteractorNode && !((InteractorNode) selected).getAccession().isEmpty()) {
			Window.open(((InteractorNode) selected).getUrl(), "_blank", "");
		} else if (selected instanceof InteractorEdge) {
			ProteinNode protein = ((InteractorEdge) selected).getProtein();
			
			diagramPanel.getController().getReferenceEntity(protein.getReactomeId(), 
															openInteractionPage((InteractorEdge) selected));
		}
		
	}
	
	private RequestCallback openInteractionPage(final InteractorEdge selected) {
		RequestCallback openInteractionPage = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					selected.getProtein().setRefId(response.getText());
					if (!selected.getUrl().isEmpty())	
						Window.open(selected.getUrl(), null, null);
				} else {
					diagramPanel.getController().requestFailed("Could not open interaction page.  " +
															   "Unable to retrieve reference entity for " +
															   selected.getProtein().getDisplayName());
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				diagramPanel.getController().requestFailed(exception);
			}			
		};
		
		return openInteractionPage;
	}
 }
