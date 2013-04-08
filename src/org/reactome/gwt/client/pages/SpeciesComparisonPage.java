/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.panels.SortableTablePanel;
import org.reactome.gwt.client.services.SpeciesComparisonService;
import org.reactome.gwt.client.services.SpeciesComparisonServiceAsync;
import org.reactome.gwt.client.widgets.PercentageBarPanel;
import org.reactome.gwt.client.widgets.buttons.LaunchELVButton;
import org.reactome.gwt.client.widgets.sortableTable.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Allows a user to select from a list of species to compare with human and
 * summarizes the comparison on a per-pathway basis.
 * 
 * The state hash for this page contains the following items:
 * 
 * tableModel
 * 
 * @author David Croft
 */
public class SpeciesComparisonPage extends Page {
	private SpeciesComparisonServiceAsync selectSpeciesForComparisonService = GWT.create(SpeciesComparisonService.class);
	private ListBox speciesSelector = null;
	private String[][] tableModel = null;
	private String[][] speciesArray = null;
	private SortableTablePanel sortableTablePanel = null;
	private HorizontalPanel speciesSelectorStripPanel = new HorizontalPanel();
	
	public SpeciesComparisonPage(ReactomeGWT controller) {
		this.controller = controller;
		setTitle("Species Comparison");
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "This tool allows you to compare pathways between human and any of the other species inferred from Reactome by orthology.";
		moreDescriptionText = "Use the species selector to choose the other species; the table which appears will provide you with a summary of the differences for all pathways.  By clicking on a \"View\" button, you will be taken to a pathway diagram, where you can examine the differences in more detail.";
		
		super.onModuleLoad();
		
		makeSpeciesSelector();
	}
	
