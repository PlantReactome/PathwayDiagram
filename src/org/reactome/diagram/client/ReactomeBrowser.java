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
import org.reactome.diagram.event.PathwayChangeEvent;
import org.reactome.diagram.event.PathwayChangeEventHandler;
import org.reactome.diagram.event.SubpathwaySelectionEvent;
import org.reactome.diagram.event.SubpathwaySelectionEventHandler;

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
	    diagramPane.setRestServiceURL("/ReactomeRESTfulAPI/RESTfulWS/");
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
	    addTestHandlers();
	    RootPanel.get(holderId).add(diagramPane);
	}
	
	private void addTestHandlers() {
		diagramPane.addPathwayChangeEventHandler(new PathwayChangeEventHandler() {

			@Override
			public void onPathwayChange(PathwayChangeEvent event) {
				System.out.println("Current Pathway - " + event.getCurrentPathwayDBId());
			}			
		});
		
		diagramPane.addSubpathwaySelectionEventHandler(new SubpathwaySelectionEventHandler() {

			@Override
			public void onSubpathwaySelection(SubpathwaySelectionEvent event) {
				System.out.println("Sub-pathway Id - " + event.getSubpathwayId());
				System.out.println("Diagram Id - " + event.getDiagramPathwayId());
			}			
		}); 
			
		
	}
	
}

