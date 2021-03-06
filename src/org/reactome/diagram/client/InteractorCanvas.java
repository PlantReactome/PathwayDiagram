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

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorCanvasModel.InteractorConfidenceScoreColourModel;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.model.ProteinNode.InteractorCountNode;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.HyperEdgeRenderer;
import org.reactome.diagram.view.InteractorRenderer;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.touch.client.Point;


/**
 * This class is used to draw interactors.
 * @author gwu
 *
 */
public class InteractorCanvas extends DiagramCanvas {
    //private Context2d c2d;
    private PathwayDiagramPanel diagramPanel;
    private String userMessage;
    private Integer reObtainedProteinCount;
    private Integer previousProteinCount;
    
    // Proteins mapped to their list of interactors
	private Map<ProteinNode, List<InteractorNode>> proteinsToInteractors;
    // Interactor objects mapped to their accession ids or display name if no accession is available 
	private List<InteractorNode> uniqueInteractors; 
	private boolean loadingInteractors;

	private InteractorConfidenceScoreColourModel interactorColouring;
	
    public InteractorCanvas(PathwayDiagramPanel dPane) {
    	super(dPane);
       //	c2d = getContext2d();
       	diagramPanel = dPane;
       	userMessage = new String();
       	previousProteinCount = 0;
       	reObtainedProteinCount = 0;
       	hoverHandler = new InteractorCanvasHoverHandler(diagramPanel, this);
       	selectionHandler = new InteractorCanvasSelectionHandler(diagramPanel, this);
       	
       	//diagramPanel.getController().setInteractorDBList(interactorCanvasModel);
       	
       	proteinsToInteractors = new HashMap<ProteinNode, List<InteractorNode>>();
    	uniqueInteractors = new ArrayList<InteractorNode>();
    }
    
    public InteractorCanvas(PathwayDiagramPanel dPane, CanvasTransformation canvasTransformation) {
    	this(dPane);
    	this.canvasTransformation = new CanvasTransformation(canvasTransformation.getScale(),
    														 canvasTransformation.getTranslateX(),
    														 canvasTransformation.getTranslateY());
    }
    
    public List<ProteinNode> getProteins() {
    	return new ArrayList<ProteinNode>(this.proteinsToInteractors.keySet());
    }	
    
    public void addProtein(ProteinNode protein) {
    	List<InteractorNode> iList = new ArrayList<InteractorNode>();
    	proteinsToInteractors.put(protein, iList);
    	addOrRemoveInteractors(protein, "add");
    	update();
    	protein.setDisplayingInteractors(true);
    }
        
    public void removeProtein(ProteinNode protein) {
    	proteinsToInteractors.remove(protein);
    	addOrRemoveInteractors(protein, "remove");
    	update();
    	protein.setDisplayingInteractors(false);
    }
    
    public void removeAllProteins() {
    	removeAllProteins(true);
    }
    
    public void removeAllProteins(boolean clearInteractors) {
    	for (ProteinNode protein : getProteins()) {
    		removeProtein(protein);
    	}
    	
    	if (clearInteractors)
    		uniqueInteractors.clear();
    }
    
    private void addOrRemoveInteractors(ProteinNode protein, String action) {    	    	
    	if (protein.getInteractors() == null || protein.getInteractors().isEmpty())
    		return;
    	
    	int interactorsAdded = 0;
    	
    	// Process each interactor for the protein
    	for (InteractorNode interactor : protein.getInteractors()) {
			
    		if (!getUniqueInteractors().contains(interactor)) {
    			uniqueInteractors.add(interactor);
    		}
    		
    		InteractorNode seenInteractor = getUniqueInteractors().get(getUniqueInteractors().indexOf(interactor));
    		if (action.equals("add")) {
    			if (interactorsAdded < Parameters.TOTAL_INTERACTOR_NUM) {
    				seenInteractor.setCount(seenInteractor.getCount() + 1);
    				proteinsToInteractors.get(protein).add(seenInteractor); // Add interactor to protein's list
    				interactorsAdded++;
    			} else {	
    				break;
    			}	
    		} else if (action.equals("remove")) {
    			if (seenInteractor.getCount() > 0) {
    				seenInteractor.setCount(seenInteractor.getCount() - 1);
    				
    				ListIterator<InteractorEdge> edges = seenInteractor.getEdges().listIterator();
    				while (edges.hasNext()) {
    					InteractorEdge edge = edges.next();
    					
    					if (edge.getProtein() == protein) {
    						edges.remove();
    					}	
    				}
    			} 
    		}
    	}
    }
    
    public void update() {
    	Context2d c2d = getContext2d();
    	
    	c2d.save();
        
        clean(c2d); // Clear canvas
    	
        setGreyOutCanvas(!getVisibleInteractors().isEmpty()); // Grey out if there are unique interactors to display
        
        drawCanvasLayer(c2d);
        //drawInteractors(diagramPanel.getOverview().getContext2d());
        
        if (reObtainedProteinCount == previousProteinCount) {
        	displayUserMessage();
        	reObtainedProteinCount = 0;
        }
        	
        c2d.restore();
    }
    
