/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.model;

import com.google.gwt.touch.client.Point;

/**
 * The highest level of objects that can be displayed in canvas. This is an abstract class, and should
 * not be initialized.
 * @author gwu
 *
 */
public abstract class GraphObject extends ReactomeObject {
    // Position 
    protected Point position;
    // Color for lines
    private String lineColor;
    private double lineWidth;
    private GraphObjectType type;
    // A flag to indicate if this is a selected object
    private boolean isSelected;
    // A flag to indicate if this is a hovered over object
    private boolean isHovered;
    // A flag to indicate if this object should be highlighted
    private boolean isHighlighted;
    
    protected GraphObject() {
    }
    
    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public boolean isSelected() {
        return this.isSelected;
    }
    
    public void setIsHovered(boolean isHovered) {
    	this.isHovered = isHovered;
    }
    
    
    
    public void setPosition(int x, int y) {
        position = new Point(x, y);
    }
    
    public void setPosition(String position) {
        String[] values = position.split(" ");
        this.position = new Point(new Integer(values[0]),
                                  new Integer(values[1]));
    }
    
    public Point getPosition() {
        return position;
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setType(GraphObjectType type) {
        this.type = type;
    }

    public GraphObjectType getType() {
        return this.type;
    }

    public String getObjectType() {
    	final String PREFIX = "Renderable";
    	
    	String type = this.type.toString();
    	
    	return (type.startsWith(PREFIX) ? type.substring(PREFIX.length()) : type);
    }
    
    public double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    /**
     * Check if a GraphObject can be picked by a position.
     * @param point
     * @return
     */
    public abstract boolean isPicked(Point point);

	public boolean isHovered() {
		return isHovered;
	}
    
}
