/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.ReferrerUrlParams;
import org.reactome.gwt.client.services.PathwayAnalysisDataExampleService;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates page for uploading pathway analysis data.
 * 
 * The state hash for this page contains the following items:
 * 
 * rawData     	User-entered tab delimited rawData.
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class PathwayAnalysisDataUploadPage extends DataUploadPage {
    private RadioButton pathwayAssignmentButton = new RadioButton("radioGroup", "<b>ID mapping and pathway assignment.</b>  Takes your list of IDs and finds the corresponding pathways from Reactome, plus the corresponding UniProt IDs.", true);
    private RadioButton overrepresentationButton = new RadioButton("radioGroup", "<b>Overepresentation analysis.</b>  Finds the Reactome pathways in which IDs in your list are strongly enriched - can help to understand the biological context of your data.", true);
    private RadioButton envisionButton = new RadioButton("radioGroup", "<b>ENVISION.</b>  Analyze your data with a whole suite of EBI tools.", true);
    private RadioButton rSpiderButton = new RadioButton("radioGroup", "<b>R-Spider.</b>  Visualize your pathway data interactively.", true);
    private RadioButton gProfilerButton = new RadioButton("radioGroup", "<b>G-Profiler.</b>  Profile your data in various different ways.", true);
    private RadioButton davidButton = new RadioButton("radioGroup", "<b>David.</b>  Profile your data in various different ways.", true);
    private int analysis = 0;
    public static int ANALYSIS_OVERREPRESENTATION = 1;
    
    public PathwayAnalysisDataUploadPage(ReactomeGWT controller) {
    	super(controller);
		setTitle("Pathway Analysis");
		setDataExampleService(GWT.create(PathwayAnalysisDataExampleService.class));
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "Allows you to analyse a list of protein, gene, expression data or compound identifiers and determine how they are likely to affect pathways.";
		moreDescriptionText = "A choice of several analyses is possible; you can select one of them by clicking the appropriate radio button lower down in the page.  Click on the \"Analyse\" button to perform this analysis.";
		videoTutorialButton = new ExternalNewPageButton("http://www.youtube.com/watch?v=9cFIDt9v0wY&context=C3204927ADOEgsToPDskLe-JNwS5ZalRJi-nIF1_vn", "Video Tutorial", "_blank");
		
		super.onModuleLoad();
		
		UPLOAD_ACTION_URL += "pathwayAnalysisDataUpload";

		VerticalPanel selectAnalysisPanel = new VerticalPanel();
	    basePanel.add(selectAnalysisPanel);
		
		HTML selectAnalysisLabel = new HTML("<h2 class=\"section_heading\">Select your desired analysis tool</h2>\n");
		selectAnalysisPanel.add(selectAnalysisLabel);
		
		String analysisString = (String) state.get("analysis");
		if (analysisString != null) {
			analysis = (new Integer(analysisString)).intValue();
			if (analysis > 0) {
				innerDataEntryPanel.add(new Hidden("RETURN_TO_SENDER", "1"));
				innerDataEntryPanel.add(new Hidden("NO_REDIRECT", "1"));
			}
		}
		checkAnalysis();
		
		HTML inhouseLabel = new HTML("<p><b>Inhouse services:</b><p>\n");
		selectAnalysisPanel.add(inhouseLabel);
	    selectAnalysisPanel.add(pathwayAssignmentButton);
	    overrepresentationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				innerDataEntryPanel.add(new Hidden("RETURN_TO_SENDER", "1"));
				innerDataEntryPanel.add(new Hidden("NO_REDIRECT", "1"));
			}
	    });
	    selectAnalysisPanel.add(overrepresentationButton);
