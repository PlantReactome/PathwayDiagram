/*
 * Created on Aug 3, 2012
 *
 */
package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * A customized CanvasPathway for disease, which has some special properties related to
 * disease only.
 * @author gwu
 *
 */
public class DiseaseCanvasPathway extends CanvasPathway {
    private List<GraphObject> normalObjects;
    private List<GraphObject> diseaseObjects;
    private List<GraphObject> crossedObjects;
    private List<GraphObject> overlaidObjects;
    private List<GraphObject> lofObjects;
    // Flag to indicate this pathway should be used for a normal pathway draw.
    // A disease pathway diagram is shared between normal and disease pathway
    private boolean isForNormalDraw;
    
    public DiseaseCanvasPathway() {
    	normalObjects = new ArrayList<GraphObject>();
    	diseaseObjects = new ArrayList<GraphObject>();
    	crossedObjects = new ArrayList<GraphObject>();
    	overlaidObjects = new ArrayList<GraphObject>();
    	lofObjects = new ArrayList<GraphObject>();
    }

    @Override
    public void buildPathway(Element pathwayElm) {
        super.buildPathway(pathwayElm);
        // Check if this is for a normal draw
        String isForNormalDraw = pathwayElm.getAttribute("forNormalDraw");
        if (isForNormalDraw != null && isForNormalDraw.length() > 0) {
            this.isForNormalDraw = new Boolean(isForNormalDraw);
        }
        List<GraphObject> all = getGraphObjects();
        Map<Integer, GraphObject> idToObject = new HashMap<Integer, GraphObject>();
        for (GraphObject obj : all) {
            idToObject.put(obj.getId(), obj);
        }
        NodeList nodeList = pathwayElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            com.google.gwt.xml.client.Node node = nodeList.item(i);
            String name = node.getNodeName();
            if (name.equals("normalComponents")) {
                parseObjectIds(node,
                               normalObjects,
                               idToObject);
            }
            else if (name.equals("diseaseComponents")) {
                parseObjectIds(node, diseaseObjects, idToObject);
            }
            else if (name.equals("crossedComponents")) {
                parseObjectIds(node, crossedObjects, idToObject);
            }
            else if (name.equals("overlaidComponents")) {
                parseObjectIds(node, overlaidObjects, idToObject);
            }
            else if (name.equals("lofNodes")) {
                parseObjectIds(node, lofObjects, idToObject);
            }
        }
    }
    
    private void parseObjectIds(com.google.gwt.xml.client.Node idNode,
                                List<GraphObject> list,
                                Map<Integer, GraphObject> idToObject) {
        String idText = idNode.getFirstChild().getNodeValue();
//        System.out.println(idText);
        String[] tokens = idText.split(",");
        for (String id : tokens) {
            GraphObject obj = idToObject.get(new Integer(id));
            if (obj == null)
                continue;
            list.add(obj);
        }
    }

    public List<GraphObject> getObjectsForRendering() {
    	if (isForNormalDraw()) {
    		return getNormalObjects();
    	} else {
    		Set<GraphObject> objectsForRendering = new HashSet<GraphObject>();
    		objectsForRendering.addAll(getDiseaseObjects());
    		objectsForRendering.addAll(getOverlaidObjects());
    		objectsForRendering.addAll(getCrossedObjects());
    		objectsForRendering.addAll(getLofObjects());
    		objectsForRendering.addAll(getNormalObjects());
    		
    		return new ArrayList<GraphObject>(objectsForRendering);
    	}
    }
    
    public List<GraphObject> getNormalObjects() {
        return normalObjects;
    }

    public List<GraphObject> getDiseaseObjects() {
        return diseaseObjects;
    }

    public List<GraphObject> getCrossedObjects() {
        return crossedObjects;
    }

    public List<GraphObject> getOverlaidObjects() {
        return overlaidObjects;
    }

    public List<GraphObject> getLofObjects() {
        return lofObjects;
    }

    public boolean isForNormalDraw() {
        return isForNormalDraw;
    }
    
}
