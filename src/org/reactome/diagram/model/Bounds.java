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
    int x;
    int y;
    int width;
    int height;
    
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
    
    public Bounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Bounds(Bounds bounds) {
        this(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Check if this Bounds contain a Point.
     * @param p
     * @return
     */
    public boolean contains(Point p) {
        double x1 = p.getX();
        double y1 = p.getY();
        return (x1 >= x && x1 <= x + width && y1 >= y && y1 <= y + height);
    }
    
    public String toString() {
        return "x, y, width, height: " + x + ", " + y + ", " + width + ", " + height; 
    }
    
}
