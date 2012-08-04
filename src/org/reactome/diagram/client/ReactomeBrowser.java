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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Implements the Module EntryPoint Interface.
 */

public class ReactomeBrowser implements EntryPoint {
	
	// Initialization
	static final String holderId = "maincanvas";
	
	private PathwayDiagramPanel diagramPane;
	
	public void onModuleLoad() {
	    
	    diagramPane = new PathwayDiagramPanel();
//	    System.out.println("Size: " + Window.getClientWidth() + ", " + Window.getClientHeight());
	    diagramPane.setSize(Window.getClientWidth(),
	                        Window.getClientHeight());
	    Window.addResizeHandler(new ResizeHandler() {
	        
	        @Override
	        public void onResize(ResizeEvent event) {
	            diagramPane.setSize(event.getWidth(), 
	                                event.getHeight());
	            //          diagramPane.update();
	        }
	    });
	    RootPanel.get(holderId).add(diagramPane);
	}
	
}

