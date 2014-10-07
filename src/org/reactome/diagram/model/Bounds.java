/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.model;

import com.google.gwt.touch.client.Point;

/**
 * Used to encode bounds information. This is similar to java.awt.Rectangle.
 * @author gwu
 *
 */
public class Bounds {
    double x;
    double y;
    double width;
    double height;
    
    public Bounds() {
    }
    
    /**
     * bounds should be a four element space delimited String.
     * @param bounds
     */
    public Bounds(String bounds) {
        String[] tokens = bounds.split(" ");
        x = Integer.parseInt(tokens[0]);
        y = Integer.parseInt(tokens[1]);
        width = Integer.parseInt(tokens[2]);
        height = Integer.parseInt(tokens[3]);
    }
    
    public Bounds(double left, double top, double adjustedWidth, double adjustedHeight) {
        this.x = left;
        this.y = top;
        this.width = adjustedWidth;
        this.height = adjustedHeight;
    }
    
    public Bounds(Bounds bounds) {
        this(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point getCentre() {
    	double x = this.x + (this.width / 2);
    	double y = this.y + (this.height / 2);
    	
    	return new Point(x, y);
    }
    
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getRight() {
		return getX() + getWidth();
    }
    
    public double getBottom() {
		return getY() + getHeight();
    }
    
    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
    }
    
    /**
     * Check if this Bounds contain a Point.
     * @param p
     * @return
     */
    public boolean contains(Point p) {
        return contains(p.getX(), p.getY());
    }
    
    public boolean contains(double x1, double y1) {
        return (x1 >= x && x1 <= x + width && y1 >= y && y1 <= y + height);
    }

    public boolean isColliding(Bounds object) {
    	double box1_x1 = this.x;
    	double box1_x2 = this.x + this.width;
    	double box1_y1 = this.y;
    	double box1_y2 = this.y + this.height;
    	
    	double box2_x1 = object.x;
    	double box2_x2 = object.x + object.width;
    	double box2_y1 = object.y;
    	double box2_y2 = object.y + object.height; 
    	
    	if (box1_x2 >= box2_x1 && box1_x1 <= box2_x2 && box1_y2 >= box2_y1 && box1_y1 <= box2_y2)
    		return true;
    	return false;	    	
    }
    
    public String toString() {
        return "x, y, width, height: " + x + ", " + y + ", " + width + ", " + height; 
    }
    
    public boolean equals(Object obj) {
    	if (obj instanceof Bounds && sameBoundaries((Bounds) obj))
    		return true;
    	
    	return false;
    }
    
    private boolean sameBoundaries(Bounds bounds) {
    	if (bounds.getX() == getX() &&	bounds.getY() == getY() &&
    		bounds.getWidth() == getWidth() && bounds.getHeight() == getHeight())
    		return true;
    	return false;
    }
    
}
