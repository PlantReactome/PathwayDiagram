/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.List;

import org.reactome.diagram.model.HyperEdge;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.touch.client.Point;

/**
 * A dashed line needs to be drawn for EntitySetAndMemberLink. However, dash-line drawing is not supported
 * in GWT API yet. The following code related to this draw is adapted from this web page: 
 * http://www.navwin.com/Topics/UsingGWT_WithCanvas/UsingGWT_WithCanvas.aspx.
 * @author gwu
 *
 */
public class EntitySetAndMemberLinkRenderer extends HyperEdgeRenderer {
    
    public EntitySetAndMemberLinkRenderer() {
    }
    
    @Override
    public void render(Context2d c2d, 
                       HyperEdge edge) {
        setLineColor(c2d, edge);
        setStroke(c2d, edge);
        List<Point> points = edge.getBackbone();
        double[] pattern = new double[]{5.0d, 5.0d};
        for (int i = 0; i < points.size() - 1; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);
            drawDashedLine(c2d, start, end, pattern);
        }
        drawCircle(c2d, points);
    }
    
    private void drawCircle(Context2d c2d, List<Point> points) {
        c2d.beginPath();
        Point last = points.get(points.size() - 1);
        c2d.arc(last.getX(),
                last.getY(),
                EDGE_MODULATION_WIDGET_WIDTH / 3.0d, // This is radiant, not diameter
                0.0d,
                2.0d * Math.PI);
        c2d.closePath();
        c2d.fill();
    }
    
    private void drawDashedLine(Context2d c2d, 
                                Point from, 
                                Point end, 
                                double[] pattern) {
        c2d.beginPath();
        
        double fromX = from.getX();
        double fromY = from.getY();
        double toX = end.getX();
        double toY = end.getY();
        // Used to check if the drawing is done yet
        boolean xgreaterThan = fromX > toX;
        boolean ygreaterThan = fromY > toY;
        
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
