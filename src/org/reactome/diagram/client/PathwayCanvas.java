/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.ViewChangeEvent;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A specialized PlugInSupportCanvas that is used to draw CanvasPathway only.
 * @author gwu
 *
 */
public class PathwayCanvas extends PlugInSupportCanvas {
    
    // Pathway to be displayed
    private CanvasPathway pathway;
    // These are used for translate
    private double translateX;
    private double translateY;
    // This is for scale
    private double scale;
    // For view change
    protected ViewChangeEvent viewEvent;
    
    public PathwayCanvas() {
        scale = 1.0d;
    }
    
    public void setPathway(CanvasPathway pathway) {
        this.pathway = pathway;
        fireViewChangeEvent();
    }
    
    public CanvasPathway getPathway() {
        return this.pathway;
    }
    
    public void translate(double dx, double dy) {
        this.translateX += dx;
        this.translateY += dy;
        fireViewChangeEvent();
    }
    
    private void fireViewChangeEvent() {
        if (viewEvent == null)
            viewEvent = new ViewChangeEvent();
        viewEvent.setScale(scale);
        viewEvent.setTranslateX(translateX);
        viewEvent.setTranslateY(translateY);
        viewEvent.setWidth(getCoordinateSpaceWidth());
        viewEvent.setHeight(getCoordinateSpaceHeight());
        super.fireEvent(viewEvent);
    }
    
    public double getTranslateX() {
        return translateX;
    }
    
    public double getTranslateY() {
        return translateY;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public void scale(double scale) {
        this.scale *= scale;
        fireViewChangeEvent();
    }
    
    public void reset() {
        translateX = 0.0d;
        translateY = 0.0d;
        scale = 1.0d;
        fireViewChangeEvent();
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        if (pathway == null)
            return;
        List<Node> nodes = pathway.getChildren();
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        Context2d c2d = getContext2d();
        c2d.save();
        c2d.clearRect(0.0d, 
                      0.0d, 
                      getOffsetWidth(),
                      getOffsetHeight());
        c2d.translate(translateX, translateY);
        c2d.scale(scale, scale);
        if (nodes != null) {
            // Always draw compartments first
            for (Node node : nodes) {
                if (node.getType() == GraphObjectType.RenderableCompartment) {
                    NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                    if (renderer != null)
                        renderer.render(c2d, node);
                }
            }
            for (Node node : nodes) {
                if (node.getType() == GraphObjectType.RenderableCompartment)
                    continue;
                NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                if (renderer != null)
                    renderer.render(c2d,
                                    node);
            }
        }
        // Draw edges
        List<HyperEdge> edges = pathway.getEdges();
        if (edges != null) {
            for (HyperEdge edge : edges) {
                HyperEdgeRenderer renderer = viewFactory.getEdgeRenderere(edge);
                if (renderer == null)
                    continue;
                renderer.render(c2d, 
                                edge);
            }
        }
        c2d.restore();
    }    
    
}
