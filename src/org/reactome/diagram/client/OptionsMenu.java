/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;


import java.util.Iterator;

import org.reactome.diagram.model.InteractorCanvasModel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author weiserj
 *
 */
public class OptionsMenu extends PopupPanel {
       
    private PathwayDiagramPanel diagramPane;
    private Image optionsIcon;
    private Boolean showing;
    
    public OptionsMenu(PathwayDiagramPanel diagramPane) {
        super(true); // Hide when clicking outside pop-up
    	this.diagramPane = diagramPane;
    	this.optionsIcon = new Image (getResources().options());
        this.showing = false;
    	init();
    }
    
    private void init() {
    	setWidget(getOptionsMenu());
    	optionsIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (showing) {  
					hide();
					showing = false;
				} else {
					showMenuUnderOptionsIcon();
					showing = true; 
				}
			}	
				
    	});
    
    	getElement().getStyle().setZIndex(2);
    }    	   

    private MenuBar getOptionsMenu() {
    	MenuBar optionsMenu = new MenuBar(true);
    	
    	optionsMenu.addItem(getSearchBarMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getDownloadDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getInteractionOverlayMenuItem());
    	
    	setOptionsMenuStyle(optionsMenu);
    	
    	return optionsMenu;
    }
    
    
    private void setOptionsMenuStyle(MenuBar optionsMenu) {
		Style menuStyle = optionsMenu.getElement().getStyle();
		menuStyle.setOpacity(1);		
	}

	private MenuItem getSearchBarMenuItem() {
    	MenuItem searchBar = new MenuItem("Search Diagram (ctrl-f/cmd-f)", new Command() {
    		
    		@Override
    		public void execute() {
    			diagramPane.showSearchPopup();
    			hide();
    		}
    	});
    	
    	return searchBar;
    }
    
    private MenuItem getDownloadDiagramMenuItem() {
        MenuItem downloadDiagram = new MenuItem("Download Diagram", new Command() {

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
				InteractionOverlayOptionsPopup optionsPopup = new InteractionOverlayOptionsPopup();
				optionsPopup.center();
				
				hide();
			}
    		
    	});
		return interactionOverlay;    	
    }
        
    interface Resources extends ClientBundle {
    	@Source("Options.png")
    	ImageResource options();
    }
    
    private Resources getResources() {
    	return GWT.create(Resources.class);
    }
    
    private void showMenuUnderOptionsIcon() {
    	
    	setPopupPositionAndShow(new PopupPanel.PositionCallback() {
    		
    		@Override
    		public void setPosition(int offsetWidth, int offsetHeight) { 
    			// 'Right-justify' menu under the options icon
    			Integer menuX = getOptionsIcon().getAbsoluteLeft() + getOptionsIcon().getOffsetWidth() - offsetWidth; 
    			Integer menuY = getOptionsIcon().getAbsoluteTop() + getOptionsIcon().getOffsetHeight();
    	
    			setPopupPosition(menuX, menuY);
    		}					
		});
    }
    
	public Image getOptionsIcon() {
		return optionsIcon;
	}

	public void setOptionsIcon(Image optionsIcon) {
		this.optionsIcon = optionsIcon;
	}

	public void updateIconPosition() {
		AbsolutePanel container = (AbsolutePanel) getOptionsIcon().getParent();
		Integer buffer = 4;
		
		// Top-right corner
		Integer left = container.getOffsetWidth() - getOptionsIcon().getOffsetWidth() - buffer;

		// TODO This is a hack to get correct initial placement of the icon when the container width
		// 		and icon width are unavailable making the left equal to the negative buffer.
		// 		THIS SHOULD BE REPLACED WITH DYNAMIC CALCULATION OF INITIAL POSITION
		final Integer CONTAINERBUFFER = 40;
		final Integer ICONWIDTH = 30;
		if (left == -buffer)
			left = Window.getClientWidth() - CONTAINERBUFFER - ICONWIDTH - buffer;
		
		
		Integer top = buffer;
		
		container.setWidgetPosition(getOptionsIcon(), left, top);
	}
	
	private class InteractionOverlayOptionsPopup extends DialogBox {
		private final String FILEIDPREFIX = "Interaction_File";
		private InteractorCanvasModel interactorCanvasModel;
		private FlexTable optionsTable;
		
		public InteractionOverlayOptionsPopup() {
			super(Boolean.FALSE, Boolean.TRUE);
			setText("Interaction Overlay Options");
			init();
		}
		
		public void init() { 
			interactorCanvasModel = diagramPane.getInteractorCanvasModel();
			
			optionsTable = new FlexTable();
			optionsTable.setText(0, 0, "Interaction Database:");
			optionsTable.setWidget(0, 1, getInteractorDBListBox());
			optionsTable.setText(1, 0, "Upload a file");
			optionsTable.setWidget(1, 1, getFileUploadButton());
			optionsTable.setText(2, 0, "Clear Overlay");
			optionsTable.setWidget(2, 1, getClearOverlayButton());
			optionsTable.setText(3, 0, "Submit a new PSICQUIC service");
			optionsTable.setWidget(3, 1, getPSICQUICServiceButton());
			optionsTable.setWidget(4, 0, new Button("Close", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
				
			}));
			
			optionsTable.getFlexCellFormatter().setColSpan(4, 0, 2);
			optionsTable.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_RIGHT);

			getElement().getStyle().setZIndex(2);
	//		addStyleName(resources.DialogBoxCss().gwtDialogBox());
			//addStyleDependentName(resources.DialogBoxCss().Caption());
			
			setWidget(optionsTable);
		}
		
		private ListBox getInteractorDBListBox() {
			ListBox interactorDBListBox = interactorCanvasModel.getInteractorDBListBox();
			String currentDBSelection = interactorCanvasModel.getInteractorDatabase();
			
			for (Integer i = 0; i < interactorDBListBox.getItemCount(); i++) {
				String selection;
				
				if (currentDBSelection.contains(FILEIDPREFIX))
					selection = interactorDBListBox.getValue(i);
				else
					selection = interactorDBListBox.getItemText(i);
					
				if (selection.equals(currentDBSelection)) {
					interactorDBListBox.setSelectedIndex(i);
					break;
				}	
			}
			
			return interactorDBListBox;			
		}
		
		private Button getFileUploadButton() {
			Button fileUploadButton = new Button("Select File", new ClickHandler() {
				private FormPanel form;
				
				private TextBox fileLabel;
				private FileUpload fileUpload;
				private VerticalPanel fileTypePanel;
				
				@Override
				public void onClick(ClickEvent event) {
					final DialogBox uploadFileDialogBox = new DialogBox(Boolean.FALSE, Boolean.TRUE);
					uploadFileDialogBox.setText("Upload a file");
					
					FlexTable uploadFileTableLayout = new FlexTable();
					
					form = new FormPanel();
					form.setAction(diagramPane.getController().getHostUrl() + "uploadInteractionFile");
					form.setEncoding(FormPanel.ENCODING_MULTIPART);
					form.setMethod(FormPanel.METHOD_POST);
					
					fileLabel = new TextBox(); 
					fileLabel.setName("fileLabel");
					
					fileUpload = new FileUpload();
					fileUpload.setName("file");
					
					fileTypePanel = createFileTypePanel();
					
					form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

						@Override
						public void onSubmitComplete(SubmitCompleteEvent event) {
							String userLabel = fileLabel.getText();
							String results = event.getResults();
							
							if (!results.contains(FILEIDPREFIX)) {
								AlertPopup.alert("Our service could not process your file -- please check the format and try again");
								GWT.log(results);
								return;
							}
						
							String serviceKey = results.substring(results.indexOf(">") + 1, results.lastIndexOf("<"));
							
							interactorCanvasModel.addNewUploadedUserFile(userLabel, serviceKey);
							interactorCanvasModel.setInteractorDatabase(serviceKey);
							optionsTable.setWidget(0, 1, getInteractorDBListBox());
							uploadFileDialogBox.hide();
						}
						
					});
					
					
					Button submitButton = new Button("Submit", new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							String inputErrors = getInputErrors();
							if (inputErrors.isEmpty()) {
								form.submit();
							} else {
								AlertPopup.alert(inputErrors);
							}
						}
						
					});
					
					Button cancelButton = new Button("Cancel", new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							uploadFileDialogBox.hide();
						}
						
					});
					
					HorizontalPanel submissionPanel = new HorizontalPanel();
					submissionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
					submissionPanel.add(submitButton);
					submissionPanel.add(cancelButton);					
					
					uploadFileTableLayout.setText(0, 0, "Label for data set");
					uploadFileTableLayout.setWidget(0, 1, fileLabel);
					uploadFileTableLayout.setText(1, 0, "Select a file to upload");
					uploadFileTableLayout.setWidget(1, 1, fileUpload);
					uploadFileTableLayout.setText(2, 0, "Choose file type");
					uploadFileTableLayout.setWidget(2, 1, fileTypePanel);
					
					uploadFileTableLayout.setWidget(3, 0, submissionPanel);
					
					form.setWidget(uploadFileTableLayout);
					
					uploadFileDialogBox.setWidget(form);
					uploadFileDialogBox.getElement().getStyle().setZIndex(2);
					uploadFileDialogBox.center();
				}
				
				private VerticalPanel createFileTypePanel() {
					VerticalPanel fileTypePanel = new VerticalPanel();
					
					String rbGroup = "fileType";
					RadioButton geneToGene = new RadioButton(rbGroup, "Gene pairs (tab delimited gene names)");
					RadioButton proteinToProtein = new RadioButton(rbGroup, "Protein pairs (tab delimited uniprot accessions)");
					RadioButton psimitab = new RadioButton(rbGroup, "PSI-MITAB");
					
					geneToGene.setFormValue("gene");
					proteinToProtein.setFormValue("protein");
					psimitab.setFormValue("psimitab");
					
					fileTypePanel.add(geneToGene);
					fileTypePanel.add(proteinToProtein);
					fileTypePanel.add(psimitab);
					
					return fileTypePanel;					
				}
				
				private String getInputErrors() {
					String errors = new String();
					
					if (fileLabel.getText().trim().length() == 0)
						errors = errors.concat("Please enter a label for the file <br />");
						
					if (fileUpload.getFilename().length() == 0)
						errors = errors.concat("Please select a file <br />");
					
					Boolean noButtonChecked = Boolean.TRUE;
					Iterator<Widget> fileTypeIterator = fileTypePanel.iterator();
					while (fileTypeIterator.hasNext()) {
						RadioButton fileTypeButton = (RadioButton) fileTypeIterator.next();
						if (fileTypeButton.getValue()) {
							noButtonChecked = Boolean.FALSE;
						}
					}
					
					if (noButtonChecked)
						errors = errors.concat("Please choose a file type");
					
					return errors;					
				}
				
			});
			
			return fileUploadButton;		
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
}
