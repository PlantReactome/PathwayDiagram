/*
 * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;

/**
 * @author weiserj
 *
 */
public class ExpressionSegmentRendererHelper {
	private List<String> componentColors;
	
    public ExpressionSegmentRendererHelper() {
    }
    
    public void drawLineWithBubbleLetters(int lineBreak, Context2d context2d, String dashLastPhrase, int x0 , int y0) {
    	double wordX = x0;
    	double wordY = y0 + lineBreak * Parameters.LINE_HEIGHT;
    	double measure = context2d.measureText(dashLastPhrase).getWidth();
    	
    	final FillStrokeStyle oldFillStyle = context2d.getFillStyle();
    	final FillStrokeStyle oldStrokeStyle = context2d.getStrokeStyle();
    	final double lineWidth = context2d.getLineWidth();
    	
    	context2d.setFillStyle("rgb(255,255,255)"); // White fill 
    	context2d.setStrokeStyle("rgb(0, 0, 0)"); // Black stroke
    	context2d.setLineWidth(2.5);
    	
    	context2d.strokeText(dashLastPhrase, wordX, wordY, measure);
    	context2d.fillText(dashLastPhrase, wordX, wordY, measure);
     	
    	context2d.setFillStyle(oldFillStyle);
    	context2d.setStrokeStyle(oldStrokeStyle);
    	context2d.setLineWidth(lineWidth);
    }
    
    public List<String> getComponentColors() {
    	return componentColors;
    }
    
    public void setComponentColors(List<String> componentColors) {
		this.componentColors = componentColors;
	}

	public List<String> getNonWhiteComponentColors() {
    	List<String> nonWhiteComponentColors = new ArrayList<String>();
    	
    	for (String componentColor : getComponentColors()) {
    		if (componentColor.equals(Parameters.defaultExpressionColor.value()))
    			continue;
    		nonWhiteComponentColors.add(componentColor);
    	}
    	
    	return nonWhiteComponentColors;
    }
    
    public Set<String> getUniqueComponentColors() {
    	return new HashSet<String>(getComponentColors());
    }
}
