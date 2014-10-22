/*
 * Created on Feb 13, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractionType;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

/**
 * This class is used to render RenderableInteraction.
 * @author gwu
 *
 */
public class RenderableInteractionRenderer extends HyperEdgeRenderer {
    
    public RenderableInteractionRenderer() {
        
    }

    @Override
    public void render(Context2d c2d, HyperEdge edge) {
        setLineColor(c2d, edge);
        setStroke(c2d, edge);
//        c2d.setStrokeStyle(drawColor);
        // Draw backbone
        List<Point> points = edge.getBackbone();
        drawLines(c2d, points);
        // Draw arrows
        drawInteractionType(c2d, edge);
    }
    
    private void drawInteractionType(Context2d c2d, HyperEdge edge) {
        List<Point> backbone = edge.getBackbone();
        Point position = backbone.get(backbone.size() - 1);
        Point controlPoint = backbone.get(backbone.size() - 2);
        InteractionType type = edge.getInteractionType();
        if (type == null || type == InteractionType.INTERACT || type == InteractionType.UNKNOWN)
            return; // Don't draw anything
        if (type == InteractionType.ACTIVATE || type == InteractionType.ENCODE || type == InteractionType.ENHANCE)
            drawArrow(c2d, controlPoint, position, true);
        else if (type == InteractionType.INHIBIT || type == InteractionType.REPRESS)
            drawInhibitor(c2d, position, edge);
    }
    
    private void drawInhibitor(Context2d context, Point position, HyperEdge edge) {
    	List<Point> edgeBackbone = edge.getBackbone();
    	double deltaY = edgeBackbone.get(1).getY() - edgeBackbone.get(0).getY();
    	double deltaX = edgeBackbone.get(1).getX() - edgeBackbone.get(0).getX();
    	
    	double angle = deltaY == 0 ? Math.PI / 2 : Math.atan(-deltaX/deltaY);
    	double x1 = position.getX() - (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
    	double x2 = position.getX() + (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
    	double y1 = position.getY() + (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
    	double y2 = position.getY() - (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
    	
    	context.beginPath();
    	context.moveTo(x1, y1);
    	context.lineTo(x2, y2);
    	context.closePath();
    	context.stroke();
    }
    
}
