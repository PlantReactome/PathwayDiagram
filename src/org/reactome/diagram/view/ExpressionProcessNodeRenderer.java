/*
 * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.client.ExpressionCanvas.ProcessNodeExpression;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author weiserj
 *
 */
public class ExpressionProcessNodeRenderer extends NodeRenderer {
	private ProcessNodeExpression processNodeExpression;
    
    public ExpressionProcessNodeRenderer() {
        super();
    }
    
    public void setProcessNodeExpression(ProcessNodeExpression processNodeExpression) {
    	this.processNodeExpression = processNodeExpression;
    }

    @Override
    protected void drawRectangle(Bounds bounds,
    							 Context2d context,
                                 Node node) {    	
    	
    	drawSegment(bounds, Parameters.defaultExpressionColor.value(), context);
		if (processNodeExpression != null && processNodeExpression.getColor() != null) {
            Bounds segmentBounds = new Bounds(bounds);
            segmentBounds.setWidth((int) (bounds.getWidth() * ((double) processNodeExpression.getFound() / processNodeExpression.getTotal())));
    		drawSegment(segmentBounds, processNodeExpression.getColor(), context);
    	
        }
        
        context.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    private void drawSegment(Bounds bounds, String color, Context2d context) {
    	context.setFillStyle(color);
    	context.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
}
