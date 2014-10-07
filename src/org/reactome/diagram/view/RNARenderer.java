/*
 * Created on Aug 2, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A Renderer for drawing RNA entities. This class is ported from DefaultRNARenderer in the
 * curator tool project.
 * @author gwu
 *
 */
public class RNARenderer extends NodeRenderer {
    private final int LOOP_WIDTH = 16;
    
    /**
     * Default constructor
     */
    public RNARenderer() {
    }

    @Override
    protected void drawRectangle(Bounds bounds, Context2d context, Node node) {
        double x0 = bounds.getX();
        double y0 = bounds.getY();
        double x01 = bounds.getX() + bounds.getWidth();
        double y01 = bounds.getY() + bounds.getHeight();
        context.beginPath();
        double x = x0 + LOOP_WIDTH;
        double y = y0 + LOOP_WIDTH / 2;
        context.moveTo(x, y);
        x = x01 - LOOP_WIDTH;
        context.lineTo(x, y);
        y = y0 + bounds.getHeight() / 2;
        context.quadraticCurveTo(x01, y0, x01, y);
        x = x01 - LOOP_WIDTH;
        y = y01 - LOOP_WIDTH / 2;
        context.quadraticCurveTo(x01, y01, x, y);
        x = x0 + LOOP_WIDTH;
        context.lineTo(x, y);
        y = y0 + bounds.getHeight() / 2;
        context.quadraticCurveTo(x0, y01, x0, y);
        x = x0 + LOOP_WIDTH;
        y = y0 + LOOP_WIDTH / 2;
        context.quadraticCurveTo(x0, y0, x, y);
        context.closePath();
        context.fill();
        context.stroke();
    }
    
}
