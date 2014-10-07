/*
 * Created on Jul 18, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

/**
 * Renderer that is used to render proteins.
 * @author gwu
 *
 */
public class ProteinRenderer extends NodeRenderer {
    
    public ProteinRenderer() {
    }
    
    /**
     * This is a template that should be implemented by a sub-class.
     * @param bounds
     * @param context
     * @param node
     */
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
        if (node.isNeedDashedBorder())
            drawDashedRectangle(bounds, context, true);
        else
            drawRectangle(bounds, 
                          context,
                          true);
    }
    
    private void drawDashedRectangle(Bounds bounds,
                                     Context2d context,
                                     boolean needFill) {
        double x0 = bounds.getX();
        double y0 = bounds.getY();
        int r = getRadius();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        context.setLineWidth(Parameters.dashedLineWidth);
        // Draw four dashed lines
        Point p1 = new Point(x0 + r, y0);
        Point p2 = new Point(x0 + w - r, y0);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0 + w, y0 + r);
        p2 = new Point(x0 + w, y0 + h - r);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0 + w - r, y0 + h);
        p2 = new Point(x0 + r, y0 + h);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0, y0 + h - r);
        p2 = new Point(x0, y0 + r);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        // Need to draw rounded corners
        context.beginPath();
        context.moveTo(x0, y0 + r);
        context.quadraticCurveTo(x0, y0, x0 + r, y0);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + w - r, y0);
        context.quadraticCurveTo(x0 + w, y0, x0 + w, y0 + r);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + w, y0 + h - r);
        context.quadraticCurveTo(x0 + w, y0 + h, x0 + w - r, y0 + h);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + r, y0 + h);
        context.quadraticCurveTo(x0, y0 + h, x0, y0 + h - r);
        context.closePath();
        context.stroke();
        if (needFill) {
            context.beginPath();
            context.moveTo(x0+r, y0);
            context.lineTo(x0+w-r, y0);
            context.quadraticCurveTo(x0+w, y0, x0+w, y0+r);
            context.lineTo(x0+w, y0+h-r);
            context.quadraticCurveTo(x0+w, y0+h, x0+w-r, y0+h);
            context.lineTo(x0+r, y0+h);
            context.quadraticCurveTo(x0, y0+h, x0, y0+h-r);
            context.lineTo(x0, y0+r);
            context.quadraticCurveTo(x0, y0, x0+r, y0);
            context.closePath();
            context.fill();
        }
    }
    
}
