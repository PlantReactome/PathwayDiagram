/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author weiserj
 *
 */
public class OptionsMenu extends PopupPanel {
       
    private PathwayDiagramPanel diagramPane;
    private Point pointClicked;
    
    public OptionsMenu(PathwayDiagramPanel diagramPane) {
        super(true); // Hide when clicking outside pop-up
    	this.diagramPane = diagramPane;
    	init();
    }    
    private void init() {
    	setWidget(getOptionsMenu());    	
    	bringToFront(this);
    }
    	
    private void bringToFront(Widget widget) {
    	widget.getElement().getStyle().setZIndex(2);
    }    	   

    public void showPopup(MouseEvent<? extends EventHandler> event) {
    	event.stopPropagation();
    	event.preventDefault();
    	
    	hide();
    	
    	pointClicked = new Point(event.getX(), event.getY());
    	
    	final Integer OFFSET = 2;
    	setPopupPosition(event.getClientX() + OFFSET,
    					 event.getClientY() + OFFSET);
    	
    	show();
    }
    
    public void hide() {
    	super.hide();
    }
    
    private MenuBar getOptionsMenu() {
    	MenuBar optionsMenu = new MenuBar(true);
    	
    	optionsMenu.addItem(getSearchBarMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getDownloadDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getInteractionOverlayMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getZoomInMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getZoomOutMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getCenterDiagramMenuItem());
    	
    	setOptionsMenuStyle(optionsMenu);
    	
    	return optionsMenu;
    }
    
    
    private void setOptionsMenuStyle(MenuBar optionsMenu) {
		Style menuStyle = optionsMenu.getElement().getStyle();
		menuStyle.setOpacity(1);		
	}

	private MenuItem getSearchBarMenuItem() {
    	MenuItem searchBar = new MenuItem("Search Diagram (Ctrl-f)", new Command() {
    		
    		@Override
    		public void execute() {
    			diagramPane.showSearchPopup();
    			hide();
    		}
    	});
    	
    	return searchBar;
    }
    
    private MenuItem getDownloadDiagramMenuItem() {
        MenuItem downloadDiagram = new MenuItem("Snapshot of Diagram's Current View", new Command() {

			@Override
			public void execute() {							
				if (diagramPane.getPathway() == null) {
					AlertPopup.alert("Please choose a pathway to download");
					hide();
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas(diagramPane.getCanvasList().get(0));
				
				Context2d context = downloadCanvas.getContext2d();
				
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList())					
					context.drawImage(canvasLayer.getCanvasElement(),0,0);				
				
				String canvasUrl = downloadCanvas.toDataUrl("application/octet-stream");
				Window.open(canvasUrl, null, null);
				
				hide();
			}

			private Canvas createDownloadCanvas(DiagramCanvas diagramCanvas) {				
				Integer width = diagramCanvas.getCanvasElement().getWidth();
				Integer height = diagramCanvas.getCanvasElement().getHeight();
				
				Canvas downloadCanvas = Canvas.createIfSupported();				
				downloadCanvas.setWidth(width + "px");
				downloadCanvas.setHeight(height + "px");
				downloadCanvas.setCoordinateSpaceWidth(width);
				downloadCanvas.setCoordinateSpaceHeight(height);
				
				return downloadCanvas;				
			}        	
        });
        
        return downloadDiagram;        
	}
    
    private MenuItem getInteractionOverlayMenuItem() {
    	MenuItem interactionOverlay = new MenuItem("Interaction Overlay Options...", new Command() {

			@Override
			public void execute() {
				InteractionOverlayOptionsPopup optionsPopup = new InteractionOverlayOptionsPopup(diagramPane);
				optionsPopup.center();
				
				hide();
			}
    		
    	});
		
    	return interactionOverlay;    	
    }
     
    private MenuItem getZoomInMenuItem() {
    	MenuItem zoomIn = new MenuItem("Zoom In", new Command() {

			@Override
			public void execute() {
				diagramPane.zoomIn();
				diagramPane.update();
				hide();
			}    	    		
    	});
    	
    	return zoomIn;
    }
    
    private MenuItem getZoomOutMenuItem() {
    	MenuItem zoomOut = new MenuItem("Zoom Out", new Command() {

			@Override
			public void execute() {
				diagramPane.zoomOut();
				diagramPane.update();
				hide();
			}    		
    	});
    	
    	return zoomOut;
    }
        
    private MenuItem getCenterDiagramMenuItem() {
    	MenuItem centerDiagram = new MenuItem("Center Diagram Here", new Command() {

			@Override
			public void execute() {
				diagramPane.center(pointClicked);
				diagramPane.update();
				hide();
			}
    		
    	});
    	
    	return centerDiagram;
    }   
}
