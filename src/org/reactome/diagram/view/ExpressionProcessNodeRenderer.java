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
            Double segmentWidth = (bounds.getWidth() * ((double) processNodeExpression.getFound() / processNodeExpression.getTotal()));
    		segmentWidth = SegmentWidthAdjuster.getInstance().getVisibleWidth(bounds.getWidth(), segmentWidth, 1);
            
    		Bounds segmentBounds = new Bounds(bounds);
    		segmentBounds.setWidth(segmentWidth.intValue());
            drawSegment(segmentBounds, processNodeExpression.getColor(), context);
    	}
        
        context.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    private void drawSegment(Bounds bounds, String color, Context2d context) {
    	context.setFillStyle(color);
    	context.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    @Override
    protected void drawLine(int lineBreak, Context2d context2d, String dashLastPhrase, int x0, int y0) {
    	new ExpressionSegmentRendererHelper().drawLineWithBubbleLetters(lineBreak, context2d, dashLastPhrase, x0, y0);
    }
}
