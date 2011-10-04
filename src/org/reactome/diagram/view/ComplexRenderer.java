/*
 * Created on Oct 3, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author gwu
 *
 */
public class ComplexRenderer extends NodeRenderer {
    private static final double LINE_WIDTH = 2.0d;
    
    public ComplexRenderer() {
    }

    @Override
    protected void drawRectangle(Bounds bounds, Context2d context) {
        int x = bounds.getX();
        int y = bounds.getY();
        int w = bounds.getWidth();
        int h = bounds.getHeight();
        context.beginPath();
        int x1 = x + COMPLEX_RECT_ARC_WIDTH;
        int y1 = y;
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
        double oldLineWidth = context.getLineWidth();
        context.setLineWidth(LINE_WIDTH);
        context.fill();
        context.stroke();
        context.setLineWidth(oldLineWidth);
    }
    
}
