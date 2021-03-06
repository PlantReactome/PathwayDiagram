/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.ConnectWidget;
import org.reactome.diagram.model.ConnectWidget.ConnectRole;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.ReactionType;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.touch.client.Point;

/**
 * This Renderer is used to draw HyperEdge objects.
 * @TODO: Need to rotate some widgets!
 * @author gwu
 *
 */
//TODO: Add a renderer for EntitySetAndMemberLink. Currently it uses the default HyperEdgeRenderer.
public class HyperEdgeRenderer extends AbstractRenderer<HyperEdge> {
    
    public void render(Context2d c2d,
                       HyperEdge edge) {
        setLineColor(c2d, edge);
        setStroke(c2d, edge);
//        c2d.setStrokeStyle(drawColor);
        // Draw backbone
        List<Point> points = edge.getBackbone();
        drawLines(c2d, points);
        // Draw inputs
        drawBranches(c2d, edge.getInputBranches(), ConnectRole.INPUT, edge);
        // Draw outputs
        drawBranches(c2d, edge.getOutputBranches(), ConnectRole.OUTPUT, edge);
        // Draw catalysts
        drawBranches(c2d, edge.getCatalystBranches(), ConnectRole.CATALYST, edge);
        // Draw inhibitors
        drawBranches(c2d, edge.getInhibitorBranches(), ConnectRole.INHIBITOR, edge);
        // Draw activators
        drawBranches(c2d, edge.getActivatorBranches(), ConnectRole.ACTIVATOR, edge);
        // Draw a little nodes for reactions
        drawReactionNode(c2d, edge);
        // Draw arrows
        drawArrows(c2d, edge);
        // Draw stoichiometries
        drawStoichiometries(c2d, edge);
    }

    protected void setLineColor(Context2d c2d, 
                                HyperEdge edge) {
        // Set up drawing environment
        String fgColor = edge.getLineColor();
        if (fgColor == null)
            fgColor = "rgba(0, 0 , 0, 1)";
        CssColor drawColor = CssColor.make(fgColor);
        double width = edge.getLineWidth();
        c2d.setLineWidth(width);
        c2d.setFillStyle(drawColor);
    }
    
    private void drawStoichiometries(Context2d context, HyperEdge edge) {
        List<ConnectWidget> widgets = edge.getConnectWidgets();
        if (widgets == null)
            return;
        for (ConnectWidget widget : widgets) {
            int stoi = widget.getStoichiometry();
            if (stoi == 0 || stoi == 1)
                continue; // No need to draw
            drawStoichiometry(widget.getPoint(),
                              widget.getControlPoint(),
                              edge,
                              stoi,
                              context);
        }
    }
    
    private void drawStoichiometry(Point position,
                                   Point controlPoint,
                                   HyperEdge edge,
                                   int value,
                                   Context2d context) {
        // Get the box size
        String text = value + "";
        context.setFont(WIDGET_FONT);
        context.setTextAlign(TextAlign.CENTER);
        context.setTextBaseline(TextBaseline.MIDDLE);
        TextMetrics size = context.measureText(text);
        double width = size.getWidth();
        if (width + 4 < EDGE_TYPE_WIDGET_WIDTH)
            width = EDGE_TYPE_WIDGET_WIDTH;
        else
            width += 4;
        // Draw a box first
        context.setFillStyle(CssColor.make(255, 255, 255));
        double x0 = (position.getX() + controlPoint.getX()) / 2.0d;
        double y0 = (position.getY() + controlPoint.getY()) / 2.0d;
        context.beginPath();
        context.rect(x0 - width / 2.0d,
                     y0 - EDGE_TYPE_WIDGET_WIDTH / 2.0d, 
                     width, 
                     EDGE_TYPE_WIDGET_WIDTH);
        context.closePath();
        context.fill();
        context.stroke();
        // Draw text
        // Need to use stroke for text
        context.setFillStyle(context.getStrokeStyle());
        context.fillText(text, x0, y0);
    }
    
