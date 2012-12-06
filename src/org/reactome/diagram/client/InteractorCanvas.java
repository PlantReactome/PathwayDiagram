/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Window;

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
    	c2d = getContext2d();
    	proteinsToInteractors = new HashMap<ProteinNode, List<InteractorNode>>();
    	uniqueInteractors = new HashMap<String, InteractorNode>();
    }
    
    public Set<ProteinNode> getProteins() {
    	return this.proteinsToInteractors.keySet();
    }	
    
    public void addProtein(ProteinNode protein) {    	
    	List<InteractorNode> iList = new ArrayList<InteractorNode>();
    	proteinsToInteractors.put(protein, iList);
    	addOrRemoveInteractors(protein, "add");
    	update();
    }
        
    public void removeProtein(ProteinNode protein) {
    	proteinsToInteractors.remove(protein);
    	addOrRemoveInteractors(protein, "remove");
    	update();
    }
    
    private void addOrRemoveInteractors(ProteinNode protein, String action) {    	
    	List<InteractorNode> iList = proteinsToInteractors.get(protein); 
    	for (InteractorNode i : protein.getInteractors()) {
    		String id = i.getRefId();
    		if (uniqueInteractors.containsKey(id)) {
    			InteractorNode ui = uniqueInteractors.get(id); 
    			if (action.equals("add")) {
    				ui.setCount(ui.getCount() + 1);
    				iList.add(ui);
    			} else if (action.equals("remove")) {
    				if (ui.getCount() > 1) {
    					ui.setCount(ui.getCount() - 1);
    				} else {
    					uniqueInteractors.remove(id);
    				}
    			}
    		} else {
    			if (action.equals("add")) {
    				iList.add(i);
    				uniqueInteractors.put(id, i);
    			}	
    		}
    	}
    }
    
    /**
     * This method is used to draw the interactors.     
     */
    public void update() {
        GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        if (!proteinsToInteractors.isEmpty()) {
        	for (ProteinNode prot : proteinsToInteractors.keySet()) {
        		List<InteractorNode> interactors = proteinsToInteractors.get(prot);
        		for (int i = 0; i < interactors.size(); i++) {
        			InteractorNode interactor = interactors.get(i);
        			
        			NodeRenderer renderer = viewFactory.getNodeRenderer(interactor);
                	if (renderer != null && !interactor.isShowing()) {
                		interactor.setBounds(getInteractorBounds(prot.getBounds(), i));
                		renderer.render(c2d, interactor);
                	}	
        		}
        	}
        }
            // Draw edges
            //List<HyperEdge> edges = null;
            //if (edges != null) {
            //    for (HyperEdge edge : edges) {
            //        HyperEdgeRenderer renderer = viewFactory.getEdgeRenderere(edge);
            //        if (renderer == null)
             //           continue;
             //       renderer.render(c2d, 
             //                       edge);
            //}
        //}
    }
    
    private Bounds getInteractorBounds(Bounds protBounds, int nodeNum) {
    	final double OFFSET = Math.PI / 2;
    	
    	double angle = (OFFSET - (2 * Math.PI / Parameters.TOTAL_INTERACTOR_NUM * nodeNum));

    	double protCentreX = protBounds.getCentre().getX();
    	double protCentreY = protBounds.getCentre().getY();
    	
    	double interactorCentreX = protCentreX + Math.cos(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    	double interactorCentreY = protCentreY + Math.sin(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    
    	int interactorX = (int) ((int) interactorCentreX - (Parameters.INTERACTOR_WIDTH));
    	int interactorY = (int) ((int) interactorCentreY - (Parameters.INTERACTOR_HEIGHT));
    
    	return new Bounds(interactorX, interactorY, Parameters.INTERACTOR_WIDTH, Parameters.INTERACTOR_HEIGHT); 
    }

	@Override
	protected void updateOthers(Context2d c2d) {
		// TODO Auto-generated method stub
		
	}
}
