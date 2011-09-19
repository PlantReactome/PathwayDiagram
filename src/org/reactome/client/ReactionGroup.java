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
 * Plots the reaction edges linking all the specified points for the main reaction branch
 */
public class ReactionGroup extends EdgeGroup{
	
	/**Constructor to plot main Reaction Edges
	 * 
	 * @param idno Id Number of the Reaction
	 * @param Reactionpoints Reaction Points of the Renderable Reaction Edge
	 * @param Position Reaction Position of the Renderable Reaction Edge
	 * @param Bounds Reaction Points of the Renderable Reaction Edge
	 */
	public ReactionGroup(String idno, String Reactionpoints, String Position, String Bounds) {
		super(idno, Reactionpoints, Position, Bounds);	
	}

	/**Renders the Reaction Group on the Context
	 * 
	 * @param context The Context2d object on which the Reaction Group is rendered
	 */
	public void plotReactionGroup(Context2d context) {
		for(int i = 1; i < noofEdges; i++)
	    {
	    	String inittrimpoints = reactPoints[i-1].trim();
	    	String trimpoints = reactPoints[i].trim();
	    	HyperEdge midReactionEdge = new HyperEdge(inittrimpoints,trimpoints);
	    	midReactionEdge.updateEdge(context);
	    }
	    
	}

	/**Sets the position to  for render of the Reaction Nodes on the Context
	 * 
	 * @param context The Context2d Object on which the Reaction Nodes are Rendered
	 */
	public void plotReactionNodeGroup(Context2d context) {
		String bgColor = "rgba(255,255,255,1)";
		String strokeColor = "rgba(0,0,0,1)";
		HyperEdge ReactionNode = new HyperEdge(position,bgColor,strokeColor);
		ReactionNode.updateReactNode(context);
	}
}
