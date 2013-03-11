/*
 * Created on Feb , 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;

/**
 * A specialized PlugInSupportCanvas that is used to overlay expression data on to a pathway.
 * @author gwu
 *
 */
public class ExpressionCanvas extends DiagramCanvas {
    private String analysisType;
	private Map<Long, String> entityColorMap;
    private Map<Long, Double> entityExpressionLevelMap;
    private Map<Long, String> entityExpressionIdMap;
    private Map<Long, List<Long>> physicalToReferenceEntityMap;
    
    private CanvasPathway pathway;
    
    public ExpressionCanvas() {

    }
    
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    }
   
    public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public Map<Long, String> getEntityColorMap() {
		return entityColorMap;
	}

	public void setEntityColorMap(Map<Long, String> entityColorMap) {
		this.entityColorMap = entityColorMap;
	}

	public Map<Long, Double> getEntityExpressionLevelMap() {
		return entityExpressionLevelMap;
	}

	public void setEntityExpressionLevelMap(Map<Long, Double> entityExpressionLevelMap) {
		this.entityExpressionLevelMap = entityExpressionLevelMap;
	}

	public Map<Long, String> getEntityExpressionIdMap() {
		return entityExpressionIdMap;
	}

	public void setEntityExpressionIdMap(Map<Long, String> entityExpressionIdMap) {
		this.entityExpressionIdMap = entityExpressionIdMap;
	}

	public Map<Long, List<Long>> getPhysicalToReferenceEntityMap() {
		return physicalToReferenceEntityMap;
	}
	
	public CanvasPathway getPathway() {
		return pathway;
	}

	public void setPathway(CanvasPathway pathway) {
		setPathway(pathway, true);
	}
	
	public void setPathway(CanvasPathway pathway, final boolean updateCanvas) {
		this.pathway = pathway;
		physicalToReferenceEntityMap = new HashMap<Long, List<Long>>();
		
		if (pathway != null) {
			final PathwayDiagramController controller = this.diagramPane.getController();		
			controller.getPhysicalToReferenceEntityMap(pathway.getReactomeId(), new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {					
						JSONArray mapObjects = (JSONArray) JSONParser.parseStrict(response.getText());
						for (int i = 0; i < mapObjects.size(); i++) {
							JSONObject entityMap = mapObjects.get(i).isObject();
							Long physicalEntityId = new Long((long) entityMap.get("peDbId").isNumber().doubleValue());
							JSONArray referenceEntityArray = entityMap.get("refDbIds").isArray();
							ArrayList<Long> referenceEntityIds = new ArrayList<Long>(); 
							
							physicalToReferenceEntityMap.put(physicalEntityId, referenceEntityIds);
							for (int j = 0; j < referenceEntityArray.size(); j++) {
								Long referenceEntityId = new Long((long) referenceEntityArray.get(j).isNumber().doubleValue());
								referenceEntityIds.add(referenceEntityId);
							}						
						}
						
						if (updateCanvas)
							update();
					} else {
						controller.requestFailed("Could not retrieve physical to reference entity map");
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					controller.requestFailed(exception);
				}			
			});
		} else {
			if (updateCanvas)
				update();
		}
	}

	public List<GraphObject> getGraphObjects() {
    	if (pathway == null)
    		return null;
    	
    	return pathway.getGraphObjects();
    }	
    
    /**
     * Update drawing.
     */
    public void update() {
        Context2d c2d = getContext2d();
        c2d.save();

        clean(c2d);
        
        if (pathway != null) {
            for (GraphObject entity : getGraphObjects()) {
            	if (entity instanceof Node) {
            		if (entity.getType() == GraphObjectType.RenderableCompartment)
            			continue;
            		
            		Long entityId = entity.getReactomeId();
            		List<Long> referenceEntityIds = physicalToReferenceEntityMap.get(entityId);
            		
            		Long refEntityId = null;            		
            		try {
            			if (entity.getType() != GraphObjectType.RenderableComplex && referenceEntityIds.size() == 1) 
            				refEntityId = referenceEntityIds.get(0);
            		} catch (NullPointerException npe) {
            			System.out.println("Entity Id - " + entityId);
            			for (Long id : physicalToReferenceEntityMap.keySet())	
            				System.out.println("Map Id -" + id);
            		}
            				            		
            		String oldBgColor = ((Node) entity).getBgColor();
            		String oldFgColor = ((Node) entity).getFgColor();
            		
            		if (entity.getType() == GraphObjectType.RenderableComplex) {
            			((Node) entity).setBgColor("rgb(0,0,0)"); // Black background
            			((Node) entity).setFgColor("rgb(255,255,255)"); // White Text
            		} else {
            			String nodeColor = null;
            			
            			String assignedNodeColor = entityColorMap.get(refEntityId);
            			if (analysisType.equals("expression")) {
            				if (assignedNodeColor != null) {
            					nodeColor = assignedNodeColor;
            				}
            			} else if (analysisType.equals("species_comparison")) {
            				if (entity.getType() == GraphObjectType.RenderableProtein) {
            					if (assignedNodeColor != null) {
            						nodeColor = assignedNodeColor;
            					} else {
            						nodeColor = "rgb(0,0,255)"; // Blue for no inference
            						entityColorMap.put(refEntityId, nodeColor);
            					}
            				}
            			} else {
            				Window.alert("Unknown analysis type");
            				break;
            			}
            			
            			if (nodeColor == null) {
            				nodeColor = "rgb(192,192,192)"; // Grey for no data
            			}
            			
            			((Node) entity).setBgColor(nodeColor); 
            		}
            			
            		GraphObjectRendererFactory factory = GraphObjectRendererFactory.getFactory();
            		NodeRenderer renderer = factory.getNodeRenderer((Node) entity);
            		renderer.render(c2d, (Node) entity);
            		
            		((Node) entity).setBgColor(oldBgColor);
            		((Node) entity).setFgColor(oldFgColor);
            	}
            }
            updateOthers(c2d);
        }
        
        c2d.restore();
    }

    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
