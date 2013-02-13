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
        // Have to find which direction outputHub is
        List<Node> outputs = edge.getOutputNodes();
        if (outputs == null || outputs.size() == 0)
            return;
        Node node = outputs.get(0); // There should be only one
        Bounds bounds = node.getBounds();
        double y = position.getY();
        if ((y < bounds.getY()) || 
            (y > bounds.getY() + bounds.getHeight())) { // Should use a horizontal line
            // use horizontal line
            context.beginPath();
            double x1 = position.getX() - EDGE_MODULATION_WIDGET_WIDTH / 2.0d;
            double x2 = position.getX() + EDGE_MODULATION_WIDGET_WIDTH / 2.0d;
            context.moveTo(x1, position.getY());
            context.lineTo(x2, position.getY());
            context.closePath();
            context.stroke();
        }
        else {
            // Draw vertical line
            context.beginPath();
            double y1 = position.getY() - EDGE_MODULATION_WIDGET_WIDTH / 2.0d;
            double y2 = position.getY() + EDGE_MODULATION_WIDGET_WIDTH / 2.0d;
            context.moveTo(position.getX(), y1);
            context.lineTo(position.getX(), y2);
            context.closePath();
            context.stroke();
        }
    }
    
}
