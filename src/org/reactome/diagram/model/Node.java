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

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.view.Parameters;

public class Node extends GraphObject {
    private Bounds bounds;
    // If this node contains other node
    protected List<Node> children;
    // For link information
    private List<ConnectWidget> connectWidgets;
    // Some colors in the format: rgbr(?, ?, ?, ?)
    protected String bgColor;
    protected String fgColor;
    // TODO: Should be deleted
    protected String strokeColor;

	double cenX;
	double cenY;
	double coX;
	double coY;
	double idno;
	double nodeWidth;
	double nodeHeight;
	
	
	int zoomFactor;
	int upHeight;
	double radius;
	double relativeX, relativeY;
	double reactWidth;
	double reactHeight;
	String defaultbgColor;
	String defaultstrokeColor;
	
	/**
	 * Default node for subclassing.
	 */
	public Node() {
	}
	
	/**
	 * Constructor to initialize attributes for a particular Node based on values from XML text.
	 * @param id Id Number of the Node
	 * @param bounds X Coordinate of Start Position, Y Coordinate of Start Position, Node Width and Node Height
	 * @param position Center Position of the Node
	 * @param bgColor Fill Color of Node
	 */
	public Node(String id, 
	            String bounds,
	            String position,
	            String bgColor) {
		zoomFactor = Parameters.ZoomFactor;
		upHeight = Parameters.UpHeight;
		radius = Parameters.radius/zoomFactor;
		
		String[] values = position.split(" ");
		setPosition(Integer.parseInt(values[0]),
		            Integer.parseInt(values[1]));
		
		values = bounds.split(" ");
		this.bounds = new Bounds(Integer.parseInt(values[0]),
		                         Integer.parseInt(values[1]),
		                         Integer.parseInt(values[2]),
		                         Integer.parseInt(values[3]));
		
		setId(new Integer(id));
		
		String bgColorValue;
		if(bgColor != "") {
			String[] colorAtt = bgColor.split(" ");
			bgColorValue = ModelHelper.makeColor(colorAtt);
		} 
		else {
			bgColorValue = "rgba(204,255,204,1)";
		}
		this.bgColor = bgColorValue;
		
		fgColor = "rgba(0,0,0,1)";
	}
	
	public void setBgColor(String color) {
	    this.bgColor = color;
	}
	
	public String getBgColor() {
	    return this.bgColor;
	}
	
	public void setFgColor(String color) {
	    this.fgColor = color;
	}
	
	public String getFgColor() {
	    return this.fgColor;
	}
	
	public Bounds getBounds() {
	    return this.bounds;
	}
	
	public void setBounds(Bounds bounds) {
	    this.bounds = new Bounds(bounds);
	}
	
	public void addChild(Node node) {
	    if (children == null)
	        children = new ArrayList<Node>();
	    children.add(node);
	}
	
	public void setChildren(List<Node> nodes) {
	    this.children = nodes;
	}
	
	public List<Node> getChildren() {
	    return this.children;
	}
	
	public void addConnectWidget(ConnectWidget widget) {
	    if (connectWidgets == null)
	        connectWidgets = new ArrayList<ConnectWidget>();
	    connectWidgets.add(widget);
	}
	
	public void removeConnectWidget(ConnectWidget widget) {
	    if (connectWidgets != null)
	        connectWidgets.remove(widget);
	}
	
	public void validateConnectWidget(ConnectWidget widget) {
	    
	}
}
