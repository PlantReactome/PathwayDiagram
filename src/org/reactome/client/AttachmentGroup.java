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
 * Plots the reaction edges linking all the specified points for any one particular Catalyst, Activator or Inhibitor element.
 */

public class AttachmentGroup extends EdgeGroup{

	String bgColor;
	
	/**Constructor to plot Attachment Element Edges
	 * 
	 * @param idno Id Number of the Attachment Node
	 * @param reactionpoints Reaction Points of the Renderable Reaction Edge
	 * @param position Reaction Position of the Renderable Reaction Edge
	 * @param bounds Attachment Edge Points
	 */
	
	public AttachmentGroup(String idno, String reactionpoints, String position, String bounds) {
		super(idno, reactionpoints, position, bounds);	
	}

	/**Renders the Attachment Group (Catalyst Group, Activator Group, Inhibitor Group) on the Context
	 * 
	 * @param context The Context2d object on which the Attachment Group is rendered
	 */
	public void plotAttachmentGroup(Context2d context) {
		if(noofPoints != 0)
	    {
		    for(int i = 1; i < noofPoints; i++)
		    {
		    	String inittrimpoints = points[i-1].trim();
		    	String trimpoints = points[i].trim();
		    	HyperEdge midCatalystEdge = new HyperEdge(inittrimpoints,trimpoints);
		    	midCatalystEdge.updateEdge(context);	
		    }
		    
		    String finaltrimpoints = points[noofPoints-1].trim();
		    HyperEdge finalCatalystEdge = new HyperEdge(finaltrimpoints,position);
	    	finalCatalystEdge.updateEdge(context);
	    	
	    }
	}
	
	/**Sets the position to  for render of the Catalyst Node Connectors on the Context
	 * 
	 * @param context The Context2d Object on which the Catalyst Node Connectors are Rendered
	 */
	public void plotCatalystNodeGroup (Context2d context) {
		if(noofPoints != 0)
	    {   
			String finaltrimpoints = points[noofPoints-1].trim();
		    HyperEdge catalystNode = new HyperEdge(finaltrimpoints,position);
	    	catalystNode.updateAttachNode(context);
	    }
	}
	
	/**Sets the position to  for render of the Inihibitor Node Connectors on the Context
	 * 
	 * @param context The Context2d Object on which the Inihibitor Node Connectors are Rendered
	 */
	public void plotInhibitorNodeGroup (Context2d context) {
		if(noofPoints != 0)
	    {   
			
	    }
	}
	
	/**Sets the position to  for render of the Activator Node Connectors on the Context
	 * 
	 * @param context The Context2d Object on which the Activator Node Connectors are Rendered
	 */
	public void plotActivatorNodeGroup (Context2d context) {
		if(noofPoints != 0)
	    {   
			
	    }
	}
}