    protected void drawReactionNode(Context2d context, HyperEdge edge) {
        // This case has been covered by RenderableInteraction
        // since InteractorEdge is a RenderableInteraction.
//        if (edge instanceof InteractorEdge)
//        	return;
        
        GraphObjectType edgeType = edge.getType();
        if (edgeType == GraphObjectType.FlowLine)
            return; // Don't draw any nodes for these two types of edges
        	
        ReactionType reactionType = edge.getReactionType();	
        	
        if (reactionType == ReactionType.ASSOCIATION ||
            reactionType == ReactionType.DISSOCIATION) {
            drawAssociationType(context, edge, reactionType);
            return;
        }
        context.setFillStyle(CssColor.make("rgba(255, 255, 255, 1)"));
        Point position = edge.getPosition();
        double x = position.getX();
        double y = position.getY();
        context.beginPath();
        context.rect(x - EDGE_TYPE_WIDGET_WIDTH / 2, 
                     y - EDGE_TYPE_WIDGET_WIDTH / 2,
                     EDGE_TYPE_WIDGET_WIDTH,
                     EDGE_TYPE_WIDGET_WIDTH);
        context.closePath(); // Make sure path has been closed
        context.fill(); // Need a white empty rectangle
        context.stroke();
        if (reactionType == ReactionType.OMITTED_PROCESS) {
            int pad = 3;
            x -= EDGE_TYPE_WIDGET_WIDTH / 2;
            y -= EDGE_TYPE_WIDGET_WIDTH / 2;
            double x1 = x + pad;
            double y1 = y + pad;
            double x2 = x + EDGE_TYPE_WIDGET_WIDTH / 2;
            double y2 = y + EDGE_TYPE_WIDGET_WIDTH - pad;
            context.beginPath();
            context.moveTo(x1, y1);
            context.lineTo(x2, y2);
            x1 = x + EDGE_TYPE_WIDGET_WIDTH / 2;
            x2 = x + EDGE_TYPE_WIDGET_WIDTH - pad;
            context.moveTo(x1, y1);
            context.lineTo(x2, y2);
            context.closePath();
            context.stroke();
        }
        else if (reactionType == ReactionType.UNCERTAIN_PROCESS) {
            // Draw a question mark
            context.setFont(WIDGET_FONT);
            context.setFillStyle(CssColor.make(0, 0, 0));
            context.fillText("?", x, y);
        }
    }
    
    private void drawAssociationType(Context2d context, 
                                     HyperEdge edge, 
                                     ReactionType reactionType) {
        String fillColor = null;
        if (reactionType == ReactionType.ASSOCIATION) {
            String associationFillColor = edge.getLineColor();
            
            if (associationFillColor != null)
            	fillColor = associationFillColor;
            else	
            	fillColor = "rgba(0, 0, 0, 1)"; // Black
        } else  
            fillColor = "rgba(255, 255, 255, 1)"; // White
        
            context.setFillStyle(CssColor.make(fillColor));
        
        context.beginPath();
        Point position = edge.getPosition();
        context.arc(position.getX(),
                    position.getY(), 
                    EDGE_TYPE_WIDGET_WIDTH / 2.0d, 
                    0.0d, 
                    Math.PI * 2.0d);
        context.closePath();
        context.fill();
        if (reactionType == ReactionType.DISSOCIATION) {
            // Draw two circles
            context.stroke();
            context.beginPath();
            context.arc(position.getX(),
                        position.getY(), 
                        EDGE_TYPE_WIDGET_WIDTH / 2.0d - 2.0d, 
                        0.0d, 
                        Math.PI * 2.0d);
            context.closePath();
            context.stroke();
        }
    }
    
    private void drawBranches(Context2d c2d, 
                              List<List<Point>> branches,
                              ConnectRole connectRole,
                              HyperEdge edge) {
        if (branches == null || branches.size() == 0)
            return;
        List<Point> tmpPoints = new ArrayList<Point>();
        Point controlPoint = getLastPointForBranch(edge,
                                                   connectRole);
        for (List<Point> points : branches) {
            tmpPoints.clear();
            tmpPoints.addAll(points);
            if (connectRole == ConnectRole.INPUT || connectRole == ConnectRole.OUTPUT) {
                tmpPoints.add(controlPoint);
                drawLines(c2d, tmpPoints);
            }
            else {
                Point anchor = anchorPositionInBranch(points, 
                                                      controlPoint, 
                                                      getDistanceForAnchor(connectRole));
                tmpPoints.add(anchor);
                drawLines(c2d, tmpPoints);
                drawHelperSymbols(c2d, 
                                  points, 
                                  anchor,
                                  connectRole);
            }
        }
    }
    
