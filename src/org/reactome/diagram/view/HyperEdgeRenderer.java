/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.List;

import org.reactome.diagram.model.HyperEdge;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.touch.client.Point;

/**
 * @author gwu
 *
 */
public class HyperEdgeRenderer extends AbstractRenderer<HyperEdge> {
    
    public void render(Context2d c2d,
                       HyperEdge edge) {
        // Set up drawing environment
        String fgColor = edge.getLineColor();
        if (fgColor == null)
            fgColor = "rgba(0, 0 , 0, 1)";
        CssColor drawColor = CssColor.make(fgColor);
        double width = edge.getLineWidth();
        c2d.setFillStyle(drawColor);
        c2d.setStrokeStyle(drawColor);
        // Draw backbone
        List<Point> points = edge.getBackbone();
        drawLines(c2d, points);
        // Draw inputs
        drawBranches(c2d, edge.getInputBranches());
        // Draw outputs
        drawBranches(c2d, edge.getOutputBranches());
        // Draw catalysts
        drawBranches(c2d, edge.getCatalystBranches());
        // Draw inhibitors
        drawBranches(c2d, edge.getInhibitorBranches());
        // Draw activators
        drawBranches(c2d, edge.getActivatorBranches());
        // Draw a little nodes for reactions
        drawReactionNode(c2d, edge);
    }
    
    private void drawReactionNode(Context2d context, HyperEdge edge) {
        context.setFillStyle(CssColor.make("rgba(255, 255, 255, 1)"));
        Point position = edge.getPosition();
        context.beginPath();
        context.rect(position.getX() - Parameters.Reactwidth / 2, 
                     position.getY() - Parameters.Reactheight / 2,
                     Parameters.Reactwidth,
                     Parameters.Reactheight);
        context.fill(); // Need a white empty rectangle
        context.stroke();
    }
    
    private void drawBranches(Context2d c2d, List<List<Point>> branches) {
        if (branches == null || branches.size() == 0)
            return;
        for (List<Point> points : branches) {
            drawLines(c2d, points);
        }
    }
    
    private void drawLines(Context2d c2d, List<Point> points) {
        c2d.beginPath();
        Point point = points.get(0);
        c2d.moveTo(point.getX(), point.getY());
        for (int i = 1; i < points.size(); i++) {
            point = points.get(i);
            c2d.lineTo(point.getX(), point.getY());
        }
        c2d.stroke();
    }
    
}
