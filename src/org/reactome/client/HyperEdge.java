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
 * Sets and initializes all the Edge Elements for Render on the Canvas
 */
public class HyperEdge {

	String edgePoints1;
	String edgePoints2;
	String position;
	
	Vector nullvector;
	String defaultbgColor;
	String defaultstrokeColor;
	int zoomFactor;
	int upHeight;
	double reactWidth;
	double reactHeight;
	double catRadius;
	
	/**Constructor to set unique edge points to plot an edge between them
	 * 
	 * @param EdgePoints1 First Edge Point Vector
	 * @param EdgePoints2 Second Edge Point Vector
	 */
	public HyperEdge(String EdgePoints1, String EdgePoints2) {
		// TODO Auto-generated constructor stub
		this.edgePoints1 = EdgePoints1;
		this.edgePoints2 = EdgePoints2;
		zoomFactor = Parameters.ZoomFactor;
		reactWidth = Parameters.Reactwidth/zoomFactor;
		reactHeight = Parameters.Reactheight/zoomFactor;
		catRadius = Parameters.catRadius/zoomFactor;
		upHeight = Parameters.UpHeight;
		nullvector = Parameters.nullvector;
		defaultbgColor = Parameters.defaultbgColor;
		defaultstrokeColor = Parameters.defaultstrokeColor;
	}
	
	/**Constructor to set node connector elements on the canvas
	 * 
	 * @param position Position of the Node Connector
	 * @param bgColor Fill Color of the Node Connector
	 * @param strokeColor StrokeColor of the Node Connector
	 */
	public HyperEdge(String position, String bgColor, String strokeColor) {
		this.position = position;
		this.defaultbgColor = bgColor;
		this.defaultstrokeColor = strokeColor;
		zoomFactor = Parameters.ZoomFactor;
		upHeight = Parameters.UpHeight;
		reactWidth = Parameters.Reactwidth/zoomFactor;
		reactHeight = Parameters.Reactheight/zoomFactor;
		catRadius = Parameters.catRadius/zoomFactor;
		nullvector = Parameters.nullvector;
		defaultbgColor = Parameters.defaultbgColor;
		defaultstrokeColor = Parameters.defaultstrokeColor;
	} 
	
	/**Sets two unique points to plot an edge between them
	 * 	
	 * @param context The Context2d object to plot the edge component
	 */
	public void updateEdge(Context2d context) {
		String[] initBoundValues = edgePoints1.split(" ");
	    double coX1 = (Double.parseDouble(initBoundValues[0]))/zoomFactor;
		double coY1 = ((Double.parseDouble(initBoundValues[1]))/zoomFactor) + upHeight;
		Vector InVector = new Vector(coX1,coY1);
		
    	String[] boundValues = edgePoints2.split(" ");
    	double coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
		double coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
		Vector FinVector = new Vector(coX2,coY2);
    	
		RenderableEdge midInputEdge = new RenderableEdge(InVector,FinVector,defaultstrokeColor,defaultbgColor,nullvector);
		midInputEdge.drawEdge(context);
	}
	
	/**Calculates the exact position of the Reaction Node
	 * 
	 * @param context The Context2d object to plot the edge component
	 */
	public void updateReactNode(Context2d context) {
		String[] reactPosition = position.split(" ");
		double inStartcoX1 = Double.parseDouble(reactPosition[0]);
		double inStartcoY1 = Double.parseDouble(reactPosition[1]);
		double coX = (inStartcoX1)/zoomFactor - (reactWidth/2);
		double coY = (((inStartcoY1)/zoomFactor)+upHeight) - (reactHeight/2);
		Vector startPosition = new Vector(coX,coY);
		//System.out.println("Pointing at :" + startPosition.x + "," + startPosition.y);
		RenderableEdge reactNode = new RenderableEdge(nullvector,nullvector,defaultstrokeColor,defaultbgColor,startPosition);
		reactNode.drawReactionNode(context,reactWidth,reactHeight);
	}
	
	/**Calculates the exact position of the attachment of the Catalyst Node Connector to the Reaction Node
	 * 
	 * @param context The Context2d object to plot the edge component
	 */
	public void updateAttachNode(Context2d context) {
		String[] initBoundValues = edgePoints1.split(" ");
	    double coX1 = (Double.parseDouble(initBoundValues[0]))/zoomFactor;
		double coY1 = ((Double.parseDouble(initBoundValues[1]))/zoomFactor) + upHeight;
			
    	String[] boundValues = edgePoints2.split(" ");
    	double coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
		double coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
		
		double slope = (coY2 - coY1)/(coX2 - coX1);
		double requiredX = coX2;
		double requiredY = coY2;
		double distance = (reactWidth/2) + catRadius;
		double angle = Math.atan(slope);
		if(coX2 > coX1) {
			requiredY = coY2 - distance * Math.sin(angle);
			requiredX = coX2 - distance * Math.cos(angle);
		} else if(coX2 < coX1) {
			requiredY = coY2 + distance * Math.sin(angle);
			requiredX = coX2 + distance * Math.cos(angle);
		} else {
			requiredX = coX2;
			if (coY2 > coY1)
				requiredY = coY2 - distance * Math.sin(angle);
			else
				requiredY = coY2 - distance * Math.sin(angle);
		}

		Vector centerPosition = new Vector(requiredX,requiredY);
		RenderableEdge attachNode = new RenderableEdge(nullvector,nullvector,defaultstrokeColor,defaultbgColor,centerPosition);
		attachNode.drawAttachNode(context, catRadius);
	}

