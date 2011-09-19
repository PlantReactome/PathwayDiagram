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
 * Plots the reaction edges linking all the specified points for any one particular input element.
 */

public class InputGroup extends EdgeGroup{
	
	/**Constructor to plot Input Element Edges
	 * 
	 * @param idno Id Number of the Input Node
	 * @param reactionpoints Reaction Points of the Renderable Reaction Edge
	 * @param position Reaction Position of the Renderable Reaction Edge
	 * @param bounds Input Edge Points
	 */
	
	public InputGroup(String idno, String reactionpoints, String position, String bounds) {
		super(idno, reactionpoints, position, bounds);	
	}

	/**Renders the Input Group on the Context
	 * 
	 * @param context The Context2d object on which the Input Group is rendered
	 */
	public void plotInputGroup(Context2d context) {
		if(noofPoints != 0)
	    {
		    for(int i = 1; i < noofPoints; i++)
		    {
		    	String inittrimpoints = points[i-1].trim();
		    	String trimpoints = points[i].trim();
		    	HyperEdge midInputEdge = new HyperEdge(inittrimpoints,trimpoints);
		    	midInputEdge.updateEdge(context);  	
		    }
		    String finaltrimpoints = points[noofPoints-1].trim();
		    HyperEdge finalInputEdge = new HyperEdge(finaltrimpoints,reactPoints[0]);
	    	finalInputEdge.updateEdge(context);  		    
		}
	}
}
