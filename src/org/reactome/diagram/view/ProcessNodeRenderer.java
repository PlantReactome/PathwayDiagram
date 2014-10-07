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

/**
 * Customized Renderer for drawing ProcessNode objects.
 * @author gwu
 *
 */
public class ProcessNodeRenderer extends NodeRenderer {
    public static final int RECTANGLE_DIST = 10;
    
    public ProcessNodeRenderer() {
    }

    @Override
    protected void drawRectangle(Bounds bounds, 
                                 Context2d context,
                                 Node node) {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        drawRectangle(context, x, y, w, h);
        // Get the color
        FillStrokeStyle fillStyle = context.getFillStyle();
        if (fillStyle.getType() == FillStrokeStyle.TYPE_CSSCOLOR) {
            CssColor color = (CssColor)fillStyle;
            CssColor brigher = makeBrighterColor(color);
            context.setFillStyle(brigher);
            x += RECTANGLE_DIST;
            y += RECTANGLE_DIST;
            w -= 2 * RECTANGLE_DIST;
            h -= 2 * RECTANGLE_DIST;
            drawRectangle(context, x, y, w, h);
        }
    }

    /**
     * This method is copied from java.awt.Color.brighter() method.
     * @param color
     * @return
     */
    private CssColor makeBrighterColor(CssColor color) {
        String value = color.value();
//        System.out.println("Color value: " + value);
        int r = Integer.parseInt(value.substring(1, 3), 16);
        int g = Integer.parseInt(value.substring(3, 5), 16);
        int b = Integer.parseInt(value.substring(5), 16);
        double factor = 0.7d;
        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return CssColor.make(r, g, b);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return CssColor.make(Math.min((int)(r/factor), 255),
                             Math.min((int)(g/factor), 255),
                             Math.min((int)(b/factor), 255));

    }
    
}
