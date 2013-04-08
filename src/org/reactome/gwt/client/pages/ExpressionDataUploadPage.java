/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.services.ExpressionDataExampleService;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;

import com.google.gwt.core.client.GWT;

/**
 * Creates page for uploading expression data.
 * 
 * The state hash for this page contains the following items:
 * 
 * rawData     	User-entered tab delimited rawData.
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class ExpressionDataUploadPage extends DataUploadPage {
	public ExpressionDataUploadPage(ReactomeGWT controller) {
    	super(controller);
		setTitle("Upload expression data");
		setDataExampleService(GWT.create(ExpressionDataExampleService.class));
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "Takes gene expression data (and also numerical proteomics data) and shows how expression levels affect reactions and pathways in living organisms.  May be time-consuming, depending on the number of identifiers you are submitting; less than 5000: a few seconds, 5000 - 10000: a few minutes, 10000 or more: 10 minutes or longer.";
		moreDescriptionText = "Your data should be formatted as a tab-delimited file, where the first column contains identifiers and subsequent columns contain numerical expression data.  You may paste your data into the supplied text area, or you can also upload it from a file.  Click on the \"Analyse\" button to perform this analysis.";
		videoTutorialButton = new ExternalNewPageButton("http://www.youtube.com/watch?v=TczSuUtcffE&context=C3204927ADOEgsToPDskLe-JNwS5ZalRJi-nIF1_vn", "Video Tutorial", "_blank");
		
		super.onModuleLoad();
	}
	
	public void analyze() {
		UPLOAD_ACTION_URL += "expressionDataUpload";
		form.setAction(UPLOAD_ACTION_URL);
		nextPage = "ExpressionPerPathwayPage";
		form.submit();
	}
}
