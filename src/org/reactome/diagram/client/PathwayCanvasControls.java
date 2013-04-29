/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.InteractorCanvasModel;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This customized FlexTable is used to set up controls for PathwayCanvas: e.g. 
 * zooming, translation. The set up basically is copied from Maulik's original code.
 * @author gwu
 *
 */
public class PathwayCanvasControls extends FlexTable {
	// The following resources is used to load images for controls
    interface Resources extends ClientBundle {
        @Source("Reset.png")
        ImageResource reset();
        @Source("Plus.png")
        ImageResource plus();
        @Source("Minus.png")
        ImageResource minus();
        @Source("Left.png")
        ImageResource left();
        @Source("Right.png")
        ImageResource right();
        @Source("Up.png")
        ImageResource up();
        @Source("Down.png")
        ImageResource down();
    }
    
    private PathwayDiagramPanel diagramPane;
    private static Resources resources;
    
    public PathwayCanvasControls(PathwayDiagramPanel diagramPane) {
        this.diagramPane = diagramPane;
        init();
    }
    
    private static Resources getResource() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }
    
    private void init() {
        Resources resources = getResource();
        Image refresh = new Image(resources.reset());
        refresh.setAltText("reset");
        refresh.setTitle("reset");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.reset();
                diagramPane.update();
            }
        });
        
        Image zoomPlus = new Image(resources.plus());
        zoomPlus.setAltText("zoom in");
        zoomPlus.setTitle("zoom in");
        zoomPlus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.scale(Parameters.ZOOMFACTOR);
                diagramPane.update();
            }
        });
        
        Image zoomMinus = new Image(resources.minus());
        zoomMinus.setAltText("zoom out");
        zoomMinus.setTitle("zoom out");
        zoomMinus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.scale(1 / Parameters.ZOOMFACTOR);
                diagramPane.update();
            }
        });
        
        Image scrollLeft = new Image(resources.left());
        scrollLeft.setAltText("move left");
        scrollLeft.setTitle("move left");
        scrollLeft.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(Parameters.MOVEX, 0);
                diagramPane.update();
            }
        });
        
        Image scrollTop = new Image(resources.up());
        scrollTop.setAltText("move up");
        scrollTop.setTitle("move up");
        scrollTop.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(0, Parameters.MOVEY);
                diagramPane.update();
            }
        });
        
        Image scrollBottom = new Image(resources.down());
        scrollBottom.setAltText("move down");
        scrollBottom.setTitle("move down");
        scrollBottom.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(0, -Parameters.MOVEY);
                diagramPane.update();
            }
        });
        
        Image scrollRight = new Image(resources.right());
        scrollRight.setAltText("move right");
        scrollRight.setTitle("move right");
        scrollRight.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(-Parameters.MOVEX, 0);
                diagramPane.update();
            }
        });
    
        SearchPanel searchPanel = new SearchPanel();
        
        Button downloadDiagram = new Button("Download Diagram", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {							
				if (diagramPane.getPathway() == null) {
					AlertPopup.alert("Please choose a pathway to download.");
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas(diagramPane.getCanvasList().get(0));
				
				Context2d context = downloadCanvas.getContext2d();
				
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList())					
					context.drawImage(canvasLayer.getCanvasElement(),0,0);				
				
				String canvasUrl = downloadCanvas.toDataUrl("application/octet-stream");
				Window.open(canvasUrl, null, null);
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
               
//        setButtonSize(refresh);
//        setButtonSize(zoomPlus);
//        setButtonSize(zoomMinus);
//        setButtonSize(scrollLeft);
//        setButtonSize(scrollRight);
//        setButtonSize(scrollBottom);
//        setButtonSize(scrollTop);
        
        FlexCellFormatter cellFormatter = getFlexCellFormatter();
        setWidget(0, 0, refresh);
        cellFormatter.setRowSpan(0, 0, 2);
        setWidget(0, 1, zoomPlus);
        cellFormatter.setRowSpan(0, 1, 2);
        setWidget(0, 2, zoomMinus);
        cellFormatter.setRowSpan(0, 2, 2);
        setWidget(0, 3, scrollLeft);
        cellFormatter.setRowSpan(0, 3, 2);
        setWidget(0, 4, scrollTop);
        setWidget(1, 0, scrollBottom);
        setWidget(0, 5, scrollRight);
        cellFormatter.setRowSpan(0, 5, 2);				
		setWidget(0, 6, searchPanel);
		cellFormatter.setRowSpan(0, 6, 2);
		setWidget(0, 7, downloadDiagram);
		cellFormatter.setRowSpan(0, 7, 2);
	}
	
    public void addInteractionOverlayButton() {
    	Button interactionOverlay = new Button("Interaction Overlay Options...", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				InteractionOverlayOptionsPopup optionsPopup = new InteractionOverlayOptionsPopup();
				optionsPopup.center();
			}
    		
    	});
    	
    	setWidget(0, 8, interactionOverlay);
    	getFlexCellFormatter().setRowSpan(0, 8, 2);
    	
    }
    
	private class InteractionOverlayOptionsPopup extends DialogBox {
		private InteractorCanvasModel interactorCanvasModel;
		private FlexTable optionsTable;	
		 
		public InteractionOverlayOptionsPopup() {
			super(true, true);
			setText("Interaction Overlay Options");
			init();
		}
		
		public void init() { 
			interactorCanvasModel = diagramPane.getInteractorCanvasModel();
			
			optionsTable = new FlexTable();
			optionsTable.setText(0, 0, "Interaction Database:");
			optionsTable.setWidget(0, 1, getInteractorDBListBox());
			optionsTable.setText(1, 0, "Upload a file");
			optionsTable.setWidget(1, 1, new FileUpload());
			optionsTable.setText(2, 0, "Clear Overlay");
			optionsTable.setWidget(2, 1, getClearOverlayButton());
			optionsTable.setText(3, 0, "Submit a new PSICQUIC service");
			optionsTable.setWidget(3, 1, getPSICQUICServiceButton());

			getElement().getStyle().setZIndex(2);
			setWidget(optionsTable);
		}
		
		private ListBox getInteractorDBListBox() {
			ListBox interactorDBListBox = interactorCanvasModel.getInteractorDBListBox();
			String currentDBSelection = interactorCanvasModel.getInteractorDatabase();
			
			for (Integer i = 0; i < interactorDBListBox.getItemCount(); i++) {
				if (interactorDBListBox.getItemText(i).equals(currentDBSelection)) {
					interactorDBListBox.setSelectedIndex(i);
					break;
				}	
			}
			
			return interactorDBListBox;			
		}

		private Button getClearOverlayButton() {
			Button clearButton = new Button("Clear", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					InteractorCanvas interactorCanvas = interactorCanvasModel.getInteractorCanvas();
					if (interactorCanvas != null) {
						interactorCanvas.removeAllProteins();	
					}
				}
			});
			
			return clearButton;
		}
		
		private Button getPSICQUICServiceButton() {
			Button serviceButton = new Button("New Service", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					final DialogBox newService = new DialogBox(Boolean.FALSE, Boolean.TRUE);
					newService.setText("Add New PSICQUIC Service");
					
					FlexTable newServiceWidgets = new FlexTable();
					
					final TextBox serviceName = new TextBox();
					final TextBox serviceUrl = new TextBox();
					newServiceWidgets.setText(0, 0, "Service Name");
					newServiceWidgets.setWidget(0, 1, serviceName);
					newServiceWidgets.setText(1, 0, "Service Url");
					newServiceWidgets.setWidget(1, 1, serviceUrl);
					newServiceWidgets.setWidget(2, 0, new Button("Add Service", new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							String name = serviceName.getText();
							String url = serviceUrl.getText();
							
							if (name.isEmpty() || url.isEmpty()) {
								AlertPopup.alert("Please specify a service name and url");
								return;
							}	
							
							url = SafeHtmlUtils.fromString(url).asString();
							interactorCanvasModel.addNewPSICQUICService(name, url);
							optionsTable.setWidget(0, 1, getInteractorDBListBox());
							newService.hide();
							//InteractionOverlayOptionsPopup.this.center();
						}
						
					}));
					newServiceWidgets.setWidget(2, 1, new Button("Cancel", new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							newService.hide();
						}
						
					}));
					
					newService.setWidget(newServiceWidgets);
					newService.getElement().getStyle().setZIndex(2);
					newService.center();
				}
				
			});
			
			return serviceButton;
		}
	}

	private class SearchPanel extends HorizontalPanel {
		private List<Long> matchingEntityIds;
		private Label searchLabel;
		private TextBox searchBox;
		private Label resultsLabel;
		private Button previousButton; 
		private Button nextButton;
		private Button selectAllButton;
		private Integer selectedIndex;
		private String searchString;
		
		public SearchPanel() {
			super();
			init();
		}
		
		private void init() {
			matchingEntityIds = new ArrayList<Long>();
			searchLabel = new Label("Find Reaction/Entity:");
			searchBox = new TextBox();
			searchBox.addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {	
					String newSearchString = searchBox.getText();
					
					if (!newSearchString.equalsIgnoreCase(searchString))
						doSearch(newSearchString);
					
					searchString = newSearchString;
				}
			});
			
			resultsLabel = new Label();
			
			previousButton = new Button("Previous", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectEntity(selectedIndex - 1);					
				}
				
			});
			
			nextButton = new Button("Next", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectEntity(selectedIndex + 1);
				}
				
			});
			
			selectAllButton = new Button("Select All", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					diagramPane.setSelectionIds(matchingEntityIds);
					selectedIndex = -1;
					resultsLabel.setText("Focused on: All " + matchingEntityIds.size());
				}
				
			});
			
			setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			add(searchLabel);
			add(searchBox);						
			add(previousButton);
			add(nextButton);
			add(selectAllButton);
			add(resultsLabel);
			
			enableButtons(Boolean.FALSE);
			
			Style style = this.getElement().getStyle();
			style.setPadding(1, Style.Unit.PX);
			style.setBorderColor("rgb(0, 0, 0)");
			style.setBorderWidth(1, Style.Unit.PX);	
		}
		
		// Returns a list of db ids for objects in the pathway diagram
		// which match the given query
		private List<Long> searchDiagram(String query) {
			List<Long> matchingObjectIds = new ArrayList<Long>();
			
			if (!query.isEmpty()) {
			
				List<GraphObject> pathwayObjects = diagramPane.getPathway().getGraphObjects(); 			
				for (GraphObject pathwayObject: pathwayObjects) {
					if (pathwayObject.getDisplayName() == null)
						continue;
					
					if (pathwayObject.getDisplayName().toLowerCase().contains(query.toLowerCase()))
						matchingObjectIds.add(pathwayObject.getReactomeId());
				}						
			}
				
			return matchingObjectIds;			
		}

		private void doSearch(String query) {
			if (query.isEmpty() || diagramPane.getPathway() == null) {
				resultsLabel.setText(null);
				matchingEntityIds.clear();
				enableButtons(Boolean.FALSE);
				return;
			}
			
			matchingEntityIds = searchDiagram(query);
			
			if (matchingEntityIds.isEmpty()) {
				enableButtons(Boolean.FALSE);
				resultsLabel.setText("No matches found for " + searchBox.getText());
				return;
			} else {
				enableButtons(Boolean.TRUE);
			}
			
			selectEntity(0);
		}
		
		private void selectEntity(Integer index) {
			if (index >= matchingEntityIds.size()) {
				index = 0;
			} else if (index < 0) {
				index = matchingEntityIds.size() - 1;
			}
			
			selectedIndex = index;
			
			resultsLabel.setText("Focused on: " + (index + 1) + " of " + matchingEntityIds.size());
			
			diagramPane.setSelectionId(matchingEntityIds.get(index));
		}
		
		private void enableButtons(Boolean enable) {
			previousButton.setEnabled(enable);
			nextButton.setEnabled(enable);
			selectAllButton.setEnabled(enable);
		}
	}
    
//    private void setButtonSize(PushButton btn) {
//        btn.setSize("10px", "10px");
//    }
    
}
