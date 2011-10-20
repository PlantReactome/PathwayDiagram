/*
 * Created on Oct 18, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.touch.client.Point;

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

    @Override
    protected void drawName(Context2d context, Node node) {
        if (node.getDisplayName() == null)
            return;
        String fgColor = node.getFgColor();
        if (fgColor == null)
            fgColor = "rgba(0, 0, 0, 1)";
        CssColor strokeStyleColor = CssColor.make(fgColor);
        context.setFillStyle(strokeStyleColor);
        String font = "12px Lucida Sans"; // This should be fixed
        context.setFont(font);
        context.setTextAlign(TextAlign.LEFT);
        context.setTextBaseline(TextBaseline.TOP);

        String name = node.getDisplayName();
        Point position = node.getTextPosition();
        context.fillText(name,
                         position.getX() + 4, // These two numbers should be adjusted more.
                         position.getY() + 8);
    }
    
}
