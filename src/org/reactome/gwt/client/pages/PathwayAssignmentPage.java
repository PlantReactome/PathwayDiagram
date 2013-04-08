/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.panels.SortableTablePanel;
import org.reactome.gwt.client.services.PathwayAssignmentService;
import org.reactome.gwt.client.services.PathwayAssignmentServiceAsync;
import org.reactome.gwt.client.services.ProjectPropertiesService;
import org.reactome.gwt.client.services.ProjectPropertiesServiceAsync;
import org.reactome.gwt.client.transport.PathwayInfo;
import org.reactome.gwt.client.transport.PathwayInfoPerIdentifier;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;
import org.reactome.gwt.client.widgets.sortableTable.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;

/**
 * Creates page that displays the assignment of user-supplied genes or proteins
 * to Reactome pathways.
 * 
 * The state hash for this page contains the following items:
 * 
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class PathwayAssignmentPage extends TabularResultsPage {
	private PathwayInfoPerIdentifier tableModel = null;
	private PathwayAssignmentServiceAsync tableModelService = GWT.create(PathwayAssignmentService.class);
	protected String dbName = null; // if this is not set, something is wrong with your code!
	
	public PathwayAssignmentPage(ReactomeGWT controller) {
		super(controller);
		setTitle("Pathway Assignment");
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "For each of your identifiers, this table provides the pathways in which it takes part.  Note that the column sort operation will be very slow if you have more than 1000 identifiers, and no \"busy\" cursor will appear to let you know that sorting is in progress.";
		if (state.get("fadeDbIds") != null)
			moreDescriptionText = "Additional information is also supplied, namely, the UniProt IDs corresponding to your IDs and the species in which they are found.  IDs which appear faded represent proteins which are present in the pathway, but which are <i>not</i> in your data.";
		videoTutorialButton = new ExternalNewPageButton("http://www.youtube.com/watch?v=9cFIDt9v0wY&context=C3204927ADOEgsToPDskLe-JNwS5ZalRJi-nIF1_vn", "Video Tutorial", "_blank");
		boolean qualifyNumericalIdentifiers = true;
		if (state.get("qualifyNumericalIdentifiers") != null && state.get("qualifyNumericalIdentifiers").equals("0"))
			qualifyNumericalIdentifiers = false;
		String numericalIdentifierType = (String)state.get("numericalIdentifierType");
		super.onModuleLoad();
		
		waitCursor(true);
		
		progressBarPanel.setProgressMonitorAsync(tableModelService);
		basePanel.add(progressBarPanel);
		
		tableModel = (PathwayInfoPerIdentifier)state.get("tableModel");
		if (state.get("dbName") != null)
			dbName = (String)state.get("dbName");
		if (tableModel == null) {
			tableModelService.mapToPathways(qualifyNumericalIdentifiers, numericalIdentifierType,
					new AsyncCallback<PathwayInfoPerIdentifier>() {
						@Override
						public void onFailure(Throwable caught) {
							warningMessage = caught.getMessage();
							if (warningMessage == null || warningMessage.isEmpty())
								warningMessage = "Server side error - cannot display results\n";

							showWarning();
							
							basePanel.remove(progressBarPanel);
							waitCursor(false);
						}

						@Override
						public void onSuccess(PathwayInfoPerIdentifier result) {
							tableModel = result;
							descriptionText = "This table provides an overview of your expression data in a pathway context.  For each Reactome pathway, the total number of proteins is shown, plus the number of genes/proteins in your dataset that match.  By clicking on the \"View\" button for a given pathway, you will be taken to an interactive graphical representation of the pathway, where your expression levels are represented as coloration of proteins.";

							ProjectPropertiesServiceAsync projectPropertiesService = GWT.create(ProjectPropertiesService.class);

							// Get the database name from the server.  This is an asynchronous call,
							// hence the weird code structure.
							projectPropertiesService.getProperty("GKB.Config.GK_ENTITY_DB_NAME", 
									new AsyncCallback<String>() {
								public void onFailure(Throwable caught) {
									String warningMessage = caught.getMessage();
									if (warningMessage == null || warningMessage.isEmpty())
										warningMessage = "Server side error - problem getting database name, cannot display ELV for identifier\n";
									
									showWarning();
									
									basePanel.remove(progressBarPanel);
									waitCursor(false);
								}

								public void onSuccess(String result) {
									if (result != null && !result.equals(""))
										dbName = result;
									
									progressBarPanel.finalize();
									basePanel.remove(progressBarPanel);
									
									createSortableTable(tableModel);
									
									waitCursor(false);
								}
							});
						}
					});
			progressBarPanel.progressLooper(progressBarPanel.getProgress(), progressBarPanel.getProgress() + 1500.0);
		} else {
			createSortableTable(tableModel);
			
			waitCursor(false);
			
			progressBarPanel.finalize();
			
			basePanel.remove(progressBarPanel);
		}
	}
	
	/**
	 * Display the table from the data in the model
	 * 
	 * @param tableModel
	 */
	private void createSortableTable(PathwayInfoPerIdentifier tableModel) {
		displayTableWarnings(tableModel.pathwayInfoPerIdentifier.size(), tableModel.defaultNumericalIdentifierType, tableModel.numericalIdentifierTypes, tableModel.firstNumericalIdentifier);
				
		SortableTable sortableTable = new SortableTable(tableModel.pathwayInfoPerIdentifier.size() + 1, 4);
				
		progressBarPanel.setProgressComment("Create SortableTablePanel");
		progressBarPanel.incrementProgress(100.0);

		// This panel adds useful decoration to the table, particularly,
		// a download feature.
		SortableTablePanel sortableTablePanel = new SortableTablePanel(sortableTable);
		sortableTablePanel.onModuleLoad();
		
		progressBarPanel.setProgressComment("Add SortableTablePanel to base panel");
		progressBarPanel.incrementProgress(300.0);

		basePanel.add(sortableTablePanel);

		sortableTable.addColumnHeader("ID",  0);
		sortableTable.addColumnHeader("UniProt ID",  1);
		sortableTable.addColumnHeader("Species",  2);
		sortableTable.addColumnHeader("Pathway names",  3);
		
		progressBarPanel.setProgressComment("Construct table");
		progressBarPanel.incrementProgress(100.0);

		// The rowIndex should begin with 1 as rowIndex 0 is for the Header
		// Any row with index == 0 will not be displayed.
		int rowIndex = 1;
		for (PathwayInfo pathwayExpressionData: tableModel.pathwayInfoPerIdentifier) {
			progressBarPanel.setProgressComment("Building table row: " + rowIndex);
			
			// Fade out IDs if they are DB_IDs and the "fadeDbIds" has been set.
			String id = pathwayExpressionData.id;
			int numericalId = (-1);
			try {
				numericalId = (new Integer(id)).intValue();
			} catch (NumberFormatException e) {
				// Ugly hack
			}
			if (state.get("fadeDbIds") != null && numericalId >= 0)
				id = "<i style=\"color:gray\">" + id + "</i>";
			
			String uniProtId = pathwayExpressionData.uniProtId;
			String uniProtAccessUrl;
			if (tableModel.uniProtAccessUrl != null && !tableModel.uniProtAccessUrl.isEmpty()) {
				uniProtAccessUrl = tableModel.uniProtAccessUrl.replaceAll("###ID###", uniProtId);
				uniProtId = "<A TARGET=\"_blank\" HREF=\"" + uniProtAccessUrl  + "\">" + uniProtId + "</A>";
			}

			String pathwayNames = "";
			String pathwayName;
			long pathwayDbId;
			String speciesId;
			String url;
			String referenceEntityDbId = pathwayExpressionData.referenceEntityDbId;
			for (int i=0; i<pathwayExpressionData.pathwayNames.length; i++) {
				if (i >= pathwayExpressionData.pathwayDbIds.length)
					break;
				
				pathwayName = pathwayExpressionData.pathwayNames[i];
				pathwayDbId = pathwayExpressionData.pathwayDbIds[i];
				speciesId = pathwayExpressionData.speciesIds[0];
				for (int j=0; j<pathwayExpressionData.speciesNames.length; j++)
					if (pathwayExpressionData.speciesNames[j].equals("Homo sapiens")) {
						speciesId = pathwayExpressionData.speciesIds[j];
						break;
				}
//				url = "/cgi-bin/eventbrowser?DB=" + dbName + "&FOCUS_SPECIES_ID=" + speciesId + "&FOCUS_PATHWAY_ID=" + pathwayDbId + "&ID=" + referenceEntityDbId;
				url = "/entitylevelview/PathwayBrowser.html#DB=" + dbName + "&FOCUS_SPECIES_ID=" + speciesId + "&FOCUS_PATHWAY_ID=" + pathwayDbId + "&ID=" + referenceEntityDbId;
				
				if (!pathwayNames.isEmpty())
					pathwayNames += "; ";
				pathwayNames += "<A TARGET=\"_blank\" HREF=\"" + url  + "\">" + pathwayName + "</A>";
			}
				
			String speciesNames = "";
			for (String speciesName: pathwayExpressionData.speciesNames) {
				if (!speciesNames.isEmpty())
					speciesNames += "; ";
				speciesNames += speciesName;
			}
				
			sortableTable.setValue(rowIndex, 0, id);
			sortableTable.setValue(rowIndex, 1, uniProtId);
			sortableTable.setValue(rowIndex, 2, speciesNames);
			sortableTable.setValue(rowIndex, 3, pathwayNames);

			rowIndex++;
		}
		
		sortableTable.touch();
	}
	
	public HashMap getState() {
		super.getState();
		state.put("tableModel", tableModel);
		state.put("dbName", dbName);
		
		return state;
	}
}
