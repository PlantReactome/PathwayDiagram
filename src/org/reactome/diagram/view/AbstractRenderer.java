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
    // This will be used for overview drawing: this lineWidth should be used without considering
    // the GraphObject one
    private Double absoluteLineWidth;
    
    
    public void setAbsoluteLineWidth(Double lineWidth) {
        this.absoluteLineWidth = lineWidth;
    }
    
    public Double getAbsoluteLineWidth() {
        return this.absoluteLineWidth;
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

    protected void drawArrow(Context2d c2d, 
                             Point controlPoint, 
                             Point position,
                             boolean isOpen) {
        // The the angle of the line segment
        double alpha = Math.atan((double)(position.getY() - controlPoint.getY()) / (position.getX() - controlPoint.getX()));
        if (controlPoint.getX() > position.getX())
            alpha += Math.PI;
        double angle = ARROW_ANGLE - alpha;
        float x1 = (float)(position.getX() - ARROW_LENGTH * Math.cos(angle));
        float y1 = (float)(position.getY() + ARROW_LENGTH * Math.sin(angle));
        c2d.beginPath(); // Have to call this begin path. Otherwise, the following path will be mixed with other unknown drawings.
        c2d.moveTo(x1, y1);
        c2d.lineTo(position.getX(), position.getY());
        angle = ARROW_ANGLE + alpha;
        float x2 = (float)(position.getX() - ARROW_LENGTH * Math.cos(angle));
        float y2 = (float)(position.getY() - ARROW_LENGTH * Math.sin(angle));
        c2d.lineTo(x2, y2);
        c2d.closePath();
        if (isOpen)
            c2d.setFillStyle(CssColor.make("rgba(255, 255, 255, 1)"));
        c2d.fill();
        c2d.stroke();
    }
    
    
}
