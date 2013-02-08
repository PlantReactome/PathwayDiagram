/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

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
	private static Map<String, String> psicquicMap;
	private static Map<String, String> interactorDBMap; 
	private boolean loadingInteractors;
	
    public InteractorCanvas(PathwayDiagramPanel dPane) {
    	super(dPane);
       	c2d = getContext2d();
       	diagramPanel = dPane;
       	hoverHandler = new InteractorCanvasHoverHandler(diagramPanel, this);
       	selectionHandler = new InteractorCanvasSelectionHandler(diagramPanel, this);
       	
    	diagramPanel.getController().setInteractorDBList();
       	proteinsToInteractors = new HashMap<ProteinNode, List<InteractorNode>>();
    	uniqueInteractors = new HashMap<String, InteractorNode>();
    }
        
    public List<ProteinNode> getProteins() {
    	return new ArrayList<ProteinNode>(this.proteinsToInteractors.keySet());
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
    		String id = i.getAccession();
    			
    			
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
    	
        drawInteractors(c2d);
        drawInteractors(diagramPanel.getOverview().getContext2d());
        
        c2d.restore();
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
    		
    		this.setStyleName(getStyle());
        } else {
        	this.removeStyleName(getStyle());
        }     
    }
        
    // Gets interactor boundaries based on protein boundaries and how many
    // interactors have already been rendered for this protein
    // (interactors drawn in a circle around the protein)
    private Bounds getInteractorBounds(InteractorNode interactor, Bounds protBounds, double angle) {
    	double protCentreX = protBounds.getCentre().getX();
    	double protCentreY = protBounds.getCentre().getY();
    	
    	double interactorCentreX = protCentreX + Math.cos(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    	double interactorCentreY = protCentreY - Math.sin(angle) * Parameters.INTERACTOR_EDGE_LENGTH;
    
    	int interactorX;
    	int interactorY;
    	int width;
    	int height;
    	
    	if (interactor.getChemicalId() != null) {
    		width = Parameters.IMAGE_WIDTH;
    		height = Parameters.IMAGE_HEIGHT;
    		interactorX = (int) (interactorCentreX - (width / 2));
    		interactorY = (int) (interactorCentreY - (height / 2));
    	} else {	
    		String name = interactor.getDisplayName();    		    	
    		String [] lines = name.split(" ");
    	
    		// Establish width of interactor bounds
    		int maxLineWidth = 5; // Default minimum 
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
		setInteractorDatabase(interactorDatabase, false);
	}
	
	public void setInteractorDatabase(String interactorDatabase, boolean initializing) {
		this.interactorDatabase = interactorDatabase;		
		InteractorEdge.setUrl(InteractorCanvas.interactorDBMap, interactorDatabase);
		
		List<ProteinNode> proteinList = new ArrayList<ProteinNode>(getProteins());
		removeAllProteins();
		if (!initializing && !proteinList.isEmpty())
			setStyleName(getStyle());
		
		for (ProteinNode protein: proteinList)
			this.diagramPanel.getController().getInteractors(protein);		
	}
	
	public static Map<String, String> getPSICQUICMap() {
		return psicquicMap;
	}
	
	public void setPSICQUICMap(String xml) {
		psicquicMap = new HashMap<String, String>();
		
		Document psicquicDom = XMLParser.parse(xml);
		Element psicquicElement = psicquicDom.getDocumentElement();
		XMLParser.removeWhitespace(psicquicElement);
		
		NodeList nodeList = psicquicElement.getChildNodes();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			
			if (name.equals("service")) {
				Element serviceElement = (Element) node;
				
				Node nameNode = serviceElement.getElementsByTagName("name").item(0);
				String serviceName = nameNode.getChildNodes().item(0).getNodeValue();
				
				Node urlNode = serviceElement.getElementsByTagName("restUrl").item(0);
				String serviceUrl = urlNode.getChildNodes().item(0).getNodeValue();
				
				psicquicMap.put(serviceName, serviceUrl);
			}
		}
		
		addToInteractorDBMap(psicquicMap);
	}
	
	public static Map<String, String> getInteractorDBMap() {
		return interactorDBMap;
	}
	
	public void addToInteractorDBMap(Map<String, String> map) {
		if (interactorDBMap == null) {
			interactorDBMap = map;
		} else {
			for (String db : map.keySet()) {
				if (!interactorDBMap.containsKey(db))
					interactorDBMap.put(db, map.get(db));
			}
		}
			
		ListBox interactorDBList = diagramPanel.getControls().getInteractionDBList();		
		interactorDBList.clear();
		
		List<String> dbs = new ArrayList<String>(interactorDBMap.keySet());
		Collections.sort(dbs);
		for (int i = 0; i < dbs.size(); i++) {
			String db = dbs.get(i);
			
			interactorDBList.addItem(db, map.get(db));
			if (db.equals("IntAct")) {
				interactorDBList.setSelectedIndex(i);
				setInteractorDatabase(db, true);				
			}
		}		
	}		
	
	private String getStyle() {
		return diagramPanel.getStyle().interactorCanvas();
	}

	public boolean isLoadingInteractors() {
		return loadingInteractors;
	}

	public void setLoadingInteractors(boolean loadingInteractors) {
		this.loadingInteractors = loadingInteractors;
		if (loadingInteractors) {
			diagramPanel.setCursor(Cursor.WAIT);
		} else {
			diagramPanel.setCursor(Cursor.DEFAULT);
		}
	}
}