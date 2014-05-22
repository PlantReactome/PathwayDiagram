/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.analysis.factory.AnalysisModelException;
import org.reactome.diagram.analysis.factory.AnalysisModelFactory;
import org.reactome.diagram.analysis.model.PathwayIdentifiers;
import org.reactome.diagram.expression.DataController;
import org.reactome.diagram.expression.ExpressionDataController;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.expression.model.ExpressionCanvasModel.ExpressionInfo;
import org.reactome.diagram.expression.model.PathwayOverlay;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.CanvasPathway.ReferenceEntity;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

import org.reactome.diagram.view.GraphObjectExpressionRendererFactory;
import org.reactome.diagram.view.ExpressionProcessNodeRenderer;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

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
    private Timer readyToDrawOverlayChecker;
       
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    	expressionCanvasModel = new ExpressionCanvasModel();
    }
   
    public ExpressionCanvas(PathwayDiagramPanel diagramPane, CanvasTransformation canvasTransformation) {
    	this(diagramPane);
    	this.canvasTransformation = new CanvasTransformation(canvasTransformation.getScale(),
    														 canvasTransformation.getTranslateX(),
    														 canvasTransformation.getTranslateY());
    }
    
    public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = AnalysisType.getAnalysisType(analysisType);
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

	public List<GraphObject> getObjectsForRendering() {
    	if (pathway == null)
    		return null;
    	
    	return pathway.getObjectsForRendering();
    }	
    
    /**
     * Update drawing.
     */
    public void update() { 
    	Context2d c2d = getContext2d();
    	   	    	
    	clean(c2d);
    	
    	drawCanvasLayer(c2d);
    }	
    
    public void drawCanvasLayer(Context2d c2d) {
    	ExpressionPathway oldExpressionPathway = expressionPathway;
    	
    	if (pathway == null) {
    		if (oldExpressionPathway != null)
    			readyToDrawOverlayChecker.cancel();
    			
    		return;
    	} else {
    		getPathwayNodeDataBeforeRendering(oldExpressionPathway);
    		getComplexNodeComponentDataBeforeRendering();
    		drawExpressionOverlayWhenReady(c2d);
    	}
    }
    
    private void getPathwayNodeDataBeforeRendering(ExpressionPathway oldExpressionPathway) {
    	if (oldExpressionPathway == null || 
    		oldExpressionPathway.getPathway() != pathway ||
    		oldExpressionPathway.getDataPointIndex() != getDataPointIndexFromDataController()) {
    			
    			if (readyToDrawOverlayChecker != null)
    				readyToDrawOverlayChecker.cancel();
    		
    			expressionPathway = new ExpressionPathway(pathway, getDataPointIndexFromDataController());
    			for (GraphObject entity : getObjectsForRendering()) {
    				if (entity.getType() == GraphObjectType.ProcessNode) {
    					PathwayDiagramController.getInstance().getReferenceEntity(entity.getReactomeId(), getEntityInfoForProcessNodeAndColor(entity));
    				}
    			}
    	}
    }	
    
    private void getComplexNodeComponentDataBeforeRendering() {
    	for (GraphObject entity : getObjectsForRendering()) {
    		if (isComplexWithoutComponentData(entity)) {
    			PathwayDiagramController.getInstance().getParticipatingMolecules(entity.getReactomeId(), 
    																  ((ComplexNode) entity).setParticipatingMolecules());
    		}
    	}
    }
    
    private void drawExpressionOverlayWhenReady(final Context2d c2d) {
    	if (expressionPathway.allProcessNodesReady() && allComplexNodesHaveComponentData()) {
    		drawExpressionOverlay(c2d);
    	} else {    	
    		readyToDrawOverlayChecker = new Timer() {

    			@Override
    			public void run() {
    				if (expressionPathway.allProcessNodesReady() && allComplexNodesHaveComponentData()) {
    					cancel();
    					drawExpressionOverlay(c2d);
    				}
    			}
    		};
    		
    		readyToDrawOverlayChecker.scheduleRepeating(200);
    	}
    }
    
    private boolean allComplexNodesHaveComponentData() {
    	for (GraphObject entity : getObjectsForRendering()) {
    		if (isComplexWithoutComponentData(entity)) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    private boolean isComplexWithoutComponentData(GraphObject entity) {
    	return (entity.getType() == GraphObjectType.RenderableComplex && !((ComplexNode) entity).participatingMoleculesObtained());
    }
    
    private void drawExpressionOverlay(Context2d c2d) { 	
        Map<Long, List<ReferenceEntity>> physicalToReferenceEntityMap = pathway.getDbIdToRefEntity();
        	
        for (GraphObject entity : getObjectsForRendering()) {
           	if (entity instanceof Node) {
           		if (entity.getType() == GraphObjectType.RenderableCompartment)
           			continue;
           		            		
           		String oldBgColor = ((Node) entity).getBgColor();
           		String oldFgColor = ((Node) entity).getFgColor();
           		
           		GraphObjectExpressionRendererFactory factory = GraphObjectExpressionRendererFactory.getFactory();
           		NodeRenderer renderer = factory.getNodeRenderer((Node) entity);
           		
           		if (entity.getType() == GraphObjectType.RenderableComplex) {
           			addExpressionInfoToComplexComponents((ComplexNode) entity);
           		} 
           		else if (entity.getType() == GraphObjectType.ProcessNode) {
           			((Node) entity).setBgColor(getDefaultColor(entity.getType()));
           			((ExpressionProcessNodeRenderer) renderer).setColorList(
           					expressionPathway.getProcessNodeExpressionObject(entity).getColorList(expressionPathway.getDataPointIndex()));
           		}	
           		else {
           			Long refEntityId = getReferenceEntityId((physicalToReferenceEntityMap.get(entity.getReactomeId())));
           			String nodeColor = getEntityColor(refEntityId, entity.getType());
           			
           			((Node) entity).setBgColor(nodeColor);
           			((Node) entity).setFgColor(((Node) entity).getVisibleFgColor(nodeColor));
           		}
           		
           		renderer.render(c2d, (Node) entity);
           		
           		((Node) entity).setBgColor(oldBgColor);
           		((Node) entity).setFgColor(oldFgColor);
           	}
        }                
    }

	private Long getReferenceEntityId(List<ReferenceEntity> referenceEntities) {
		if (referenceEntities != null && referenceEntities.size() > 0) {
		     return referenceEntities.get(0).getDbId();
		}
		return null;
	}
	
	public ExpressionInfo getExpressionInfo(Long refEntityId) {
		if (expressionCanvasModel.getEntityExpressionInfoMap() == null)
			return null;
			
		return expressionCanvasModel.getEntityExpressionInfoMap().get(refEntityId);
	}
	
	public List<String> getEntityExpressionId(Long refEntityId) {		
		if (getExpressionInfo(refEntityId) == null) 
			return null;
			
		return getExpressionInfo(refEntityId).getIdentifiers();
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
		
	private void addExpressionInfoToComplexComponents(ComplexNode complex) {
		if (complex == null)
			return;
		
		for (Component component : complex.getComponents()) {
			Long refId = component.getRefEntityId();
			GraphObjectType entityType = getEntityType(component.getSchemaClass());
			
			List<String> componentExpressionId = null;
			String componentExpressionColor =  getEntityColor(refId, entityType);
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
	
	private GraphObjectType getEntityType(String schemaClass) {
		if (schemaClass.equalsIgnoreCase("ReferenceGeneProduct") || schemaClass.equalsIgnoreCase("ReferenceIsoform")) {
			return GraphObjectType.RenderableProtein;
		} else if (schemaClass.equalsIgnoreCase("ReferenceMolecule")) {
			return GraphObjectType.RenderableChemical;
		} else {
			return null;
		}
	}
	
	private RequestCallback getEntityInfoForProcessNodeAndColor(final GraphObject entity) {
		expressionPathway.incrementCallbacksInProgress();
		
		RequestCallback getGenesForProcessNodeAndColor = new RequestCallback() {
			final private String ERROR_MSG = "Could not retrieve pathway entity information"; 
			
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					abortCallback(ERROR_MSG + " for " + entity.getDisplayName());
					return;
				}
								
				colorProcessNode(entity, getRefIds(response.getText()));
				expressionPathway.decrementCallbacksInProgress();
			}
			
			public void onError(Request request, Throwable exception) {
				abortCallback(ERROR_MSG + exception);
			}
		};
		
		return getGenesForProcessNodeAndColor;
	}
	
	private List<Long> getRefIds(String referenceEntityJSON) {
		List<Long> refIds = new ArrayList<Long>();
		refIds.addAll(getRefIdsForPathwayProteins(referenceEntityJSON));
		
		if (getAnalysisType() != AnalysisType.SpeciesComparison) {
			refIds.addAll(getRefIdsForPathwayRNAs(referenceEntityJSON));
			refIds.addAll(getRefIdsForPathwayGenes(referenceEntityJSON));
			refIds.addAll(getRefIdsForPathwaySmallMolecules(referenceEntityJSON));
		}
		
		return refIds;
		
	}
	
	private List<Long> getRefIdsForPathwayProteins(String referenceEntityJSON) {
		return getRefIdsForPathwayForSchemaClass(referenceEntityJSON, Arrays.asList("ReferenceGeneProduct", "ReferenceIsoform"));
	}
	
	private List<Long> getRefIdsForPathwayRNAs(String referenceEntityJSON) {
		return getRefIdsForPathwayForSchemaClass(referenceEntityJSON, Arrays.asList("ReferenceRNASequence"));
	}
	
	private List<Long> getRefIdsForPathwayGenes(String referenceEntityJSON) {
		return getRefIdsForPathwayForSchemaClass(referenceEntityJSON, Arrays.asList("ReferenceDNASequence"));
	}
	
	
	private List<Long> getRefIdsForPathwaySmallMolecules(String referenceEntityJSON) {
		return getRefIdsForPathwayForSchemaClass(referenceEntityJSON, Arrays.asList("ReferenceMolecule"));
	}
		
	private List<Long> getRefIdsForPathwayForSchemaClass(String referenceEntityJSON, List<String> schemaClasses) {
		JSONValue referenceEntities = JSONParser.parseStrict(referenceEntityJSON);
		if (referenceEntities == null || referenceEntities.isArray() == null || schemaClasses == null)
			return new ArrayList<Long>();
	
		
		List<Long> refIds = new ArrayList<Long>();
		
		JSONArray refEntityArray = referenceEntities.isArray();		
		for (int i = 0; i < refEntityArray.size(); i++) {
			JSONObject refEntity = refEntityArray.get(i).isObject();
			
			JSONValue schemaClassJSON = refEntity.get("schemaClass"); 
			
			String schemaClass = null;
			if (schemaClassJSON != null) {
				schemaClass = schemaClassJSON.isString().stringValue();
			}
				
			if (schemaClasses.contains(schemaClass)) {
				Long refId = (long) refEntity.get("dbId").isNumber().doubleValue();
				refIds.add(refId);
			}
		}
			
		return refIds;
	}
				
	private void colorProcessNode(GraphObject entity, List<Long> refIdsForPathwayEntities) {
		ProcessNodeExpression processNodeExpression = getProcessNodeExpressionObject(entity); 
		processNodeExpression.setRefIdsForPathwayEntities(refIdsForPathwayEntities);
		
		getCanvasPathway(entity);
		getPathwaySummary(entity);
		//getCanvasPathway(entity);
	}
	
	private ProcessNodeExpression getProcessNodeExpressionObject(GraphObject pathwayEntity) {
		if (expressionPathway.getProcessNodeExpressionObject(pathwayEntity) == null)
			expressionPathway.addProcessNodeExpressionObject(pathwayEntity, new ProcessNodeExpression());
			
		return expressionPathway.getProcessNodeExpressionObject(pathwayEntity);
	}
	
	private void getPathwaySummary(final GraphObject pathwayEntity) {
		expressionPathway.incrementCallbacksInProgress();
		
		AnalysisController analysisController = new AnalysisController();
		analysisController.retrievePathwaySummary(dataController.getAnalysisResult().getSummary().getToken(),
												  pathwayEntity.getReactomeId(),
												  new RequestCallback() {
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != Response.SC_OK && response.getStatusCode() != Response.SC_NOT_FOUND) {
					expressionPathway.decrementCallbacksInProgress();
					//abortCallback("Unable to retrieve pathway summary for " + pathwayEntity.getDisplayName());
					return;
				}
				
				final PathwayIdentifiers pathwayIdentifiers;
				try {
					pathwayIdentifiers = AnalysisModelFactory.getModelObject(PathwayIdentifiers.class, response.getText());
				} catch (AnalysisModelException ex) {
					expressionPathway.decrementCallbacksInProgress();					
					//abortCallback("Unable to retrieve pathway identifiers for " + pathwayEntity.getDisplayName() + ": " + ex);
					return;
				}
				
				expressionPathway.getProcessNodeExpressionObject(pathwayEntity).setPathwayIdentifiers(pathwayIdentifiers);
				expressionPathway.decrementCallbacksInProgress();
			}

			@Override
			public void onError(Request request, Throwable exception) {
				abortCallback("Unable to retrieve pathway summary: " + exception);
			}
		});
	}
	
	private void getCanvasPathway(final GraphObject pathwayEntity) {
		expressionPathway.incrementCallbacksInProgress();
	
		PathwayDiagramController.getInstance().getCanvasPathwayXML(pathwayEntity.getReactomeId(), new RequestCallback() {
				
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != Response.SC_OK) {	
					abortCallback("Unable to retrieve pathway XML for " + pathwayEntity.getReactomeId() + ": " + 
									response.getStatusCode() + " response code");
					return;
				}
									
				Document pathwayDom;
				try {
					pathwayDom = XMLParser.parse(response.getText());
				} catch (DOMParseException e) {
					abortCallback("Unable to parse pathway XML for " + pathwayEntity.getReactomeId() + ": " + e);
					return;
				}
				
				Element pathwayElement = pathwayDom.getDocumentElement();
				XMLParser.removeWhitespace(pathwayElement);
				final CanvasPathway pathway = CanvasPathway.createPathway(pathwayElement);
				pathway.buildPathway(pathwayElement);
				pathway.setReactomeId(pathwayEntity.getReactomeId());
				
				getPhysicalToReferenceEntityMap(pathwayEntity, pathway);
				expressionPathway.decrementCallbacksInProgress();
			}
			
			public void onError(Request request, Throwable exception) {
				abortCallback("Unable to retrieve pathway XML: " + exception);
			}
		});
	}
	
	private void getPhysicalToReferenceEntityMap(final GraphObject pathwayEntity, final CanvasPathway pathway) {
		expressionPathway.incrementCallbacksInProgress();
		PathwayDiagramController.getInstance().getPhysicalToReferenceEntityMap(pathway, new RequestCallback() {
				
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != Response.SC_OK) {
					abortCallback("Unable to retrieve physical to reference entity map for " + pathway.getDisplayName());
					return;
				}
				
				pathway.setDbIdToRefEntity(response.getText());
				expressionPathway.getProcessNodeExpressionObject(pathwayEntity).setPathway(pathway);
				expressionPathway.decrementCallbacksInProgress();
			}
					
			public void onError(Request request, Throwable exception) {
				abortCallback("Unable to retrieve physical to reference entity map: " + exception);
			}
		});
	}
	
	private void abortCallback(String error) {
		expressionPathway.decrementCallbacksInProgress();
		AlertPopup.alert(error);
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
    	private Map<GraphObject, ProcessNodeExpression> processNodeToExpression;
    	
    	
    	public ExpressionPathway(CanvasPathway pathway, Integer dataPointIndex) {
    		this.pathway = pathway;
    		this.dataPointIndex = dataPointIndex;
    		this.processNodeCount = 0;
    		this.processNodeToExpression = new HashMap<GraphObject, ProcessNodeExpression>();
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
    	
    	public ProcessNodeExpression getProcessNodeExpressionObject(GraphObject pathwayEntity) {
    		return processNodeToExpression.get(pathwayEntity);
    	}
    	
    	public void addProcessNodeExpressionObject(GraphObject processNode, ProcessNodeExpression processNodeExpression) {
    		processNodeToExpression.put(processNode, processNodeExpression);
    	}
    }
    
    private class ProcessNodeExpression {
    	private CanvasPathway pathway;
    	private PathwayIdentifiers pathwayIdentifiers;
    	private List<Long> refIdsForPathwayEntities;
    	
    	public ProcessNodeExpression() {
    		
    	}
    	
    	public void setPathway(CanvasPathway pathway) {
    		this.pathway = pathway;
    	}
    	
    	public void setPathwayIdentifiers(PathwayIdentifiers pathwayIdentifiers) {
    		this.pathwayIdentifiers = pathwayIdentifiers;
    	}
    	
    	public void setRefIdsForPathwayEntities(List<Long> refIdsForPathwayEntities) {
    		this.refIdsForPathwayEntities = refIdsForPathwayEntities;
    	}
    	
    	public List<String> getColorList(int dataPointIndex) {
    		return expressionCanvasModel.getColorList(getRefIdsForPathwayEntities(), 
    											getPathwayExpressionComponents(dataPointIndex));
    	}

		private CanvasPathway getPathway() {
    		return pathway;
    	}
    	
    	private PathwayIdentifiers getPathwayIdentifiers() {
    		return pathwayIdentifiers;
    	}
    	
    	private List<Long> getRefIdsForPathwayEntities() {
    		return refIdsForPathwayEntities;
    	}
    	
    	private Map<Long, ExpressionInfo> getPathwayExpressionComponents(int dataPointIndex) {
    		HashMap<Long, ExpressionInfo> pathwayExpressionComponents = new HashMap<Long, ExpressionInfo>();
    		
    		PathwayOverlay pathwayExpression = new PathwayOverlay(getPathway(),getPathwayIdentifiers());
    		for (Long pathwayComponentId : pathwayExpression.getDbIdToExpressionId(dataController.getResourceName()).keySet())
    			pathwayExpressionComponents.put(pathwayComponentId, 
    					getExpressionInfo(pathwayComponentId, pathwayExpression, dataPointIndex));
    		
    		return pathwayExpressionComponents;
    	}
    	
    	private ExpressionInfo getExpressionInfo(Long pathwayComponentId, PathwayOverlay pathwayExpression, int dataPointIndex) {
    		Map<Long, List<String>> expressionIds = pathwayExpression.getDbIdToExpressionId(dataController.getResourceName());
    		Map<Long, Double> expressionLevels = pathwayExpression.getExpressionValuesForDataPoint(dataPointIndex, dataController.getResourceName());
    		Map<Long, String> expressionColors = dataController.convertValueToColor(expressionLevels);
    		
    		return expressionCanvasModel.new ExpressionInfo(expressionIds.get(pathwayComponentId),
    														expressionLevels.get(pathwayComponentId),
    														expressionColors.get(pathwayComponentId));
    	}
    }
}
