/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.InteractorType;
import org.reactome.diagram.model.ProteinNode.InteractorCountNode;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.HTML;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class InteractorCanvasHoverHandler extends HoverHandler {
    private InteractorCanvas interactorCanvas;
	
    public InteractorCanvasHoverHandler(PathwayDiagramPanel diagramPanel, InteractorCanvas interactorCanvas) {
        super(diagramPanel, interactorCanvas);
        this.interactorCanvas = interactorCanvas; 
    }

    public GraphObject hover(Point hoverPoint) {
        this.hoverPoint = hoverPoint;
    	
    	if (interactorCanvas.getObjectsForRendering() == null)
            return null;
                
        List<GraphObject> objects = interactorCanvas.getObjectsForRendering();
        super.hover(objects);

        if (hoveredObject != null && !(hoveredObject instanceof InteractorCountNode)) {
        	WidgetStyle.setCursor(interactorCanvas, Cursor.POINTER);
        } else if (interactorCanvas.isLoadingInteractors()) {
        	WidgetStyle.setCursor(interactorCanvas, Cursor.WAIT);
        } else {
        	WidgetStyle.setCursor(interactorCanvas, Cursor.DEFAULT);
        }

        if (!(isOverSameObject && timeElapsed)) 
        	showTooltip();
                
        return hoveredObject;
    }
        
    protected void showTooltip() {
    	String tooltipHTML = getToolTipContents();
    	
    	if (tooltipHTML != null) {
    		tooltip.setWidget(new HTML(tooltipHTML));
    		super.showTooltip();
    	}
    }
    
    private String getToolTipContents()  {
    	if (hoveredObject instanceof InteractorNode) {
    		final InteractorNode hoveredInteractor = (InteractorNode) hoveredObject;
    				
    		String description = null;
    		if (hoveredInteractor.getRefType() == InteractorType.Protein) {
    			description = "Uniprot Accession: " + hoveredInteractor.getAccession();
    		} else if (hoveredInteractor.getRefType() == InteractorType.Chemical) {
    			description = "Chemical Id: " + hoveredInteractor.getChemicalId() != null ?
    						  hoveredInteractor.getChemicalId() :
    						  hoveredInteractor.getAccession();
    		}
    		
    		if (description != null)
    			return "Interactor: " + hoveredInteractor.getGeneName() + "<br />" +
    				   description + "<br />" +
    				   "Confidence Level Score: " + hoveredInteractor.getScore();
    	} else if (hoveredObject instanceof InteractorEdge) {
    		 return ((InteractorEdge) hoveredObject).getProtein().getDisplayName() + " interacts with " +
    				((InteractorEdge) hoveredObject).getInteractor().getGeneName();
    	} else if (hoveredObject instanceof InteractorCountNode) {
    		return ((InteractorCountNode) hoveredObject).getProteinName() + " has " + 
    			   ((InteractorCountNode) hoveredObject).getCount() + " interactors <br />" +
    			   	"in the selected interactor database";
    	}
    	
    	return null;
    }
    
    protected Boolean overridesOtherHoverHandlers() {
    	return true;
    }
}
