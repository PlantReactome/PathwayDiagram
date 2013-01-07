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
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.InteractorRenderer;
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
	private List<InteractorNode> drawnInteractors;
	    
    public InteractorCanvas(PathwayDiagramPanel dPane) {
    	super();
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
    	
    	int interactorsAdded = 0;
    	// Process each interactor for the protein
    	for (InteractorNode i : protein.getInteractors()) {
    		String id = i.getRefId();

			// If the interactor is already known by the canvas
    		// increase/decrease count for that interactor
    		if (uniqueInteractors.containsKey(id)) {
    			InteractorNode ui = uniqueInteractors.get(id); 
    			    			     			
    			if (action.equals("add")) {
    				if (interactorsAdded < Parameters.TOTAL_INTERACTOR_NUM) {	
    					ui.setCount(ui.getCount() + 1);
    					iList.add(ui); // Add interactor to protein's list
    					interactorsAdded++;
    				} else {	
    					break;
    				}	
    			} else if (action.equals("remove")) {
    				if (ui.getCount() > 1) {
    					ui.setCount(ui.getCount() - 1);
    					
    					for (InteractorEdge edge : ui.getEdges()) {
    						if (edge.getProtein() == protein) {
    							ui.removeEdge(edge);
    						}	
    					}
    				} else {
    					uniqueInteractors.remove(id); // Removed if count is zero
    				}
    			}
    		} else {
    			// Add interactor to protein and canvas lists 
    			if (action.equals("add")) {
    				if (interactorsAdded < Parameters.TOTAL_INTERACTOR_NUM) {
    					iList.add(i);
    					interactorsAdded++;
    					uniqueInteractors.put(id, i);
    				}	
    			}	
    		}
    	}
    }
    
    /**
     * This method is used to draw the interactors.     
     */
    public void update() {
        c2d.save();
        
        clean();
    	    	
        
    	GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        drawnInteractors = new ArrayList<InteractorNode>();
    	if (!proteinsToInteractors.isEmpty()) {
        	for (ProteinNode prot : proteinsToInteractors.keySet()) {
        		List<InteractorNode> interactors = proteinsToInteractors.get(prot);
        		double interactorNum = Math.min(interactors.size(), Parameters.TOTAL_INTERACTOR_NUM);

        		for (int i = 0; i < interactorNum; i++) {
        			double angle = 2 * Math.PI / interactorNum * i;
        			
        			// Draw interactor
        			InteractorNode interactor = interactors.get(i);
           			InteractorRenderer renderer = (InteractorRenderer) viewFactory.getNodeRenderer(interactor);                	
           			if (!interactor.isDragging()) {
           				if (renderer != null && !drawnInteractors.contains(interactor)) {
           					interactor.setBounds(getInteractorBounds(prot.getBounds(), angle));
           					interactor.setPosition(interactor.getBounds().getX(), interactor.getBounds().getY());
           					renderer.render(c2d, interactor);
           					drawnInteractors.add(interactor);                		
           				}
           				
           				// Draw connector between the protein and the interactor
           				InteractorEdge edge = createInteractorEdge(prot, interactor);
           				interactor.addEdge(edge);	
           				if (edge != null) {               		
           					HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere(edge);
                			if (edgeRenderer == null)
                				continue;
                			edgeRenderer.render(c2d, edge);
           				}           					
                	} else {
                		renderer.render(c2d, interactor);
                		//Window.alert("Edges: " + interactor.getEdges().size());
                		for (InteractorEdge edge : interactor.getEdges()) {
                			HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere(edge);
                			edgeRenderer.render(c2d, edge);
                		}
                		interactor.setDragging(false);
                	}	
                }
        	}
    	}	
        c2d.restore();
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

    private InteractorEdge createInteractorEdge(ProteinNode prot, InteractorNode interactor) {
    	Bounds p = prot.getBounds();
    	Bounds i = interactor.getBounds();		
        
    	double rise = p.getCentre().getY() - i.getCentre().getY();
    	double run = p.getCentre().getX() - i.getCentre().getX(); 
    	
    	double angle = Math.atan2(-rise, run) + Math.PI;
    	
    	if (angle == 2 * Math.PI) 
    		angle = 0;
    	
    	Point start = getBoundaryIntersection(p, "start", angle);
    	Point end = getBoundaryIntersection(i, "end", angle);
    	    	
    	List<Point> backbone = new ArrayList<Point>(); 
    	backbone.add(start);
    	backbone.add(end);
    	
    	Point midPoint = new Point((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    	
    	InteractorEdge edge = new InteractorEdge();
    	edge.setBackbone(backbone);
    	edge.setPosition(midPoint);
    	edge.setProtein(prot);
    	edge.setInteractor(interactor);
    	return edge;
    }
    	
    private Point getBoundaryIntersection(Bounds rect, String node, double angle) {
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
    
    public void drag(InteractorNode interactor, int dx, int dy) {
    	interactor.setDragging(true);
       	interactor.getBounds().translate(dx, dy);
    
    	for (InteractorEdge edge : interactor.getEdges()) {
    		interactor.removeEdge(edge);
    		interactor.addEdge(createInteractorEdge(edge.getProtein(), interactor));
    		//Point end = edge.getBackbone().get(1);    		
    		//edge.getBackbone().set(1, end.plus(new Point(dx, dy)));    		 
    	}
    	update();
    }

    public InteractorNode getDraggableNode(Point point) {
    	point = getCorrectedCoordinates(point);
    	
    	for (InteractorNode interactor : getUniqueInteractors()) {
    		if (interactor.isPicked(point))
    			return interactor;
    	}
    	return null;    		
    }
    
    public List<InteractorNode> getUniqueInteractors() {
    	List<InteractorNode> interactorList = new ArrayList<InteractorNode>(); 
    	interactorList.addAll(uniqueInteractors.values());
    	return interactorList;
    }
    
    public List<GraphObject> getGraphObjects() {
    	List<GraphObject> edges = new ArrayList<GraphObject>();
    	for (InteractorNode i : getUniqueInteractors()) {
    		for (InteractorEdge edge : i.getEdges()) 
    			edges.add(edge);
    	}
    	
    	List<GraphObject> objects = new ArrayList<GraphObject>();
    	objects.addAll(edges);
    	objects.addAll(getUniqueInteractors());
    	
    	return objects;
    }
    
	@Override
	protected void updateOthers(Context2d c2d) {
		// TODO Auto-generated method stub
		
	}
}
