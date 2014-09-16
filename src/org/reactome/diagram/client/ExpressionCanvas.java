/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.analysis.factory.AnalysisModelException;
import org.reactome.diagram.analysis.factory.AnalysisModelFactory;
import org.reactome.diagram.analysis.model.PathwaySummary;
import org.reactome.diagram.expression.DataController;
import org.reactome.diagram.expression.ExpressionDataController;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.expression.model.ExpressionCanvasModel.ExpressionInfo;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.CanvasPathway.ReferenceEntity;
import org.reactome.diagram.model.CompositionalNode;
import org.reactome.diagram.model.CompositionalNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

import org.reactome.diagram.view.GraphObjectExpressionRendererFactory;
import org.reactome.diagram.view.ExpressionProcessNodeRenderer;
import org.reactome.diagram.view.NodeRenderer;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * A specialized PlugInSupportCanvas that is used to overlay expression data on to a pathway.
 * @author gwu
 *
 */
public class ExpressionCanvas extends DiagramCanvas {
    private Context2d c2d;
	private AnalysisType analysisType;
	private ExpressionCanvasModel expressionCanvasModel;
    private CanvasPathway pathway;
    private DataController dataController;
    private Map<String, Map<Long, PathwayExpression>> resourceToPathwayExpressionMap;
    private PathwayExpressionForDataPoint currentPathwayExpressionForDataPoint;
    private ComplexComponentRequests complexComponentRequests;
    private String currentResource;
	private boolean drawn;
    
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    	expressionCanvasModel = new ExpressionCanvasModel();
    	resourceToPathwayExpressionMap  = new HashMap<String, Map<Long,PathwayExpression>>();
    	complexComponentRequests = new ComplexComponentRequests();
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
		this.currentResource = dataController.getResourceName();
		clearCache();
	}

	public PathwayExpressionForDataPoint getCurrentPathwayExpressionForDataPoint() {
		return currentPathwayExpressionForDataPoint;
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
    	c2d = getContext2d();
    	
    	drawCanvasLayer(c2d);
    }	

	public void clearCache() {
    	resourceToPathwayExpressionMap.clear();
    	
    	if (currentPathwayExpressionForDataPoint != null)
    		currentPathwayExpressionForDataPoint.cancelAllRequestsInProgress();
    	currentPathwayExpressionForDataPoint = null;
    }
    
    public void drawCanvasLayer(Context2d c2d) {
    	PathwayExpressionForDataPoint oldExpressionPathwayForDataPoint = currentPathwayExpressionForDataPoint;
    	
    	if (pathway == null) {
    		if (oldExpressionPathwayForDataPoint != null) {
    			currentPathwayExpressionForDataPoint.cancelAllRequestsInProgress();
    			currentPathwayExpressionForDataPoint = null;
    		}
    		clean(c2d);	
    		return;
    	}
    	
    	WidgetStyle.setCursor(this, Cursor.WAIT);
    	drawn = false;
    	getPathwayNodeDataBeforeRendering(oldExpressionPathwayForDataPoint);
    	getComplexNodeComponentDataBeforeRendering();
    	drawExpressionOverlayIfReady(c2d);
    }
    
    private void getPathwayNodeDataBeforeRendering(PathwayExpressionForDataPoint oldExpressionPathwayForDataPoint) {
    	if (oldExpressionPathwayForDataPoint == null || 
    		oldExpressionPathwayForDataPoint.getPathway().getReactomeId() != pathway.getReactomeId() ||
    		oldExpressionPathwayForDataPoint.getDataPointIndex() != getDataPointIndexFromDataController() ||
    		!currentResource.equals(dataController.getResourceName())
    			) {
    			
    			if (currentPathwayExpressionForDataPoint != null) {
    				currentPathwayExpressionForDataPoint.cancelAllRequestsInProgress();
    			}
    				
    			currentPathwayExpressionForDataPoint = getPathwayExpressionForDataPoint(pathway, getDataPointIndexFromDataController());
    			
    			Map<Long, GraphObject> pathways = new HashMap<Long, GraphObject>();
    			for (GraphObject entity : getObjectsForRendering()) {
    				if (entity.getType() == GraphObjectType.ProcessNode &&	needToCreateColorForProcessNode(entity.getReactomeId())) {
    					pathways.put(entity.getReactomeId(), entity);
    				}
    			}
    			getPathwayResults(pathways);
    			currentPathwayExpressionForDataPoint.setAllRequestsAdded(true);
    	}
    }	
    
    private PathwayExpressionForDataPoint getPathwayExpressionForDataPoint(CanvasPathway pathway, Integer dataPointIndex) {
    	currentResource = dataController.getResourceName();
    	
    	if (resourceToPathwayExpressionMap.get(currentResource) == null)
    		resourceToPathwayExpressionMap.put(currentResource, new HashMap<Long, PathwayExpression>());
    	
    	if (resourceToPathwayExpressionMap.get(currentResource).get(pathway.getReactomeId()) == null)
    		resourceToPathwayExpressionMap.get(currentResource).put(pathway.getReactomeId(), new PathwayExpression(pathway));
    	
    	if (resourceToPathwayExpressionMap.get(currentResource).get(pathway.getReactomeId()).getPathwayExpressionForDataPoint(dataPointIndex) == null)
    		resourceToPathwayExpressionMap.get(currentResource).get(pathway.getReactomeId()).addPathwayExpressionForDataPoint(dataPointIndex, new PathwayExpressionForDataPoint(pathway, dataPointIndex));
    	
    	return resourceToPathwayExpressionMap.get(currentResource).get(pathway.getReactomeId()).getPathwayExpressionForDataPoint(dataPointIndex);
    }
    
    private boolean needToCreateColorForProcessNode(Long processNodeId) {
    	ProcessNodeExpression processNodeExpression = currentPathwayExpressionForDataPoint.getProcessNodeExpressionObject(processNodeId);
    	
    	if (processNodeExpression == null)
    		return true;
    	
    	return false;
    }
    
    private void getPathwayResults(final Map<Long, GraphObject> pathways) {
    	if (pathways.isEmpty())
    		return;
    	
    	AnalysisController analysisController = new AnalysisController();
    	
    	Request request = analysisController.retrievePathwayResults(dataController.getToken(),
    											  new ArrayList<Long>(pathways.keySet()),
    											  currentResource,
    											  new RequestCallback() {
    		private final String ERROR_MSG = "Unable to get overlay information for pathways " + pathways.keySet();
    		
			@Override
			public void onResponseReceived(Request request,	Response response) {
				if (response.getStatusCode() != Response.SC_OK && response.getStatusCode() != Response.SC_NOT_FOUND) {
					removePathwayNodeRequest(request);
					AlertPopup.alert(ERROR_MSG);
					return;
				}
				
				List<PathwaySummary> pathwayResults;
				try {
					pathwayResults = AnalysisModelFactory.getPathwaySummaryList(response.getText());
				} catch (AnalysisModelException ex) {
					removePathwayNodeRequest(request);
					return;
				}
				
				for (PathwaySummary pathwayResult : pathwayResults) {
					GraphObject pathway = pathways.get(pathwayResult.getDbId());
					Integer entitiesFound = pathwayResult.getEntities().getFound();
					Integer entitiesTotal = pathwayResult.getEntities().getTotal();
					
					List<Double> expValues = pathwayResult.getEntities().getExp();
					if (expValues == null || expValues.isEmpty()) {
						currentPathwayExpressionForDataPoint.addProcessNodeExpressionObject(pathway.getReactomeId(), new ProcessNodeExpression(null, 
																														   entitiesFound,
																														   entitiesTotal));
					} else {
						for (Integer index = 0; index < expValues.size(); index++) {
							getPathwayExpressionForDataPoint(ExpressionCanvas.this.pathway, index)
								.addProcessNodeExpressionObject(pathway.getReactomeId(), new ProcessNodeExpression(expValues.get(index), entitiesFound, entitiesTotal));
						}
					}
						
				}
				removePathwayNodeRequest(request);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				removePathwayNodeRequest(request);
				AlertPopup.alert(ERROR_MSG + ": " + exception);
			}
    	});
    	currentPathwayExpressionForDataPoint.addRequestInProgress(request);
    }
    
    private void getComplexNodeComponentDataBeforeRendering() {
    	complexComponentRequests.cancelAllRequestsInProgress();
    	complexComponentRequests.setAllRequestsAdded(false);
    	for (GraphObject entity : getObjectsForRendering()) {
    		if (isComplexWithoutComponentData(entity)) {
    			Request request = PathwayDiagramController.getInstance().getParticipatingMolecules(entity.getReactomeId(), 
    																 setParticipatingMolecules((CompositionalNode) entity));
    			complexComponentRequests.add(request);
    		}
    	}
    	complexComponentRequests.setAllRequestsAdded(true);
    }
    
    private RequestCallback setParticipatingMolecules(final CompositionalNode complex) {
    	return new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				complex.setParticipatingMolecules().onResponseReceived(request,  response);
				removeComplexComponentRequest(request);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				complex.setParticipatingMolecules().onError(request, exception);
				removeComplexComponentRequest(request);
			}
    		
    	};
    }
    
    private void removeComplexComponentRequest(Request request) {
    	complexComponentRequests.remove(request);
    	drawExpressionOverlayIfReady(c2d);
    }
    
    private void removePathwayNodeRequest(Request request) {
    	currentPathwayExpressionForDataPoint.removeRequestInProgress(request);
    	drawExpressionOverlayIfReady(c2d);
    }
    
    private boolean isComplexWithoutComponentData(GraphObject entity) {
    	return (entity.isSetOrComplex() && !((CompositionalNode) entity).participatingMoleculesObtained());
    }
    
    private void drawExpressionOverlayIfReady(Context2d c2d) {
    	if (!drawn && complexComponentRequests.allComplexNodesReady() && 
    		(currentPathwayExpressionForDataPoint == null || currentPathwayExpressionForDataPoint.allProcessNodesReady()))
    		drawExpressionOverlay(c2d);
    }
    
    private void drawExpressionOverlay(Context2d c2d) {
    	clean(c2d);
    	drawn = true;
        Map<Long, List<ReferenceEntity>> physicalToReferenceEntityMap = pathway.getDbIdToRefEntity();
        	
        for (GraphObject entity : getObjectsForRendering()) {
           	if (entity instanceof Node) {
           		if (entity.getType() == GraphObjectType.RenderableCompartment)
           			continue;
           		            		
           		String oldBgColor = ((Node) entity).getBgColor();
           		String oldFgColor = ((Node) entity).getFgColor();
           		
           		GraphObjectExpressionRendererFactory factory = GraphObjectExpressionRendererFactory.getFactory();
           		NodeRenderer renderer = factory.getNodeRenderer((Node) entity);
           		
           		if (entity.isSetOrComplex()) {
           			addExpressionInfoToComplexComponents((CompositionalNode) entity);
           		} 
           		else if (entity.getType() == GraphObjectType.ProcessNode) {
           			((ExpressionProcessNodeRenderer) renderer).setProcessNodeExpression(currentPathwayExpressionForDataPoint.getProcessNodeExpressionObject(entity.getReactomeId()));
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
        WidgetStyle.setCursor(this, Cursor.DEFAULT);
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
				defaultColor =  Parameters.defaultSpeciesComparisonProteinColor.value(); // Blue for protein in species comparison with no inference
			}
		} else {
			AlertPopup.alert(analysisType.name() + " is an unknown analysis type");
		}
		
		return defaultColor;
		
	}
		
	private void addExpressionInfoToComplexComponents(CompositionalNode complex) {
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
		if (schemaClass.equalsIgnoreCase("ReferenceGeneProduct") || 
			schemaClass.equalsIgnoreCase("ReferenceIsoform") ||
			schemaClass.equalsIgnoreCase("EntityWithAccessionedSequence")) {
			return GraphObjectType.RenderableProtein;
		} else if (schemaClass.equalsIgnoreCase("ReferenceMolecule") ||
				   schemaClass.equalsIgnoreCase("SimpleEntity")) {
			return GraphObjectType.RenderableChemical;
		} else {
			return null;
		}
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
    
    private class ComplexComponentRequests {
    	private List<Request> requests;
    	private boolean allAdded;
    	
    	public ComplexComponentRequests() {
    		requests = new ArrayList<Request>();
    		allAdded = true;
    	}
    	
    	public void add(Request request) {
    		requests.add(request);
    	}
    	
		public void remove(Request request) {
			requests.remove(request);
		}

		public void setAllRequestsAdded(boolean allAdded) {
			this.allAdded = allAdded;
		}
		
		public boolean allRequestsAdded() {
			return allAdded;
			
		}
		
		public boolean allComplexNodesReady() {
			return allRequestsAdded() && requests.isEmpty();
		}
		
		public void cancelAllRequestsInProgress() {
			for (Request request : requests)
				request.cancel();
			
			setAllRequestsAdded(false);
			
			requests.clear();
		}
    }
    
    private class PathwayExpression {
    	private CanvasPathway pathway;
    	private Map<Integer, PathwayExpressionForDataPoint> dataPointToExpression;

    	public PathwayExpression(CanvasPathway pathway) {
    		this.pathway = pathway;
    		this.dataPointToExpression = new HashMap<Integer, PathwayExpressionForDataPoint>();
    	}
    	
		public PathwayExpressionForDataPoint getPathwayExpressionForDataPoint(Integer dataPointIndex) {
			return dataPointToExpression.get(dataPointIndex);
		}

		public void addPathwayExpressionForDataPoint(Integer dataPointIndex, PathwayExpressionForDataPoint pathwayExpressionForDataPoint) {
			dataPointToExpression.put(dataPointIndex, pathwayExpressionForDataPoint);
		}
		
		public CanvasPathway getPathway() {
			return pathway;
		}
    }
    
    public class PathwayExpressionForDataPoint {
    	private CanvasPathway pathway;
    	private List<Request> requestsInProgress;
    	private boolean allRequestsAdded;
    	private Integer dataPointIndex;
    	private Map<Long, ProcessNodeExpression> processNodeToExpression;
    	
    	public PathwayExpressionForDataPoint(CanvasPathway pathway, Integer dataPointIndex) {
    		this.pathway = pathway;
    		this.requestsInProgress = new ArrayList<Request>();
    		this.dataPointIndex = dataPointIndex;
    		this.processNodeToExpression = new HashMap<Long, ProcessNodeExpression>();
    	}

		public CanvasPathway getPathway() {
    		return pathway;
    	}
    	    	    	
    	public Boolean allProcessNodesReady() {
    		return allRequestsAdded() && requestsInProgress.isEmpty();
    	}
    	
    	public void setAllRequestsAdded(boolean allRequestsAdded) {
    		this.allRequestsAdded = allRequestsAdded;
    	}
    	
    	public boolean allRequestsAdded() {
			 return allRequestsAdded;
    	}
    	
    	public void addRequestInProgress(Request request) {
    		if (request != null)	
    			requestsInProgress.add(request);
    	}
    	
    	public void removeRequestInProgress(Request request) {
    		if (request != null)
    			requestsInProgress.remove(request);
    	}
    	
    	public void cancelAllRequestsInProgress() {
    		for (Request request : requestsInProgress)
    			request.cancel();
    		
    		setAllRequestsAdded(false);
    		
    		requestsInProgress.clear();
    	}
    	
    	public Integer getDataPointIndex() {
    		return dataPointIndex;
    	}
    	
    	public ProcessNodeExpression getProcessNodeExpressionObject(Long pathway) {
    		return processNodeToExpression.get(pathway);
    	}
    	
    	public void addProcessNodeExpressionObject(Long processNode, ProcessNodeExpression processNodeExpression) {
    		processNodeToExpression.put(processNode, processNodeExpression);
    	}
    }
    
    public class ProcessNodeExpression {
    	private CanvasPathway pathway;
    	private Double expValue;
    	private int found;
    	private int total;
    	
		public ProcessNodeExpression(Double expValue, int found, int total) {
    		this.expValue = expValue;
    		this.found = found;
    		this.total = total;
    	}
    	
    	public void setPathway(CanvasPathway pathway) {
    		this.pathway = pathway;
    	}
        
    	public CanvasPathway getPathway() {
    		return pathway;
    	}
    	
    	public Double getExpValue() {
			return new BigDecimal(expValue).setScale(2, RoundingMode.HALF_UP).doubleValue();
    	}
    	
    	public int getFound() {
			return found;
		}

		public int getTotal() {
			return total;
		}

		public String getColor() {
    		return dataController.convertValueToColor(expValue);
    	}
    }
}
