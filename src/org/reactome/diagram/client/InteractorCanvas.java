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
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

/**
 * This class is used to draw interactors.
 * @author gwu
 *
 */
public class InteractorCanvas extends DiagramCanvas {
    private Context2d c2d;
    // Proteins mapped to their list of interactors
	private Map<ProteinNode, List<InteractorNode>> proteinsToInteractors;
    // Interactor objects mapped to their accession ids 
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
    	
    	// Process each interactor for the protein
    	for (InteractorNode i : protein.getInteractors()) {
    		String id = i.getRefId();
    		
    		// If the interactor is already known by the canvas
    		// increase/decrease count for that interactor
    		if (uniqueInteractors.containsKey(id)) {
    			InteractorNode ui = uniqueInteractors.get(id); 
    			    			     			
    			if (action.equals("add")) {
    				ui.setCount(ui.getCount() + 1);
    				iList.add(ui); // Add interactor to protein's list
    			} else if (action.equals("remove")) {
    				if (ui.getCount() > 1) {
    					ui.setCount(ui.getCount() - 1);
    				} else {
    					uniqueInteractors.remove(id); // Removed if count is zero
    				}
    			}
    		} else {
    			// Add interactor to protein and canvas lists 
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
        			if (i >= Parameters.TOTAL_INTERACTOR_NUM) {
        				break;
        			}
        			
        			double angle = 2 * Math.PI / Parameters.TOTAL_INTERACTOR_NUM * i;
        			
        			// Draw interactor
        			InteractorNode interactor = interactors.get(i);
           			NodeRenderer renderer = viewFactory.getNodeRenderer(interactor);
                	if (renderer != null && !interactor.isShowing()) {
                		interactor.setBounds(getInteractorBounds(prot.getBounds(), angle));
                		renderer.render(c2d, interactor);
                	}	
                	
                    // Draw connector between the protein and the interactor
                	HyperEdge edge = createHyperEdge(prot, interactor, angle);
                	if (edge != null) {
                		HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere(edge);
                	    if (edgeRenderer == null)
                	      	continue;
                	    edgeRenderer.render(c2d, edge);
                	}
        		}
        	}
        }
    }    
    
    // Gets interactor boundaries based on protein boundaries and how many
    // interactors have already been rendered for this protein
    // (interactors drawn in a circle around the protein)
    private Bounds getInteractorBounds(Bounds protBounds, double angle) {
    	double protCentreX = protBounds.getCentre().getX();
    	double protCentreY = protBounds.getCentre().getY();
    	
    	double interactorCentreX = protCentreX + Math.cos(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    	double interactorCentreY = protCentreY - Math.sin(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    
    	int interactorX = (int) ((int) interactorCentreX - (Parameters.INTERACTOR_WIDTH / 2));
    	int interactorY = (int) ((int) interactorCentreY - (Parameters.INTERACTOR_HEIGHT / 2));
    
    	return new Bounds(interactorX, interactorY, Parameters.INTERACTOR_WIDTH, Parameters.INTERACTOR_HEIGHT); 
    }

    private HyperEdge createHyperEdge(ProteinNode prot, InteractorNode interactor, double angle) {
    	Bounds p = prot.getBounds();
    	Bounds i = interactor.getBounds();		
        	
    	Point start = getBoundaryIntersection(p, angle, "start");
    	Point end = getBoundaryIntersection(i, angle, "end");
    	    	
    	List<Point> backbone = new ArrayList<Point>(); 
    	backbone.add(start);
    	backbone.add(end);
    	
    	HyperEdge edge = new HyperEdge();
    	edge.setBackbone(backbone);
    	return edge;
    }
    	
    private Point getBoundaryIntersection(Bounds rect, double angle, String node) {
    	double rCentreX = rect.getCentre().getX();
    	double rCentreY = rect.getCentre().getY();
    	
    	double slope = -Math.tan(angle);
    	double intercept = rCentreY - (slope * rCentreX);
    	
    	String horiz = null;
    	String vert = null;
    	
    	if (angle <= Math.PI / 2) {
    		if (node.equals("start")) {
    			horiz = "top";
    			vert = "right";
    		} else if (node.equals("end")) {
    			horiz = "bottom";
    			vert = "left";
    		}
    	} else if (angle <= Math.PI) {
    		if (node.equals("start")) {
    			horiz = "top";
    			vert = "left";
    		} else if (node.equals("end")) {
    			horiz = "bottom";
    			vert = "right";
    		}    				
    	} else if (angle <= 3 * Math.PI / 2) {
    		if (node.equals("start")) {
    			horiz = "bottom";
    			vert = "left";
    		} else if (node.equals("end")) {
    			horiz = "top";
    			vert = "right";
    		}
    	} else if (angle < 2 * Math.PI) {
    		if (node.equals("start")) {
    			horiz = "bottom";
    			vert = "right";
    		} else if (node.equals("end")) {
    			horiz = "top";
    			vert = "left";
    		}
    	}
    	
    	Point intersection = getHorizIntersection(rect, horiz, slope, intercept);
    	if (intersection == null) {
    		intersection = getVertIntersection(rect, vert, slope, intercept);
    	}	
    	return intersection;
    }
    
    private Point getVertIntersection(Bounds rect, String side, double slope, double intercept) {
    	int x = rect.getX();
    	
    	if (side.equals("right")) 
    		x = x + rect.getWidth();	
    	
    	int y = (int) (slope * x + intercept);
    
    	if (rect.getY() <= y && y <= rect.getY() + rect.getHeight()) {
    		return new Point(x,y); 
    	}
    	
    	return null;
    }
    
    private Point getHorizIntersection(Bounds rect, String side, double slope, double intercept) {
    	int y = rect.getY();
    	
    	if (side.equals("bottom"))
    		y = y + rect.getHeight();
    	
    	int x = (int) ((y - intercept) / slope);
    
    	if (rect.getX() <= x && x <= rect.getX() + rect.getWidth()) {
    		return new Point(x,y);
    	}	
    	
    	return null;
    }
    
	@Override
	protected void updateOthers(Context2d c2d) {
		// TODO Auto-generated method stub
		
	}
}