	/**Calculates the three vertices of a triangular arrow attached to an Output Node
	 * 
	 * @param context The Context2d object to plot the edge component
	 * @param outputBounds Output Bounds of the Output Node obtained from the Bounds HashMap
	 */
	public void updateArrow(Context2d context, String outputBounds) {
		// TODO Auto-generated method stub
		String[] initBoundValues = edgePoints1.split(" ");
	    double coX1 = (Double.parseDouble(initBoundValues[0]))/zoomFactor;
		double coY1 = ((Double.parseDouble(initBoundValues[1]))/zoomFactor) + upHeight;
				
    	String[] boundValues = edgePoints2.split(" ");
    	double coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
		double coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
		
		double slope = (coY2 - coY1)/(coX2 - coX1);
		double intersectY = coY2 - slope * coX2;
		double requiredSlope = (-1/slope);
		double distanceSq = ((coY2 - coY1) * (coY2 - coY1)) + ((coX2 - coX1) * (coX2 - coX1));
		String[] outputBoundValues = outputBounds.split(" ");
		double outputcoX1 = (Double.parseDouble(outputBoundValues[0]))/zoomFactor;
		double outputcoY1 = (Double.parseDouble(outputBoundValues[1]))/zoomFactor + upHeight;
		double outputWidth = (Double.parseDouble(outputBoundValues[2]))/zoomFactor;
		double outputHeight = (Double.parseDouble(outputBoundValues[3]))/zoomFactor;
		
		if(Math.abs(slope) < 0.05)
			slope = 0;
		if(Math.abs(requiredSlope) < 0.05)
			requiredSlope = 0;
		
		double requiredX = coX1, mainX = coX1;
		double requiredY = coY1, mainY = coY1;
		double pointX1,pointX2;
		int flag = 0;
		double pointY1,pointY2;
		
		if(slope != 0 && requiredSlope !=0){
			double angle = Math.atan(slope);
			double intersectYedge = slope * outputcoX1 + intersectY;
			double intersectXedge = (outputcoY1 - intersectY)/slope;
			if(intersectXedge > outputcoX1 && intersectXedge < (outputcoX1+outputWidth))
				flag = 1;
			else if(intersectYedge > outputcoY1 && intersectYedge < (outputcoY1+outputHeight))
				flag = 2;
			
			if(flag == 1) {
				double mainIncreaseY = ((3+(outputHeight/2)) * (coY2 - coY1))/(Math.sqrt(distanceSq));
				double increaseY = ((10 * (coY2 - coY1))/(Math.sqrt(distanceSq)) + mainIncreaseY);
				requiredY = coY1 + (increaseY/zoomFactor);
				mainY = coY1 + (mainIncreaseY/zoomFactor);
				requiredX = (requiredY - intersectY)/slope;
				mainX = (mainY - intersectY)/slope;
			} else if (flag == 2) {
				double mainIncreaseX = ((3+(outputWidth/2)) * (coX2 - coX1))/(Math.sqrt(distanceSq));
				double increaseX = ((10 * (coX2 - coX1))/(Math.sqrt(distanceSq)) + mainIncreaseX);
				requiredX = coX1 + (increaseX/zoomFactor);
				mainX = coX1 + (mainIncreaseX/zoomFactor);
				requiredY = (slope * requiredX) + intersectY;
				mainY = (slope * mainX) + intersectY;
			}
			pointX1 = requiredX + Math.sqrt(100/(3 * zoomFactor * zoomFactor * ((requiredSlope * requiredSlope) + 1)));
			pointX2 = requiredX - Math.sqrt(100/(3 * zoomFactor * zoomFactor * ((requiredSlope * requiredSlope) + 1)));
			pointY1 = requiredY + requiredSlope * (pointX1 - requiredX);
			pointY2 = requiredY + requiredSlope * (pointX2 - requiredX);
			coX1 = mainX;
			coY1 = mainY;
		} else {
			if(slope == 0) {
				if(coX2 > coX1) {
					coX1 = coX1 + (outputWidth/2);
					requiredX = coX1 + (10/zoomFactor);
				} else {
					coX1 = coX1 - (outputWidth/2);
					requiredX = coX1 - (10/zoomFactor);
				}
				requiredY = coY1;
				pointX1 = requiredX;
				pointY1 = requiredY + (10/(Math.sqrt(3)*zoomFactor));
				pointX2 = requiredX;
				pointY2 = requiredY - (10/(Math.sqrt(3)*zoomFactor));
			} else {
				if(coY2 > coY1) {
					coY1 = coY1 + (outputHeight/2);
					requiredY = coY1 + (10/zoomFactor);
				} else {
					coY1 = coY1 - (outputHeight/2);
					requiredY = coY1 - (10/zoomFactor);
				}
				requiredX = coX1;
				pointX1 = requiredX + (10/(Math.sqrt(3)*zoomFactor));
				pointY1 = requiredY;
				pointX2 = requiredX - (10/(Math.sqrt(3)*zoomFactor));
				pointY2 = requiredY;
			}
		}
//	      // The the angle of the line segment
//	    // The first point is used as a control point
//	    String[] tokens = edgePoints2.split(" ");
//	    int x1 = Integer.parseInt(tokens[0]);
//	    int y1 = Integer.parseInt(tokens[1]);
//	    // the second point is used as the point position of the arrow
//	    tokens = edgePoints1.split(" ");
//	    int x2 = Integer.parseInt(tokens[0]);
//	    int y2 = Integer.parseInt(tokens[1]);
//        double alpha = Math.atan((double)(y2 - y1) / (x2 - x1));
//        if (x1 > x2)
//            alpha += Math.PI;
//        double ARROW_ANGLE = Math.PI / 6;
//        int ARROW_LENGTH = 8;
//        double angle = ARROW_ANGLE - alpha;
//        double x11 = x2 - ARROW_LENGTH * Math.cos(angle);
//        double y11 = y2 + ARROW_LENGTH * Math.sin(angle);
//        angle = ARROW_ANGLE + alpha;
//        double x12 = x2 - ARROW_LENGTH * Math.cos(angle);
//        double y12 = y2 - ARROW_LENGTH * Math.sin(angle);
//        Vector invector = new Vector(x11, y11 + upHeight);
//		Vector point1 = new Vector(x2, y2 + upHeight);
//		Vector point2 = new Vector(x12, y12 + upHeight);
		
		Vector invector = new Vector(coX1,coY1);
		Vector point1 = new Vector(pointX1,pointY1);
		Vector point2 = new Vector(pointX2,pointY2);
		String bgColor = "rgba(0,0,0,1)";
		RenderableEdge arrow = new RenderableEdge(invector, point1, point2, defaultstrokeColor, bgColor);
		arrow.drawArrow(context);
	}