    @Override
    public void drawCanvasLayer(Context2d c2d) {
    	drawInteractors(c2d);
    }
        
    public void drawInteractors(Context2d c2d) {    
        if (!proteinsToInteractors.isEmpty()) {
    		GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
    		InteractorNode draggingNode = null;
    		List<InteractorNode> interactorsToDraw = new ArrayList<InteractorNode>();
    		List<InteractorEdge> edgesToDraw = new ArrayList<InteractorEdge>();
        
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
           						interactor.setBounds(getInteractorBounds(interactor, prot.getBounds(), angle, c2d));
           						interactor.setPosition(interactor.getBounds().getX(), interactor.getBounds().getY());
           					}
           					interactorsToDraw.add(interactor);
           				} else {
           					// Set the interactor to the one contained in the drawing list which
           					// has the boundaries set -- this is needed to create the interactor
           					// edge connecting it to the protein
           					interactor = interactorsToDraw.get(interactorsToDraw.indexOf(interactor));
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
        
    		// Draw edges
    		for (int i = 0; i < edgesToDraw.size(); i++) {
    			InteractorEdge edge = edgesToDraw.get(i);
        	
    			HyperEdgeRenderer edgeRenderer = viewFactory.getEdgeRenderere(edge);
    			if (edgeRenderer != null) {
    				edgeRenderer.render(c2d, edge);
    			}
    		}
        
    		// Draw protein interactor count node
    		for (ProteinNode protein : proteinsToInteractors.keySet()) {
    			protein.getInteractorCountNode().updateCount();
    			
    			NodeRenderer renderer = viewFactory.getNodeRenderer(protein.getInteractorCountNode());
    			
    			if (renderer != null) {
    				renderer.render(c2d, protein.getInteractorCountNode());
    			}
    		}
    		
    		
    		// Draw interactors after edges (i.e. above them)        
    		for (int i = 0; i < interactorsToDraw.size(); i++) {
    			InteractorNode interactor = interactorsToDraw.get(i);
        	
    			InteractorRenderer renderer = (InteractorRenderer) viewFactory.getNodeRenderer(interactor);
    			if (renderer != null) {
    				renderer.setContextState(renderer.new Context2DState(getTranslateX(), getTranslateY(), getScale()));
    				renderer.render(c2d, interactor, interactorColouring);
    			}
    		}
    	}
    }
        
    // Gets interactor boundaries based on protein boundaries and how many
    // interactors have already been rendered for this protein
    // (interactors drawn in a circle around the protein)
    private Bounds getInteractorBounds(InteractorNode interactor, Bounds protBounds, double angle, Context2d c2d) {
    	double protCentreX = protBounds.getCentre().getX();
    	double protCentreY = protBounds.getCentre().getY();
    	
    	double interactorCentreX = protCentreX + Math.cos(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    	double interactorCentreY = protCentreY - Math.sin(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    
    	int interactorX;
    	int interactorY;
    	int width;
    	int height;
    	
    	if (interactor.getChemicalId() != null && !interactor.getChemicalId().isEmpty()) {
    		width = Parameters.IMAGE_WIDTH;
    		height = Parameters.IMAGE_HEIGHT;
    		interactorX = (int) (interactorCentreX - (width / 2));
    		interactorY = (int) (interactorCentreY - (height / 2));
    	} else {	
    		String name = interactor.getDisplayName();    		    	
    		String [] lines = name.split(" ");
    	
    		// Establish width of interactor bounds
    		int maxLineWidth = 6; // Default minimum 
    		for (String line : lines) {    			
    			maxLineWidth = Math.max(line.length(), maxLineWidth);
    		}    	
    		width = maxLineWidth * Parameters.INTERACTOR_CHAR_WIDTH;
    	
    	
    		// Establish height of interactor bounds
    		GraphObjectRendererFactory viewFactory = GraphObjectRendererFactory.getFactory();
    		InteractorRenderer renderer = (InteractorRenderer) viewFactory.getNodeRenderer(interactor);

    		height = (renderer.splitName(name, c2d, width).size() + 1) * Parameters.LINE_HEIGHT;
    	
    	
    	
    		interactorX = (int) ((int) interactorCentreX - (width / 2));
    		interactorY = (int) ((int) interactorCentreY - (height / 2));
    	}	
    		
    		
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
    	double x = rect.getX();
    	
    	if (side.equals("right")) 
    		x = x + rect.getWidth();	
    	
    	double y = slope * x + intercept;
    
    	if (rect.getY() <= y && y <= rect.getY() + rect.getHeight()) {
    		return new Point(x,y); 
    	}
    	
    	return null;
    }
    
    private Point getHorizIntersection(Bounds rect, String side, double slope, double intercept) {
    	double y = rect.getY();
    	
    	if (side.equals("bottom"))
    		y = y + rect.getHeight();
    	
    	double x = (y - intercept) / slope;
    
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
    	
    	for (InteractorNode interactor : getVisibleInteractors()) {
    		if (interactor.isPicked(point))
    			return interactor;
    	}
    	return null;    		
    }
    
    private List<InteractorNode> getUniqueInteractors() {
		return uniqueInteractors;
    }
    
    public List<InteractorNode> getVisibleInteractors() {
    	List<InteractorNode> visibleInteractors = new ArrayList<InteractorNode>();
    	for (InteractorNode interactor : getUniqueInteractors()) {
    		if (interactor.isVisible())
    			visibleInteractors.add(interactor);
    	}
    	
    	return visibleInteractors;
    }

    private List<InteractorEdge> getInteractorEdges() {
    	List<InteractorEdge> edges = new ArrayList<InteractorEdge>();
    	for (InteractorNode i : getVisibleInteractors()) {
    		for (InteractorEdge edge : i.getEdges()) 
    			edges.add(edge);
    	}
    	
    	return edges;
    }

    public List<InteractorCountNode> getProteinInteractorCountNodes() {
    	List<InteractorCountNode> proteinInteractorCountNodes = new ArrayList<InteractorCountNode>();
    	
    	for (ProteinNode protein : getProteins()) {
    		proteinInteractorCountNodes.add(protein.getInteractorCountNode());
    	}
    	
    	return proteinInteractorCountNodes;
    }
    
    public List<GraphObject> getObjectsForRendering() {
    	List<GraphObject> objects = new ArrayList<GraphObject>();
    	objects.addAll(getVisibleInteractors());
    	objects.addAll(getProteinInteractorCountNodes());
    	objects.addAll(getInteractorEdges());
    	
    	return objects;
    }
    
	@Override
	protected void updateOthers(Context2d c2d) {
		
	}
	
	public void reObtainProteinsForNewInteractorDatabase() {
		// Cache proteins before removing them from the canvas
		List<ProteinNode> proteinList = new ArrayList<ProteinNode>(getProteins());
		previousProteinCount = proteinList.size();
		removeAllProteins();
		
		clearUserMessage();
		
		// Re-obtain proteins for the new interactor database
		for (Integer i = 0; i < proteinList.size(); i++) {
			PathwayDiagramController.getInstance().getInteractors(proteinList.get(i), setInteractors(proteinList.get(i)), diagramPanel);
		}
	}
	
	public RequestCallback setInteractors(final ProteinNode protein) {
		RequestCallback setInteractors = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					final String interactorDatabase = diagramPanel.getInteractorCanvasModel().getInteractorDatabase();
					
					protein.getInteractors().clear();
					
					if (response.getText().contains("errorMessage")) {
						String errorMessage = interactorDatabase + " is currently unavailable";
						
						if (!getUserMessage().contains(errorMessage))
							addToUserMessage(errorMessage);												
					} else {
						protein.setInteractors(response.getText());
						
						if (protein.getInteractors() == null || protein.getInteractors().isEmpty())
							addToUserMessage(protein.getDisplayName() + " has no interactors for the " + 
											 interactorDatabase + " database");
					}
					
					setReObtainedProteinCount(getReObtainedProteinCount() + 1);
					addProtein(protein);					
				} else {
					AlertPopup.alert("Failed to get interactors - " + response.getStatusText());
				}
				
				setLoadingInteractors(false);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				AlertPopup.alert(exception.getMessage());
				setLoadingInteractors(false);
			}
			
		};
		
		return setInteractors;
	}

	public boolean isLoadingInteractors() {
		return loadingInteractors;
	}

	public void setLoadingInteractors(boolean loadingInteractors) {
		this.loadingInteractors = loadingInteractors;
		if (loadingInteractors) {
			WidgetStyle.setCursor(this, Cursor.WAIT);
		} else {
			WidgetStyle.setCursor(this, Cursor.DEFAULT);
		}
	}
	
	public String getUserMessage() {
		return userMessage;
	}

	public void addToUserMessage(String message) {
		userMessage = userMessage.concat(message + "<br />"); 
	}
	
	private void displayUserMessage() {
		if (userMessage != null && !userMessage.isEmpty()) {
			AlertPopup.alert(userMessage);
			clearUserMessage();
		}	
	}

	private void clearUserMessage() {
		userMessage = new String();
	}
	
	public Integer getReObtainedProteinCount() {
		return reObtainedProteinCount;
	}


	public void setReObtainedProteinCount(Integer reObtained) {
		this.reObtainedProteinCount = reObtained;
	}


	public InteractorConfidenceScoreColourModel getInteractorColouring() {
		return interactorColouring;
	}


	public void setInteractorColouring(InteractorConfidenceScoreColourModel interactorColouring) {
		this.interactorColouring = interactorColouring;
		update();
	}
}
