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
            		
            		Long entityId = entity.getReactomeId();
            		List<Long> referenceEntityIds = physicalToReferenceEntityMap.get(entityId);
            		
            		Long refEntityId = null;       
            		if (referenceEntityIds != null && referenceEntityIds.size() > 0 &&
            		    entity.getType() != GraphObjectType.RenderableComplex) {
            		    refEntityId = referenceEntityIds.get(0);
            		}
            				            		
            		String oldBgColor = ((Node) entity).getBgColor();
            		String oldFgColor = ((Node) entity).getFgColor();
            		
            		if (entity.getType() == GraphObjectType.RenderableComplex) {
            			((Node) entity).setBgColor("rgb(0,0,0)"); // Black background
            			((Node) entity).setFgColor("rgb(255,255,255)"); // White Text
            		} 
            		else {
            			String nodeColor = null;
            			
            			String assignedNodeColor = expressionCanvasModel.getEntityColorMap().get(refEntityId);
            			if (analysisType == AnalysisType.Expression) {
            				if (assignedNodeColor != null) {
            					nodeColor = assignedNodeColor;
            				}
            			} else if (analysisType == AnalysisType.SpeciesComparison) {
            				if (entity.getType() == GraphObjectType.RenderableProtein) {
            					if (assignedNodeColor != null) {
            						nodeColor = assignedNodeColor;
            					} 
            					else {
            						nodeColor = "rgb(0,0,255)"; // Blue for no inference
            						expressionCanvasModel.getEntityColorMap().put(refEntityId, nodeColor);
            					}
            				}
            			} 
            			else {
            				AlertPopup alert = new AlertPopup("Unknown analysis type");
            				alert.center();
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
