/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.DiseaseCanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * This class is used to draw pathways.
 * @author gwu
 *
 */
public class PathwayCanvasDrawer {
    // Cached two types of drawing
    private PathwayDrawer normalDrawer;
    private PathwayDrawer diseaseDrawer;
    
    public PathwayCanvasDrawer() {
        normalDrawer = new NormalPathwayDrawer();
        diseaseDrawer = new DiseasePathwayDrawer();
    }
    
    /**
     * This method is used to draw pathway actually.
     * @param c2d
     */
    public void drawPathway(CanvasPathway pathway,
                            PathwayCanvas canvas,
                            Context2d c2d) {
        if (pathway instanceof DiseaseCanvasPathway)
            diseaseDrawer.drawPathway(pathway, canvas, c2d);
        else
            normalDrawer.drawPathway(pathway, canvas, c2d);
    }    
    
    private interface PathwayDrawer {
        public void drawPathway(CanvasPathway pathway, PathwayCanvas canvas, Context2d c2d);
    }
    
    private class NormalPathwayDrawer implements PathwayDrawer {
        
        public NormalPathwayDrawer() {
        }

        @Override
        public void drawPathway(CanvasPathway pathway,
                                PathwayCanvas canvas,
                                Context2d c2d) {
            GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
            List<Node> nodes = pathway.getChildren();
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
                	System.out.println(node.getDisplayName() + " - " + node.getBounds().toString());
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
        }
        
    }
    
    private class DiseasePathwayDrawer implements PathwayDrawer {
        
        public DiseasePathwayDrawer() {
        }

        @Override
        public void drawPathway(CanvasPathway pathway,
                                PathwayCanvas canvas,
                                Context2d c2d) {
            if (!(pathway instanceof DiseaseCanvasPathway)) // Just in case
                return;
            DiseaseCanvasPathway diseasePathway = (DiseaseCanvasPathway) pathway;
            drawNormalObjects(c2d, 
                              diseasePathway);
            if (diseasePathway.isForNormalDraw())
                return; // That's it for the normal pathway!
            // Draw a shade to cover all normal objects.
            c2d.setFillStyle(org.reactome.diagram.view.Parameters.defaultShadeColor);
            // Have to reset to the original point
            c2d.translate(-canvas.getTranslateX(), -canvas.getTranslateY());
            c2d.scale(1.0d / canvas.getScale(), 1.0d / canvas.getScale());
            c2d.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
            System.out.println("Size of canvas: " + canvas.getOffsetWidth() + ", " + canvas.getOffsetHeight());
            // Have to reset back to the original place
            c2d.translate(canvas.getTranslateX(), canvas.getTranslateY());
            c2d.scale(canvas.getScale(), canvas.getScale());
            drawDiseaseObjects(c2d, diseasePathway);
            drawOverlaidObjects(c2d, diseasePathway);
            drawCrossedObjects(c2d, diseasePathway);
            // Don't need to handle LOF objects here. It should be handled already after setting needDashedLines.
        }
        
        private void drawCrossedObjects(Context2d c2d, 
                                        DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> crossedObjects = diseasePathway.getCrossedObjects();
            if (crossedObjects == null || crossedObjects.size() == 0)
                return;
            c2d.setLineWidth(Parameters.defaultCrossWidth);
            c2d.setStrokeStyle(Parameters.defaultCrossColor);
            for (GraphObject obj : crossedObjects)
                drawCross(obj, c2d);
        }
        
        private void drawCross(GraphObject obj, Context2d c2d) {
            if (!(obj instanceof Node))
                return;
            Node node = (Node) obj;
            Bounds bounds = node.getBounds();
            // Draw the first line
            c2d.beginPath();
            c2d.moveTo(bounds.getX(), bounds.getY());
            c2d.lineTo(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
            c2d.closePath();
            c2d.stroke();
            // Draw the second line
            c2d.beginPath();
            c2d.moveTo(bounds.getX() + bounds.getWidth(), bounds.getY());
            c2d.lineTo(bounds.getX(),  bounds.getHeight() + bounds.getY());
            c2d.closePath();
            c2d.stroke();
        }
        
        private void drawOverlaidObjects(Context2d c2d,
                                         DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> overlaidObjects = diseasePathway.getOverlaidObjects();
            if (overlaidObjects == null || overlaidObjects.size() == 0)
                return;
            drawComponents(c2d, overlaidObjects);
        }
        
        private void drawDiseaseObjects(Context2d c2d,
                                        DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> diseaseObjects = diseasePathway.getDiseaseObjects();
            if (diseaseObjects == null || diseaseObjects.size() == 0)
                return;
            drawComponents(c2d, 
                           diseaseObjects);
        }

        public void drawComponents(Context2d c2d,
                                   List<GraphObject> diseaseObjects) {
            GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
            // Want to draw edges first to avoid any edge crossover onto nodes.
            for (GraphObject obj : diseaseObjects) {
                // Draw nodes first
                if (obj instanceof HyperEdge) {
                    HyperEdgeRenderer renderer = viewFactory.getEdgeRenderere((HyperEdge)obj);
                    if (renderer == null)
                        continue;
                    renderer.render(c2d, (HyperEdge)obj);
                }
            }
            for (GraphObject obj : diseaseObjects) {
                // Draw nodes first
                if (obj instanceof Node) {
                    // This is weird: compartment should not be here
                    if (obj.getType() == GraphObjectType.RenderableCompartment)
                        continue;
                    NodeRenderer renderer = viewFactory.getNodeRenderer((Node)obj);
                    if (renderer == null)
                        continue;
                    renderer.render(c2d, (Node)obj);
                }
            }
        }

        private void drawNormalObjects(Context2d c2d,
                                      DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> normalObjects = diseasePathway.getNormalObjects();
            if (normalObjects == null || normalObjects.size() == 0)
                return;
            GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
            List<Node> nodes = diseasePathway.getChildren();
            // Draw normal objects
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
                    if (!normalObjects.contains(node))
                        continue;
                    NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                    if (renderer != null)
                        renderer.render(c2d,
                                        node);
                }
            }
            // Draw edges
            List<HyperEdge> edges = diseasePathway.getEdges();
            if (edges != null) {
                for (HyperEdge edge : edges) {
                    if (!normalObjects.contains(edge))
                        continue;
                    HyperEdgeRenderer renderer = viewFactory.getEdgeRenderere(edge);
                    if (renderer == null)
                        continue;
                    renderer.render(c2d, 
                                    edge);
                }
            }
        }
    }
    
}
