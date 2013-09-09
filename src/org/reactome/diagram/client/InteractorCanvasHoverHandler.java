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
    	
    	if (interactorCanvas.getGraphObjects() == null)
            return null;
                
        List<GraphObject> objects = interactorCanvas.getGraphObjects();
        super.hover(objects);

        if (hoveredObject != null) {
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
    	String label;
    	
    	if (hoveredObject instanceof InteractorNode) {
    		InteractorType type = ((InteractorNode) hoveredObject).getRefType();
    				
    		String description;
    		if (type == InteractorType.Protein) {
    			description = "Uniprot Accession:";
    		} else if (type == InteractorType.Chemical) {
    			description = "Chemical Id:";
    		} else {	
    			return;
    		}	
    		label =  super.getLabel() + "<br />" +
    				 description + ((InteractorNode) hoveredObject).getAccession() + "<br />" +
    				 "Confidence Level Score: " + ((InteractorNode) hoveredObject).getScore();    			
    	} else if (hoveredObject instanceof InteractorEdge) {
    		label = ((InteractorEdge) hoveredObject).getProtein().getDisplayName() + " interacts with " +
    				((InteractorEdge) hoveredObject).getInteractor().getDisplayName();     		
    	} else {
    		return;
    	}
    	 	
    	tooltip.setWidget(new HTML(label));
    
    	super.showTooltip();
    }
    
    protected Boolean overridesOtherHoverHandlers() {
    	return true;
    }
}
