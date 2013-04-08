/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.services.DataExampleServiceAsync;
import org.reactome.gwt.client.widgets.panels.HorizontalBipolarInnerPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Base class for creating a page for uploading data.
 * 
 * The state hash for this page contains the following items:
 * 
 * rawData     		User-entered tab delimited rawData.
 * warningMessage	Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class DataUploadPage extends Page {
	protected int TEXT_AREA_WIDTH = BASE_PANEL_WIDTH - 25;
	protected String UPLOAD_ACTION_URL = GWT.getModuleBaseURL(); // root URL, needs to be completed
	protected String rawData = "";
	protected DataExampleServiceAsync dataExampleService;
	protected String nextPage = null;
	protected FormPanel form = new FormPanel();
	protected VerticalPanel dataEntryPanel = new VerticalPanel();
	protected VerticalPanel innerDataEntryPanel = new VerticalPanel();
	protected TextArea pastedDataTextArea;
	protected Button analyseButton = new Button("Analyse");
	protected Button exampleButton = new Button("Example");
	protected Button clearButton = new Button("Clear");
	protected FileUpload upload = new FileUpload();
	
	public DataUploadPage(ReactomeGWT controller) {
		super();
		this.controller = controller;
	}

	public void onModuleLoad() {
		rawData = (String) state.get("rawData");
		warningMessage = (String) state.get("warningMessage");
		super.onModuleLoad();
		
		dataEntryPanel.setStyleName("pale_blue_textbox"); // CSS
		dataEntryPanel.setWidth("100%");
		basePanel.add(dataEntryPanel);
		
		form.setStyleName("vertical_inner_panel"); // CSS

		// Because we're going to add a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    form.add(innerDataEntryPanel);
	    
		innerDataEntryPanel.setStyleName("textbox"); // CSS
		dataEntryPanel.add(form);

		pastedDataTextArea = new TextArea() {
			/**
			 * Override this, so that however text gets put into the
			 * test area, the data also gets stored for use in the
			 * history mechanism.
			 */
			@Override
			public void setText(String text) {
				super.setText(text);
				rawData = text;
				
				if (text != null && ! text.isEmpty()) {
					basePanel.remove(warningMessageLabel);
					state.remove("warningMessage");
				}
			}
		};
		pastedDataTextArea.setSize(TEXT_AREA_WIDTH + "px", "200px");

		HorizontalBipolarInnerPanel formTopRow = new HorizontalBipolarInnerPanel();
		HTML pasteOrUploadLabel = new HTML("<b>Paste or upload your data:</b>");
		formTopRow.addLeft(pasteOrUploadLabel);
		exampleButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Disable buttons until the data has been loaded
				disableAllButtons();
				
				// Insert example rawData into the text area
				dataExampleService.getData(
						new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								// Enable buttons if something went wrong
								enableAllButtons();
							}

							@Override
							public void onSuccess(String result) {
								pastedDataTextArea.setText(result);
																
								// Enable buttons once the data has been loaded
								enableAllButtons();
							}
						});
			}
			
		});
		formTopRow.addRight(exampleButton);
		innerDataEntryPanel.add(formTopRow);

		pastedDataTextArea.setName("QUERY");
		if (rawData !=null)
			pastedDataTextArea.setText(rawData);
		innerDataEntryPanel.add(pastedDataTextArea);

		HorizontalBipolarInnerPanel formMiddleRow = new HorizontalBipolarInnerPanel();
	    upload.setName("FILE");
	    formMiddleRow.addLeft(upload);
	    clearButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pastedDataTextArea.setText("");
			}
			
		});
	    formMiddleRow.addRight(clearButton);
	    innerDataEntryPanel.add(formMiddleRow);

	    analyseButtonAddClickHandler();
		innerDataEntryPanel.add(analyseButton);

		formAddSubmitCompleteHandler();
		formAddSubmitHandler();
	}
	
	protected void analyseButtonAddClickHandler() {
		analyseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Disable the buttons once analysis is complete
				disableAllButtons();
				
				analyze();
			}
		});
	}
	
	// Deal with the event generated when a form has been submitted.
	protected void formAddSubmitHandler() {
		// Re-enable buttons if the user cancels the submit.
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				if (event.isCanceled()) {
					enableAllButtons();
				}
			}
		});
	}
	
	// Deal with the event generated when form submission is complete
	protected void formAddSubmitCompleteHandler() {
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (event == null) {
					showWarning("onSubmitComplete: event is null!!");
					return;
				}
				
				// If the user didn't actually enter any data, present a warning message,
				// otherwise present a table of pathways.
				if ((upload.getFilename() == null || upload.getFilename().isEmpty()) && pastedDataTextArea.getText().isEmpty()) {
					HashMap state = new HashMap();
					state.put("rawData", rawData);
					state.put("warningMessage", "It looks like you have forgotten to paste or upload your data!");
					String className = getClasseName();
					controller.createPage(className, state);
				} else
					controller.createPage(nextPage, null);
				
				// Enable the buttons once analysis is complete
				enableAllButtons();
			}
		});
	}
	
	protected void disableAllButtons() {
		analyseButton.setEnabled(false);
		exampleButton.setEnabled(false);
		clearButton.setEnabled(false);
	}
	
	protected void enableAllButtons() {
		analyseButton.setEnabled(true);
		exampleButton.setEnabled(true);
		clearButton.setEnabled(true);
	}
	
	/**
	 * The action which is performed when the user clicks the "Analyze" button.
	 * This default implementation simply runs the form's "submit" method.  You
	 * should override this if you want to do something more complicated.
	 */
	public void analyze() {
	    form.setAction(UPLOAD_ACTION_URL);
		form.submit();
	}
	
	public void setDataExampleService(Object dataExampleService) {
		this.dataExampleService = (DataExampleServiceAsync)dataExampleService;
	}

	public HashMap getState() {
		super.getState();
		state.put("rawData", rawData);
		
		return state;
	}
	
	public void destroy() {
//		dataEntryPanel.remove(form); // TODO: workaround for GWT History bug
	}
}
