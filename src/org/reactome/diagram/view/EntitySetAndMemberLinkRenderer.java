/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.view;

import java.util.List;

import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;

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
        if (!shouldRender(edge))
            return;
        setLineColor(c2d, edge);
        setStroke(c2d, edge);
        List<Point> points = edge.getBackbone();
        for (int i = 0; i < points.size() - 1; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);
            drawDashedLine(c2d, start, end, Parameters.dashedLinePattern);
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
    
    private boolean shouldRender(HyperEdge edge) {
        List<Node> nodes = edge.getConnectedNodes();
        if (nodes == null || nodes.size() < 2) // If there is only one or nothing linked to it, don't render it.
            return false;
        return true;
    }
    
}
