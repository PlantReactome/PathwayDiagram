/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * The top-level renderer for rendering GraphObject.
 * @author gwu
 *
 */
public abstract class AbstractRenderer<T extends GraphObject> implements GraphObjectRenderer<T> {
    protected double defaultLineWidth = 1.0d;
    protected CssColor defaultLineColor = Parameters.defaultstrokeColor;
    
    /**
     * Converts the Stroke and Fill Colors to Standard CssColor Objects and sets it on the Canvas
     * @param context Context2d object for which the Color Settings are initialized
     */
    public void setColors(Context2d context,
                          String fgColor,
                          String bgColor) {
        CssColor strokeStyleColor = CssColor.make(fgColor);
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(bgColor);
        context.setFillStyle(fillStyleColor);
    }

    protected void setStroke(Context2d c2d, T obj) {
        String color;
        if (obj.isSelected() || obj.isHighlighted()) {
            if (obj.isSelected())
                c2d.setStrokeStyle(Parameters.defaultSelectionColor);
            else
                c2d.setStrokeStyle(Parameters.defaultHighlightColor);
            if (obj instanceof HyperEdge)
                c2d.setLineWidth(Parameters.defaultEdgeSelectionLineWidth);
            else
                c2d.setLineWidth(Parameters.defaultNodeSelectionLineWidth);
        }
        else {
            if (obj.getLineWidth() == 0.0d)
                c2d.setLineWidth(defaultLineWidth);
            else
                c2d.setLineWidth(obj.getLineWidth());
            color = obj.getLineColor();
            if (color == null)
                c2d.setStrokeStyle(defaultLineColor);
            else
                c2d.setStrokeStyle(CssColor.make(color));
        }
    }
    
}
