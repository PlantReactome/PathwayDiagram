/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.model.GraphObject;
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
    private Map<GraphObjectType, GraphObjectRenderer<? extends GraphObject>> typeToRenderer;
    // Default node and edge renderer
    private NodeRenderer nodeRenderer = new NodeRenderer();
    private HyperEdgeRenderer edgeRenderer = new HyperEdgeRenderer();
    
    protected GraphObjectRendererFactory() {
        registerRenderers();
    }

    private void registerRenderers() {
        typeToRenderer = new HashMap<GraphObjectType, GraphObjectRenderer<? extends GraphObject>>();
        typeToRenderer.put(GraphObjectType.Note,
                           new NoteRenderer());
        typeToRenderer.put(GraphObjectType.RenderableProtein, 
                           new ProteinRenderer());
        typeToRenderer.put(GraphObjectType.RenderableInteractor,
        				   new InteractorRenderer());
        typeToRenderer.put(GraphObjectType.RenderableComplex, 
                           new ComplexRenderer());
        typeToRenderer.put(GraphObjectType.RenderableChemical,
                           new ChemicalRenderer());
        typeToRenderer.put(GraphObjectType.RenderableEntitySet,
                           new EntitySetRenderer());
        typeToRenderer.put(GraphObjectType.ProcessNode,
                           new ProcessNodeRenderer());
        typeToRenderer.put(GraphObjectType.RenderableCompartment,
                           new CompartmentRenderer());
        typeToRenderer.put(GraphObjectType.EntitySetAndMemberLink, 
                           new EntitySetAndMemberLinkRenderer());
        EntitySetAndMemberLinkRenderer renderer = new EntitySetAndMemberLinkRenderer();
        renderer.setNeedOutput(false);
        renderer.setDashLinePattern(Parameters.setToSetLinePattern);
        typeToRenderer.put(GraphObjectType.EntitySetAndEntitySetLink,
                           renderer);
        typeToRenderer.put(GraphObjectType.RenderableInteraction,
                           new RenderableInteractionRenderer());
        typeToRenderer.put(GraphObjectType.RenderableGene,
                           new GeneRenderer());
        typeToRenderer.put(GraphObjectType.RenderableRNA,
                           new RNARenderer());
    }
    
    public static GraphObjectRendererFactory getFactory() {
        if (factory == null)
            factory = new GraphObjectRendererFactory();
        return factory;
    }
    
    public NodeRenderer getNodeRenderer(Node node) {
        if (node.getType() == GraphObjectType.RenderablePathway)
            return null; // This is not supported
        GraphObjectRenderer<? extends GraphObject> nodeRenderer = typeToRenderer.get(node.getType());
        if (nodeRenderer != null)
            return (NodeRenderer) nodeRenderer;
        return this.nodeRenderer;
    }
    
    public HyperEdgeRenderer getEdgeRenderere(HyperEdge edge) {
        HyperEdgeRenderer renderer = (HyperEdgeRenderer) typeToRenderer.get(edge.getType());
        if (renderer != null)
            return renderer;
        return edgeRenderer;
    }
    
}
