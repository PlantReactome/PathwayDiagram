/*
 * Created on Sep 20, 2011
 *
 */
package org.reactome.diagram.model;

import org.reactome.diagram.model.ConnectWidget.ConnectRole;

import com.google.gwt.touch.client.Point;

/**
 * A simple class that is used to link a Node and a HyperEdge together. This class is ported from
 * org.gk.render.ConnectWidget in the gkdev project for the curator tool.
 * @author gwu
 *
 */
public class ConnectWidget {
    // Use a little buffer 
    public static final int BUFFER = 3;
    private Point point; // connecting point
    private Point controlPoint; // Another point that decide the line segement with point.
    private int index; // index of inputs, outputs or helpers.
    private int stoichiometry = 1; // default
    private ConnectRole role;
    private Node node;
    private HyperEdge edge;
    // A flag to set if new connection position should be calculated
    private boolean invalidate;
    
    public ConnectWidget() {
    }
    
    /** Creates a new instance of AttachWidget */
    public ConnectWidget(Point p, 
                         Point controlP, 
                         ConnectRole role, 
                         int index) {
        this.point = p;
        this.controlPoint = controlP;
        this.role = role;
        this.index = index;
    }
    
    public Point getPoint() {
        return this.point;
    }
    
    public void setPoint(Point p) {
        this.point = p;
    }
    
    public Point getControlPoint() {
        return this.controlPoint;
    }
    
    public ConnectRole getRole() {
        return this.role;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Set the connected node. 
     * @param node a Renderable that can be connected to a RenderableReaction.
     */
    public void setNode(Node node) {
        this.node = node;
        invalidate = true;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public void setEdge(HyperEdge reaction) {
        this.edge = reaction;
    }
    
    public HyperEdge getEdge() {
        return this.edge;
    }
    
    public void setControlPoint(Point p) {
        this.controlPoint = p;
    }
    
    /**
     * Mark this ConnectWidget as invalid. An invalid ConnectWidget should be validated before its information
     * is used for drawing.
     */
    public void invalidate() {
        invalidate = true;
    }
    
    public boolean isInvalid() {
        return invalidate;
    }
    
    /**
     * Use this method to re-calculate the connected position between node and link.
     */
    public void validate() {
        // Do nothing.
        if (!invalidate || node == null || edge == null)
            return;
        node.validateConnectWidget(this);
        invalidate = false;
    }
    
    /**
     * Override the superclass method and let the contained point to determine the identity of ConnectWidget.
     */
    public boolean equals(Object obj) {
        if (obj instanceof ConnectWidget) {
            ConnectWidget another = (ConnectWidget)obj;
            if ((point != null) && (point == another.point))
                return true;
            else
                return super.equals(obj);
        }
        else
            return false;
    }
    
    /**
     * Override superclass method and let the contained point to determine the identity of ConnectWidget.
     */
    public int hashCode() {
        if (point != null)
            return point.hashCode();
        return super.hashCode();
    }
    
    /**
     * Connect the selected node and edge.
     */
    public void connect() {
        if (edge != null)
            edge.addConnectWidget(this);
        if (node != null)
            node.addConnectWidget(this);
    }
    
    /**
     * Disconnect the node and edge.
     */
    public void disconnect() {
        if (edge != null)
            edge.removeConnectWidget(this);
        if (node != null)
            node.removeConnectWidget(this);
    }
    
    public void setStoichiometry(int stoi) {
        this.stoichiometry = stoi;
    }
    
    public int getStoichiometry() {
        return this.stoichiometry;
    }
    
    public void setRole(ConnectRole role) {
        this.role = role;
    }
    
    public enum ConnectRole {
        INPUT,
        OUTPUT,
        CATALYST,
        INHIBITOR,
        ACTIVATOR
    }
    
}
