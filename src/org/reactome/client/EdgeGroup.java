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

/**
 * Initializes the attributes to all the edges and linked nodes
 */

public class EdgeGroup{

	int noofPoints;
	int noofEdges;
	double id;
	String[] points;
	String position;
	String[] reactPoints;
	
	/**Constructor to set Id number, Reaction points, Reaction position, Element bounds for all Edge Groups
	 * 
	 * @param idno Id number of element Node
	 * @param reactionpoints Reaction points of the Renderable Reaction Edge
	 * @param position Reaction position of the Renderable Reaction Edge
	 * @param bounds Element Edge points
	 */
	public EdgeGroup(String idno, String reactionpoints, String position, String bounds) {
		// TODO Auto-generated constructor stub
		this.id = Double.parseDouble(idno);
		this.points = bounds.split(",");
	    this.noofPoints = points.length;
	    this.reactPoints = reactionpoints.split(",");
	    this.noofEdges = reactPoints.length;
	    this.position = position;
	}

	
}
