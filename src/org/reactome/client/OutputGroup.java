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

package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Plots the reaction edges linking all the specified points for any one particular output element.
 */
public class OutputGroup extends EdgeGroup{

	/**Constructor to plot Output Element Edges
	 * 
	 * @param idno Id Number of the Output Node
	 * @param reactionpoints Reaction Points of the Renderable Reaction Edge
	 * @param position Reaction Position of the Renderable Reaction Edge
	 * @param bounds Output Edge Points
	 */
	public OutputGroup(String idno, String reactionpoints, String position, String bounds) {
		super(idno, reactionpoints, position, bounds);	
	}
	
	/**Renders the Output Group on the Context
	 * 
	 * @param context The Context2d object on which the Output Group is rendered
	 */
	public void plotOutputGroup(Context2d context) {
		if(noofPoints != 0)
	    {
		    for(int i = 1; i < noofPoints; i++)
		    {
		    	String inittrimpoints = points[i-1].trim();
		    	String trimpoints = points[i].trim();
		    	HyperEdge midOutputEdge = new HyperEdge(inittrimpoints,trimpoints);
		    	midOutputEdge.updateEdge(context);
		    }
		    
		    String starttrimpoints = reactPoints[noofEdges-1].trim();
		    String finaltrimpoints = points[noofPoints-1].trim();
		    HyperEdge finalOutputEdge = new HyperEdge(finaltrimpoints,starttrimpoints);
		    finalOutputEdge.updateEdge(context);
		    
		    String startarrowPoints = points[0].trim();
		    String endarrowPoints = "";
		    if(noofPoints > 1) {
		    	endarrowPoints = points[1].trim();
		    } else {
		    	endarrowPoints = reactPoints[noofEdges-1].trim();
		    }
		    
		    String OutputBounds = NodeGroup.BoundsHashmap.get(id);
		    HyperEdge arrow = new HyperEdge(startarrowPoints,endarrowPoints);
		    arrow.updateArrow(context,OutputBounds);
		    
	    }
	}

}
