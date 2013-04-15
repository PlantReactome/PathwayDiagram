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
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

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
            		
            		if (entity.getType() == GraphObjectType.RenderableComplex) {
            			((Node) entity).setBgColor("rgb(0,0,0)"); // Black background
            			((Node) entity).setFgColor("rgb(255,255,255)"); // White Text
            		} 
            		else {
            			Long refEntityId = getReferenceEntityId(physicalToReferenceEntityMap.get(entity.getReactomeId()));            			
            			String nodeColor = getEntityColor(refEntityId, entity.getType());            
            			
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

	private Long getReferenceEntityId(List<Long> referenceEntityIds) {
		if (referenceEntityIds != null && referenceEntityIds.size() > 0) {
		     return referenceEntityIds.get(0);
		}
		return null;
	}

	private String getEntityColor(Long refEntityId, GraphObjectType entityType) {
		if (expressionCanvasModel.getEntityColorMap() != null) {            					
			String color = expressionCanvasModel.getEntityColorMap().get(refEntityId);
			if (color == null) {
				color = getDefaultColor(entityType);
				expressionCanvasModel.getEntityColorMap().put(refEntityId, color); // Cache color for future lookups
			}
			
			return color;
		} else {		
			return getDefaultColor(entityType);
		}
	}

	private String getDefaultColor(GraphObjectType entityType) {
		String defaultColor = null;
		if (AnalysisType.contains(analysisType.name())) {
			defaultColor = "rgb(192,192,192)"; // Grey by default
			
			if (analysisType == AnalysisType.SpeciesComparison && entityType == GraphObjectType.RenderableProtein) {
				defaultColor =  "rgb(0, 0, 255)"; // Blue for protein in species comparison with no inference
			}			
		} else {
			AlertPopup alert = new AlertPopup(analysisType.name() + " is an unknown analysis type");
		}
		
		return defaultColor;
		
	}
 
    /**
     * A template method so that other kinds of things can be updated. Nothing
     * has been done in this class.
     */
    protected void updateOthers(Context2d c2d) {
        
    }
    
}
