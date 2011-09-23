/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
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
    
    private GraphObjectRendererFactory() {
        registerRenderers();
    }

    private void registerRenderers() {
        typeToRenderer = new HashMap<GraphObjectType, GraphObjectRenderer<Node>>();
        typeToRenderer.put(GraphObjectType.RenderableProtein, 
                           new NodeRenderer());
    }
    
    public static GraphObjectRendererFactory getFactory() {
        if (factory == null)
            factory = new GraphObjectRendererFactory();
        return factory;
    }
    
    public GraphObjectRenderer<Node> getNodeRenderer(GraphObject graphObject) {
        return nodeRenderer;
    }
    
}
