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

package org.reactome.diagram.model;

import org.reactome.diagram.client.RenderableNode;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Plots a rectangular Compartment at the specified coordinates with the specific Compartment Height and Width
 *
 */

public class Compartment extends Node{
	
	public Compartment(String idno, String bounds, String Position, String bgColor) {
		super(idno, bounds, Position, bgColor);
	}


	public void draw(Context2d context) {
		RenderableNode compartment = new RenderableNode(cenX, cenY, coX, coY, nodeWidth, nodeHeight, strokeColor, bgColor);
		compartment.drawRectangle(context, radius);
	}

}
