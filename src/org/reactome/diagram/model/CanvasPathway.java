/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.model.ConnectWidget.ConnectRole;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.touch.client.Point;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * This class is used to hold all objects that should be rendered in a HTML 5 canvas as a pathway
 * diagram. A CanvasPathway should contain a list of nodes and HyperEdges. This basically is a more
 * advanced Graph data structure.
 * @author gwu
 *
 */
public class CanvasPathway extends Node {
    // A list of edges that should be rendered
    private List<HyperEdge> edges;
    // Mapping of each child node's internal id to reference entity 
    private Map<Long, List<ReferenceEntity>> dbIdToRefEntity;
    
    public CanvasPathway() {
    	dbIdToRefEntity = new HashMap<Long, List<ReferenceEntity>>();
    }
    
    /**
     * A method to fill nodes and edges from an XML element.
     * @param pathwayElm
     */
    public void buildPathway(Element pathwayElm) {
        String reactomeId = pathwayElm.getAttribute("reactomeId");
        if (reactomeId != null)
            setReactomeId(new Long(reactomeId));
        NodeList nodeList = pathwayElm.getChildNodes();
        Map<Integer, Node> idToNode = new HashMap<Integer, Node>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node = nodeList.item(i);
            String name = node.getNodeName();
            if (name.equals("Properties"))
                parseDisplayName(node, this);
            else if (name.equals("Nodes"))
                idToNode = parseNodes((Element)node);
            else if (name.equals("Edges"))
                parseEdges((Element)node,
                           idToNode);
        }
    }
    
    public List<HyperEdge> getEdges() {
        return this.edges;
    }    
    
    public Map<Long, List<ReferenceEntity>> getDbIdToRefEntity() {
		return dbIdToRefEntity;
	}

    public Map<Long, Map<Long, ReferenceEntity>> getDbIdToRefEntityWithRefIdMapping() {
    	Map<Long, Map<Long, ReferenceEntity>> dbIdToRefEntityWithMapping = new HashMap<Long, Map<Long, ReferenceEntity>>();

    	for (Long dbId : dbIdToRefEntity.keySet()) {
    		Map<Long, ReferenceEntity> refEntityMapping = new HashMap<Long, ReferenceEntity>();
    		for (ReferenceEntity refEntity : dbIdToRefEntity.get(dbId)) {
    			refEntityMapping.put(refEntity.getDbId(), refEntity);
    		}
    		dbIdToRefEntityWithMapping.put(dbId, refEntityMapping);
    	}
    	
    	return dbIdToRefEntityWithMapping;
    }
    
	public void setDbIdToRefEntity(String json) {
		clearDbIdToRefEntity();
		
		JSONArray mapObjects = (JSONArray) JSONParser.parseStrict(json);		
		for (int i = 0; i < mapObjects.size(); i++) {
			JSONObject entityMap = mapObjects.get(i).isObject();
			dbIdToRefEntity.put(getPhysicalEntityId(entityMap), getReferenceEntities(entityMap));
		}
	}
	
	private void clearDbIdToRefEntity() {
		dbIdToRefEntity.clear();
	}

	private Long getPhysicalEntityId(JSONObject entityMap) {
		return (long) entityMap.get("peDbId").isNumber().doubleValue();
	}
	
	private List<ReferenceEntity> getReferenceEntities(JSONObject entityMap) {
		List<ReferenceEntity> refEntities = new ArrayList<ReferenceEntity>();
		
		JSONValue refEntitiesJSON = entityMap.get("refEntities");
		
		if (refEntitiesJSON != null && refEntitiesJSON.isArray() != null) {
			JSONArray refEntityArray = refEntitiesJSON.isArray();
			for (int i = 0; i < refEntityArray.size(); i++) {
				JSONObject refEntity = refEntityArray.get(i).isObject();
			
				if (refEntity != null) {
					JSONNumber dbIdFromJson = refEntity.get("dbId").isNumber();
				
					Long dbId = (dbIdFromJson != null) ? (long) dbIdFromJson.doubleValue() : null;
					String displayName = refEntity.get("displayName").toString().replaceAll("\"", "");
					String schemaClass = refEntity.get("schemaClass").toString().replaceAll("\"", "");
				
					refEntities.add(new ReferenceEntity(dbId, displayName, schemaClass));
				}
			}
		}
		return refEntities;
	}
	
	/**
     * Get all contained GraphObjects in this CanvasPathway, which include both 
     * nodes and edges.
     * @return
     */
    public List<GraphObject> getGraphObjects() {
        List<GraphObject> objects = new ArrayList<GraphObject>();
        List<Node> nodes = getChildren();
        if (nodes != null)
            objects.addAll(nodes);
        if (edges != null)
            objects.addAll(edges);
        return objects;
    }

    public List<GraphObject> getObjectsForRendering() {
		return getGraphObjects();
    }
    
    public List<ReferenceEntity> getReferenceEntitiesForAllProteins() {
    	List<ReferenceEntity> proteinReferenceEntities = new ArrayList<ReferenceEntity>();
    	
    	for (Long dbId : dbIdToRefEntity.keySet()) {
    		for (ReferenceEntity refEntity : dbIdToRefEntity.get(dbId)) {
    			if (refEntity.getSchemaClass().equals("ReferenceGeneProduct") || refEntity.getSchemaClass().equals("ReferenceIsoform")) {
    				proteinReferenceEntities.add(refEntity);
    			}
    		}
    	}
    	
    	return proteinReferenceEntities;
    }
    
    public List<ProteinNode> getProteinNodes() {
    	List<ProteinNode> proteins = new ArrayList<ProteinNode>();
    	
    	List<Node> nodes = getChildren();    	
    	if (nodes != null) {
    		for (Node node : nodes) {    			
    			if (node.getType() == GraphObjectType.RenderableProtein) {
    				proteins.add((ProteinNode) node);
    			}
    		}
    	}
    	
    	return proteins;
    }
    
    public ProteinNode getProteinNodeByDBId(Long dbId) {
    	for (ProteinNode protein : getProteinNodes()) {
    		if (dbId.equals(protein.getReactomeId()))
    			return protein;
    	}
    	
    	return null;
    }
    
    public List<ProteinNode> getProteinNodesByRefId(Long refId) {
    	if (refId == null)
    		return null;
    	
    	List<ProteinNode> proteinNodesForRefId = new ArrayList<ProteinNode>();
    	for (ProteinNode protein : getProteinNodes()) {
    		for (ReferenceEntity proteinRefEntity : dbIdToRefEntity.get(protein.getReactomeId())) {
    			if (proteinRefEntity.getDbId().equals(refId) && !proteinNodesForRefId.contains(protein)) {
    				proteinNodesForRefId.add(protein);    			
    			}
    		}
    	}
    	
    	return proteinNodesForRefId;
    }

    public Long getReferenceIdForProtein(ProteinNode protein) {
    	if (protein == null) return null;
    	
    	return getReferenceIdForProtein(protein.getReactomeId());
    }	
    	
    public Long getReferenceIdForProtein(Long dbId){
    	if (getProteinNodeByDBId(dbId) == null) return null;
    	if (dbIdToRefEntity.get(dbId) == null || dbIdToRefEntity.get(dbId).isEmpty()) return null;
    	
    	return dbIdToRefEntity.get(dbId).get(0).getDbId();   	
    }
    
    public List<Long> getReferenceIds(List<ProteinNode> proteins) {
    	List<Long> referenceIds = new ArrayList<Long>();
    	
    	for (ProteinNode protein : proteins) {
    		Long refId = getReferenceIdForProtein(protein);
    		
    		if (refId != null)
    			referenceIds.add(refId);
    	}
    	
    	return referenceIds;
    }
    
    private void parseDisplayName(com.google.gwt.xml.client.Node node,
                                  GraphObject obj) {
        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName("displayName");
        if (nodeList == null || nodeList.getLength() == 0)
            return;
        Element nameElm = (Element) nodeList.item(0);
        String name = nameElm.getFirstChild().getNodeValue();
        // Get rid of "(name copied from...")
        int index = name.indexOf("(name copied from");
        if (index > 0) {
            name = name.substring(0, index);
        }
        obj.setDisplayName(name);
    }
    
    private void parseNodeAttachments(Element elm, Node node) {
        // The following method can get all descendant elements.
        NodeList nodeList = elm.getElementsByTagName("org.gk.render.RenderableFeature");
        if (nodeList == null || nodeList.getLength() == 0)
            return;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element attachmentElm = (Element) nodeList.item(i);
            NodeAttachment attachment = new NodeAttachment();
            String value = attachmentElm.getAttribute("relativeX");
            if (value != null)
                attachment.setRelativeX(new Double(value));
            value = attachmentElm.getAttribute("relativeY");
            if (value != null)
                attachment.setRelativeY(new Double(value));
            value = attachmentElm.getAttribute("label");
            if (value != null)
                attachment.setLabel(value);
            value = attachmentElm.getAttribute("description");
            if (value != null)
                attachment.setDescription(value);
            node.addNodeAttachment(attachment);
        }
    }
    
    private Map<Integer, Node> parseNodes(Element nodesElm) {
        Map<Integer, Node> idToNode = new HashMap<Integer, Node>();
        // We need to run the whole list twice to get components correct
        NodeList nodeList = nodesElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node1 = nodeList.item(i);
            if (node1.getNodeType() != com.google.gwt.xml.client.Node.ELEMENT_NODE)
                continue;
            Element elm = (Element) node1;
            Node node = createNode(elm);
            addChild(node);
            parseDisplayName(elm, node);
            parseNodeAttachments(elm, node);
            idToNode.put(node.getId(), node);
        }
        // Try to figure out display name and contained nodes
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node1 = nodeList.item(i);
            if (node1.getNodeType() != com.google.gwt.xml.client.Node.ELEMENT_NODE)
                continue;
            Element elm = (Element) node1;
            NodeList children = elm.getElementsByTagName("Components");
            if (children == null || children.getLength() == 0)
                continue;
            Element componentsElm = (Element) children.item(0);
            String parentId = elm.getAttribute("id");
            parseChildNodes(parentId, componentsElm, idToNode);
        }
        return idToNode;
    }
    
    private void parseChildNodes(String parentId, 
                                 Element componentsElm,
                                 Map<Integer, Node> idToNode) {
        Node parentNode = idToNode.get(new Integer(parentId));
        NodeList nodeList = componentsElm.getElementsByTagName("Component");
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node1 = nodeList.item(i);
            if (node1.getNodeType() != com.google.gwt.xml.client.Node.ELEMENT_NODE)
                continue;
            Element elm = (Element) nodeList.item(i);
            String childId = elm.getAttribute("id");
            Node childNode = idToNode.get(childId);
            if (childNode != null) { // childNode == null actually should not occur!
                parentNode.addChild(childNode); 
            	childNode.addParent(parentNode);
            }
        }
    }
    
    private Node createNode(Element nodeElm) {
        Node node = new Node();
        parseGraphObjectProperties(nodeElm, node);
        
        if (node.getType() == GraphObjectType.RenderableProtein) {
        	node = new ProteinNode();
        	parseGraphObjectProperties(nodeElm, node);
        } else if (node.getType() == GraphObjectType.RenderableComplex ||
        		   node.getType() == GraphObjectType.RenderableEntitySet) {
        	node = new CompositionalNode();
        	parseGraphObjectProperties(nodeElm, node);
        }
        
        // Some node specific properties
        String bounds = nodeElm.getAttribute("bounds");
        if (bounds != null) {
            Bounds b = new Bounds(bounds);
            node.setBounds(b);
        }
        
        String insets = nodeElm.getAttribute("insets");
        if (insets != null) {
        	Bounds i = new Bounds(insets);
        	node.setInsets(i);
        }	
        
        
        String bgColor = nodeElm.getAttribute("bgColor");
        if (bgColor != null) {
            // All color values are in integer. But alpha should be in [0.0, 1.0].
            double alpha = 1.0d;
            if (node.getType() == GraphObjectType.RenderableCompartment)
                alpha = 0.5d;
            String color = ModelHelper.makeColor(bgColor.split(" "), alpha);
            node.setBgColor(color);
        }
        String fgColor = nodeElm.getAttribute("fgColor");
        if (fgColor != null) {
            String color = ModelHelper.makeColor(fgColor.split(" "), 1.0d);
            node.setFgColor(color);
        }
        String textPosition = nodeElm.getAttribute("textPosition");
        if (textPosition != null) {
            String[] tokens = textPosition.split(" ");
            node.setTextPosition(Integer.parseInt(tokens[0]),
                                 Integer.parseInt(tokens[1]));
        }
        String needDashedBorder = nodeElm.getAttribute("needDashedBorder");
        if (needDashedBorder != null) {
            if (needDashedBorder.equals("true"))
                node.setNeedDashedBorder(true); // Default should be false
        }
        return node;
    }
    
    private void parseEdges(Element edgesElm,
                            Map<Integer, Node> idToNode) {
        NodeList nodeList = edgesElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node1 = nodeList.item(i);
            if (node1.getNodeType() != com.google.gwt.xml.client.Node.ELEMENT_NODE)
                continue;
            Element elm = (Element) nodeList.item(i);
            HyperEdge edge = createEdge(elm, idToNode);
            addEdge(edge);
        }
    }
    
    private HyperEdge createEdge(Element edgeElm,
                                 Map<Integer, Node> idToNode) {
        HyperEdge edge = new HyperEdge();
        parseGraphObjectProperties(edgeElm, edge);
        parseDisplayName(edgeElm, edge);
        // Get the reaction type if any
        String rxtTypeInfo = edgeElm.getAttribute("reactionType");
        if (rxtTypeInfo != null) {
            String type = rxtTypeInfo.toUpperCase().replaceAll(" ", "_");
            edge.setReactionType(ReactionType.valueOf(type));
        }
        // Check interactionType for RenderableInteraction if any
        String interactionTypeInfo = edgeElm.getAttribute("interactionType");
        if (interactionTypeInfo != null) {
            String type = interactionTypeInfo.toUpperCase();
            edge.setInteractionType(InteractionType.valueOf(type));
//            edge.setInteractionType(InteractionType.INHIBIT); // This is only for testing inhibit drawing
        }
        // Edge specific information
        String pointsText = edgeElm.getAttribute("points");
        List<Point> points = ModelHelper.makePoints(pointsText);
        edge.setBackbone(points);
        // Make sure position is a point in backbone
        edge.validatePosition();
        NodeList nodeList = edgeElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node child = nodeList.item(i);
            String name = child.getNodeName();
            ConnectRole role = null;
            if (name.equals("Inputs"))
                role = ConnectRole.INPUT;
            else if (name.equals("Outputs"))
                role = ConnectRole.OUTPUT;
            else if (name.equals("Catalysts"))
                role = ConnectRole.CATALYST;
            else if (name.equals("Inhibitors"))
                role = ConnectRole.INHIBITOR;
            else if (name.equals("Activators"))
                role = ConnectRole.ACTIVATOR;
            if (role != null) {
                parseEdgeBranch(child,
                                role,
                                edge,
                                idToNode);
            }
        }
        return edge;
    }
    
    private void parseEdgeBranch(com.google.gwt.xml.client.Node branchNode,
                                 ConnectWidget.ConnectRole role,
                                 HyperEdge edge,
                                 Map<Integer, Node> idToNode) {
        NodeList nodeList = branchNode.getChildNodes();
        List<Element> elmList = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element)
                elmList.add((Element)nodeList.item(i));
        }
        int length = elmList.size();
        // There are two cases for inputs and outputs: if there is only one item, no branch is needed.
        // However, for other types, a branch is always needed.
        if ((length == 1) && (role == ConnectRole.INPUT || role == ConnectRole.OUTPUT)) {
            Element elm = elmList.get(0);
            String id = elm.getAttribute("id");
            if (id == null)
                return;
            Node node = idToNode.get(new Integer(id));
            if (node == null)
                return;
            List<Point> backbone = edge.getBackbone();
            // Two points required by ConnectWidget
            Point point, controlPoint;
            if (role == ConnectRole.INPUT) {
                point = backbone.get(0);
                controlPoint = backbone.get(1);
            }
            else {
                point = backbone.get(backbone.size() - 1);
                controlPoint = backbone.get(backbone.size() - 2);
            }
            ConnectWidget widget = createConnectWidget(node, 
                                                       edge, 
                                                       elm, 
                                                       role,
                                                       0,
                                                       point,
                                                       controlPoint);
            edge.addConnectWidget(widget);
        }
        else {
            for (int i = 0; i < elmList.size(); i++) {
                Element elm = elmList.get(i);
                String pointsText = elm.getAttribute("points");
                List<Point> points = ModelHelper.makePoints(pointsText);
                edge.addBranch(points, role);
                String id = elm.getAttribute("id");
                if (id == null)
                    continue;
                Node node = idToNode.get(new Integer(id));
                if (node == null)
                    continue;
                Point point = points.get(0);
                Point controlPoint = getConnectWidgetControlPoint(points, 
                                                                  edge, 
                                                                  role);
                ConnectWidget widget = createConnectWidget(node, 
                                                           edge, 
                                                           elm, 
                                                           role,
                                                           i, 
                                                           point, 
                                                           controlPoint);
                edge.addConnectWidget(widget);
            }
        }
    }
    
    private Point getConnectWidgetControlPoint(List<Point> points, 
                                               HyperEdge edge,
                                               ConnectRole role) {
        List<Point> backbone = edge.getBackbone();
        if (points.size() > 1) {
            return points.get(1); // second point
        }
        else if (role == ConnectRole.INPUT) {
            return backbone.get(0);
        }
        else if (role == ConnectRole.OUTPUT)
            return backbone.get(backbone.size() - 1);
        else
            return edge.getPosition();
    }
    
    private ConnectWidget createConnectWidget(Node node, 
                                              HyperEdge edge, 
                                              Element elm,
                                              ConnectWidget.ConnectRole role,
                                              int index,
                                              Point point,
                                              Point controlPoint) {
        ConnectWidget connectWidget = new ConnectWidget();
        connectWidget.setRole(role);
        connectWidget.setNode(node);
        connectWidget.setEdge(edge);
        connectWidget.setIndex(index);
        connectWidget.connect();
        connectWidget.setPoint(point);
        connectWidget.setControlPoint(controlPoint);
        String stoichiometry = elm.getAttribute("stoichiometry");
        if (stoichiometry != null && connectWidget != null)
            connectWidget.setStoichiometry(Integer.parseInt(stoichiometry));
        return connectWidget;
    }
    
    private void parseGraphObjectProperties(Element elm, GraphObject graphObj) {
        String nodeName = elm.getNodeName();
        int lastIndex = nodeName.lastIndexOf(".");
        nodeName = nodeName.substring(lastIndex + 1);
        GraphObjectType type = GraphObjectType.valueOf(nodeName);
        graphObj.setType(type);
        String id = elm.getAttribute("id");
        graphObj.setId(new Integer(id));
        String reactomeId = elm.getAttribute("reactomeId");
        if (reactomeId != null)
            graphObj.setReactomeId(new Long(reactomeId));
        String position = elm.getAttribute("position");
        if (position != null) {
            graphObj.setPosition(position);
        }
        String lineColor = elm.getAttribute("lineColor");
        if (lineColor != null) {
            String color = ModelHelper.makeColor(lineColor.split(" "), 1.0d);
            graphObj.setLineColor(color);
        }
        String lineWidth = elm.getAttribute("lineWidth");
        if (lineWidth != null)
            graphObj.setLineWidth(Double.parseDouble(lineWidth));
    }
    
    private void addEdge(HyperEdge edge) {
        if (edges == null)
            edges = new ArrayList<HyperEdge>();
        edges.add(edge);
    }
    
    public Bounds getPreferredSize() {
        Bounds bounds = new Bounds();
        List<GraphObject> objects = getGraphObjects();
        if (objects == null || objects.size() == 0) {
            // Give some space
            bounds.width = 10;
            bounds.height =10;
            return bounds;
        }
        for (GraphObject obj : objects) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                Bounds nodeBounds = node.getBounds();
                if (nodeBounds != null) {
                    if (bounds.width < nodeBounds.x + nodeBounds.width)
                        bounds.width = nodeBounds.x + nodeBounds.width;
                    if (bounds.height < nodeBounds.y + nodeBounds.height)
                        bounds.height = nodeBounds.y + nodeBounds.height;
                }
                else {
                    Point pos = node.getPosition();
                    if (bounds.width < pos.getX())
                        bounds.width = (int) pos.getX();
                    if (bounds.height < pos.getY())
                        bounds.height = (int) pos.getY();
                }
            }
            else if (obj instanceof HyperEdge) {
                // Check all points
                HyperEdge edge = (HyperEdge) obj;
                List<Point> points = edge.getBackbone();
                getBoundsFromPoints(points, bounds);
                getBoundsFromBranch(bounds, edge.getInputBranches());
                getBoundsFromBranch(bounds, edge.getOutputBranches());
                getBoundsFromBranch(bounds, edge.getCatalystBranches());
                getBoundsFromBranch(bounds, edge.getActivatorBranches());
                getBoundsFromBranch(bounds, edge.getInhibitorBranches());
            }
        }
        // Give it some extra
        bounds.width += 8;
        bounds.height += 8;
        return bounds;
    }

    protected void getBoundsFromBranch(Bounds bounds, 
                                       List<List<Point>> branch) {
        if (branch != null)
            for (List<Point> list : branch)
                getBoundsFromPoints(list, bounds);
    }
    
    private void getBoundsFromPoints(List<Point> points, Bounds bounds) {
        for (Point p : points) {
            if (bounds.width < p.getX())
                bounds.width = (int) p.getX();
            if (bounds.height < p.getY())
                bounds.height = (int) p.getY();
        }
    }
    
    public class ReferenceEntity {
    	private Long dbId;
    	private String name;
    	private String referenceIdentifier;
    	private String schemaClass;
    	
    	public ReferenceEntity(Long dbId, String name, String schemaClass) {
    		this.dbId = dbId;
    		this.name = name;
    		this.schemaClass = schemaClass;
    	}
    	
    	public Long getDbId() {
    		return dbId;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public String getSchemaClass() {
    		return schemaClass;
    	}
    	
    	public String getResource() {
    		if (getReferenceIdentifier() == null)
    			return null;
    		
    		return getReferenceIdentifier().split(":")[0];
    	}
    	
    	public String getReferenceIdentifier() {
    		if (getName() == null)
    			return null;
    		
    		if (referenceIdentifier != null)
    			return referenceIdentifier;
    		
    		RegExp referenceIdentifierPattern = RegExp.compile("\\w+:\\w+(-\\d+)?");
    		
    		referenceIdentifier = referenceIdentifierPattern.test(getName()) ?
    				referenceIdentifierPattern.exec(getName()).getGroup(0).toUpperCase() :
    				null;
    				
    		return referenceIdentifier;
    	}
    	
    	public String toString() {
    		return "Reference Entity - " + name + "(" + dbId + ")" + ": " + schemaClass;
    	}
    }

	public static CanvasPathway createPathway(Element pathwayElement) {
		String isDiseaseRelated = pathwayElement.getAttribute("isDisease");
		if (isDiseaseRelated != null && isDiseaseRelated.equals("true"))
			return new DiseaseCanvasPathway();
		return new CanvasPathway();
	}
}
