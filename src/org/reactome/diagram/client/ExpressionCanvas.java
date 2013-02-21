/*
 * Created on Feb , 2013
 *
 */
package org.reactome.diagram.client;

import java.util.List;
import java.util.Map;

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
    private Map<Long, String> entityColorMap;
    private CanvasPathway pathway;
    
    public ExpressionCanvas() {

    }
    
    public ExpressionCanvas(PathwayDiagramPanel diagramPane) {
    	super(diagramPane);
    }
   
    public Map<Long, String> getEntityColorMap() {
		return entityColorMap;
	}

	public void setEntityColorMap(Map<Long, String> entityColorMap) {
		this.entityColorMap = entityColorMap;
	}

	public CanvasPathway getPathway() {
		return pathway;
	}

	public void setPathway(CanvasPathway pathway) {
		this.pathway = pathway;
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
            		String entityColor = null;
            		
            		for (Long dbId : entityColorMap.keySet()) {
            			if (dbId.equals(entityId)) {
            				entityColor = entityColorMap.get(dbId);
            				break;
            			}	            		
            		}	
            		
            		String oldBgColor = ((Node) entity).getBgColor();
            		String oldFgColor = ((Node) entity).getFgColor();
            		
            		if (entityColor != null) {
            			((Node) entity).setBgColor(entityColor);
            		} else if (entity.getType() == GraphObjectType.RenderableComplex) {
            			((Node) entity).setBgColor("rgb(0,0,0)"); // Black background
            			((Node) entity).setFgColor("rgb(255,255,255)"); // White Text
            		} else {
            			((Node) entity).setBgColor("rgb(192,192,192)"); // Grey for no data
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
