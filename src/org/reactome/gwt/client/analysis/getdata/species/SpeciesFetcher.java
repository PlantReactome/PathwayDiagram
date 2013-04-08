package org.reactome.gwt.client.analysis.getdata.species;

import org.reactome.gwt.client.SpringUtils;
import org.reactome.gwt.client.analysis.AnalysisUtils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Panel;

/**
 * Fetch species list from server and do something useful with them..
 * 
 * @author David Croft
 *
 */
public class SpeciesFetcher {
	private String[][] cachedSpeciesList = null;
	private int cachedDefaultSpeciesNum = 0;
	private Panel panel = null;
	private String moduleBaseUrl = null;

	/**
	 * A panel attached to the document root is required to make an invisible form
	 * work properly.
	 * 
	 * @param panel
	 */
	public SpeciesFetcher(Panel panel, String moduleBaseUrl) {
		this.panel = panel;
		this.moduleBaseUrl = moduleBaseUrl;
	}

	/**
	 * Poll the server for the results of an analysis.
	 * 
	 */
	public void fetch(SpeciesDisplayHandler speciesDisplayHandler) {
		// Get species list from cache, if available.
		if (cachedSpeciesList != null) {
			speciesDisplayHandler.broadcastSpecies(cachedSpeciesList, cachedDefaultSpeciesNum);
			return;
		}

		FormPanel speciesListForm = new FormPanel();
		speciesListForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		String action = null;
		speciesListForm.setMethod(FormPanel.METHOD_GET);
		action = moduleBaseUrl + "service/analysis/species";
		speciesListForm.setAction(action);
		speciesListForm.addSubmitCompleteHandler(new SpeciesListSubmitCompleteHandler(speciesDisplayHandler));
		panel.add(speciesListForm);
		speciesListForm.submit();
	}

	/**
	 * When the form submit has completed, an object of this class is used to
	 * deal with reading and parsing the returned string into JSON, and thence
	 * into a 2D string array containing the species information.
	 * 
	 * @author croft
	 *
	 */
	private class SpeciesListSubmitCompleteHandler implements SubmitCompleteHandler {
		SpeciesDisplayHandler speciesDisplayHandler;
		
		public SpeciesListSubmitCompleteHandler(SpeciesDisplayHandler speciesDisplayHandler) {
			this.speciesDisplayHandler = speciesDisplayHandler;
		}

		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			if (event == null) {
				System.err.println("onSubmitComplete: event is null!!");
				return;
			}
			String output = AnalysisUtils.extractResultsFromSubmitCompleteEvent(event);
			if (output == null || output.length() == 0)
				System.err.println("onSubmitComplete: null or empty output, aborting!");
			else {
				try {
					JSONValue jsonValue = JSONParser.parseStrict(output);
					JSONArray jsonSpeciesArray = jsonValue.isArray();
					if (jsonSpeciesArray == null) {
						JSONObject jasonObject = jsonValue.isObject();
						if (jasonObject != null) {
							jsonValue = SpringUtils.unpackFromSpringToValue(jasonObject);
							jsonSpeciesArray = jsonValue.isArray();
						}
					}
					if (jsonSpeciesArray != null) {
						int speciesCount = jsonSpeciesArray.size();
						String[][] speciesList = new String[speciesCount][2];
						for (int i=0; i<speciesCount; i++) {
							JSONValue jsonSpeciesArrayElementObject = jsonSpeciesArray.get(i);
							JSONArray jsonSpeciesArrayElement = jsonSpeciesArrayElementObject.isArray();
							speciesList[i][0] = AnalysisUtils.stripQuotes(jsonSpeciesArrayElement.get(0).toString());
							speciesList[i][1] = AnalysisUtils.stripQuotes(jsonSpeciesArrayElement.get(1).toString());
						}
						cachedSpeciesList = speciesList;
						cachedDefaultSpeciesNum = 0; // Set value more intelligently in future
						speciesDisplayHandler.broadcastSpecies(cachedSpeciesList, cachedDefaultSpeciesNum);
					}
				} catch (Exception e) {
					System.err.println("onSubmitComplete: problem with JSON, output=" + output);
					e.printStackTrace(System.err);
				}
			}
		}
	}
}
