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
import org.reactome.diagram.client.Vector;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;

/**Renders the Node Attachments on the Canvas depending on the Relative Positions with respect to the particular Node
 * 
 */
public class NodeAttachment extends Node{

	static String bgColor = "255 255 255";
	double relativeX;
	double relativeY;
	
	/**Constructor to specify the Node Attachment attributes
	 * 
	 * @param idno Id Number of the Node
	 * @param Bounds The Bounds of the Node
	 * @param Position The Position of the Node
	 * @param relativeX The relative position compared to the X coordinate of the Node
	 * @param relativeY The relative position compared to the Y coordinate of the Node
	 */
	public NodeAttachment(String idno, String Bounds, String Position, String relativeX, String relativeY) {
		super(idno, Bounds, Position, bgColor);
		this.relativeX = Double.parseDouble(relativeX);
		this.relativeY = Double.parseDouble(relativeY);
		reactWidth = Parameters.Reactwidth/zoomFactor;
		reactHeight = Parameters.Reactheight/zoomFactor;		
	}
	
	/**Calculates the exact position of the Node Attachment from the Relative Values and renders it on the Canvas
	 * 
	 * @param context The Context2d Object to render the Node Attachment
	 * @param label The Label of the Node Attachment
	 */
	public void updateNodeAttachment(Context2d context, String label) {
		double requiredX = coX + (nodeWidth * relativeX) - (reactWidth/2);
		double requiredY = coY + (nodeHeight * relativeY) - (reactHeight/2);
		Vector startPosition = new Vector(requiredX,requiredY);
		RenderableNode attachmentNode = new RenderableNode(startPosition, reactWidth, reactHeight, defaultbgColor, defaultstrokeColor);
		attachmentNode.drawAttachmentRectangle(context, label);
	}
}
