/*
 * Created on Jul 18, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;

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
    
    @Override
	protected void drawRectangle(Context2d context, Bounds bounds) {
    	double coX = bounds.getX();
        double coY = bounds.getY();
        int radius = getRadius();
        double nodeWidth = bounds.getWidth();
        double nodeHeight = bounds.getHeight();
       
        context.beginPath();
        context.moveTo(coX+radius, coY);
        context.lineTo(coX+nodeWidth-radius, coY);
        context.quadraticCurveTo(coX+nodeWidth, coY, coX+nodeWidth, coY+radius);
        context.lineTo(coX+nodeWidth, coY+nodeHeight-radius);
        context.quadraticCurveTo(coX+nodeWidth, coY+nodeHeight, coX+nodeWidth-radius, coY+nodeHeight);
        context.lineTo(coX+radius, coY+nodeHeight);
        context.quadraticCurveTo(coX, coY+nodeHeight, coX, coY+nodeHeight-radius);
        context.lineTo(coX, coY+radius);
        context.quadraticCurveTo(coX, coY, coX+radius, coY);
        context.closePath();
	}

	protected void drawDashedRectangle(Bounds bounds,
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
