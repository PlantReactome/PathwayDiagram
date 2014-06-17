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
public class GraphObjectExpressionRendererFactory extends GraphObjectRendererFactory {
    private static GraphObjectExpressionRendererFactory factory;
    // Keep a registered renderer here
    private Map<GraphObjectType, GraphObjectRenderer<? extends GraphObject>> typeToExpressionRenderer;
        
    private GraphObjectExpressionRendererFactory() {
        super();
    	registerRenderers();
    }

    private void registerRenderers() {
        typeToExpressionRenderer = new HashMap<GraphObjectType, GraphObjectRenderer<? extends GraphObject>>();
    
        typeToExpressionRenderer.put(GraphObjectType.RenderableComplex, 
                           new ExpressionComplexRenderer());
        typeToExpressionRenderer.put(GraphObjectType.ProcessNode,
                           new ExpressionProcessNodeRenderer());
        typeToExpressionRenderer.put(GraphObjectType.RenderableEntitySet,
                           new ExpressionEntitySetRenderer());
        typeToExpressionRenderer.put(GraphObjectType.RenderableGene,
                           new ExpressionGeneRenderer());
    }
    
    public static GraphObjectExpressionRendererFactory getFactory() {
        if (factory == null)
            factory = new GraphObjectExpressionRendererFactory();
        return factory;
    }
    
    public NodeRenderer getNodeRenderer(Node node) {
        GraphObjectRenderer<? extends GraphObject> nodeRenderer = typeToExpressionRenderer.get(node.getType());
        
        if (nodeRenderer != null)
            return (NodeRenderer) nodeRenderer;
        return super.getNodeRenderer(node);
    }    
}
