/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
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
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

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
    private Context2d c2d;
       
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    	expressionCanvasModel = new ExpressionCanvasModel();
    }
   
    public ExpressionCanvas(PathwayDiagramPanel diagramPane, CanvasTransformation canvasTransformation) {
    	this(diagramPane);
    	this.canvasTransformation = canvasTransformation;
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
    	c2d = getContext2d();
    	   	    	
    	clean(c2d);
    	
    	ExpressionPathway oldExpressionPathway = expressionPathway;
    	
    	if (pathway == null) {
    		if (oldExpressionPathway != null)
    			oldExpressionPathway.getTimerCheckingIfProcessNodeInfoObtained().cancel();    			
    			
    		return;
    	} else {
    		getPathwayNodeDataBeforeRendering(oldExpressionPathway);
    	}
    }
    
    private void getPathwayNodeDataBeforeRendering(ExpressionPathway oldExpressionPathway) {
    	if (oldExpressionPathway == null || 
    		oldExpressionPathway.getPathway() != pathway ||
    		oldExpressionPathway.getDataPointIndex() != getDataPointIndexFromDataController()) {
    			if (oldExpressionPathway != null)
    				oldExpressionPathway.getTimerCheckingIfProcessNodeInfoObtained().cancel();
    		
    			expressionPathway = new ExpressionPathway(c2d, pathway, getDataPointIndexFromDataController());
    			for (GraphObject entity : getGraphObjects()) {    		
    				if (entity.getType() == GraphObjectType.ProcessNode) {
    					diagramPane.getController().getReferenceEntity(entity.getReactomeId(), getGenesForProcessNodeAndColor(expressionPathway, entity));
    				}
    			}
    	}
    		
    	if (expressionPathway.allProcessNodesReady()) // True if no pathway nodes to render
    		drawExpressionOverlay(expressionPathway);
    	else {
    		expressionPathway.getTimerCheckingIfProcessNodeInfoObtained().scheduleRepeating(200);
    	}    	
    	
    }	
    	
    private void drawExpressionOverlay(ExpressionPathway expressionPathway) { 	
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
    }

	private Long getReferenceEntityId(List<Long> referenceEntityIds) {
		if (referenceEntityIds != null && referenceEntityIds.size() > 0) {
		     return referenceEntityIds.get(0);
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
 
	private void addExpressionInfoToComplexComponents(ComplexNode complex, List<Long> componentIds) {
		if (complex == null || componentIds == null)
			return;
		
		for (Long refId : componentIds) 
			complex.addComponent(refId);
		
		for (Component component : complex.getComponents()) {
			Long refId = component.getRefEntityId();
			List<String> componentExpressionId = null;			
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
	
	private RequestCallback getGenesForProcessNodeAndColor(final ExpressionPathway expressionPathway, final GraphObject entity) {
		expressionPathway.incrementCallbacksInProgress();
		
		RequestCallback getGenesForProcessNodeAndColor = new RequestCallback() {
			final private String ERROR_MSG = "Could not retrieve pathway genes"; 
			
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					AlertPopup.alert(ERROR_MSG + " for " + entity.getDisplayName());
					expressionPathway.decrementCallbacksInProgress();
					return;
				}
								
				colorProcessNode(expressionPathway, entity, getRefIdsForPathwayGenes(response.getText()));
			}
			
			public void onError(Request request, Throwable exception) {
				AlertPopup.alert(ERROR_MSG + exception);
				expressionPathway.decrementCallbacksInProgress();
			}
		};
		
		return getGenesForProcessNodeAndColor;
	}
	
	private List<Long> getRefIdsForPathwayGenes(String referenceEntityXML) {
		List<Long> geneRefIds = new ArrayList<Long>();
		
		ReactomeXMLParser referenceEntityParser = new ReactomeXMLParser(referenceEntityXML);
		
		Element referenceEntityElement = referenceEntityParser.getDocumentElement();
		
		NodeList referenceGeneProducts = referenceEntityElement.getElementsByTagName("referenceGeneProduct");
		for (int i = 0; i < referenceGeneProducts.getLength(); i++) {
			Element referenceGeneProduct = (Element) referenceGeneProducts.item(i);
						
			try {
				Long refId = Long.parseLong(referenceEntityParser.getXMLNodeValue(referenceGeneProduct, "dbId"));
				geneRefIds.add(refId);
			} catch (NumberFormatException ex) {
				AlertPopup.alert("Unable to parse gene product id " + ex);
			}
		}
		
		NodeList referenceIsoforms = referenceEntityElement.getElementsByTagName("referenceIsoform");
		for (int i = 0; i < referenceIsoforms.getLength(); i++) {
			Element referenceIsoform = (Element) referenceIsoforms.item(i);
			
			try {
				Long refId = Long.parseLong(referenceEntityParser.getXMLNodeValue(referenceIsoform, "dbId"));
				geneRefIds.add(refId);
			} catch (NumberFormatException ex) {
				AlertPopup.alert("Unable to parse isoform id " + ex);
			}
		}
				
		return geneRefIds;
	}
				
	private void colorProcessNode(ExpressionPathway expressionPathway, GraphObject entity, List<Long> refIdsForPathwayGenes) {
		Map<Long, ExpressionInfo> pathwayExpressionComponents = new HashMap<Long, ExpressionInfo>();
		
		PathwayExpressionValue pathwayExpression = dataController.getDataModel().getPathwayExpressionValue(entity.getReactomeId());
			
		if (pathwayExpression != null) { 
			Map<Long, List<String>> expressionIds = pathwayExpression.getDbIdsToExpressionIds();
			Map<Long, Double> expressionLevels = pathwayExpression.getExpressionValueForDataPoint(expressionPathway.getDataPointIndex());
			Map<Long, String> expressionColors = dataController.convertValueToColor(expressionLevels);
		
			for (Long pathwayComponentId : expressionIds.keySet()) 
				pathwayExpressionComponents.put(pathwayComponentId,	
						expressionCanvasModel.new ExpressionInfo(expressionIds.get(pathwayComponentId),
																 expressionLevels.get(pathwayComponentId),
																 expressionColors.get(pathwayComponentId)
																)
				);			
					
			expressionPathway.addProcessNodeColorList(
				entity,
				expressionCanvasModel.getColorList(refIdsForPathwayGenes, pathwayExpressionComponents)
			);
		}
		
		expressionPathway.decrementCallbacksInProgress();
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
    	
    	public ExpressionPathway(Context2d c2d, CanvasPathway pathway, Integer dataPointIndex) {
    		this.pathway = pathway;
    		this.dataPointIndex = dataPointIndex;
    		this.processNodeCount = 0;
    		this.processNodeToColorList = new HashMap<GraphObject, List<String>>();
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
    	
    	//public void setContext2d(Context2d c2d) {
    		//this.c2d = c2d;
    	//}
    	
    	private void createTimer() {
    		processNodeInfoObtainedTimer = new Timer() {
    			
    			public void run() {
    				if (allProcessNodesReady()) {
    					cancel();
    					drawExpressionOverlay(ExpressionPathway.this);
    				}
    			}
    			
    		};
    	}
    }
}