	/**Calculates the exact position of the Stoichiometric Node
	 * 
	 * @param context The Context2d object to plot the edge component
	 * @param stoichiometry Stoichiometry of the reaction
	 * @param flag Flag to determine the type of reaction
	 */
	public void updateStoichNode(Context2d context, String stoichiometry, int flag) {
		// TODO Auto-generated method stub
		String[] reactPoints = edgePoints1.split(",");
		int noofEdges = reactPoints.length;
		int point1 = 0;
		int point2 = 1;
		switch(flag) {
			case 2:
				point1 = noofEdges - 1;
				point2 = noofEdges - 2;
				break;
			default:
				point1 = 0;
				point2 = 1;
				break;
		}
		double coX1,coX2,coY1,coY2;
		if(edgePoints2 != "") {
			String[] stoichPoints = edgePoints2.split(",");
			String[] initBoundValues = stoichPoints[0].split(" ");
		    coX1 = (Double.parseDouble(initBoundValues[0]))/zoomFactor;
			coY1 = ((Double.parseDouble(initBoundValues[1]))/zoomFactor) + upHeight;
			if(stoichPoints.length > 1) {
				String finPoints = stoichPoints[1].trim();
		    	String[] boundValues = finPoints.split(" ");
		    	coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
				coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
			} else {
				String finPoints = reactPoints[point1].trim();
		    	String[] boundValues = finPoints.split(" ");
		    	coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
				coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
			}
		} else {
			String[] initBoundValues = reactPoints[point1].split(" ");
		    coX1 = (Double.parseDouble(initBoundValues[0]))/zoomFactor;
			coY1 = ((Double.parseDouble(initBoundValues[1]))/zoomFactor) + upHeight;
			String finPoints = reactPoints[point2].trim();
	    	String[] boundValues = finPoints.split(" ");
	    	coX2 = (Double.parseDouble(boundValues[0]))/zoomFactor;
			coY2 = ((Double.parseDouble(boundValues[1]))/zoomFactor) + upHeight;
		}
		double cenX = ((coX1 + coX2)/2) - (reactWidth/2);
		double cenY = ((coY1 + coY2)/2) - (reactHeight/2);
		Vector centerPosition = new Vector(cenX,cenY);
		RenderableEdge stoichNode = new RenderableEdge(nullvector,nullvector,defaultstrokeColor,defaultbgColor,centerPosition);
		stoichNode.drawStoichNode(context, reactWidth, reactHeight, stoichiometry);
	}

}
