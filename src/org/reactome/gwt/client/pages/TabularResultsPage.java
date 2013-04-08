/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.widgets.ProgressBarPanel;
import org.reactome.gwt.client.widgets.ReactomePopup;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class that creates page that displays results in a table.
 * 
 * The state hash for this page contains the following items:
 * 
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public abstract class TabularResultsPage extends Page {
	protected ProgressBarPanel progressBarPanel = new ProgressBarPanel();
	
	public TabularResultsPage(ReactomeGWT controller) {
		this.controller = controller;
	}
	
	protected void displayTableWarnings(int tableSize, String defaultNumericalIdentifierType, String[][] numericalIdentifierTypes, String firstNumericalIdentifier) {
		if (firstNumericalIdentifier != null)
			new NumericalIdentifierWarningPopup(defaultNumericalIdentifierType, numericalIdentifierTypes, firstNumericalIdentifier);
		if (tableSize == 0) {
			showWarning("Your query produced no results.  Possible reasons:<br><ul><li>None of the identifiers in your list matched anything in Reactome;</li><li>The format of your data may be wrong - check to make sure it is a table of tab-delimited values.</li></ul>");
			return;
		}
		if (!controller.HISTORY_ACTIVE)
			showWarning("WARNING: if you navigate away from this page, you will lose the displayed data.  Use the download facility to secure it.");
	}
	
	protected class NumericalIdentifierWarningPopup extends ReactomePopup {
		private ListBox numericalIdentifierTypeSelector = new ListBox();
		private String[][] numericalIdentifierTypes;

		public NumericalIdentifierWarningPopup(String defaultNumericalIdentifierType, String[][] numericalIdentifierTypesIn, String firstNumericalIdentifier) {
			super();
			this.numericalIdentifierTypes = numericalIdentifierTypesIn;
			
			String text = "Your data contains numerical IDs, e.g. \"" + firstNumericalIdentifier + "\".  For this analysis, Reactome has assumed that they are " + defaultNumericalIdentifierType + " IDs.  If your IDs are <i>not</i> " + defaultNumericalIdentifierType + ", please select the appropriate ID type and rerun the analysis:";
			setText(text);
			
			String itemText;
			for (String[] numericalIdentifierType: numericalIdentifierTypes) {
				if (defaultNumericalIdentifierType != null && numericalIdentifierType[0].equals(defaultNumericalIdentifierType))
					continue;
				itemText = numericalIdentifierType[0];
				if (numericalIdentifierType[1] != null && !numericalIdentifierType[1].isEmpty())
					itemText += " (" + numericalIdentifierType[1] + ")";
				numericalIdentifierTypeSelector.addItem(itemText);
			}
			if (numericalIdentifierTypeSelector.getItemCount() > 0)
				numericalIdentifierTypeSelector.setSelectedIndex(0);
			sparePanel.add(numericalIdentifierTypeSelector);

			ClickListener expressionListener = new ClickListener()
			{
				public void onClick(Widget sender)
				{
					hide();
					HashMap state = new HashMap();
					state.put("numericalIdentifierType", numericalIdentifierTypes[numericalIdentifierTypeSelector.getSelectedIndex() + 1][0]);
					controller.createPage(getClasseName(), state);
				}
			};
			Button expressionButton = new Button("Rerun Analysis", expressionListener);
			buttonPanel.add(expressionButton);
			
			closeButton.setText("Ignore this");
			
			setWidth("250px");
		}		
	}
}
