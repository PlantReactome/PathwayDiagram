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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

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
	}
	
}

