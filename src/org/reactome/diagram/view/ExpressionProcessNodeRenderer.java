/*
 * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * @author weiserj
 *
 */
public class ExpressionProcessNodeRenderer extends NodeRenderer {
    private Double currentX;
    private Double currentY;
	private List<String> colorList;
	
    public ExpressionProcessNodeRenderer() {
        super();
    }

    @Override
    protected void drawRectangle(Bounds bounds,
    							 Context2d context,
                                 Node node) {    	
    	
    	if (colorList == null || colorList.isEmpty()) {
    		colorList = new ArrayList<String>();
    		colorList.add(node.getBgColor());
    	}
    	
        currentX = (double) bounds.getX();
        currentY = (double) bounds.getY();                
        Double segmentWidth = ((double) bounds.getWidth() / colorList.size());
        Integer nodeHeight = bounds.getHeight();
        
        for (Integer i = 0; i < colorList.size(); i++) {
        	drawSegment(segmentWidth, nodeHeight, colorList.get(i), context);        
        	currentX += segmentWidth;
        }
        
        context.strokeRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    private void drawSegment(Double width, Integer height, String color, Context2d context) {
    	context.setFillStyle(color);
    	context.fillRect(currentX, currentY, width, height);
    }
    
    public void setColorList(List<String> colorList) {
    	this.colorList = colorList;
    }
}
