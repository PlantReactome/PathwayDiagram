/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.reactome.diagram.model.InteractorCanvasModel;
import org.reactome.diagram.model.InteractorCanvasModel.InteractorConfidenceScoreColourModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mogaleaf.client.common.widgets.ColorHandler;
import com.mogaleaf.client.common.widgets.SimpleColorPicker;

/**
 * 
 * @author weiserj
 *
 */
public class InteractionOverlayOptionsPopup extends DialogBox {
	private final String FILEIDPREFIX = "Interaction_File";
	
	private PathwayDiagramPanel diagramPane;
	private InteractorCanvasModel interactorCanvasModel;		
	
	public InteractionOverlayOptionsPopup(PathwayDiagramPanel dPane) {
		super(Boolean.FALSE, Boolean.TRUE);
		diagramPane = dPane;
		interactorCanvasModel = diagramPane.getInteractorCanvasModel();
		
		init();
	}
	
	public void init() { 		
		setText("Interaction Overlay Options");
		setWidget(optionsPanel());
		bringToFront(this);
	}
	
	private VerticalPanel optionsPanel() {		
		VerticalPanel optionsPanel = new VerticalPanel();
		optionsPanel.add(new InteractionDBOptions());
		optionsPanel.add(new InteractorColorOptions());
		optionsPanel.add(getClosePopupButton());
		
		return optionsPanel;
	}
		
	private void bringToFront(Widget widget) {
		widget.getElement().getStyle().setZIndex(2);
	}
		
	private Button getClosePopupButton() {
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
			
		return closeButton;				
	}
		
	private void setTableRows(FlexTable table, Map<String, Widget> rows) {
		for (Entry<String, Widget> row : rows.entrySet()) {
			String text = row.getKey();
			Widget widget = row.getValue();
			
			Integer index = table.getRowCount();
			
			table.setText(index, 0, text);
			table.setWidget(index, 1, widget);
		}
	}		

	private class InteractionDBOptions extends DecoratorPanel {
		private FlexTable optionsTable;
		
		public InteractionDBOptions() {
			optionsTable = new FlexTable();
			setTableRows(optionsTable, getRows());
			setWidget(optionsTable);
		}
		
		private Map<String, Widget> getRows() {
			Map<String, Widget> rows = new LinkedHashMap<String, Widget>();
			
			rows.put("Interaction Database", getInteractorDBListBox());
			rows.put("Upload a file", getFileUploadButton());
			rows.put("Clear Overlay", getClearOverlayButton());
			rows.put("Submit a new PSICQUIC Service", getPSICQUICServiceButton());
			
			return rows;
		}
		
		private ListBox getInteractorDBListBox() {
			ListBox interactorDBListBox = interactorCanvasModel.getInteractorDBListBox();
			String currentDBSelection = interactorCanvasModel.getInteractorDatabase();
		
			setSelectedItem(interactorDBListBox, currentDBSelection);
		
			return interactorDBListBox;			
		}	
			
