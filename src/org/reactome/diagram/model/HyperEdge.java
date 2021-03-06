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

import org.reactome.diagram.model.ConnectWidget.ConnectRole;

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
    // For interaction type. Most likely a new class called RenderableInteraction should be created.
    // However, lump all edge related properties in this super-class for easy managing for the time
    // being
    private InteractionType interactionType;
    // Defined for sensing
    private final int SENSING_DISTANCE = 10;
    
    /**
     * Default constructor.
     */
    public HyperEdge() {
        
    }
    
    public List<Node> getConnectedNodes() {
        List<Node> nodes = new ArrayList<Node>();
        if (connectWidgets != null) {
            for (ConnectWidget widget : connectWidgets) {
                if (widget.getNode() != null)
                    nodes.add(widget.getNode());
            }
        }
        return nodes;
    }
    
    public List<Node> getOutputNodes() {
        List<Node> nodes = new ArrayList<Node>();
        if (connectWidgets != null) {
            for (ConnectWidget widget : connectWidgets) {
                if (widget.getRole() == ConnectRole.OUTPUT &&
                    widget.getNode() != null)
                    nodes.add(widget.getNode());
            }
        }
        return nodes;
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
	
	/**
	 * If this HyperEdge is used for a RenderableInteraction, an InteractionType
	 * should be set is available.
	 * Note: Most likely a new class RenderableInteraction should be created. 
	 * @param type
	 */
	public void setInteractionType(InteractionType type) {
	    this.interactionType = type;
	}
	
	public InteractionType getInteractionType() {
	    return this.interactionType;
	}

    @Override
    public boolean isPicked(Point point) {
        // Check distance between point and position
        double x = position.getX();
        double y = position.getY();
        double x0 = point.getX();
        double y0 = point.getY();
        if (x0 >= x - SENSING_DISTANCE && x0 <= x + SENSING_DISTANCE &&
            y0 >= y - SENSING_DISTANCE && y0 <= y + SENSING_DISTANCE)
            return true;
        return false;
    }
}
