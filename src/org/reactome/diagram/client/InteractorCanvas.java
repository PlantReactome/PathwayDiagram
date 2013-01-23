/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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
    private PathwayDiagramPanel diagramPanel;
    
    private String interactorDatabase;
    // Proteins mapped to their list of interactors
	private Map<ProteinNode, List<InteractorNode>> proteinsToInteractors;
    // Interactor objects mapped to their accession ids 
	private Map<String, InteractorNode> uniqueInteractors; 
		    
    public InteractorCanvas(PathwayDiagramPanel dPane) {
    	super(dPane);
       	c2d = getContext2d();
       	diagramPanel = dPane;
       	hoverHandler = new InteractorCanvasHoverHandler(diagramPanel, this);
       	selectionHandler = new InteractorCanvasSelectionHandler(diagramPanel, this);
       	
       	setInteractorDatabase("IntAct"); 
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
    
    public void removeAllProteins() {
    	proteinsToInteractors.clear();
    	uniqueInteractors.clear();
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
    					
    					ListIterator<InteractorEdge> edges = ui.getEdges().listIterator();
    					while (edges.hasNext()) {
    						InteractorEdge edge = edges.next();
    						
    						if (edge.getProtein() == protein) {
    							edges.remove();
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
    
    public void update() {
    	c2d.save();
        
        clean(c2d); // Clear canvas
    	    	
        
    	GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
        InteractorNode draggingNode = null;
    	List<InteractorNode> interactorsToDraw = new ArrayList<InteractorNode>();
    	List<InteractorEdge> edgesToDraw = new ArrayList<InteractorEdge>();
        
        if (!proteinsToInteractors.isEmpty()) {
    		for (ProteinNode prot : proteinsToInteractors.keySet()) {
        		List<InteractorNode> interactors = proteinsToInteractors.get(prot);
        		  
        		double interactorNum = Math.min(interactors.size(), Parameters.TOTAL_INTERACTOR_NUM);
        		
        		for (int i = 0; i < interactorNum; i++) {
        			double angle = 2 * Math.PI / interactorNum * i;
        			
        			
        			InteractorNode interactor = interactors.get(i);        			
           			if (!interactor.isDragging()) {
           				
           				// Add interactor to the 'to be drawn' list if not already added
           				if (!interactorsToDraw.contains(interactor)) {
           					if (interactor.getBounds() == null) {
           						interactor.setBounds(getInteractorBounds(interactor, prot.getBounds(), angle));
           						interactor.setPosition(interactor.getBounds().getX(), interactor.getBounds().getY());
           					}
           					interactorsToDraw.add(interactor);                		
           				}
           				
           				// Add connector between the protein and the interactor to edge drawing list 
           				InteractorEdge edge = createInteractorEdge(prot, interactor);
           				interactor.addEdge(edge);	
           				edgesToDraw.add(edge);           				           					
                	} else {
                		draggingNode = interactor;                		
                		interactor.setDragging(false);
                	}	
                }
        	}
    		
    		// Add dragging node and edges here to be drawn last and above every other
    		// node and edge
    		if (draggingNode != null) {
    			interactorsToDraw.add(draggingNode);
    			
    			for (InteractorEdge edge : draggingNode.getEdges()) {
    				edgesToDraw.add(edge);
    			}
    		}
    	}	
        
        // Draw edges
        for (int i = 0; i < edgesToDraw.size(); i++) {
        	InteractorEdge edge = edgesToDraw.get(i);
        	
        	HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere(edge);
        	if (edgeRenderer != null) {
        		edgeRenderer.render(c2d, edge);
        	}
        }
        
        // Draw interactors after edges (i.e. above them)        
        for (int i = 0; i < interactorsToDraw.size(); i++) {
        	InteractorNode interactor = interactorsToDraw.get(i);
        	
        	InteractorRenderer renderer = (InteractorRenderer) viewFactory.getNodeRenderer(interactor);
        	if (renderer != null) {
        		renderer.render(c2d, interactor);
        	}
        }
                
        c2d.restore();
    }    
        
    // Gets interactor boundaries based on protein boundaries and how many
    // interactors have already been rendered for this protein
    // (interactors drawn in a circle around the protein)
    private Bounds getInteractorBounds(InteractorNode interactor, Bounds protBounds, double angle) {
    	double protCentreX = protBounds.getCentre().getX();
    	double protCentreY = protBounds.getCentre().getY();
    	
    	double interactorCentreX = protCentreX + Math.cos(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    	double interactorCentreY = protCentreY - Math.sin(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    
    	String name = interactor.getDisplayName();
    	String [] lines = name.split(" ");
    	
    	// Establish width of interactor bounds
    	int maxLineWidth = 5; // Default minimum 
    	for (String line : lines) {
    		maxLineWidth = Math.max(line.length(), maxLineWidth);
    	}    	
    	int width = maxLineWidth * Parameters.INTERACTOR_CHAR_WIDTH;
    	
    	
    	// Establish height of interactor bounds
    	GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
    	InteractorRenderer renderer = (InteractorRenderer) viewFactory.getNodeRenderer(interactor);

    	int height = (renderer.splitName(name, c2d, width).size() + 1) * Parameters.LINE_HEIGHT;
    	
    	
    	
    	int interactorX = (int) ((int) interactorCentreX - (width / 2));
    	int interactorY = (int) ((int) interactorCentreY - (height / 2));
    
    	return new Bounds(interactorX, interactorY, width, height); 
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
    	
    	if (i.isColliding(p)) {
    		start = end;
    	}
    	
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
       	interactor.setPosition(interactor.getPosition().plus(new Point(dx, dy)));
       	
       	List<InteractorEdge> modifiedEdges = new ArrayList<InteractorEdge>(); 
    	for (InteractorEdge edge : interactor.getEdges()) {
    		modifiedEdges.add(createInteractorEdge(edge.getProtein(), interactor));    		    		 
    	}
    	
    	interactor.setEdges(modifiedEdges);
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

	public String getInteractorDatabase() {
		return interactorDatabase;
	}

	public void setInteractorDatabase(String interactorDatabase) {
		this.interactorDatabase = interactorDatabase;
		this.diagramPanel.getController().setInteractorEdgeUrl(interactorDatabase);
	}
}
