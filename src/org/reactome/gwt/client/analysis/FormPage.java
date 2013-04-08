/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.pages.Page;
import org.reactome.gwt.client.services.DataExampleServiceAsync;
import org.reactome.gwt.client.services.ExpressionDataExampleService;
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
 * Creates page for uploading data.
 * 
 * The state hash for this page contains the following items:
 * 
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class FormPage extends Page {
	private int TEXT_AREA_WIDTH = BASE_PANEL_WIDTH - 25;
	private DataExampleServiceAsync dataExampleService;
	private String nextPage = null;
	private FormPanel form = new FormPanel();
	private String analysisName = null;
	private String action = null;
	private VerticalPanel dataEntryPanel = new VerticalPanel();
	private VerticalPanel innerDataEntryPanel = new VerticalPanel();
	private TextArea pastedDataTextArea;
	private Button analyseButton = new Button("Analyse");
	private Button exampleButton = new Button("Example");
	private Button clearButton = new Button("Clear");
	private FileUpload upload = new FileUpload();
	
	public FormPage(ReactomeGWT controller) {
    	super();
		setTitle("Upload your data for analysis"); // generic title
		setDataExampleService(GWT.create(ExpressionDataExampleService.class)); // default, can be used with pathway analysis too, at a stretch
		this.controller = controller;
	}

	public void setDataExampleService(Object dataExampleService) {
		this.dataExampleService = (DataExampleServiceAsync)dataExampleService;
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
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
		form.setAction(getAction());
	    
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

		pastedDataTextArea.setName(analysisName);
		innerDataEntryPanel.add(pastedDataTextArea);

		HorizontalBipolarInnerPanel formMiddleRow = new HorizontalBipolarInnerPanel();
	    upload.setName(analysisName);
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
				// Disable the buttons until analysis is complete
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
	
	/**
	 *  Deal with the event generated when form submission is complete.
	 *  This gets the ID for the analysis from the server and passes it
	 *  on to the results page.  It also switches to the next page.
	 */
	protected void formAddSubmitCompleteHandler() {
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (event == null) {
					showWarning("onSubmitComplete: event is null!!");
					return;
				}
				HashMap<String,Object> state = new HashMap<String,Object>();
				String results = AnalysisUtils.extractResultsFromSubmitCompleteEvent(event);
				if (results == null)
					showWarning("Server side error - cannot display expression data (results == null)\n");
				else if (results.length() == 0)
					showWarning("Server side error - cannot display expression data (zero-length results set)\n");
				else if ((upload.getFilename() == null || upload.getFilename().isEmpty()) && pastedDataTextArea.getText().isEmpty())
					showWarning("It looks like you have forgotten to paste or upload your data!\n");
				else {
					String analysisId = AnalysisUtils.stripQuotes(results);
					
					state.put(Constants.ANALYSIS_ID_KEY, analysisId);
					state.put(Constants.ANALYSIS_ACTION_URL_KEY, getAction());
					controller.createPage(getNextPage(), state);
				}
				
				// Enable the buttons once analysis is complete
				enableAllButtons();
				
				// Let the next page handle the wait cursor, to avoid synchronization issues.
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
		waitCursor(true);
		form.submit();
	}
}
