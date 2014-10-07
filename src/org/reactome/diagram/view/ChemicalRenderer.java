/*
 * Created on Oct 3, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author gwu
 *
 */
public class ChemicalRenderer extends NodeRenderer {
    
    public ChemicalRenderer() {
    }

    /**
     * The following method is used to draw an ellipse is based on this URL:
     * http://www.williammalone.com/briefs/how-to-draw-ellipse-html5-canvas/.
     * The method basically uses bezier curve with two vertex as control points.
     */
    @Override
    protected void drawRectangle(Bounds bounds, 
                                 Context2d context,
                                 Node node) {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        // Draw an ellipse
        context.beginPath();
        double x1 = x;
        double y1 = y + h / 2;
        context.moveTo(x1, y1);
        x1 = x + w;
        context.bezierCurveTo(x, y,
                              x + w, y, 
                              x1, y1);
        x1 = x;
        context.bezierCurveTo(x + w, y + h, 
                              x, y + h,
                              x1, y1);
        context.closePath();
        context.fill();
        context.stroke();
    }
    
    
    
}