		private void setSelectedItem(ListBox interactorDBListBox, String currentDBSelection) {			
			for (Integer i = 0; i < interactorDBListBox.getItemCount(); i++) {
				// User uploaded files need the file id stored previously in the list box value, but all other list box items have 
				// the interaction database name as their text
				String selection = (currentDBSelection.contains(FILEIDPREFIX)) ? interactorDBListBox.getValue(i) : interactorDBListBox.getItemText(i);
				
				if (selection.equals(currentDBSelection)) {
					interactorDBListBox.setSelectedIndex(i);
					break;
				}	
			}										
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
	
	private class InteractorColorOptions extends DecoratorPanel {		
		private FlexTable optionsTable;
		
		private InteractorConfidenceScoreColourModel confidenceLevelScoreModel;
		private TextBox confidenceLevelThreshold;
		private Label aboveThresholdColor;
		private Label belowThresholdColor;
		private CheckBox coloringMode;
		
		public InteractorColorOptions() {
			confidenceLevelScoreModel = interactorCanvasModel.getConfidenceScoreColourModel();
			
			optionsTable = new FlexTable();
			initWidgets();
			setTableRows(optionsTable, getRows());
			setWidget(optionsTable);
		}
		
		private void initWidgets() {
			confidenceLevelThreshold = getConfidenceLevelThresholdTextBox();
			aboveThresholdColor = getColorPaletteLabel(confidenceLevelScoreModel.getColourAboveThreshold(), new InteractorAboveThresholdColorHandler());
			belowThresholdColor = getColorPaletteLabel(confidenceLevelScoreModel.getColourBelowThreshold(), new InteractorBelowThresholdColorHandler());
			coloringMode = getColoringModeCheckBox();
		}
		
		private Map<String, Widget> getRows() {
			Map<String, Widget> rows = new LinkedHashMap<String, Widget>();
		
			rows.put("Set confidence level threshold:", confidenceLevelThreshold);
			rows.put("Above Threshold:", aboveThresholdColor);
			rows.put("Below Threshold", belowThresholdColor);
			rows.put("Coloring Mode", coloringMode);
		
			return rows;
		}

		private TextBox getConfidenceLevelThresholdTextBox() {
			final TextBox thresholdLevelTextBox = new TextBox();
			thresholdLevelTextBox.setText(confidenceLevelScoreModel.getConfidenceLevelThreshold().toString());
			thresholdLevelTextBox.setVisibleLength(5);
			thresholdLevelTextBox.addKeyUpHandler(new KeyUpHandler() {
				private Double thresholdLevel = confidenceLevelScoreModel.getConfidenceLevelThreshold(); 
				
				@Override
				public void onKeyUp(KeyUpEvent event) {						
					Double newThresholdLevel;
					try {	
						newThresholdLevel = Double.parseDouble(thresholdLevelTextBox.getText());
					} catch (NumberFormatException e) {
						return; // Don't attempt to set a new confidence level threshold value if it isn't a number
					}
							
					// Nothing to be done if the new threshold's numeric value hasn't changed
					// (e.g. 0.50 becoming 0.5)
					if (thresholdLevel == newThresholdLevel)
						return;
					
					confidenceLevelScoreModel.setConfidenceLevelThreshold(newThresholdLevel);
					
					thresholdLevel = newThresholdLevel;
				}
				
			});
			
			return thresholdLevelTextBox;
		}

		private abstract class InteractorColorHandler implements ColorHandler {
			private Label interactorColorLabel;
			
			public void newColorSelected(String color) {
				setBackgroundColor(interactorColorLabel, color);					
			}
			
			public void setColorLabel(Label colorLabel) {
				interactorColorLabel = colorLabel;
			}
		}
		
		private class InteractorAboveThresholdColorHandler extends InteractorColorHandler {
			
			public void newColorSelected(String color) {
				super.newColorSelected(color);
				confidenceLevelScoreModel.setColourAboveThreshold(color);
			}			
		}
		
		private class InteractorBelowThresholdColorHandler extends InteractorColorHandler {
			
			public void newColorSelected(String color) {
				super.newColorSelected(color);
				confidenceLevelScoreModel.setColourBelowThreshold(color);
			}
		}
		
		private Label getColorPaletteLabel(String defaultColor, final InteractorColorHandler colorHandler) {
			final Label paletteColor = new Label();
			setBackgroundColor(paletteColor, defaultColor);
			paletteColor.setPixelSize(35, 35);
			colorHandler.setColorLabel(paletteColor);
			
			paletteColor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					SimpleColorPicker palette = new SimpleColorPicker();
					palette.addListner(colorHandler);					
					bringToFront(palette);
					setPopupPositionAndShow(paletteColor, palette);
					setCursor(Style.Cursor.DEFAULT);
				}				
			});
			
			changeMouseCursorToPointerOnHovering(paletteColor);
			
			return paletteColor;
		}
		
		private void setPopupPositionAndShow(final Label paletteColor, final SimpleColorPicker palette) {
			palette.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					Integer left = paletteColor.getAbsoluteLeft() + paletteColor.getOffsetWidth();
					Integer top = paletteColor.getAbsoluteTop();
							
					palette.setPopupPosition(left, top);
				}
			});
		}
		
		private void changeMouseCursorToPointerOnHovering(Label paletteColor) {			
			paletteColor.addDomHandler(cursorToPointerOnMouseOver(), MouseOverEvent.getType()); 
			paletteColor.addDomHandler(cursorToDefaultOnMouseOut(), MouseOutEvent.getType());						
		}
		
		private MouseOverHandler cursorToPointerOnMouseOver() {
			MouseOverHandler mouseOverHandler = new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					setCursor(Style.Cursor.POINTER);
				}				
			};
			
			return mouseOverHandler;			
		}
		
		private MouseOutHandler cursorToDefaultOnMouseOut() {
			MouseOutHandler mouseOutHandler = new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					setCursor(Style.Cursor.DEFAULT);
				}				
			};
			
			return mouseOutHandler;
		}
		
		private void setCursor(Style.Cursor cursor) {
			getStyle(InteractionOverlayOptionsPopup.this).setCursor(cursor);			
		}
		
		private void setBackgroundColor(Widget widget, String color) {
			getStyle(widget).setBackgroundColor(color);
		}

		private Style getStyle(Widget widget) {
			return widget.getElement().getStyle();
		}
			
		private CheckBox getColoringModeCheckBox() {			
			final CheckBox coloringMode = new CheckBox("Turn on colouring mode");
			
			coloringMode.setValue(confidenceLevelScoreModel.getColoringModeOn()); // Off by default
			coloringMode.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					confidenceLevelScoreModel.setColoringModeOn(coloringMode.getValue());					
				}
				
			});
			
			return coloringMode;
		}	    
	}	
}
