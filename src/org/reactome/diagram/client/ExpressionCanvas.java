/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.util.List;
import java.util.Map;

import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

import org.reactome.diagram.view.ExpressionComplexRenderer;
import org.reactome.diagram.view.GraphObjectRendererFactory;
import org.reactome.diagram.view.NodeRenderer;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A specialized PlugInSupportCanvas that is used to overlay expression data on to a pathway.
 * @author gwu
 *
 */
public class ExpressionCanvas extends DiagramCanvas {
    private AnalysisType analysisType;
	private ExpressionCanvasModel expressionCanvasModel;    
    private CanvasPathway pathway;
    
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    	hoverHandler = new ExpressionCanvasHoverHandler(diagramPane, this);
    	expressionCanvasModel = new ExpressionCanvasModel(this);
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

		if (pathway != null) {	
			diagramPane.getController().getPhysicalToReferenceEntityMap(pathway.getReactomeId(), updateCanvas);
		} 
		else {
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
        
        Map<Long, List<Long>> physicalToReferenceEntityMap = expressionCanvasModel.getPhysicalToReferenceEntityMap();
        if (pathway != null && physicalToReferenceEntityMap != null) {
            for (GraphObject entity : getGraphObjects()) {
            	if (entity instanceof Node) {
            		if (entity.getType() == GraphObjectType.RenderableCompartment)
            			continue;
            		            		
            		String oldBgColor = ((Node) entity).getBgColor();
            		String oldFgColor = ((Node) entity).getFgColor();
            		
            		GraphObjectRendererFactory factory = GraphObjectRendererFactory.getFactory();
            		NodeRenderer renderer;
            		
            		if (entity.getType() == GraphObjectType.RenderableComplex) {
            			((Node) entity).setFgColor("rgb(0,0,0)"); // Black text
            		//	((Node) entity).setFgColor("rgb(255,255,255)");
            			
            			List<Long> componentIds = physicalToReferenceEntityMap.get(entity.getReactomeId());
            			addExpressionInfoToComplexComponents((ComplexNode) entity, componentIds);
            			
            			//renderer = factory.getNodeRenderer((Node) entity); 
            			renderer = new ExpressionComplexRenderer();
            		} 
            		else {
            			Long refEntityId = getReferenceEntityId(physicalToReferenceEntityMap.get(entity.getReactomeId()));            			
            			String nodeColor = getEntityColor(refEntityId, entity.getType());            
            			
            			((Node) entity).setBgColor(nodeColor);             		
            			renderer = factory.getNodeRenderer((Node) entity);
            		}
            		
            		renderer.render(c2d, (Node) entity);
            		
            		((Node) entity).setBgColor(oldBgColor);
            		((Node) entity).setFgColor(oldFgColor);
            	}
            }
            updateOthers(c2d);
        }
        
        c2d.restore();
    }

	private Long getReferenceEntityId(List<Long> referenceEntityIds) {
		if (referenceEntityIds != null && referenceEntityIds.size() > 0) {
		     return referenceEntityIds.get(0);
		}
		return null;
	}
	
	public String getEntityExpressionId(Long refEntityId) {
		String id = null;
		
		if (expressionCanvasModel.getEntityExpressionIdMap() != null)
			id = expressionCanvasModel.getEntityExpressionIdMap().get(refEntityId);
		
		return id;
	}

	public Double getEntityExpressionLevel(Long refEntityId) {
		Double level = null;
		
		if (expressionCanvasModel.getEntityExpressionLevelMap() != null)
			level = expressionCanvasModel.getEntityExpressionLevelMap().get(refEntityId);
		
		return level;				
	}
	
	public String getEntityColor(Long refEntityId, GraphObjectType entityType) {		
		String color = null;		
		if (expressionCanvasModel.getEntityColorMap() != null) {            					
			color = expressionCanvasModel.getEntityColorMap().get(refEntityId);
		} 		
							
		if (color == null) {
			color = getDefaultColor(entityType);
			//expressionCanvasModel.getEntityColorMap().put(refEntityId, color); // Cache color for future lookups
		}

		return color;
	}

	private String getDefaultColor(GraphObjectType entityType) {
		String defaultColor = null;
		if (AnalysisType.contains(analysisType.name())) {
			defaultColor = "rgb(192,192,192)"; // Grey by default
			
			if (analysisType == AnalysisType.SpeciesComparison && entityType == GraphObjectType.RenderableProtein) {
				defaultColor =  "rgb(0, 0, 255)"; // Blue for protein in species comparison with no inference
			}
		} else {
			AlertPopup.alert(analysisType.name() + " is an unknown analysis type");
		}
		
		return defaultColor;
		
	}
 
	private void addExpressionInfoToComplexComponents(ComplexNode complex, List<Long> componentIds) {
		for (Long refId : componentIds)
			complex.addComponent(refId);
		
		for (Component component : complex.getComponents()) {
			Long refId = component.getRefEntityId();
			String componentExpressionId = null;			
			String componentExpressionColor = null;
			Double componentExpressionLevel = null;
			
			if (refId != null) {
				componentExpressionId = getEntityExpressionId(refId);
				componentExpressionColor = getEntityColor(refId, null);
				componentExpressionLevel = getEntityExpressionLevel(refId);
			}
						
			component.setExpressionId(componentExpressionId);
			component.setExpressionColor(componentExpressionColor);
			component.setExpressionLevel(componentExpressionLevel);
		}
	}
	
    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
