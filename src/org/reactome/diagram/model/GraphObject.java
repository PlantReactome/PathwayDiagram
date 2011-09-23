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
public abstract class GraphObject {
    
    private String displayName;
    private Long reactomeId;
    private Integer id;
    // Position 
    protected Point position;
    // Color for lines
    private String lineColor;
    private double lineWidth;
    private GraphObjectType type;
    
    protected GraphObject() {
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
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return this.id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getReactomeId() {
        return reactomeId;
    }

    public void setReactomeId(Long reactomeId) {
        this.reactomeId = reactomeId;
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

    public double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }
    
}
