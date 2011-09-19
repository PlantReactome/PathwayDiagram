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

import java.util.Iterator;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * Builds HashMaps from the XML Element with the data of the id, bounds and position of the nodes and edges, and renders them on the Canvas.
 */

public class CanvasElements {

	Element pathwayElement;
	static CompartmentGroup compartments;
	static EdgeElements edges;
	static EntityGroup entitys;
	static ProteinGroup proteins;
	static ChemicalGroup chemicals;
	static ComplexGroup complexes;
	
	/** Constructor to build the HashMaps from the XML element
	 * 
	 * @param pathwayElement The XML Element from which the HashMaps are built
	 */
	public CanvasElements(Element pathwayElement) {
		this.pathwayElement = pathwayElement;
		NodeList Nodes = pathwayElement.getElementsByTagName("Nodes");
		Element AllNodes = (Element) Nodes.item(0);
		compartments = new CompartmentGroup(AllNodes);
		entitys = new EntityGroup(AllNodes);
		proteins = new ProteinGroup(AllNodes);
		chemicals = new ChemicalGroup(AllNodes);
		complexes = new ComplexGroup(AllNodes);
		
		NodeList Edges = pathwayElement.getElementsByTagName("Edges");
		Element AllEdges = (Element) Edges.item(0);
		edges = new EdgeElements(AllEdges);
	}

	/**
	 * Empty Constructor used to render the Canvas Elements on the Context after building the HashMaps
	 */
	public CanvasElements() {
		
	}
	
	/**
	 * Process the XML Elements and Build HashMaps containing information of the Id, Bounds and Positions of the Nodes and Edges
	 */
	public void process() {
		compartments.buildHashMap();
		entitys.buildHashMap();
		proteins.buildHashMap();
		chemicals.buildHashMap();
		complexes.buildHashMap();
		edges.buildHashMap();
	}
	
	/** Renders the different Canvas Elements on the Context
	 * 
	 * @param context Context2d object to render the Canvas Elements
	 */
	public void redraw(Context2d context) {
		compartments.update(context);
		edges.updateReactionGroup(context);
		edges.updateInputGroup(context);
		edges.updateCatalystGroup(context);
		edges.updateOutputGroup(context);
		edges.updateInhibitorGroup(context);

		edges.updateReactionNode(context);
		edges.updateCatalystNode(context);
		edges.updateStoichiometricNode(context);
		
		entitys.update(context);
		proteins.update(context);
		chemicals.update(context); 
		complexes.update(context); 
	}

	/** Get the maximum dimensions of a Pathway Diagram
	 * 
	 * @return Vector containing the maximum Dimensions (width and height) of the Pathway Diagram
	 */
	
	public Vector getMaxDim() {
		double maxWidth = 0.0;
		double maxHeight = 0.0;
		double ZoomFactor = Parameters.ZoomFactor;
		double UpHeight = Parameters.UpHeight;
		
		Iterator<Double> iterator = NodeGroup.BoundsHashmap.keySet().iterator();
		while(iterator.hasNext()) {
			Double key = (Double) iterator.next();
			String bounds = NodeGroup.BoundsHashmap.get(key);
			
			Parser parser = new Parser();
			String[] BoundCo = parser.splitbySpace(bounds);
			double coX = (Double.parseDouble(BoundCo[0]))/ZoomFactor;
			double coY = ((Double.parseDouble(BoundCo[1]))/ZoomFactor)+UpHeight;
			double nodeWidth = (Double.parseDouble(BoundCo[2]))/ZoomFactor;
			double nodeHeight = (Double.parseDouble(BoundCo[3]))/ZoomFactor;
			
			if(maxWidth <= (coX + nodeWidth)) {
				maxWidth = (coX + nodeWidth);
			}
			
			if(maxHeight <= (coY + nodeHeight)) {
				maxHeight = (coY + nodeHeight);
			}
		}
		
		maxWidth = maxWidth + 200;
		maxHeight = maxHeight + 200;
		Vector maxDim = new Vector(maxWidth, maxHeight);
		return maxDim;
	}
}
