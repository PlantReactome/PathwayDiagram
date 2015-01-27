/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.DiseaseCanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * This class is used to draw pathways.
 * @author gwu
 *
 */
public class PathwayCanvasDrawer {
	private GraphObjectRendererFactory rendererFactory;
    // Cached two types of drawing
    private PathwayDrawer normalDrawer;
    private PathwayDrawer diseaseDrawer;
    
    // Adjustment of selection/highlighted line thickness based on canvas scale
    private double lineWidthScale;
    
    public PathwayCanvasDrawer() {
    	rendererFactory = GraphObjectRendererFactory.getFactory();
        normalDrawer = new NormalPathwayDrawer();
        diseaseDrawer = new DiseasePathwayDrawer();
    }
    
    /**
     * This method is used to draw pathway actually.
     * @param c2d canvas context 2d
     */
    public void drawPathway(CanvasPathway pathway,
                            PathwayCanvas canvas,
                            Context2d c2d,
                            boolean scaleLineWidth) {
    	setLineWidthScale(scaleLineWidth ? canvas.getScale() : 1);
        if (pathway instanceof DiseaseCanvasPathway)
            diseaseDrawer.drawPathway(pathway, canvas, c2d);
        else
            normalDrawer.drawPathway(pathway, canvas, c2d);
    }
    
    private void setLineWidthScale(double canvasScale) {
    	this.lineWidthScale = 1 / canvasScale;
    }
    
    private double getLineWidthScale() {
    	return lineWidthScale;
    }
    
    private void renderGraphObject(Context2d c2d, GraphObject object) {
    	if (object instanceof Node) {
    		NodeRenderer nodeRenderer = rendererFactory.getNodeRenderer((Node) object);
    		
    		if (nodeRenderer != null) {
    			nodeRenderer.setLineWidthScale(getLineWidthScale());
    			nodeRenderer.render(c2d, (Node) object);
    		}
    	} else if (object instanceof HyperEdge) {
    		HyperEdgeRenderer edgeRenderer = rendererFactory.getEdgeRenderere((HyperEdge) object);
    		
    		if (edgeRenderer != null) {
    			edgeRenderer.setLineWidthScale(getLineWidthScale());
    			edgeRenderer.render(c2d, (HyperEdge) object);
    		}
    	}
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
            List<Node> nodes = pathway.getChildren();
            if (nodes != null) {
                // Always draw compartments first
                for (Node node : nodes) {
                    if (node.getType() == GraphObjectType.RenderableCompartment) {
                        renderGraphObject(c2d, node);
                    }
                }
                for (Node node : nodes) {
                    if (node.getType() != GraphObjectType.RenderableCompartment) {
                    	renderGraphObject(c2d, node);
                    }
                }
            }
            // Draw edges
            List<HyperEdge> edges = pathway.getEdges();
            if (edges != null) {
                for (HyperEdge edge : edges) {
                    renderGraphObject(c2d, edge);
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
            drawNormalObjects(c2d, diseasePathway);
            if (diseasePathway.isForNormalDraw())
                return; // That's it for the normal pathway!
            // Draw a shade to cover all normal objects.
            c2d.setFillStyle(org.reactome.diagram.view.Parameters.defaultShadeColor);
            // The canvas size actually doesn't change. So need to scale back the size
            // of the gray rectangle to offset the canvas zooming. Don't forget re-scale
            // back the translation origin.
            double scale = canvas.getScale();
            
            double left = Math.min(-canvas.getTranslateX() / scale, 0);
            double top = Math.min(-canvas.getTranslateY() / scale, 0);
            double right = Math.max((-canvas.getTranslateX() / scale) + (canvas.getOffsetWidth() / scale),
            						 pathway.getPreferredSize().getWidth());
            double bottom = Math.max((-canvas.getTranslateY() / scale) + (canvas.getOffsetHeight() / scale),
									 pathway.getPreferredSize().getHeight());
            c2d.fillRect(left, top, right - left, bottom - top);
//            System.out.println("Size of canvas: " + canvas.getOffsetWidth() + ", " + canvas.getOffsetHeight());
            drawDiseaseObjects(c2d, diseasePathway);
            drawOverlaidObjects(c2d, diseasePathway);
            // Don't need to handle LOF objects here. It should be handled already after setting needDashedLines.
        }
        
        private void drawOverlaidObjects(Context2d c2d, DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> overlaidObjects = diseasePathway.getOverlaidObjects();
            if (overlaidObjects == null || overlaidObjects.size() == 0)
                return;
            drawComponents(c2d, overlaidObjects);
        }
        
        private void drawDiseaseObjects(Context2d c2d, DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> diseaseObjects = diseasePathway.getDiseaseObjects();
            if (diseaseObjects == null || diseaseObjects.size() == 0)
                return;
            drawComponents(c2d, diseaseObjects);
        }

        public void drawComponents(Context2d c2d, List<GraphObject> diseaseObjects) {
        // Want to draw edges first to avoid any edge crossover onto nodes.
            for (GraphObject obj : diseaseObjects) {
                // Draw edges first
                if (obj instanceof HyperEdge) {
                    renderGraphObject(c2d, (HyperEdge)obj);
                }
            }
            for (GraphObject obj : diseaseObjects) {
                // Draw nodes first
                if (obj instanceof Node) {
                    // This is weird: compartment should not be here
                    if (obj.getType() == GraphObjectType.RenderableCompartment)
                        continue;
                    renderGraphObject(c2d, (Node)obj);
                }
            }
        }

        private void drawNormalObjects(Context2d c2d, DiseaseCanvasPathway diseasePathway) {
            List<GraphObject> normalObjects = diseasePathway.getNormalObjects();
            if (normalObjects == null || normalObjects.size() == 0)
                return;

            if (diseasePathway.getChildren() == null)
            	return;
            
            // Always draw compartments first
            for (Node node : diseasePathway.getChildren()) {
            	if (node.getType() == GraphObjectType.RenderableCompartment) {
            		renderGraphObject(c2d, node);
                }
            }
            
            // Draw nodes
            for (GraphObject normalObject : normalObjects) {
               if (normalObject.getType() == GraphObjectType.RenderableCompartment)
                  continue;
               
               if (normalObject instanceof Node) {
                  renderGraphObject(c2d, (Node) normalObject);
               }
            }
            
            // Draw edges
            List<HyperEdge> edges = diseasePathway.getEdges();
            if (edges != null) {
                for (HyperEdge edge : edges) {
                    if (!normalObjects.contains(edge))
                        continue;
                    renderGraphObject(c2d, edge);
                }
            }
        }
    }
    
}
