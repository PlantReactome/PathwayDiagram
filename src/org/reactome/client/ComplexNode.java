/**
 * 
 * @author Maulik Kamdar
 * Google Summer of Code Project 
 * Canvas Based Pathway Visualization Tool
 * 
 * Parts of Code have been derived from the tutorials and samples provided at Google Web Toolkit Website 
 * http://code.google.com/webtoolkit/doc/latest/tutorial/
 * 
 * Ideas for canvas based initiation functions derived and modified from Google Canvas API Demo
 * http://code.google.com/p/gwtcanvasdemo/source/browse/
 * 
 * Interactivity ideas taken from HTML5 canvas tutorials
 * http://www.html5canvastutorials.com/
 * 
 * 
 */

package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Plots a rectangular Complex Node at the specified coordinates with the specific Node Height and Width
 *
 */

public class ComplexNode extends GraphNode{
	
	RenderableNode complexNode;
	
	public ComplexNode(String idno, String bounds, String Position, String bgColor) {
		// TODO Auto-generated constructor stub
		super(idno, bounds, Position, bgColor);
		complexNode = new RenderableNode(cenX, cenY, coX, coY, nodeWidth, nodeHeight, strokeColor, this.bgColor);
	}

	public void draw(Context2d context){
		complexNode.drawRectangle(context, radius);
	}
	
}
