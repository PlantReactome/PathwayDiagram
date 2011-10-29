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

package org.reactome.diagram.client;

// Required Imports
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Implements the Module EntryPoint Interface.
 */

public class ReactomeBrowser implements EntryPoint {
	
	// Initialization
	static final String holderId = "maincanvas";
	
	private PathwayDiagramPanel diagramPane;
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	public void onModuleLoad() {
		
		diagramPane = new PathwayDiagramPanel();
		diagramPane.setSize(Window.getClientWidth(),
		                    Window.getClientHeight());
		RootPanel.get(holderId).add(diagramPane);
//		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "Mitotic G1-G1_S phases.xml");
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "EGFR_Simple_37.xml");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);
				}
				public void onResponseReceived(Request request, Response response) {
//					System.out.println("Acquired Document");
					renderXML(response.getText());
				}
			});
		} catch (RequestException ex) {
			requestFailed(ex);
		} 
	}
	
	/** 
	 * Parses the XML Text and Builds a HashMap of the nodes and the edges. Renders the Canvas Visualization.
	 * @param xmlText The XML Text to be parsed
	 */
	private void renderXML(String xmlText) {
//	       testPathwayBuild(xmlText);

		Document pathwayDom = XMLParser.parse(xmlText);
		Element pathwayElement = pathwayDom.getDocumentElement();
		XMLParser.removeWhitespace(pathwayElement);
        CanvasPathway pathway = new CanvasPathway();
        pathway.buildPathway(pathwayElement);
        diagramPane.setPathway(pathway);
//        diagramPane.update();
        // Check size of diagramPane
//        System.out.println("Size of diagram pane: " + diagramPane.getOffsetWidth() + ", " + 
//                           diagramPane.getOffsetHeight());
	}

	void testPathwayBuild(String xmlText) {
	    try {
	        Document document = XMLParser.parse(xmlText);
	        Element element = document.getDocumentElement();
	        CanvasPathway pathway = new CanvasPathway();
	        pathway.buildPathway(element);
	        List<Node> nodes = pathway.getChildren();
	        List<HyperEdge> edges = pathway.getEdges();
	        // Do some simple test
	        System.out.println("ReactomeId: " + pathway.getReactomeId() + 
	                           "\nName: " + pathway.getDisplayName());
	        System.out.println("A List of node: " + nodes.size());
	        for (Node node : nodes) {
	            System.out.println("Node: " + node.getDisplayName() + " (" + node.getReactomeId() + ")");
	        }
	        System.out.println("A list of edges: " + edges.size());
	        for (HyperEdge edge : edges) {
	            System.out.println("Edge: " + edge.getDisplayName() + " (" + edge.getReactomeId() + ")");
	        }
	        // Check one edges
	        HyperEdge edge = edges.get(0);
	        System.out.println(edge.getInputBranches());
	        System.out.println(edge.getOutputBranches());
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * @param exception Exception whenever the XML file is not load
	 */
	private void requestFailed(Throwable exception) {
		Window.alert("Failed to send the message: " + exception.getMessage());
	}
	

}