	private void makeSpeciesSelector() {
		speciesSelectorStripPanel.setStyleName("pale_blue_textbox"); // CSS
		speciesSelectorStripPanel.setWidth("100%");
		speciesSelectorStripPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		
		HorizontalPanel speciesSelectorPanel = new HorizontalPanel();
		speciesSelectorPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		speciesSelectorPanel.setStyleName("horizontal_inner_panel"); // CSS
		speciesSelectorStripPanel.add(speciesSelectorPanel);
		
		HTML speciesSelectLabel = new HTML("Compare all <b>human</b> pathways with:");
		speciesSelectorPanel.add(speciesSelectLabel);
		
		// Create an empty, inactive species selector, as eye-candy for the user
		speciesSelector = new ListBox();
		speciesSelectorPanel.add(speciesSelector);

		VerticalPanel miscPanel = new VerticalPanel();
		final Button applyButton = new Button("Apply");
		applyButton.setEnabled(false);
		applyButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) {
				applyButton.setEnabled(false); // don't let user click twice
				
				waitCursor(true);

				int selectedIndex = speciesSelector.getSelectedIndex();
				if (selectedIndex >= 0) {
					selectSpeciesForComparisonService.getSpeciesTable(speciesArray[selectedIndex][0],
							new AsyncCallback<String[][]>() {
								@Override
								public void onFailure(Throwable caught) {
									warningMessage = caught.getMessage();
									if (warningMessage == null || warningMessage.isEmpty())
										warningMessage = "Server side error - cannot display species table\n";

									showWarning();
									
									waitCursor(false);
									
									applyButton.setEnabled(true);
								}

								@Override
								public void onSuccess(String[][] result) {
									tableModel = result;
									createSortableTable(result);
									
									basePanel.remove(speciesSelectorStripPanel);

									waitCursor(false);
									
									applyButton.setEnabled(true);
								}
							});
				}
			}
		});
		miscPanel.add(applyButton);
		speciesSelectorPanel.add(miscPanel);

		// If we are revisiting this page, and there are already species comparison results
		// stored in the state, display them.
		tableModel = (String[][])state.get("tableModel");
		if (tableModel != null)
			createSortableTable(tableModel);

		basePanel.add(speciesSelectorStripPanel);

		// Now populate species selector with species and activate it.
		selectSpeciesForComparisonService.getSpeciesList(
				new AsyncCallback<String[][]>() {
					@Override
					public void onFailure(Throwable caught) {
						warningMessage = caught.getMessage();
						if (warningMessage == null || warningMessage.isEmpty())
							warningMessage = "Server side error - cannot get species\n";

						showWarning();
					}

					@Override
					public void onSuccess(String[][] result) {
						speciesArray = result;
						for (String[] species: speciesArray) {
							speciesSelector.addItem(species[1]);
							applyButton.setEnabled(true);
						}
					}
				});
	}

	/**
	 * Given a table model for a species to be compared with, create a table showing
	 * a comparison between human and that species.
	 * 
	 * @param speciesDB_IDString
	 */
	private void createSortableTable(final String[][] tableModel) {
		// Get rid of any pre-existing table.
		if (sortableTablePanel != null)
			basePanel.remove(sortableTablePanel);
		
		if (!controller.HISTORY_ACTIVE)
			showWarning("WARNING: if you navigate away from this page, you will lose the displayed data.  Use the download facility to secure it.");
				
		SortableTable sortableTable = new SortableTable(tableModel.length + 1, 6);
		
		// This panel adds useful decoration to the table, particularly,
		// a download feature.
		sortableTablePanel = new SortableTablePanel(sortableTable);
		sortableTablePanel.onModuleLoad();

		basePanel.add(sortableTablePanel);
		
		sortableTable.addColumnMode("Unsortable", 5);
		sortableTable.addColumnHeader("Pathway name",  0);
		sortableTable.addColumnHeader("Other species",  1);
		sortableTable.addColumnHeader("Proteins, human", 2);
		sortableTable.addColumnHeader("Proteins, other species", 3);
		sortableTable.addColumnHeader("% in other species", 4);
		sortableTable.addColumnHeader("Click button to view pathway", 5);

		// The rowIndex should begin with 1 as rowIndex 0 is for the Header
		int rowIndex = 1;
		for (String[] tableRow: tableModel) {
			LaunchELVButton launchELVButton = new LaunchSpeciesComparisonELVButton(tableRow[2], tableRow[0]);
			PercentageBarPanel percentageBarPanel = new PercentageBarPanel((new Double(tableRow[5])).doubleValue() * 100.0 / (new Double(tableRow[4])).doubleValue());

			sortableTable.setValue(rowIndex, 0, tableRow[3]);
			sortableTable.setValue(rowIndex, 1, tableRow[1]);
			sortableTable.setValue(rowIndex, 2, new Integer(tableRow[4]));
			sortableTable.setValue(rowIndex, 3, new Integer(tableRow[5]));
			
			// TODO: I did try setWidget, but the cell boundaries did not get constructed
			// properly.  The only reason that setValue works is because the toString
			// method for Widget returns the HTML needed to build the widget.  If Google
			// ever decides to change this, then the following two lines will produce
			// gibberish.
			sortableTable.setValue(rowIndex, 4, percentageBarPanel);
			sortableTable.setValue(rowIndex, 5, launchELVButton);

			rowIndex++;
		}
	}

	public HashMap getState() {
		super.getState();
		state.put("tableModel", tableModel);
		return state;
	}
	
	private class LaunchSpeciesComparisonELVButton extends LaunchELVButton {
		private String speciesDbId;
		
		public LaunchSpeciesComparisonELVButton(String pathwayDbId, String speciesDbId) {
			super(pathwayDbId, "SpeciesComparison", basePanel);
			this.speciesDbId = speciesDbId;
		}

		@Override
		protected void action() {
			// Get the database table name from the server.  This is an asynchronous call.
			selectSpeciesForComparisonService.getDataTableName(
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							String warning = caught.getMessage();
							if (warning == null || warning.isEmpty())
								warning = "Server side error - cannot get name of data table from server\n";

							dealWithWarning(warning);
						}

						@Override
						public void onSuccess(String dataTableName) {
							setDataTableName(dataTableName);
							formLauncher();
						}
					});
		}

		@Override
		protected void dealWithWarning(String warning) {
			warningMessage = warning;
			showWarning();
		}
		
		@Override
		protected String createUrl() {
			String url = super.createUrl() + "&OTHER_SPECIES_DB_ID=" + speciesDbId;
			return url;
		}
		
		@Override
		protected HashMap createParams() {
			HashMap<String,String> params = super.createParams();
			params.put("OTHER_SPECIES_DB_ID", speciesDbId);

			return params;
		}
	}
}
