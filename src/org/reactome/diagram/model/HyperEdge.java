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

/**
 * Sets and initializes all the Edge Elements for Render on the Canvas
 */
public class HyperEdge extends GraphObject {
    // connect information
    private List<ConnectWidget> connectWidgets;
    // Different branches
    private List<List<Point>> inputBranches;
    private List<List<Point>> outputBranches;
    private List<List<Point>> catalystBranches;
    private List<List<Point>> inhibitorBranches;
    private List<List<Point>> activatorBranches;
    private List<Point> backbone;
    // For reaction type if it is a reaction
    private ReactionType reactionType;
    
    /**
     * Default constructor.
     */
    public HyperEdge() {
        
    }
    
    public List<ConnectWidget> getConnectWidgets() {
        return connectWidgets;
    }

    public void setConnectWidgets(List<ConnectWidget> connectWidgets) {
        this.connectWidgets = connectWidgets;
    }

    public List<List<Point>> getInputBranches() {
        return inputBranches;
    }

    public void setInputBranches(List<List<Point>> inputBranches) {
        this.inputBranches = inputBranches;
    }

    public List<List<Point>> getOutputBranches() {
        return outputBranches;
    }

    public void setOutputBranches(List<List<Point>> outputBranches) {
        this.outputBranches = outputBranches;
    }

    public List<List<Point>> getCatalystBranches() {
        return catalystBranches;
    }

    public void setCatalystBranches(List<List<Point>> catalystBranches) {
        this.catalystBranches = catalystBranches;
    }

    public List<List<Point>> getInhibitorBranches() {
        return inhibitorBranches;
    }

    public void setInhibitorBranches(List<List<Point>> inhibitorBranches) {
        this.inhibitorBranches = inhibitorBranches;
    }

    public List<List<Point>> getActivatorBranches() {
        return activatorBranches;
    }

    public void setActivatorBranches(List<List<Point>> activatorBranches) {
        this.activatorBranches = activatorBranches;
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
	
	public void setBackbone(List<Point> points) {
	    this.backbone = points;
	}
	
	public List<Point> getBackbone() {
	    return this.backbone;
	}
	
	/**
	 * Call this method to make sure position is an element of the backbone points.
	 */
	public void validatePosition() {
	    if (position == null || backbone == null)
	        return;
	    for (Point point : backbone) {
	        if (position.equals(point)) {
	            position = point;
	            break;
	        }
	    }
	}
	
	public void addBranch(List<Point> points, ConnectWidget.ConnectRole role) {
	    switch (role) {
	        case INPUT :
	            if (inputBranches == null)
	                inputBranches = new ArrayList<List<Point>>();
	            inputBranches.add(points);
	            break;
	        case OUTPUT :
	            if (outputBranches == null)
	                outputBranches = new ArrayList<List<Point>>();
	            outputBranches.add(points);
	            break;
	        case CATALYST :
	            if (catalystBranches == null)
	                catalystBranches = new ArrayList<List<Point>>();
	            catalystBranches.add(points);
	            break;
	        case INHIBITOR :
	            if (inhibitorBranches == null)
	                inhibitorBranches = new ArrayList<List<Point>>();
	            inhibitorBranches.add(points);
	            break;
	        case ACTIVATOR :
	            if (activatorBranches == null)
	                activatorBranches = new ArrayList<List<Point>>();
	            activatorBranches.add(points);
	            break;
	    }
	}
	
	public void setReactionType(ReactionType type) {
	    this.reactionType = type;
	}
	
	public ReactionType getReactionType() {
	    return this.reactionType;
	}

}
