/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A specialized PlugInSupportCanvas that is used to draw CanvasPathway only.
 * @author gwu
 *
 */
public class PathwayCanvas extends DiagramCanvas {
    // Pathway to be displayed
    private CanvasPathway pathway;
    // Used to draw pathway
    private PathwayCanvasDrawer drawer;
    
    public PathwayCanvas() {
    	super();
    	drawer = new PathwayCanvasDrawer();
    }
    
    public PathwayCanvas(PathwayDiagramPanel diagramPane) {
    	this(diagramPane, true);
    }
    
    public PathwayCanvas(PathwayDiagramPanel diagramPane, Boolean installEventHandlers) {
    	super(diagramPane, installEventHandlers);
    	
    	if (installEventHandlers) {
    		hoverHandler = new PathwayCanvasHoverHandler(diagramPane, this);
    		selectionHandler = new PathwayCanvasSelectionHandler(diagramPane, this);
    	}
    	drawer = new PathwayCanvasDrawer();
    }
    
    public void setPathway(CanvasPathway pathway) {
        this.pathway = pathway;
        fireViewChangeEvent();
    }
    
    public CanvasPathway getPathway() {
        return this.pathway;
    }

    public List<GraphObject> getGraphObjects() {
    	if (getPathway() == null)
    		return null;
    	
    	return getPathway().getGraphObjects();
    }	
    
    @Override
    protected void fireViewChangeEvent() {
    	super.fireViewChangeEvent();
    	
    	if (hoverHandler != null && hoverHandler instanceof PathwayCanvasHoverHandler)
    		((PathwayCanvasHoverHandler) hoverHandler).getInfoIconPopup().hide();
    }    
    
    /**
     * Update drawing.
     */
    public void update() {
        Context2d c2d = getContext2d();
        c2d.save();

        clean(c2d);
        if (pathway != null) {
            drawer.drawPathway(pathway, this, c2d);
            updateOthers(c2d);
        }
        
        c2d.restore();
    }

    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
