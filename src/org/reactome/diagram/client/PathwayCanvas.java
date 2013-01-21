/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.model.CanvasPathway;

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
    	drawer = new PathwayCanvasDrawer();
    }
    
    public PathwayCanvas(PathwayDiagramPanel diagramPane) {
    	super();
    	hoverHandler = new PathwayCanvasHoverHandler(diagramPane, this);
        drawer = new PathwayCanvasDrawer();
    }
    
    public void setPathway(CanvasPathway pathway) {
        this.pathway = pathway;
        fireViewChangeEvent();
    }
    
    public CanvasPathway getPathway() {
        return this.pathway;
    }
            
    /**
     * Update drawing.
     */
    public void update() {
        if (pathway == null)
            return;
        Context2d c2d = getContext2d();
        c2d.save();

        clean();
        
        drawer.drawPathway(pathway, this, c2d);
        updateOthers(c2d);
        
        c2d.restore();
    }

    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
