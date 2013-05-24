/*
 * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.ComplexNode;
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
	
    public ExpressionComplexRenderer() {
        super();
    }

    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
    	List<String> componentColors = ((ComplexNode) node).getComponentColors();
     	
    	Set<String> uniqueColors = new HashSet<String>(componentColors);
    	if (uniqueColors.size() == 1) {
    		context.setFillStyle(componentColors.get(0));
    		super.drawRectangle(bounds, context, node);
    		return;
    	}
    	
    	setStroke(context, node);
        currentX = (double) bounds.getX();
        currentY = (double) (bounds.getY() + COMPLEX_RECT_ARC_WIDTH);
        innerRectStartX = currentX + COMPLEX_RECT_ARC_WIDTH;
        innerRectEndX = currentX + bounds.getWidth() - COMPLEX_RECT_ARC_WIDTH;
        
        Double segmentWidth = ((double) bounds.getWidth() / componentColors.size());
        segmentHeight = (double) (bounds.getHeight() - 2 * COMPLEX_RECT_ARC_WIDTH);
        
        for (Integer i = 0; i < componentColors.size(); i++) {
        	drawSegment(segmentWidth, segmentHeight, bounds.getHeight(), componentColors.get(i), context);        
        }
        
        createPath(bounds, context);
        context.stroke();
    }
    
    private void drawSegment(Double width, Double height, Integer maxSegmentHeight, String color, Context2d context) {
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
    		rightHeight = leftHeight - (2 * Math.min(width, COMPLEX_RECT_ARC_WIDTH));
    		
    		Double pastArc = (double) 0;
    		if (segmentStart < innerRectEndX)
    			pastArc = innerRectEndX - segmentStart;
    		
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
