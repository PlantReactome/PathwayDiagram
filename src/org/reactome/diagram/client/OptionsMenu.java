/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

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
    	this(diagramPane, true);
    }
    
    public OptionsMenu(PathwayDiagramPanel diagramPane, Boolean includeDiagramTransformationOptions) {
        super(true); // Hide when clicking outside pop-up
    	this.diagramPane = diagramPane;
    	init(includeDiagramTransformationOptions);
    }    
    private void init(Boolean includeDiagramTransformationOptions) {
    	setWidget(getOptionsMenu(includeDiagramTransformationOptions));    	
    	bringToFront(this);
    }
    	
    private void bringToFront(Widget widget) {
    	widget.getElement().getStyle().setZIndex(2);
    }    	   

    public void showPopup(Point point) {
    	hide();
    	
    	pointClicked = point;
    	
    	setPopupPosition((int) point.getX(), (int) point.getY());
    	
    	show();
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
    
    private MenuBar getOptionsMenu(Boolean includeDiagramTransformationOptions) {
    	MenuBar optionsMenu = new MenuBar(true);
    	
    	optionsMenu.addItem(getSearchBarMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getSnapshotDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getDownloadDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getInteractionOverlayMenuItem());
    	
    	if (includeDiagramTransformationOptions) {
    		optionsMenu.addSeparator();
    		optionsMenu.addItem(getZoomInMenuItem());
    		optionsMenu.addSeparator();
    		optionsMenu.addItem(getZoomOutMenuItem());
    		optionsMenu.addSeparator();
    		optionsMenu.addItem(getCenterDiagramMenuItem());
    	}
    		
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
    
    private MenuItem getSnapshotDiagramMenuItem() {
        MenuItem snapshotDiagram = new MenuItem("Snapshot Current View", new Command() {

			@Override
			public void execute() {							
				if (diagramPane.getPathway() == null) {
					AlertPopup.alert("Please choose a pathway to download");
					hide();
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas(diagramPane.getPathwayCanvas().getCoordinateSpaceWidth(),
															 diagramPane.getPathwayCanvas().getCoordinateSpaceHeight());
								
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList())					
					downloadCanvas.getContext2d().drawImage(canvasLayer.getCanvasElement(),0,0);
				
				showDiagramImage(downloadCanvas);
								
				hide();
			}		
        });
        
        return snapshotDiagram;        
	}

    private MenuItem getDownloadDiagramMenuItem() {
    	MenuItem downloadDiagram = new MenuItem("Download Diagram", new Command() {

			@Override
			public void execute() {
				final Long pathwayId = diagramPane.getPathway().getReactomeId();
				
				diagramPane.getController().getPathwayDiagram(pathwayId, new RequestCallback() {
					
					public void onResponseReceived(Request request, Response response) {
						if (response.getStatusCode() != 200) {
							AlertPopup.alert("Unable to download diagram - " + response.getStatusCode() + ": " + response.getStatusText());
							return;
						}
												
						String pathwayDiagramData = "data:image/png;base64," + response.getText();
						
						System.out.println(pathwayDiagramData);
						
						Window.open(pathwayDiagramData, null, null);
						
						//final Image pathwayDiagram = new Image(pathwayDiagramData);
						//pathwayDiagram.addLoadHandler(new LoadHandler() {
							
							//public void onLoad(LoadEvent event) {
								//System.out.println("load");
								//Canvas diagramCanvas = createDownloadCanvas(diagramPane.getPathwayCanvas().getCoordinateSpaceWidth(),
						//													diagramPane.getPathwayCanvas().getCoordinateSpaceHeight());
						
								//diagramCanvas.getContext2d().drawImage(ImageElement.as(pathwayDiagram.getElement()),
								//													   0, 0, diagramCanvas.getOffsetWidth(), diagramCanvas.getOffsetHeight());
						
								//showDiagramImage(diagramCanvas);
								
								//RootPanel.get().remove(pathwayDiagram);
							//}
						//});
						
						//System.out.println("creating");
						//pathwayDiagram.setVisible(false);
						//RootPanel.get().add(pathwayDiagram);
					}
					
					public void onError(Request request, Throwable exception) {
						AlertPopup.alert(exception.getMessage());
					}
				});
				
				hide();
			}
    		
    	});
    	
    	return downloadDiagram;
    }

    private Canvas createDownloadCanvas(Integer width, Integer height) {
    	Canvas downloadCanvas = Canvas.createIfSupported();
    	
    	downloadCanvas.setWidth(width + "px");
    	downloadCanvas.setHeight(height + "px");
    	downloadCanvas.setCoordinateSpaceWidth(width);
    	downloadCanvas.setCoordinateSpaceHeight(height);
    	
    	return downloadCanvas;
    }
    
    private void showDiagramImage(Canvas downloadDiagramCanvas) {
    	Window.open(downloadDiagramCanvas.toDataUrl("application/octet-stream"), null, null);
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
