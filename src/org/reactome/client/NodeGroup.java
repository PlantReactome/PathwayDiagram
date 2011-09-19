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

import java.util.HashMap;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * Extracts one particular type of nodes (Chemical, Complex, Protein or Entity) from all the nodes.
 * Stores the Bounds of each Node to a HashMap.
 */
public class NodeGroup {

	Element allNodes;
	int noofnodes;
	NodeList nodes;
	String nameTag;
	public static final HashMap<Double, String> BoundsHashmap = new HashMap<Double, String>();
	
	public NodeGroup(Element allNodes, String nameTag) {
		this.allNodes = allNodes;
		this.nameTag = nameTag;
		
		nodes = this.allNodes.getElementsByTagName(this.nameTag);
		noofnodes = nodes.getLength();	
	}

}