    private double getDistanceForAnchor(ConnectRole connectRole) {
        if (connectRole == ConnectRole.CATALYST)
            return (EDGE_TYPE_WIDGET_WIDTH + EDGE_MODULATION_WIDGET_WIDTH) * 0.6;
        else
            return EDGE_TYPE_WIDGET_WIDTH * 0.75;
    }
    
    private void drawHelperSymbols(Context2d context, 
                                   List<Point> branch,
                                   Point anchor,
                                   ConnectRole role) {
        context.setFillStyle("rgba(255, 255, 255, 1)");
        if (role == ConnectRole.CATALYST) {
            context.beginPath();
            context.arc(anchor.getX(), 
                        anchor.getY(), 
                        EDGE_MODULATION_WIDGET_WIDTH / 2.0d, 
                        0.0d,
                        2.0d * Math.PI);
            context.closePath();
            context.fill();
            context.stroke();
        }
        else if (role == ConnectRole.INHIBITOR) {
        	Point controlPoint = branch.get(branch.size() - 1);
        	double deltaY = anchor.getY() - controlPoint.getY();
        	double deltaX = controlPoint.getX() - anchor.getX();
        	
        	double angle = deltaY == 0 ? Math.PI / 2 : Math.atan(-deltaX/deltaY);
        	double x1 = anchor.getX() - (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        	double x2 = anchor.getX() + (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        	double y1 = anchor.getY() + (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        	double y2 = anchor.getY() - (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        	
        	context.beginPath();
        	context.moveTo(x1, y1);
        	context.lineTo(x2, y2);
        	context.closePath();
        	context.stroke();
        }
        else if (role == ConnectRole.ACTIVATOR) { // Draw an open arrow
            Point controlPoint = branch.get(branch.size() - 1);
            drawArrow(context, controlPoint, anchor, true);
        }
    }
    
    private Point anchorPositionInBranch(List<Point> branch,
                                         Point p1,
                                         double dist) {
        Point p2 = (Point) branch.get(branch.size() - 1);
        // Remember: the y axis is contrary to the ordinary coordinate system
        double tan = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        double theta = Math.atan(tan);
        if (p2.getX() - p1.getX() < 0)
            theta +=  Math.PI;
        double x = p1.getX() + dist * Math.cos(theta);
        double y = p1.getY() + dist * Math.sin(theta);
        return new Point(x, y);
    }
                                   
    protected void drawLines(Context2d c2d, List<Point> points) {
        c2d.beginPath();
        Point point = points.get(0);
        c2d.moveTo(point.getX(), point.getY());
        for (int i = 1; i < points.size(); i++) {
            point = points.get(i);
            c2d.lineTo(point.getX(), point.getY());
        }
        c2d.stroke();
    }
    
    private void drawArrows(Context2d c2d, HyperEdge edge) {
        String color = edge.getLineColor();
        if (color == null)
            color = "rgba(0, 0, 0, 1)";
        // Keep it to reset it to the original style is always a good practice
        FillStrokeStyle oldStyle = c2d.getFillStyle();
        c2d.setFillStyle(CssColor.make(color));
        if (edge.getOutputBranches() == null || edge.getOutputBranches().size() == 0) {
            // Draw edge for output in the backbone
            List<Point> backbone = edge.getBackbone();
            Point position = backbone.get(backbone.size() - 1);
            Point controlPoint = backbone.get(backbone.size() - 2);
            drawArrow(c2d, controlPoint, position, false);
        }
        else {
            // Used for single point branch
            List<Point> backbone = edge.getBackbone();
            Point hub = backbone.get(backbone.size() - 1);
            for (List<Point> branch : edge.getOutputBranches()) {
                if (branch.size() == 1) {
                    drawArrow(c2d, hub, branch.get(0), false);
                }
                else
                    drawArrow(c2d, branch.get(1), branch.get(0), false);
            }
        }
        c2d.setFillStyle(oldStyle);
    }
    
    private Point getLastPointForBranch(HyperEdge edge,
                                        ConnectRole role) {
        List<Point> backbone = edge.getBackbone();
        if (role == ConnectRole.INPUT)
            return backbone.get(0);
        else if (role == ConnectRole.OUTPUT)
            return backbone.get(backbone.size() - 1);
        else
            return edge.getPosition();
    }
    
}
