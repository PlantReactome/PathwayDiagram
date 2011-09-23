/*
 * Created on Sep 22, 2011
 * Most of code here is copied from Maulik's original GSoC canvas prototype project.
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * This customized Renderer is use to render Protein. 
 * @author gwu
 *
 */
public class NodeRenderer extends AbstractRenderer<Node> {
    
    /* (non-Javadoc)
     * @see org.reactome.diagram.view.GraphObjectRenderer#render(com.google.gwt.canvas.dom.client.Context2d)
     */
    @Override
    public void render(Context2d c2d,
                       Node node) {
        setColors(c2d, node.getFgColor(), node.getBgColor());
        Bounds bounds = node.getBounds();
        drawRectangle(bounds, c2d);
    }
    
    public void drawRectangle(Bounds bounds,
                              Context2d context) {
        int coX = bounds.getX();
        int coY = bounds.getY();
        int radius = Parameters.radius;
        int nodeWidth = bounds.getWidth();
        int nodeHeight = bounds.getHeight();
        context.beginPath();
        context.moveTo(coX+radius, coY);
        context.lineTo(coX+nodeWidth-radius, coY);
        context.quadraticCurveTo(coX+nodeWidth, coY, coX+nodeWidth, coY+radius);
        context.lineTo(coX+nodeWidth, coY+nodeHeight-radius);
        context.quadraticCurveTo(coX+nodeWidth, coY+nodeHeight, coX+nodeWidth-radius, coY+nodeHeight);
        context.lineTo(coX+radius, coY+nodeHeight);
        context.quadraticCurveTo(coX, coY+nodeHeight, coX, coY+nodeHeight-radius);
        context.lineTo(coX, coY+radius);
        context.quadraticCurveTo(coX, coY, coX+radius, coY);
        context.fill();
        context.stroke();
        context.closePath();
    }
    
}
