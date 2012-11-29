/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * This class is used to draw interactors.
 * @author gwu
 *
 */
public class InteractorCanvas extends DiagramCanvas {
    private Context2d c2d;
	private Map<ProteinNode, List<InteractorNode>> proteinsToInteractors;
    private Map<String, InteractorNode> uniqueInteractors; 
	
    
    public InteractorCanvas() {
    	c2d = this.getContext2d();
    }
    
    public Set<ProteinNode> getProteins() {
    	return this.proteinsToInteractors.keySet();
    }	
    
    public void addProtein(ProteinNode protein) {
    	proteinsToInteractors.put(protein, new ArrayList<InteractorNode>());
    	addOrRemoveInteractors(protein, "add");
    	update();
    }
        
    public void removeProtein(ProteinNode protein) {
    	proteinsToInteractors.remove(protein);
    	addOrRemoveInteractors(protein, "remove");
    	update();
    }
    
    private void addOrRemoveInteractors(ProteinNode protein, String action) {    	
    	List<InteractorNode> iList = proteinsToInteractors 
    	for (InteractorNode i : protein.getInteractors()) {
    		String id = i.getRefId();
    		if (uniqueInteractors.containsKey(id)) {
    			InteractorNode ui = uniqueInteractors.get(id); 
    			if (action.equals("add")) {
    				ui.setCount(ui.getCount() + 1);
    			} else if (action.equals("remove")) {
    				if (ui.getCount() > 1) {
    					ui.setCount(ui.getCount() - 1);
    				} else {
    					uniqueInteractors.remove(id);
    				}
    			}
    		} else {
    			uniqueInteractors.put(id, i);
    		}
    	}
    }
    
    /**
     * This method is used to draw the interactors.     
     */
    public void update() {
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        if (proteins != null) {
                for (ProteinNode node : proteins) {
                                           
                    NodeRenderer renderer = viewFactory.getNodeRenderer(node);
                    if (renderer != null)
                        renderer.render(c2d, node);
                }
            }
            // Draw edges
            List<HyperEdge> edges = null;
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

	@Override
	protected void updateOthers(Context2d c2d) {
		// TODO Auto-generated method stub
		
	}
}