//		HTML externalLabel = new HTML("<p><b>External services:</b><p>\n");
//		selectAnalysisPanel.add(externalLabel);
//		selectAnalysisPanel.add(envisionButton);
//		selectAnalysisPanel.add(rSpiderButton);
//		selectAnalysisPanel.add(gProfilerButton);
////		selectAnalysisPanel.add(davidButton);
//	    
//		HorizontalPanel disclaimerPanel = new HorizontalPanel();
//		disclaimerPanel.setStyleName("horizontal_inner_panel"); // CSS
//		HTML disclaimerLabel = new HTML("<img src=\"images/alert.png\"/> Some analyses are quite time consuming, it may be necessary to refresh the web pages for these analyses several times before the final results are displayed. Reactome takes no responsibility for the accuracy of data provided by external services.\n");
//		disclaimerLabel.setWidth("400px");
//		disclaimerLabel.setHorizontalAlignment(HTML.ALIGN_LEFT);
//		basePanel.add(disclaimerLabel);
	}
	
	/**
	 * Put a check dot in the appropriate analysis button.
	 * 
	 * @param analysis
	 */
	private void checkAnalysis() {
		switch (analysis) {
		case 0: pathwayAssignmentButton.setChecked(true); break;
		case 1: overrepresentationButton.setChecked(true); break;
		case 2: envisionButton.setChecked(true); break;
		case 3: rSpiderButton.setChecked(true); break;
		case 4: gProfilerButton.setChecked(true); break;
		case 5: davidButton.setChecked(true); break;
		default: pathwayAssignmentButton.setChecked(true); break;
		}
	}
	
	protected void formAddSubmitCompleteHandler() {
		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				nextPage = null;
				
				if (event == null) {
					showWarning("onSubmitComplete: event is null!!");
					return;
				}
				
				if (pathwayAssignmentButton.getValue()) {
					nextPage = "PathwayAssignmentPage";
				} else {
					String results = event.getResults();
					if (results != null) {
						results = results.replaceAll("</*[pP][rR][eE]>", "");
						if (!results.isEmpty()) {
							results = results.trim();
							if (!results.isEmpty())
								rawData = results;
						}
					} else
						rawData = pastedDataTextArea.getText();

					if (overrepresentationButton.getValue())
						overrepresentationFormLauncher();
					else if (envisionButton.getValue())
						envisionFormLauncher();
					else if (rSpiderButton.getValue())
						rSpiderFormLauncher();
					else if (gProfilerButton.getValue())
						gProfilerFormLauncher();
					else if (davidButton.getValue())
						davidFormLauncher();
					else {
						warningMessage = "No known active button, have you added a radio button without adding an action to the analyze method?";
						controller.createPage("PathwayAnalysisDataUploadPage", getState());
					}
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
					if(nextPage!=null)
						controller.createPage(nextPage, null);
				
				// Enable the buttons once analysis is complete
				enableAllButtons();
			}
		});
	}
	
	/**
	 * Uses a hidden form to launch an overrepresentation analysis on the
	 * data submitted by the user.
	 */
	private void overrepresentationFormLauncher() {
		HashMap<String,String> params = new HashMap<String,String>();
		// TODO: if this information is not in the URL, we really ought to check Config.pm as well!
		String db = ReferrerUrlParams.getDb();
		if (db != null)
			params.put("DB", db);
		// TODO: if this information is not in the URL, we really ought to check Config.pm as well!
		String dnDb = ReferrerUrlParams.getDnDb();
		if (dnDb != null)
			params.put("DNDB", dnDb);
		params.put("QUERY", rawData);
		params.put("IGNORE_VALUES", "true");
		params.put("TOOL", "SkyPainter");
		params.put("SUBMIT", "Paint!");
		String hostname = GWT.getHostPageBaseURL();
		
		if (hostname == null)
			hostname = "";
		else if (hostname.matches("/") && !hostname.matches("^/"))
			hostname = hostname.split("/")[0];
		else if (hostname.lastIndexOf("ReactomeGWT") >= 0) {
			// Safari & relatives don't understand matches/replaceAll
			int indexReactomeGWT = hostname.lastIndexOf("ReactomeGWT");
			hostname = hostname.substring(0, indexReactomeGWT);
		}
		
		if (hostname.isEmpty() || hostname.matches("^[ \t]+$"))
			hostname = "http://www.reactome.org"; // TODO: find a more general solution to this
		
		if (hostname.matches("/$"))
			hostname.replaceAll("/+$", "");
		else {
			// Safari & relatives don't understand matches/replaceAll
			while (hostname.lastIndexOf("/") == hostname.length() - 1)
				hostname = hostname.substring(0, hostname.length() - 1);
		}
		
		String actionUrl = hostname + "/cgi-bin/skypainter2";
				
		FormUtils.formCreator(basePanel, actionUrl, params).submit();
	}
	
	/**
	 * Uses a hidden form to launch an ENVISION analysis on the
	 * data submitted by the user.
	 */
	private void envisionFormLauncher() {
		HashMap<String,String> params = new HashMap<String,String>();
		// Get the query into a form that EnVision can understand
		String query = rawData;
//	    query = query.replaceAll("\cM", ""); // filter out the ^M characters
	    query = query.replaceAll("\r", ""); // filter out the ^M characters
	    query = query.replaceAll("\f", ""); // filter out the ^M characters
	    query = query.replaceAll("\n", " ");
	    query = query.replaceAll("\\n", " ");
	    query = query.replaceAll(" $", "");
	    
	    // Select the appropriate organism code
	    HashMap<String,String> organism_id_hash = new HashMap<String,String>();
	    organism_id_hash.put("Homo sapiens", "9606");
	    organism_id_hash.put("Caenorhabditis elegans", "6239");
	    organism_id_hash.put("Drosophila melanogaster", "7227");
	    organism_id_hash.put("Saccharomyces cerevisiae", "4932");
	    organism_id_hash.put("Mus musculus", "10090");
	    organism_id_hash.put("Escherichia coli", "562");
		String organism_id = "9606";
		// TODO: if this information is not in the URL, we really ought to check Config.pm as well!
		String species = ReferrerUrlParams.getSpecies();
		if (species != null && !species.isEmpty())
			organism_id = organism_id_hash.get(species);
		
		// Create a random-ish name to allow ENVISION to track the request
		String dataset_name = "Reactome_" + Random.nextInt(1000);
		
		params.put("input", query);
		params.put("inputType", "0");
		params.put("workflow", "Enfin Default Workflow");
		params.put("datasetName", dataset_name);
		params.put("speciesLimit", organism_id);
		
		FormUtils.formCreator(basePanel, "http://www.ebi.ac.uk/enfin-srv/envision2/pages/linkin.jsf", params).submit();
	}
	
	/**
	 * Uses a hidden form to launch an R-Spider analysis on the
	 * data submitted by the user.
	 */
	private void rSpiderFormLauncher() {
		HashMap<String,String> params = new HashMap<String,String>();
		// Get the query into a form that R-Spider can understand
		String query = rawData;
//	    query = query.replaceAll("\cM", ""); // filter out the ^M characters
	    query = query.replaceAll("\r", ""); // filter out the ^M characters
	    query = query.replaceAll("\f", ""); // filter out the ^M characters
	    
	    // Select the appropriate organism code
	    HashMap<String,String> organism_id_hash = new HashMap<String,String>();
	    organism_id_hash.put("Homo sapiens", "9606");
	    organism_id_hash.put("Caenorhabditis elegans", "6239");
	    organism_id_hash.put("Drosophila melanogaster", "7227");
	    organism_id_hash.put("Saccharomyces cerevisiae", "4932");
	    organism_id_hash.put("Mus musculus", "10090");
	    organism_id_hash.put("Escherichia coli", "83333");
	    organism_id_hash.put("Rattus norvegicus", "10116");
	    organism_id_hash.put("Arabidopsis thaliana", "3702");
		String organism_id = "9606";
		// TODO: if this information is not in the URL, we really ought to check Config.pm as well!
		String species = ReferrerUrlParams.getSpecies();
		if (species != null && !species.isEmpty())
			organism_id = organism_id_hash.get(species);
		
		params.put("query", query);
		params.put("Compute", "Submit");
		params.put("organism", organism_id);
		params.put("nback", "20");
		
		FormUtils.formCreator(basePanel, "http://mips.gsf.de/cgi-bin/proj/spider/reactomespider/index.pl", params).submit();
	}
	
	/**
	 * Uses a hidden form to launch an G-Profiler analysis on the
	 * data submitted by the user.
	 */
	private void gProfilerFormLauncher() {
		HashMap<String,String> params = new HashMap<String,String>();
		// Get the query into a form that G-Profiler can understand
		String query = rawData;
		query = query.replaceAll("\n", "+"); // replace newlines with plusses
		query = query.replaceAll("\\+$", ""); // remove trailing plus
	    
	    // Select the appropriate organism code
	    HashMap<String,String> organism_id_hash = new HashMap<String,String>();
	    organism_id_hash.put("Homo sapiens", "hsapiens");
	    organism_id_hash.put("Caenorhabditis elegans", "celegans");
	    organism_id_hash.put("Drosophila melanogaster", "dmelanogaster");
	    organism_id_hash.put("Saccharomyces cerevisiae", "scerevisiae");
	    organism_id_hash.put("Mus musculus", "mmusculus");
	    organism_id_hash.put("Rattus norvegicus", "rnorvegicus");
	    organism_id_hash.put("Gallus gallus", "ggallus");
		organism_id_hash.put("Danio rerio", "drerio");
		organism_id_hash.put("Bos taurus", "btaurus");
		String organism_id = "hsapiens";
		// TODO: if this information is not in the URL, we really ought to check Config.pm as well!
		String species = ReferrerUrlParams.getSpecies();
		if (species != null && !species.isEmpty())
			organism_id = organism_id_hash.get(species);
		
		params.put("query", query);
		params.put("organism", organism_id);
		params.put("analytical", "1");
		params.put("domain_size_type", "annotated");
		params.put("g:Profile!", "g:Profile!");
		params.put("significant", "1");
		params.put("sort_by_structure", "1");
		params.put("user_thr", "1.00");
		params.put("output", "png");
		
		FormUtils.formCreator(basePanel, "http://biit.cs.ut.ee/gprofiler/index.cgi", params).submit();
	}
	
	/**
	 * Uses a hidden form to launch an David analysis on the
	 * data submitted by the user.
	 */
	private void davidFormLauncher() {
		HashMap<String,String> params = new HashMap<String,String>();
		// Get the query into a form that David can understand
		String query = rawData;
//	    query = query.replaceAll("\cM", ""); // filter out the ^M characters
	    query = query.replaceAll("\r", ""); // filter out the ^M characters
	    query = query.replaceAll("\f", ""); // filter out the ^M characters
	    query = query.replaceAll("\n", " ");
	    query = query.replaceAll("\\n", " ");
	    query = query.replaceAll(" $", "");
	    
		params.put("pasteBox", query);
		params.put("Identifier", "UNIPROT_ACCESSION");
		params.put("rbUploadType", "list");
		params.put("Submit List", "B52");
		
		FormUtils.formCreator(basePanel, "http://david.abcc.ncifcrf.gov/summary.jsp", params).submit();
	}
	
	public HashMap getState() {
		super.getState();
		state.put("analysis", (new Integer(analysis)).toString());
		
		return state;
	}
}
