/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;

/**
 * A factory that is used to provide GraphObjectRenderer.
 * @author gwu
 *
 */
public class GraphObjectRendererFactory {
    private static GraphObjectRendererFactory factory;
    // Keep a registered renderer here
    private Map<GraphObjectType, GraphObjectRenderer<Node>> typeToRenderer;
    // This is only for test
    private NodeRenderer nodeRenderer = new NodeRenderer();
    private HyperEdgeRenderer edgeRenderer = new HyperEdgeRenderer();
    
    private GraphObjectRendererFactory() {
        registerRenderers();
    }

    private void registerRenderers() {
        typeToRenderer = new HashMap<GraphObjectType, GraphObjectRenderer<Node>>();
        typeToRenderer.put(GraphObjectType.RenderableProtein, 
                           new NodeRenderer());
        typeToRenderer.put(GraphObjectType.RenderableComplex, 
                           new ComplexRenderer());
        typeToRenderer.put(GraphObjectType.RenderableChemical,
                           new ChemicalRenderer());
    }
    
    public static GraphObjectRendererFactory getFactory() {
        if (factory == null)
            factory = new GraphObjectRendererFactory();
        return factory;
    }
    
    public NodeRenderer getNodeRenderer(Node node) {
        if (node.getType() == GraphObjectType.RenderablePathway)
            return null; // This is not supported
        GraphObjectRenderer<Node> nodeRenderer = typeToRenderer.get(node.getType());
        if (nodeRenderer != null)
            return (NodeRenderer) nodeRenderer;
        return this.nodeRenderer;
    }
    
    public HyperEdgeRenderer getEdgeRenderere(HyperEdge edge) {
        return edgeRenderer;
    }
    
}
