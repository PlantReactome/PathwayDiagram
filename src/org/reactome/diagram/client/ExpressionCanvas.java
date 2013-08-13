/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.expression.DataController;
import org.reactome.diagram.expression.ExpressionDataController;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.expression.model.ExpressionCanvasModel.ExpressionInfo;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.ReactomeXMLParser;

import org.reactome.diagram.view.GraphObjectExpressionRendererFactory;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.ExpressionProcessNodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

/**
 * A specialized PlugInSupportCanvas that is used to overlay expression data on to a pathway.
 * @author gwu
 *
 */
public class ExpressionCanvas extends DiagramCanvas {
    private AnalysisType analysisType;
	private ExpressionCanvasModel expressionCanvasModel;    
    private CanvasPathway pathway;
    private DataController dataController;
    private ExpressionPathway expressionPathway;
    private Timer readyToRender;
    
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    	expressionCanvasModel = new ExpressionCanvasModel();
    }
   
    public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

	public ExpressionCanvasModel getExpressionCanvasModel() {
		return expressionCanvasModel;
	}
	
	public CanvasPathway getPathway() {
		return pathway;
	}

	public void setPathway(CanvasPathway pathway) {
		setPathway(pathway, true);
	}
	
	public void setPathway(CanvasPathway pathway, boolean updateCanvas) {
		this.pathway = pathway;

		if (updateCanvas)
			update();		
	}
	
	public void setDataController(DataController dataController) {
		this.dataController = dataController;
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
    	final Context2d c2d = getContext2d();
    	c2d.save();
    	
    	clean(c2d);
    	
    	ExpressionPathway oldExpressionPathway = expressionPathway;
    	
    	if (pathway == null) {
    		if (oldExpressionPathway != null)
    			oldExpressionPathway.getTimerCheckingIfProcessNodeInfoObtained().cancel();    			
    			
    		return;
    	} else if (pathway.getDbIdToRefEntityId() == null) {
    		checkIfPathwayReferenceMapExists(oldExpressionPathway, c2d);
    	} else {
    		getPathwayNodeDataBeforeRendering(oldExpressionPathway, c2d);
    	}
    }
    
    private void checkIfPathwayReferenceMapExists(final ExpressionPathway oldExpressionPathway, final Context2d c2d) {
    	readyToRender = new Timer() {
    		
    		public void run() {
    			
    			if (pathway.getDbIdToRefEntityId() != null) {
    				cancel();
    				getPathwayNodeDataBeforeRendering(oldExpressionPathway, c2d);
    			}
    			
    		}
    	};
    	
    	readyToRender.scheduleRepeating(200);
    }
    
    private void getPathwayNodeDataBeforeRendering(ExpressionPathway oldExpressionPathway, Context2d c2d) {
    	if (oldExpressionPathway == null || 
    		oldExpressionPathway.getPathway() != pathway ||
    		oldExpressionPathway.getDataPointIndex() != getDataPointIndexFromDataController()) {
    			if (oldExpressionPathway != null)
    				oldExpressionPathway.getTimerCheckingIfProcessNodeInfoObtained().cancel();
    		
    			expressionPathway = new ExpressionPathway(c2d, pathway, getDataPointIndexFromDataController());
    			for (GraphObject entity : getGraphObjects()) {    		
    				if (entity.getType() == GraphObjectType.ProcessNode) {
    					diagramPane.getController().getCanvasPathwayXML(entity.getReactomeId(), createPathwayForProcessNodeAndColor(expressionPathway, entity));
    				}
    			}
    	} else 
    		expressionPathway.setContext2d(c2d);
    		
    	if (expressionPathway.allProcessNodesReady()) // True if no pathway nodes to render
    		drawExpressionOverlay(c2d, expressionPathway);
    	else {
    		expressionPathway.getTimerCheckingIfProcessNodeInfoObtained().scheduleRepeating(200);
    	}    	
    	
    }	
    	
    private void drawExpressionOverlay(Context2d c2d, ExpressionPathway expressionPathway) { 	
        Map<Long, List<Long>> physicalToReferenceEntityMap = pathway.getDbIdToRefEntityId();
        	
        for (GraphObject entity : getGraphObjects()) {
           	if (entity instanceof Node) {
           		if (entity.getType() == GraphObjectType.RenderableCompartment)
           			continue;
           		            		
           		String oldBgColor = ((Node) entity).getBgColor();
           		String oldFgColor = ((Node) entity).getFgColor();
           		
           		GraphObjectExpressionRendererFactory factory = GraphObjectExpressionRendererFactory.getFactory();
           		NodeRenderer renderer = factory.getNodeRenderer((Node) entity);
           		
           		if (entity.getType() == GraphObjectType.RenderableComplex) {
           			((Node) entity).setFgColor("rgb(0,0,0)"); // Black text
           		//	((Node) entity).setFgColor("rgb(255,255,255)");
           			
           			List<Long> componentIds = physicalToReferenceEntityMap.get(entity.getReactomeId());
           			addExpressionInfoToComplexComponents((ComplexNode) entity, componentIds);            			
           		} 
           		else if (entity.getType() == GraphObjectType.ProcessNode) {
           			((Node) entity).setBgColor(getDefaultColor(entity.getType()));
           			((ExpressionProcessNodeRenderer) renderer).setColorList(expressionPathway.getColorList(entity));
           		}	
           		else {
           			Long refEntityId = getReferenceEntityId(physicalToReferenceEntityMap.get(entity.getReactomeId()));            			
           			String nodeColor = getEntityColor(refEntityId, entity.getType());            
           			
           			((Node) entity).setBgColor(nodeColor);             		
           		}
            			            		            		
           		renderer.render(c2d, (Node) entity);
           		
           		((Node) entity).setBgColor(oldBgColor);
           		((Node) entity).setFgColor(oldFgColor);
           	}
        }           
        
        c2d.restore();
    }

	private Long getReferenceEntityId(List<Long> referenceEntityIds) {
		if (referenceEntityIds != null && referenceEntityIds.size() > 0) {
		     return referenceEntityIds.get(0);
		}
		return null;
	}
	
	private ExpressionInfo getExpressionInfo(Long refEntityId) {
		if (expressionCanvasModel.getEntityExpressionInfoMap() == null)
			return null;
			
		return expressionCanvasModel.getEntityExpressionInfoMap().get(refEntityId);
	}
	
	public String getEntityExpressionId(Long refEntityId) {		
		if (getExpressionInfo(refEntityId) == null) 
			return null;
			
		return getExpressionInfo(refEntityId).getId();
	}

	public Double getEntityExpressionLevel(Long refEntityId) {		
		if (getExpressionInfo(refEntityId) == null)
			return null;
		
		return getExpressionInfo(refEntityId).getLevel();						
	}
	
	public String getEntityColor(Long refEntityId, GraphObjectType entityType) {		
		String color = null;		
		if (getExpressionInfo(refEntityId) != null) {            					
			color = getExpressionInfo(refEntityId).getColor();
		} 		
							
		if (color == null) {
			color = getDefaultColor(entityType);
		}

		return color;
	}

	private String getDefaultColor(GraphObjectType entityType) {
		String defaultColor = null;
		if (AnalysisType.contains(
				analysisType.name())) {
			defaultColor = expressionCanvasModel.getDefaultColor(); // White by default
			
			if (analysisType == AnalysisType.SpeciesComparison && entityType == GraphObjectType.RenderableProtein) {
				defaultColor =  "rgb(0, 0, 255)"; // Blue for protein in species comparison with no inference
			}
		} else {
			AlertPopup.alert(analysisType.name() + " is an unknown analysis type");
		}
		
		return defaultColor;
		
	}
 
	private void addExpressionInfoToComplexComponents(ComplexNode complex, List<Long> componentIds) {
		if (complex == null || componentIds == null)
			return;
		
		for (Long refId : componentIds) 
			complex.addComponent(refId);
		
		for (Component component : complex.getComponents()) {
			Long refId = component.getRefEntityId();
			String componentExpressionId = null;			
			String componentExpressionColor =  getEntityColor(refId, null);
			Double componentExpressionLevel = null;
			
			if (refId != null) {
				componentExpressionId = getEntityExpressionId(refId);
				componentExpressionLevel = getEntityExpressionLevel(refId);
			}
						
			component.setExpressionId(componentExpressionId);
			component.setExpressionColor(componentExpressionColor);
			component.setExpressionLevel(componentExpressionLevel);
		}
	}
	
	private RequestCallback createPathwayForProcessNodeAndColor(final ExpressionPathway expressionPathway, final GraphObject entity) {
		expressionPathway.incrementCallbacksInProgress();
		
		RequestCallback createPathwayForProcessNodeAndColor = new RequestCallback() {
				
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					AlertPopup.alert("Could not retrieve pathway xml for " + entity.getDisplayName());
					expressionPathway.decrementCallbacksInProgress();
					return;
				}
				
				ReactomeXMLParser canvasPathwayXMLParser = new ReactomeXMLParser(response.getText());
				
				CanvasPathway pathway = new CanvasPathway();
				pathway.buildPathway(canvasPathwayXMLParser.getDocumentElement());
				
				diagramPane.getController().getPhysicalToReferenceEntityMap(entity.getReactomeId(), colorProcessNode(expressionPathway, entity, pathway));
			}
			
			public void onError(Request request, Throwable exception) {
				AlertPopup.alert("Could not retrieve pathway xml " + exception);
				expressionPathway.decrementCallbacksInProgress();
			}
		};
		
		return createPathwayForProcessNodeAndColor;
	}
				
	private RequestCallback colorProcessNode(final ExpressionPathway expressionPathway, final GraphObject entity, final CanvasPathway pathway) {
		RequestCallback colorProcessNode = new RequestCallback() {
			
			private final String ERROR_MESSAGE = "Could not obtain reference entity ids for pathway " + pathway.getDisplayName(); 
			
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					AlertPopup.alert(ERROR_MESSAGE);
					expressionPathway.decrementCallbacksInProgress();
					return;
				}
				
				pathway.setDbIdToRefEntityId(response.getText());
				
				Map<Long, ExpressionInfo> pathwayExpressionComponents = new HashMap<Long, ExpressionInfo>();
				
				PathwayExpressionValue pathwayExpression = dataController.getDataModel().getPathwayExpressionValue(entity.getReactomeId());
			
				if (pathwayExpression != null) { 
					Map<Long, String> expressionIds = pathwayExpression.getDbIdsToExpressionIds();
					Map<Long, Double> expressionLevels = pathwayExpression.getExpressionValueForDataPoint(expressionPathway.getDataPointIndex());
					Map<Long, String> expressionColors = dataController.convertValueToColor(expressionLevels);
		
					for (Long pathwayComponentId : expressionIds.keySet()) 
						pathwayExpressionComponents.put(pathwayComponentId,	
								expressionCanvasModel.new ExpressionInfo(expressionIds.get(pathwayComponentId),
																		 expressionLevels.get(pathwayComponentId),
																		 expressionColors.get(pathwayComponentId)
																		)
						);
				}
					
				expressionPathway.addProcessNodeColorList(
					entity,
					expressionCanvasModel.getColorList(pathway, pathwayExpressionComponents)
				);	
				
				expressionPathway.decrementCallbacksInProgress();
			}
			
			public void onError(Request request, Throwable exception) {
				AlertPopup.alert(ERROR_MESSAGE + " " + exception);
				expressionPathway.decrementCallbacksInProgress();
			}
		};
		
		return colorProcessNode;
	}
	
	private Integer getDataPointIndexFromDataController() {
		if (dataController instanceof ExpressionDataController)
			return ((ExpressionDataController) dataController).getCurrentDataPoint();
		
		return 0;
	}
	
    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
    private class ExpressionPathway {
    	private CanvasPathway pathway;
    	private Integer dataPointIndex;
    	private Integer processNodeCount;
    	private Map<GraphObject, List<String>> processNodeToColorList;
    	private Timer processNodeInfoObtainedTimer;
    	private Context2d c2d;
    	
    	public ExpressionPathway(Context2d c2d, CanvasPathway pathway, Integer dataPointIndex) {
    		this.pathway = pathway;
    		this.dataPointIndex = dataPointIndex;
    		this.processNodeCount = 0;
    		this.processNodeToColorList = new HashMap<GraphObject, List<String>>();
    		this.c2d = c2d;
    		createTimer();
    	}
    	
    	public CanvasPathway getPathway() {
    		return pathway;
    	}
    	
    	public Integer getDataPointIndex() {
    		return dataPointIndex;
    	}
    	    	    	
    	public Boolean allProcessNodesReady() {
    		return processNodeCount == 0;
    	}
    	
    	public void incrementCallbacksInProgress() {
    		processNodeCount += 1;
    	}
    	
    	public void decrementCallbacksInProgress() {
    		processNodeCount -= 1;
    	}
    	
    	public void addProcessNodeColorList(GraphObject processNode, List<String> colorList) {
    		processNodeToColorList.put(processNode, colorList);    	
    	}
    	
    	public List<String> getColorList(GraphObject processNode) {
    		return processNodeToColorList.get(processNode);
    	}
    	
    	public Timer getTimerCheckingIfProcessNodeInfoObtained() {
    		return processNodeInfoObtainedTimer;
    	}
    	
    	public void setContext2d(Context2d c2d) {
    		this.c2d = c2d;
    	}
    	
    	private void createTimer() {
    		processNodeInfoObtainedTimer = new Timer() {
    			
    			public void run() {
    				if (allProcessNodesReady()) {
    					cancel();
    					drawExpressionOverlay(c2d, ExpressionPathway.this);
    				}
    			}
    			
    		};
    	}
    }
}
