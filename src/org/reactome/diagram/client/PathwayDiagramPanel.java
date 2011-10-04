/*
 * Created on Sep 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

/**
 * This customized wiget is used to draw pathway diagram.
 * @author gwu
 *
 */
public class PathwayDiagramPanel extends Composite {
 // Use an AbsolutePanel so that controls can be placed onto on a canvas
    AbsolutePanel contentPane;
    // Pathway to be displayed
    private CanvasPathway pathway;
    // The major canvas that is used to draw pathway
    private PlugInSupportCanvas canvas;
    // These are used for translate
    private double translateX;
    private double translateY;
    // This is for scale
    private double scale;
    
    public PathwayDiagramPanel() {
        init();
    }
    
    private void init() {
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvas = new PlugInSupportCanvas();
        // Keep the original information
        contentPane.add(canvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName("mainCanvas");
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        initWidget(contentPane);
        // default should be 1.0d
        scale = 1.0d;
    }
    
    public void setSize(int width, int height) {
        super.setSize(width + "px", height + "px");
        canvas.setSize(width - 8 + "px", height - 8 + "px");
        canvas.setCoordinateSpaceWidth(width - 8);
        canvas.setCoordinateSpaceHeight(height - 8);
    }

    public void setPathway(CanvasPathway pathway) {
        this.pathway = pathway;
        update();
    }
    
    public void translate(double dx, double dy) {
        this.translateX += dx;
        this.translateY += dy;
    }
    
    public void scale(double scale) {
        this.scale *= scale;
    }
    
    public void reset() {
        translateX = 0.0d;
        translateY = 0.0d;
        scale = 1.0d;
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        if (pathway == null)
            return;
        List<Node> nodes = pathway.getChildren();
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        Context2d c2d = canvas.getContext2d();
        c2d.save();
        c2d.clearRect(0.0d, 
                      0.0d, 
                      canvas.getOffsetWidth(),
                      canvas.getOffsetHeight());
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
//        // Set back to original coordinates
//        c2d.translate(-translateX, -translateY);
//        // Rescale back to the original
//        c2d.scale(1.0d/scale, 1.0d/scale);
    }
}
