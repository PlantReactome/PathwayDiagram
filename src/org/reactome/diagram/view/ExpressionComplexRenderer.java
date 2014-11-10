/*
 * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CompositionalNode;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author weiserj
 *
 */
public class ExpressionComplexRenderer extends ComplexRenderer {
	private Double currentX;
    private Double currentY;
	private Double innerRectStartX;
	private Double innerRectEndX;
	private Double segmentHeight;
	private ExpressionSegmentRendererHelper segmentRendererHelper;
	
    public ExpressionComplexRenderer() {
        super();
    }

    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
    	segmentRendererHelper = new ExpressionSegmentRendererHelper();
    	final CompositionalNode currentComplex = (CompositionalNode) node;
    	
    	segmentRendererHelper.setComponentColors(currentComplex.getComponentColors());
    	if (segmentRendererHelper.getUniqueComponentColors().size() == 1) {
    		final String bgColor = segmentRendererHelper.getComponentColors().get(0); 
    		
    		currentComplex.setBgColor(bgColor);
    		currentComplex.setFgColor(currentComplex.getVisibleFgColor(bgColor));
    		
    		context.setFillStyle(bgColor);
    		super.drawRectangle(bounds, context, node);
    		return;
    	}
    	
    	context.setFillStyle(Parameters.defaultExpressionColor.value());
    	super.drawRectangle(bounds, context, node);
    	
    	setStroke(context, node);
        currentX = (double) bounds.getX();
        currentY = (double) (bounds.getY() + COMPLEX_RECT_ARC_WIDTH);
        innerRectStartX = currentX + COMPLEX_RECT_ARC_WIDTH;
        innerRectEndX = currentX + bounds.getWidth() - COMPLEX_RECT_ARC_WIDTH;
        
        Double segmentWidth = ((double) bounds.getWidth() / segmentRendererHelper.getComponentColors().size());
        segmentWidth = SegmentWidthAdjuster.getInstance().getVisibleWidth(bounds.getWidth(), segmentWidth, segmentRendererHelper.getNonWhiteComponentColors().size());
        segmentHeight = (double) (bounds.getHeight() - 2 * COMPLEX_RECT_ARC_WIDTH);
        
        for (Integer i = 0; i < segmentRendererHelper.getNonWhiteComponentColors().size(); i++) {
        	drawSegment(segmentWidth, segmentHeight, bounds.getHeight(), segmentRendererHelper.getNonWhiteComponentColors().get(i), context);
        }
        
        if (!node.isNeedDashedBorder())
        	drawRectangle(bounds, context, false);
    }
    
    protected void drawLine(int lineBreak, Context2d context2d, String dashLastPhrase, int x0 , int y0) {
    	// When complex has white background, draw line as a generic node does
    	if (segmentRendererHelper.getUniqueComponentColors().size() == 1 &&
    		segmentRendererHelper.getComponentColors().get(0).equals(Parameters.defaultExpressionColor.value())) {
    		super.drawLine(lineBreak, context2d, dashLastPhrase, x0, y0);
    		return;
    	}
   
    	segmentRendererHelper.drawLineWithBubbleLetters(lineBreak, context2d, dashLastPhrase, x0, y0);
    }
    
    private void drawSegment(Double width, Double height, Double maxSegmentHeight, String color, Context2d context) {
    	context.setFillStyle(color);
    	
    	Double segmentStart = currentX;
    	Double segmentEnd = currentX + width;
    	Double leftHeight = height;
    	Double rightHeight;
    	
    	if (segmentStart < innerRectStartX) {
    		rightHeight = Math.min(leftHeight + (2 * width), maxSegmentHeight);
    		
    		Double pastArc = (double) 0;
    		if (segmentEnd > innerRectStartX)
    			pastArc = segmentEnd - innerRectStartX;
    		
    		drawSegmentInArc(width, leftHeight, rightHeight, pastArc, true, context);
    	} else if (segmentEnd > innerRectEndX) {    		
    		Double pastArc = (double) 0;
    		if (segmentStart < innerRectEndX)
    			pastArc = innerRectEndX - segmentStart;
    		
    		rightHeight = leftHeight - (2 * (width - pastArc));
    		
    		drawSegmentInArc(width, leftHeight, rightHeight, pastArc, false, context);
    	} else {
    		drawRectangleSegment(width, height, context);
    	}  	
    
    	currentX += width;    	
    }
    
    private void drawSegmentInArc(Double width, Double leftHeight, Double rightHeight, Double pastArc, Boolean leftArc, Context2d context) {
    	context.beginPath();
    	
    	Double x = currentX;
    	Double y = currentY;
    	
    	context.moveTo(x, y);
    	
    	y = y + leftHeight; 
    	context.lineTo(x, y);
    	
    	Double arcSize = width - pastArc;
    	
    	if (leftArc) {
    		x += arcSize;
    		y += arcSize; 
    	
    		context.lineTo(x, y);
    	
    		x += pastArc;
    		context.lineTo(x, y);	
    	
    		y -= rightHeight;
    		context.lineTo(x, y);
    	
    		x -= pastArc;
    		context.lineTo(x, y);
    	} else {
    		x += pastArc;
    		context.lineTo(x, y);
    		
    		x += arcSize;
    		y -= arcSize;
    		context.lineTo(x, y);
    		    		
    		y -= rightHeight;
    		context.lineTo(x, y);
    		
    		x -= arcSize;
    		y -= arcSize;
    		context.lineTo(x, y);
    	}	
    	context.closePath();
    	
    	context.fill();  
    	
    	currentY = y;
    	segmentHeight = rightHeight;
    }
    
    private void drawRectangleSegment(Double width, Double height, Context2d context) {
    	context.fillRect(currentX, currentY, width, height);
    	//currentX += width;
    }
}
