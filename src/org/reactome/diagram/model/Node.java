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

import com.google.gwt.touch.client.Point;

public class Node extends GraphObject {
    private Bounds bounds;
    private Bounds insets;
    private Point textPosition;
    // If this node contains other node
    private List<Node> children;
    // For link information
    private List<ConnectWidget> connectWidgets;
    // Some colors in the format: rgbr(?, ?, ?, ?)
    private String bgColor;
    private String fgColor;
    // Text font
    private String font;
    // For NodeAttachment
    private List<NodeAttachment> nodeAttachments;
    // A flag to indicate a dashed border should be used
    private boolean needDashedBorder;
		
	/**
	 * Default constructor.
	 */
	public Node() {
	}
	
	public int getBounsBuffer() {
	    switch (getType()) {
	        case ProcessNode :
	            return 8;
	        default :
	            return 4;
	    }
	}
	
	public boolean isNeedDashedBorder() {
        return needDashedBorder;
    }

    public void setNeedDashedBorder(boolean needDashedBorder) {
        this.needDashedBorder = needDashedBorder;
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
	
	public void setFont(String font) {
		this.font = font;
	}
			
	public String getFont() {
		 return this.font;
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
	
	public void addNodeAttachment(NodeAttachment attachment) {
	    if (nodeAttachments == null)
	        nodeAttachments = new ArrayList<NodeAttachment>();
	    nodeAttachments.add(attachment);
	}
	
	public List<NodeAttachment> getNodeAttachments() {
	    return this.nodeAttachments;
	}
	
	public List<HyperEdge> getConnectedReactions() {
	    List<HyperEdge> reactions = new ArrayList<HyperEdge>();
	    if (connectWidgets != null) {
	        for (ConnectWidget widget : connectWidgets) {
	            HyperEdge edge = widget.getEdge();
	            if (edge.getType() == GraphObjectType.RenderableReaction && 
	                !reactions.contains(edge))
	                reactions.add(edge);
	        }
	    }
	    return reactions;
	}
	
	public void setNodeAttachments(List<NodeAttachment> list) {
	    this.nodeAttachments = list;
	}
	
	public void setTextPosition(int x, int y) {
	    this.textPosition = new Point(x, y);
	}
	
	public Point getTextPosition() {
	    return this.textPosition;
	}

    @Override
    public boolean isPicked(Point point) {
        Bounds bounds = getBounds();
        return bounds.contains(point);
    }

	public Bounds getInsets() {
		return insets;
	}

	public void setInsets(Bounds insets) {
		this.insets = insets;
	}	
}
