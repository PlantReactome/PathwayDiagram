/*
 * Created on Oct 3, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

/**
 * @author gwu
 *
 */
public class ComplexRenderer extends NodeRenderer {
    
    public ComplexRenderer() {
        defaultLineWidth = 2.0d;
    }

    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 boolean needFill,
                                 boolean needStroke) {
    	createPath(bounds, context);
        
        Double oldLineWidth = context.getLineWidth();
        if (needFill)
        	context.fill();
        if (needStroke)
        	context.stroke();
        context.setLineWidth(oldLineWidth);
    }
    
    @Override
	protected void drawDashedRectangle(Bounds bounds, Context2d context,
			boolean needFill) {
		
    	double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        context.setLineWidth(Parameters.dashedLineWidth);
        // Draw four dashed lines
        Point p1 = new Point(x + COMPLEX_RECT_ARC_WIDTH, y);
        Point p2 = new Point(x + w - COMPLEX_RECT_ARC_WIDTH, y);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x + w, y + COMPLEX_RECT_ARC_WIDTH);
        p2 = new Point(x + w, y + h - COMPLEX_RECT_ARC_WIDTH);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x + COMPLEX_RECT_ARC_WIDTH, y + h);
        p2 = new Point(x + w - COMPLEX_RECT_ARC_WIDTH, y + h);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x, y + COMPLEX_RECT_ARC_WIDTH);
        p2 = new Point(x, y + h - COMPLEX_RECT_ARC_WIDTH);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        
        context.beginPath();
        context.moveTo(x, y + COMPLEX_RECT_ARC_WIDTH);
        context.lineTo(x + COMPLEX_RECT_ARC_WIDTH, y);
        context.closePath();
        context.stroke();
        
        context.beginPath();
        context.moveTo(x + w - COMPLEX_RECT_ARC_WIDTH, y);
        context.lineTo(x + w, y + COMPLEX_RECT_ARC_WIDTH);
        context.closePath();
        context.stroke();
        
        context.beginPath();
        context.moveTo(x + w, y + h - COMPLEX_RECT_ARC_WIDTH);
        context.lineTo(x + w - COMPLEX_RECT_ARC_WIDTH, y + h);
        context.closePath();
        context.stroke();
        
        context.beginPath();
        context.moveTo(x + COMPLEX_RECT_ARC_WIDTH, y + h);
        context.lineTo(x, y + h - COMPLEX_RECT_ARC_WIDTH);
        context.closePath();
        context.stroke();
        
        if (needFill) {
        	createPath(bounds, context);
        	context.fill();
        }
		
	}

	protected void createPath(Bounds bounds, Context2d context) {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        
        context.beginPath();
        double x1 = x + COMPLEX_RECT_ARC_WIDTH;
        double y1 = y;
        context.moveTo(x1, y1);
        x1 += (w - 2 * COMPLEX_RECT_ARC_WIDTH);
        context.lineTo(x1 , y1);
        x1 = x + w;
        y1 = y + COMPLEX_RECT_ARC_WIDTH;
        context.lineTo(x1, y1);
        y1 += (h - 2 * COMPLEX_RECT_ARC_WIDTH);
        context.lineTo(x1, y1);
        x1 = x + w - COMPLEX_RECT_ARC_WIDTH;
        y1 = y + h;
        context.lineTo(x1, y1);
        x1 = x + COMPLEX_RECT_ARC_WIDTH;
        context.lineTo(x1, y1);
        x1 = x;
        y1 = y + h - COMPLEX_RECT_ARC_WIDTH;
        context.lineTo(x1, y1);
        y1 = y + COMPLEX_RECT_ARC_WIDTH;
        context.lineTo(x1, y1);
        context.closePath();
    }
    
}
