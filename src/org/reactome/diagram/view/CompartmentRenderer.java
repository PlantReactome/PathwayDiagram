/*
 * Created on Oct 18, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * Customized NodeRenderer to draw Compartment.
 * @author gwu
 *
 */
public class CompartmentRenderer extends NodeRenderer {

    public CompartmentRenderer() {
        defaultLineColor = CssColor.make(255, 153, 102); 
    }

    @Override
    protected int getRadius() {
        return 2 * super.getRadius();
    }

    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
        double oldWidth = context.getLineWidth();
        context.setLineWidth(THINK_LINE_WIDTH);
        super.drawRectangle(bounds, 
                            context,
                            true);
        if (isInsetsNeeded(node)) {
            Bounds insets = new Bounds(bounds);
            int x = insets.getX();
            insets.setX(x + RECTANGLE_DIST);
            int y = insets.getY();
            insets.setY(y + RECTANGLE_DIST);
            int w = insets.getWidth();
            insets.setWidth(w - 2 * RECTANGLE_DIST);
            int h = insets.getHeight();
            insets.setHeight(h - 2 * RECTANGLE_DIST);
            super.drawRectangle(insets, context, false);
        }
        context.setLineWidth(oldWidth);
    }
    
    private boolean isInsetsNeeded(Node node) {
        String name = node.getDisplayName();
        return name != null && !name.endsWith("membrane");
    }
}
