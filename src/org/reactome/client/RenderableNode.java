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

/** Renders the various kinds of Nodes and the Node Attachments
 * 
 */
public class RenderableNode {

	double cenX;
	double cenY;
	double coX;
	double coY;
	double nodeWidth;
	double nodeHeight;
	String strokeColor;
	String bgColor;
	double zoomFactor;
	double upHeight;
	
	/**Constructor to render the Nodes using the coordinates
	 * 
	 * @param cenX X coordinate of the center point
	 * @param cenY Y coordinate of the center point
	 * @param coX X coordinate of the start point
	 * @param coY Y coordinate of the start point
	 * @param nodeWidth The Width of the Node
	 * @param nodeHeight The Height of the Node
	 * @param strokeColor The Stroke Color of the Node
	 * @param bgColor The Fill Color of the Node
	 */
	
	public RenderableNode(double cenX, double cenY, double coX, double coY, double nodeWidth, double nodeHeight, String strokeColor, String bgColor) {
		this.cenX = cenX;
		this.cenY = cenY;
		this.coX = coX;
		this.coY = coY;
		this.nodeWidth = nodeWidth;
		this.nodeHeight = nodeHeight;
		this.strokeColor = strokeColor;
		this.bgColor = bgColor;
	}
	
	/**Constructor to render the Node Attachments
	 * 
	 * @param startPosition The Start position Vector of the Node Attachment
	 * @param reactWidth The Width of the Node Attachment
	 * @param reactHeight The Height of the Node Attachment
	 * @param defaultbgColor The Fill Color of the Node Attachment
	 * @param defaultstrokeColor The Stroke Color of the Node Attachment
	 */
	
	public RenderableNode(Vector startPosition, double reactWidth,
			double reactHeight, String defaultbgColor, String defaultstrokeColor) {
		this.cenX = startPosition.x;
		this.cenY = startPosition.y;
		this.nodeWidth = reactWidth;
		this.nodeHeight = reactHeight;
		this.bgColor = defaultbgColor;
		this.strokeColor = defaultstrokeColor;
		this.zoomFactor = Parameters.ZoomFactor;
		this.upHeight = Parameters.UpHeight;
	}
	
	/**Renders the Elliptical Nodes (Chemical Nodes)
	 * 
	 * @param context The Context2d object to render the Elliptical Node
	 */
	public void drawEllipse(Context2d context){
		ContextSettings colors = new ContextSettings(bgColor,strokeColor);
		colors.makecolor(context);
		
        context.beginPath();
		context.moveTo(cenX, cenY - nodeHeight/2); // A1
		context.bezierCurveTo(
		    cenX + nodeWidth/2, cenY - nodeHeight/2, // C1
		    cenX + nodeWidth/2, cenY + nodeHeight/2, // C2
		    cenX, cenY + nodeHeight/2); // A2
		context.bezierCurveTo(
		    cenX - nodeWidth/2, cenY + nodeHeight/2, // C3
		    cenX - nodeWidth/2, cenY - nodeHeight/2, // C4
		    cenX, cenY - nodeHeight/2); // A1
		 context.fill();
		 context.stroke();
		 context.closePath();	
	}
	
	/**Renders a Rounded Rectangular Node (Complex Node, Protein Node)
	 * 
	 * @param context The Context2d object to render the Rounded Rectangular Node
	 * @param radius The Radius of the Rounded Rectangle
	 */
	
	public void drawRectangle(Context2d context, double radius){
		ContextSettings colors = new ContextSettings(bgColor,strokeColor);
		colors.makecolor(context);
        
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
	
	/** Renders the Node Attachments for any particular Node
	 * 
	 * @param context The Context2d Object to render the Node Attachment
	 * @param label The Label of the Node Attachment
	 */
	public void drawAttachmentRectangle(Context2d context, String label){
		String nodeColor = "rgba(255,255,255,1)";
		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
		colors.makecolor(context);
		
		context.beginPath();
		context.rect(cenX, cenY, nodeWidth, nodeHeight);
	    context.fillRect(cenX, cenY, nodeWidth, nodeHeight);
	    context.strokeRect(cenX, cenY, nodeWidth, nodeHeight); 
		context.closePath();
		double cordX = cenX * zoomFactor;
		double cordY = cenY * zoomFactor - upHeight;
		double centerX = cordX + (nodeWidth/2);
		double centerY = cordY + (nodeHeight/2);
		String bounds = cordX + " " + cordY + " " + nodeWidth + " " + nodeHeight;
		String position = centerX + " " + centerY;
		String idno = "0";
		
		int fontSize = 8;
		Annotations Label = new Annotations(idno,bounds,position,fontSize,label);
		Label.writeText(context);
	} 
}
