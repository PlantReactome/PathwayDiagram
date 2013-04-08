/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.panels.SortableTablePanel;
import org.reactome.gwt.client.services.ExpressionPerPathwayService;
import org.reactome.gwt.client.services.ExpressionPerPathwayServiceAsync;
import org.reactome.gwt.client.transport.ExpressionDataPerPathway;
import org.reactome.gwt.client.transport.PathwayExpressionData;
import org.reactome.gwt.client.widgets.PercentageBarPanel;
import org.reactome.gwt.client.widgets.SortableTableAnchor;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;
import org.reactome.gwt.client.widgets.buttons.LaunchELVButton;
import org.reactome.gwt.client.widgets.sortableTable.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Creates page that displays expression data in the context of a table of
 * Reactome pathways.
 * 
 * The state hash for this page contains the following items:
 * 
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class ExpressionPerPathwayPage extends TabularResultsPage {
	private ExpressionDataPerPathway expressionDataPerPathway = null;
	private ExpressionPerPathwayServiceAsync expressionPerPathwayService = GWT.create(ExpressionPerPathwayService.class);
	
	public ExpressionPerPathwayPage(ReactomeGWT controller) {
		super(controller);
		setTitle("Expression per pathway");
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "This table provides an overview of your expression data in a pathway context.";
		moreDescriptionText = "For each Reactome pathway, the total number of proteins is shown, plus the number of genes/proteins in your dataset that match.  By clicking on the \"View\" button for a given pathway, you will be taken to an interactive graphical representation of the pathway, where your expression levels are represented as coloration of proteins.";
		videoTutorialButton = new ExternalNewPageButton("http://www.youtube.com/watch?v=TczSuUtcffE&context=C3204927ADOEgsToPDskLe-JNwS5ZalRJi-nIF1_vn", "Video Tutorial", "_blank");
		 
		super.onModuleLoad();
		
		waitCursor(true);
		
		String numericalIdentifierType = (String)state.get("numericalIdentifierType");
		
		// Tell the progress bar which service it should be getting its progress
		// information from, and add it to the base panel.
		progressBarPanel.setProgressMonitorAsync(expressionPerPathwayService);
		basePanel.add(progressBarPanel);
		
		expressionDataPerPathway = (ExpressionDataPerPathway)state.get("mainPanel");
		if (expressionDataPerPathway == null) {
			expressionPerPathwayService.mapToEntities(numericalIdentifierType,
					new AsyncCallback<ExpressionDataPerPathway>() {
						@Override
						public void onFailure(Throwable caught) {
							warningMessage = caught.getMessage();
							if (warningMessage == null || warningMessage.isEmpty())
								warningMessage = "Server side error - cannot display expression data\n";

							showWarning();
							
							waitCursor(false);
						}

						@Override
						public void onSuccess(ExpressionDataPerPathway result) {
							expressionDataPerPathway = result;
								
							createSortableTable(result);
							
							waitCursor(false);
							
							progressBarPanel.finalize();
							
							basePanel.remove(progressBarPanel);
						}
					});
			progressBarPanel.progressLooper(progressBarPanel.getProgress(), progressBarPanel.getProgress() + 1500.0);
		} else {
			createSortableTable(expressionDataPerPathway);
			
			waitCursor(false);
			
			progressBarPanel.finalize();
			
			basePanel.remove(progressBarPanel);
		}
	}
	
	protected void createSortableTable(final ExpressionDataPerPathway tableModel) {
		displayTableWarnings(tableModel.expressionDataPerPathway.size(), tableModel.defaultNumericalIdentifierType, tableModel.numericalIdentifierTypes, tableModel.firstNumericalIdentifier);
		
		SortableTable sortableTable = new SortableTable(tableModel.expressionDataPerPathway.size() + 1, 6);
		
		progressBarPanel.setProgressComment("Create SortableTablePanel");
		progressBarPanel.incrementProgress(100.0);

		// This panel adds useful decoration to the table, particularly,
		// a download feature.
		SortableTablePanel sortableTablePanel = new SortableTablePanel(sortableTable);
		sortableTablePanel.setSortableTable(sortableTable);
		sortableTablePanel.onModuleLoad();
		
		progressBarPanel.setProgressComment("Add SortableTablePanel to base panel");
		progressBarPanel.incrementProgress(300.0);
		
		basePanel.add(sortableTablePanel);
		
		sortableTable.addColumnMode("Unsortable", 5);
		sortableTable.addColumnHeader("Pathway name",  0);
		sortableTable.addColumnHeader("Species",  1);
		sortableTable.addColumnHeader("Total number of proteins", 2);
		sortableTable.addColumnHeader("Matching proteins in data", 3);
		sortableTable.addColumnHeader("% in data", 4);
		sortableTable.addColumnHeader("Click button to view pathway", 5);

		progressBarPanel.setProgressComment("Construct table");
		progressBarPanel.incrementProgress(100.0);

		// The rowIndex should begin at 1, since rowIndex 0
		// is for the header.
		int rowIndex = 1;
		double percent;
		for (PathwayExpressionData pathwayExpressionData: tableModel.expressionDataPerPathway) {
			progressBarPanel.setProgressComment("Building table row: " + rowIndex);

			int matchingIdentifierCountInt = pathwayExpressionData.matchingIdentifierCount;
			int referenceEntityCountInt = pathwayExpressionData.referenceEntityCount;
			String pathwayDbId = (new Long(pathwayExpressionData.pathwayDbId)).toString();
			ParticipantsAnchor referenceEntityCount = new ParticipantsAnchor((new Integer(referenceEntityCountInt)).toString(), pathwayDbId, pathwayExpressionData.pathwayName);
			ParticipantsAnchor matchingIdentifierCount = new ParticipantsAnchor((new Integer(matchingIdentifierCountInt)).toString(), pathwayDbId, pathwayExpressionData.pathwayName);
			LaunchELVButton launchELVButton = new LaunchExpressionELVButton(pathwayDbId, tableModel.dataTableName);
			launchELVButton.setSpeciesDbId(pathwayExpressionData.speciesDbId);
			if (referenceEntityCountInt == 0) {
				if (matchingIdentifierCountInt == 0)
					percent = 0.0;
				else
					percent = 100.0; // This shouldn't really happen...
			} else
				percent = (new Double(matchingIdentifierCountInt)).doubleValue() * 100.0 / (new Double(referenceEntityCountInt)).doubleValue();
			PercentageBarPanel percentageBarPanel = new PercentageBarPanel(percent);
			
			sortableTable.setValue(rowIndex, 0, pathwayExpressionData.pathwayName);
			sortableTable.setValue(rowIndex, 1, pathwayExpressionData.speciesName);
			sortableTable.setValue(rowIndex, 2, referenceEntityCount);
			sortableTable.setValue(rowIndex, 3, matchingIdentifierCount);
			sortableTable.setValue(rowIndex, 4, percentageBarPanel);
			sortableTable.setValue(rowIndex, 5, launchELVButton);

			rowIndex++;
		}
		
		sortableTable.touch();
	}
	
	public HashMap getState() {
		super.getState();
		state.put("mainPanel", expressionDataPerPathway);
		
		return state;
	}
	
	private class LaunchExpressionELVButton extends LaunchELVButton {
		public LaunchExpressionELVButton(String pathwayDbId, String dataTableName) {
			super(pathwayDbId, "Expression", basePanel);
			this.setDataTableName(dataTableName);
		}

		@Override
		protected void dealWithWarning(String warning) {
			warningMessage = warning;
			showWarning();
		}
	}
	
	private class ParticipantsAnchor extends SortableTableAnchor {
		private String pathwayDbID;
		private String pathwayName;
		
		public ParticipantsAnchor(String text, String pathwayDbID, String pathwayName) {
			super(text);
			this.pathwayDbID = pathwayDbID;
			this.pathwayName = pathwayName;
		}

		@Override
		public void click() {
			expressionPerPathwayService.launchPathwayAnalysisForPathway(pathwayDbID,
					new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					warningMessage = caught.getMessage();
					if (warningMessage == null || warningMessage.isEmpty())
						warningMessage = "Server side error - cannot display expression data\n";

					showWarning();
				}

				@Override
				public void onSuccess(Void result) {
					warningMessage = "Nothing happened\n";

					showWarning();
					
					HashMap state = new HashMap();
					state.put("fadeDbIds", "1");
					state.put("qualifyNumericalIdentifiers", "0");
					state.put("title", "Which IDs from expression analysis are in \"" + pathwayName + "\"?");
					
					// Fire up pathway analysis page
					controller.createPage("PathwayAssignmentPage", state);
				}
			});
		}
		
		@Override
		public int compareTo(Object arg0) {
			return (new Integer(getText())).compareTo(new Integer(((SortableTableAnchor)arg0).getText()));
		}
	}
}
