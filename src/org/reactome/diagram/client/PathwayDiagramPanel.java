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
    
    public PathwayDiagramPanel() {
        init();
    }
    
    private void init() {
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvas = new PlugInSupportCanvas();
        contentPane.add(canvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName("mainCanvas");
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        initWidget(contentPane);
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
    
    /**
     * Update drawing.
     */
    public void update() {
        if (pathway == null)
            return;
        List<Node> nodes = pathway.getChildren();
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        Context2d c2d = canvas.getContext2d();
        if (nodes != null) {
            // Always draw compartments first
            for (Node node : nodes) {
                if (node.getType() == GraphObjectType.RenderableCompartment) {
                    NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                    if (renderer != null)
                        renderer.render(c2d, node);
                }
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
        if (nodes != null) {
            for (Node node : nodes) {
                if (node.getType() == GraphObjectType.RenderableCompartment)
                    continue;
                NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                if (renderer != null)
                    renderer.render(c2d,
                                    node);
            }
        }
    }
}
