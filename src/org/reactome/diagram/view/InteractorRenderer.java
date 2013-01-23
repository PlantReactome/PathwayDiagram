/*
 * Created on Dec 13, 2012
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * @author jweiser
 *
 */
public class InteractorRenderer extends NodeRenderer {
    
    public InteractorRenderer() {
        defaultLineWidth = 2.0d;
        defaultLineColor = CssColor.make("rgba(0, 0, 255, 1)");
    }

    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
        setStroke(context, node);
        int x = bounds.getX();
        int y = bounds.getY();
        int w = bounds.getWidth();
        int h = bounds.getHeight();
        context.beginPath();
        int x1 = x;
        int y1 = y;
        context.moveTo(x1, y1);
        x1 = x + w;
        context.lineTo(x1, y1);
        y1 = y + h;
        context.lineTo(x1, y1);
        x1 = x;
        context.lineTo(x1, y1);
        y1 = y;
        context.lineTo(x1, y1);
        context.closePath();
        double oldLineWidth = context.getLineWidth();
        context.fill();
        context.stroke();
        context.setLineWidth(oldLineWidth);
    }    
}
