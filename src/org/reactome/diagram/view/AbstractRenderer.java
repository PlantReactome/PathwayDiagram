/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.touch.client.Point;

/**
 * The top-level renderer for rendering GraphObject.
 * @author gwu
 *
 */
public abstract class AbstractRenderer<T extends GraphObject> implements GraphObjectRenderer<T> {
    protected double defaultLineWidth = 1.0d;
    protected CssColor defaultLineColor = Parameters.defaultstrokeColor;
    // This will be used for overview drawing: this linewith should be used without considering
    // the GraphObject one
    private Double absoluteLineWidth;
    
    
    public void setAbsoluteLineWidth(Double lineWidth) {
        this.absoluteLineWidth = lineWidth;
    }
    
    public Double getAbsoluteLineWidth() {
        return this.absoluteLineWidth;
    }
    
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
        // Overwrite any preset linewidth
        if (absoluteLineWidth != null)
            c2d.setLineWidth(absoluteLineWidth);
    }
    
    // The following three methods are used to draw dashed lines, which is not supported by Context2d.
    protected void drawDashedLine(Context2d c2d, 
                                Point from, 
                                Point end, 
                                double[] pattern) {
        c2d.beginPath();
        
        double fromX = from.getX();
        double fromY = from.getY();
        double toX = end.getX();
        double toY = end.getY();
        // Used to check if the drawing is done yet
        boolean xgreaterThan = fromX < toX;
        boolean ygreaterThan = fromY < toY;
        
        c2d.moveTo(fromX, fromY);
        
        double offsetX = fromX;
        double offsetY = fromY;
        int idx = 0;
        boolean dash = true;
        double ang = Math.atan2(toY - fromY, toX - fromX);
        double cosAng = Math.cos(ang);
        double sinAng = Math.sin(ang);
        
        while (!(isThereYet(xgreaterThan, offsetX, toX) && isThereYet(ygreaterThan, offsetY, toY))) {
            double len = pattern[idx];
            
            offsetX = cap(xgreaterThan, toX, offsetX + (cosAng * len));
            offsetY = cap(ygreaterThan, toY, offsetY + (sinAng * len));
            
            if (dash)
                c2d.lineTo(offsetX, offsetY);
            else
                c2d.moveTo(offsetX, offsetY);
            
            idx = (idx + 1) % pattern.length;
            dash = !dash;
        }
        c2d.closePath();
        c2d.stroke();
    }
    
    private Boolean isThereYet(Boolean greaterThan, double a, double b) {
        if (greaterThan)
            return a >= b;
        else
            return a <= b;
    }
    
    private double cap(Boolean greaterThan, double a, double b) {
        if (greaterThan)
            return Math.min(a, b);
        else
            return Math.max(a, b);
    }
    
    
}
