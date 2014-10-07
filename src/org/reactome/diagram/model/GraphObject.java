/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.model;

import java.util.Comparator;

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
    
    
    
    public void setPosition(double x, double y) {
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
    	
    	if (type.equals("ProcessNode"))
    		return "Pathway";
    	
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

	public boolean isSetOrComplex() {
		return getType() == GraphObjectType.RenderableComplex ||
				getType() == GraphObjectType.RenderableEntitySet;
	}
	
	public static Comparator<GraphObject> getXCoordinateComparator() {
		return new Comparator<GraphObject>() {

			@Override
			public int compare(GraphObject o1, GraphObject o2) {
				if (o1 == o2)
					return 0;
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				
				double diff = getX(o1) - getX(o2);
				if (diff < 0)
					return -1;
				else if (diff > 0)
					return 1;
				return 0;
			}
			
			private double getX(GraphObject obj) {
				if (obj instanceof Node)
					return ((Node) obj).getBounds().getX();
				
				return obj.getPosition().getX();
			}
		};
	}
    
	public static Comparator<GraphObject> getYCoordinateComparator() {
		return new Comparator<GraphObject>() {

			@Override
			public int compare(GraphObject o1, GraphObject o2) {
				if (o1 == o2)
					return 0;
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				
				double diff = getY(o1) - getY(o2);
				if (diff < 0)
					return -1;
				else if (diff > 0)
					return 1;
				return 0;
			}
			
			private double getY(GraphObject obj) {
				if (obj instanceof Node)
					return ((Node) obj).getBounds().getY();
				
				return obj.getPosition().getY();
			}
		};
	}
}
