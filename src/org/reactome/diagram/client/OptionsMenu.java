/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorNode;

import com.google.gwt.canvas.client.Canvas;
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
    	//optionsMenu.addItem(getSnapshotDiagramMenuItem());
    	//optionsMenu.addSeparator();
    	optionsMenu.addItem(getDownloadDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getGenomeSpaceMenuItem());
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
    
    private MenuItem getDownloadDiagramMenuItem() {
        MenuItem downloadDiagram = new MenuItem("Download Diagram (with data overlays)", new Command() {

			@Override
			public void execute() {							
				if (!compatibleBrowser()) {
					displayIncompatibleBrowserMessage();
					return;
				}
				
				if (diagramPane.getPathway() == null) {
					alertUser("Please choose a pathway to download");
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas();
				
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList()) {
					canvasLayer.drawCanvasLayer(downloadCanvas.getContext2d());
				}
					
				showDiagramImage(downloadCanvas);
								
				hide();
			}		
        });
        
        return downloadDiagram;
	}
    
    private MenuItem getGenomeSpaceMenuItem() {
        MenuItem downloadDiagram = new MenuItem("Save Diagram to GenomeSpace", new Command() {

			@Override
			public void execute() {							
				if (!compatibleBrowser()) {
					displayIncompatibleBrowserMessage();
					return;
				}
				
				if (diagramPane.getPathway() == null) {
					alertUser("Please choose a pathway to download");
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas();
				
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList()) {
					canvasLayer.drawCanvasLayer(downloadCanvas.getContext2d());
				}
				
				
				Long pathwayId = diagramPane.getPathway().getReactomeId();
				
				// Convert the Canvas image to a base64 string
				String mimeString = "image/png";
				String dataURL = downloadCanvas.toDataUrl(mimeString);
				String base64  = dataURL.split(",")[1];
				// Convert the base64 string to a blob and send to
				// GenomeSpace using their JavaScript API
				uploadToGenomeSpace(base64, mimeString, pathwayId);
				
				hide();
			}		
        });
        
        return downloadDiagram;
	}


    private static native void uploadToGenomeSpace(String base64, String mimeString, Long pathwayId) /*-{    	                                                                                	    
    	var binary = atob(base64);                 //
    	var array  = new Array();                  // Adapted from
    	for(var i = 0; i < binary.length; i++) {   // stackoverflow.com/questions/4998908
        	array.push(binary.charCodeAt(i));      // and many similar discussions
    	}                                          // 
    	var uarray = new Uint8Array(array);        //
    	var blob = new Blob([uarray], {type: mimeString});
    	var formData = new FormData();
    	var imageName = "Reactome_pathway_" + pathwayId + ".png";
    	formData.append("webmasterfile", blob, imageName);
    	$wnd.gsUploadByPost(formData);
    }-*/;
    
    
    
    
    /*
    private MenuItem getDownloadDiagramMenuItem() {
    	MenuItem downloadDiagram = new MenuItem("Download Diagram", new Command() {

			@Override
			public void execute() {
				if (!compatibleBrowser()) {
					displayIncompatibleBrowserMessage();
					return;
				}
				
				final Long pathwayId = diagramPane.getPathway().getReactomeId();
				
				diagramPane.getController().getPathwayDiagram(pathwayId, new RequestCallback() {
					
					public void onResponseReceived(Request request, Response response) {
						if (response.getStatusCode() != 200) {
							AlertPopup.alert("Unable to download diagram - " + response.getStatusCode() + ": " + response.getStatusText());
							return;
						}
												
						String pathwayDiagramData = "data:image/png;base64," + response.getText();
						pathwayDiagramData = pathwayDiagramData.replaceAll("-", "+");
						pathwayDiagramData = pathwayDiagramData.replaceAll("_", "/");
																		
						Window.open(pathwayDiagramData, null, null);
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
	*/

    private Canvas createDownloadCanvas() {
    	Canvas downloadCanvas = Canvas.createIfSupported();
    	
    	List<InteractorNode> visibleInteractors = diagramPane.getInteractorCanvas() != null ?
    			diagramPane.getInteractorCanvas().getVisibleInteractors() :
    			new ArrayList<InteractorNode>();
    				
    	Bounds bounds = visibleInteractors.isEmpty() ?
    			diagramPane.getPathway().getPreferredSize() :
    			getBoundsEncompassingInteractors(visibleInteractors);
    	
    	downloadCanvas.setWidth(bounds.getWidth()+ "px");
    	downloadCanvas.setHeight(bounds.getHeight() + "px");
    	downloadCanvas.setCoordinateSpaceWidth((int) bounds.getWidth());
    	downloadCanvas.setCoordinateSpaceHeight((int) bounds.getHeight());
    	
    	downloadCanvas.getContext2d().translate(-Math.min(bounds.getX(), 0), 
    											-Math.min(bounds.getY(), 0));
    	
    	return downloadCanvas;
    }
    
    private Bounds getBoundsEncompassingInteractors(List<InteractorNode> interactors) {
		Collections.sort(interactors, GraphObject.getXCoordinateComparator());
    	double minX = interactors.get(0).getBounds().getX();
    	double maxX = interactors.get(interactors.size() - 1).getBounds().getX() +
    			   interactors.get(interactors.size() - 1).getBounds().getWidth();
    	
    	Collections.sort(interactors, GraphObject.getYCoordinateComparator());
    	double minY = interactors.get(0).getBounds().getY();
    	double maxY = interactors.get(interactors.size() - 1).getBounds().getY() +
    			   interactors.get(interactors.size() - 1).getBounds().getHeight();
    	
    	Bounds pathwayBounds = diagramPane.getPathway().getPreferredSize();
    	double width = Math.max(pathwayBounds.getX() + pathwayBounds.getWidth(), maxX) -
    				Math.min(pathwayBounds.getX(), minX);
    	double height = Math.max(pathwayBounds.getY() + pathwayBounds.getHeight(), maxY) -
    				Math.min(pathwayBounds.getY(), minY);
    	
    	return new Bounds(minX,
    					  minY,
    					  width,
    					  height);
    }
    
    private void showDiagramImage(Canvas downloadDiagramCanvas) {
    	Window.open(downloadDiagramCanvas.toDataUrl("image/png"), null, null);
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
				diagramPane.zoomIn(pointClicked);
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
				diagramPane.zoomOut(pointClicked);
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
    
    // Versions of IE 10 are incompatible for some features
    private boolean compatibleBrowser() {
    	return Window.Navigator.getUserAgent().contains("IE 10.") ? false : true;
    }
    
    private void displayIncompatibleBrowserMessage() {
    	alertUser("This feature is not compatible with this browser version.  Please try another browser.");
    }
    
    private void alertUser(String message) {
    	AlertPopup.alert(message);
    	hide();
    }
}

