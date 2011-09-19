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
 * Renders the edges & the connector nodes.
 */

public class RenderableEdge {

	double inCoX;
	double inCoY;
	double finCoX;
	double finCoY;
	double cenX;
	double cenY;
	String strokeColor;
	String bgColor;
	double zoomFactor;
	double upHeight;
	
	double pointX1, pointX2, pointY1, pointY2;
	
	/**Constructor to Render the edge between two points
	 * 
	 * @param inVector Coordinates of the first point
	 * @param finVector Coordinates of the second point
	 * @param strokeColor Stroke Color of the edge 
	 * @param bgColor Fill Color of the edge
	 * @param position position of the Edge
	 */
	
	public RenderableEdge(Vector inVector, Vector finVector, String strokeColor, String bgColor, Vector position) {
		// TODO Auto-generated constructor stub
		try {
			this.inCoX = inVector.x;
			this.inCoY = inVector.y;
			this.finCoX = finVector.x;
			this.finCoY = finVector.y;
			this.strokeColor = strokeColor;
			this.cenX = position.x;
			this.cenY = position.y;
			bgColor = this.bgColor;
			this.zoomFactor = Parameters.ZoomFactor;
			this.upHeight = Parameters.UpHeight;
			
		} catch (NullPointerException nullpointer) {
			
		}
	}

	/**Constructor to Render the Catalyst Node Connector, Reaction Node, Inhibitor Node Connector
	 * 
	 * @param strokeColor Stroke Color of the Connector
	 * @param bgColor Fill Color of the Node Connector
	 * @param position position of the Connector
	 */
	
	public RenderableEdge(String strokeColor, String bgColor, Vector position) {
		
	}
	
	/**Constructor to Render the Output Arrow & the Activator Arrow
	 * 
	 * @param inVector The Tip of the Arrow, connecting to the Output Node or the Activator Node
	 * @param point1 Coordinates of the first point
	 * @param point2 Coordinates of the second point
	 * @param strokeColor Stroke Color of the Arrow
	 * @param bgColor Fill Color of the Arrow
	 */
	
	public RenderableEdge(Vector inVector, Vector point1, Vector point2, String strokeColor, String bgColor) {
		this.inCoX = inVector.x;
		this.inCoY = inVector.y;
		this.pointX1 = point1.x;
		this.pointY1 = point1.y;
		this.pointX2 =  point2.x;
		this.pointY2 = point2.y;
		bgColor = this.bgColor;
		this.strokeColor = strokeColor;
	}
	
	/**Renders the Reaction Edge between two points.
	 * 
	 * @param context The Context2d object to render the Edge.
	 */
	public void drawEdge(Context2d context) {
		ContextSettings colors = new ContextSettings(bgColor,strokeColor);
		colors.makecolor(context);
		
		context.beginPath();
    	context.moveTo(inCoX, inCoY);
		context.lineTo(finCoX, finCoY);
		context.stroke();
		context.closePath();
	}
	
	/**Renders the Reaction Node for each Renderable Reaction Edge.
	 * 
	 * @param context The Context2d object to render the Reaction Node.
	 * @param nodeWidth Width of the Reaction Node
	 * @param nodeHeight Height of the Reaction Node
	 */
	
	public void drawReactionNode(Context2d context, double nodeWidth, double nodeHeight) {
		String nodeColor = "rgba(255,255,255,1)";
		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
		colors.makecolor(context);
		
		context.beginPath();
		context.rect(cenX, cenY, nodeWidth, nodeHeight);
	    context.fillRect(cenX, cenY, nodeWidth, nodeHeight);
	    context.strokeRect(cenX, cenY, nodeWidth, nodeHeight); 
		context.closePath();
	}
	
	/** Renders the stoichiometry Node for each Edge, if exists.
	 * 
	 * @param context The Context2d object to render the Stiochiometric Node
	 * @param nodeWidth Width of the Stiochiometry Node
	 * @param nodeHeight Height of the Stiochiometry Node
	 * @param stoichiometry The Stoichiometric Number
	 */
	
	public void drawStoichNode(Context2d context, double nodeWidth, double nodeHeight, String stoichiometry) {
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
		Annotations stoichNum = new Annotations(idno,bounds,position,fontSize,stoichiometry);
		stoichNum.writeText(context);
	}
	
	/**Renders the Catalyst Node Connector for each Catalyst Edge
	 * 
	 * @param context The Context2d object to render the Catalyst Node Connector
	 * @param catRadius The radius of the Catalyst Node
	 */
	
	public void drawAttachNode(Context2d context, double catRadius) {
		String nodeColor = "rgba(255,255,255,1)";
		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
		colors.makecolor(context);
		
		context.beginPath();
	    context.arc(cenX, cenY, catRadius, 0, 2*Math.PI);
    	context.fill();
    	context.stroke();
    	context.closePath();
	}
	
	/** Renders the Output Arrows for each Output Edge
	 * 
	 * @param context The Context2d object to render the Output Arrows
	 */
	public void drawArrow(Context2d context) {
		String nodeColor = "rgba(0,0,0,1)";
		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
		colors.makecolor(context);
		context.beginPath();
		context.moveTo(inCoX, inCoY);
		context.lineTo(pointX1, pointY1);
		context.lineTo(pointX2, pointY2);
		context.lineTo(inCoX, inCoY);
		context.stroke();
		context.fill();
		context.closePath();	
	}
	
	/**Renders the Inhibitor Node Connector for each Inhibitor Edge
	 * 
	 * @param context The Context2d object to render the Inhibitor Node Connector
	 * @param length The length of the Inhibitor Node Connector
	 */
	public void drawInhibitorNode(Context2d context, double length) {
		
	}
}
