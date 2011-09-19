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

public class GraphNode{

	double cenX;
	double cenY;
	double coX;
	double coY;
	double idno;
	double nodeWidth;
	double nodeHeight;
	String bgColor;
	String strokeColor;
	int zoomFactor;
	int upHeight;
	double radius;
	double relativeX, relativeY;
	double reactWidth;
	double reactHeight;
	String defaultbgColor;
	String defaultstrokeColor;

	/**Constructor to initialize attributes for a particular Node
	 * 
	 * @param Id Id Number of the Node
	 * @param Bounds X Coordinate of Start Position, Y Coordinate of Start Position, Node Width and Node Height
	 * @param Position Center Position of the Node
	 * @param bgColor Fill Color of Node
	 */
	public GraphNode(String Id, String Bounds, String Position, String bgColor) {
		// TODO Auto-generated constructor stub
		zoomFactor = Parameters.ZoomFactor;
		upHeight = Parameters.UpHeight;
		radius = Parameters.radius/zoomFactor;
		
		Parser parser = new Parser();
		String[] PositionCo = parser.splitbySpace(Position);
		this.cenX = (Double.parseDouble(PositionCo[0]))/zoomFactor;
		this.cenY = ((Double.parseDouble(PositionCo[1]))/zoomFactor)+upHeight;
		
		String[] BoundCo = parser.splitbySpace(Bounds);
		this.coX = (Double.parseDouble(BoundCo[0]))/zoomFactor;
		this.coY = ((Double.parseDouble(BoundCo[1]))/zoomFactor)+upHeight;
		this.nodeWidth = (Double.parseDouble(BoundCo[2]))/zoomFactor;
		this.nodeHeight = (Double.parseDouble(BoundCo[3]))/zoomFactor;
		
		this.idno = Double.parseDouble(Id);
		String bgColorValue;
		if(bgColor != "") {
			String[] ColorAtt = parser.splitbySpace(bgColor);
			bgColorValue = parser.makeColor(ColorAtt);
		} else {
			bgColorValue = "rgba(204,255,204,1)";
		}
		
		this.bgColor = bgColorValue;
		strokeColor = "rgba(0,0,0,1)";
	}

}
