package org.reactome.gwt.client.analysis.getdata.results;

import java.util.HashMap;
import java.util.Map;

import org.reactome.gwt.client.analysis.AnalysisUtils;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Panel;

/**
 * Poll for results, and cache them cautiously, so that not too much is kept memory.
 * 
 * @author David Croft
 *
 */
public class ResultsPoller {
	private Map<String,Map<String,JSONObject>> jsonObjectMap = new HashMap<String,Map<String,JSONObject>>();
	private Panel panel = null;
	private String moduleBaseUrl = null;

	/**
	 * A panel attached to the document root is required to make an invisible form
	 * work properly.
	 * 
	 * @param panel
	 */
	public ResultsPoller(Panel panel, String moduleBaseUrl) {
		this.panel = panel;
		this.moduleBaseUrl = moduleBaseUrl;
	}

	/**
	 * Poll the server for the results of an analysis.
	 * 
	 */
	public void pollForResults(String analysisId, String analysisName, ResultsDisplayHandler resultsDisplayHandler) {
		// Get JSON from cache, if available.
		if (jsonObjectMap.containsKey(analysisId)) {
			Map<String,JSONObject> nameModelMap = jsonObjectMap.get(analysisId);
			if (nameModelMap.containsKey(analysisName)) {
				JSONObject jsonObject = nameModelMap.get(analysisName);
				
				// I would like to persuade the event bus
				// to recognise the existence of the new tabs, so that when the
				// results are broadcast, they get passed on to all tabs, including
				// the new ones that have been created for holding precomputed
				// results.  The problem is, the presenters for these tabs don't
				// seem to get connected to the event bus straight away, so
				// if we get the JSON out of cache, which is fast, we may
				// end up broadcasting the results before the tabs are ready.
				// Furthermore, I don't know how to find out if the tabs are
				// ready or not.  Be warned!
				
				resultsDisplayHandler.broadcastResults(jsonObject);
				return;
			}
		}
		
		WrapperResultsDisplayHandler wrapperResultsDisplayHandler = new WrapperResultsDisplayHandler(analysisId, analysisName, resultsDisplayHandler);
		Monitor resultsMonitor = new ResultsMonitor(moduleBaseUrl, analysisId, analysisName, panel, wrapperResultsDisplayHandler);
		Monitor statusMonitor = new StatusMonitor(moduleBaseUrl, analysisId, analysisName, panel, resultsMonitor, null);
		statusMonitor.poll();
	}
	
	private class WrapperResultsDisplayHandler implements ResultsDisplayHandler {
		private String analysisId;
		private String analysisName;
		private ResultsDisplayHandler resultsDisplayHandler;
		
		WrapperResultsDisplayHandler(String analysisId, String analysisName, ResultsDisplayHandler resultsDisplayHandler) {
			super();
			this.analysisId = analysisId;
			this.analysisName = analysisName;
			this.resultsDisplayHandler = resultsDisplayHandler;
		}
		
		/**
		 * Overloaded method that caches the JSON.
		 */
		@Override
		public void broadcastResults(JSONObject jsonObject) {
			if (jsonObjectMap == null) {
				showWarningInResultsDisplayPanel("ResultsPoller.WrapperResultsDisplayHandler.broadcastResults: WARNING - jsonObjectMap == null");
				return;
			}
			if (analysisName == null) {
				showWarningInResultsDisplayPanel("ResultsPoller.WrapperResultsDisplayHandler.broadcastResults: WARNING - analysisName == null");
				return;
			}
			if (analysisId == null) {
				showWarningInResultsDisplayPanel("ResultsPoller.WrapperResultsDisplayHandler.broadcastResults: WARNING - analysisId == null");
				return;
			}
			
			try {
				resultsDisplayHandler.broadcastResults(jsonObject);
			} catch (Exception e) {
				showWarningInResultsDisplayPanel("ResultsPoller.WrapperResultsDisplayHandler.broadcastResults: WARNING - results display handler could not broadcast results");
				e.printStackTrace();
				return;
			}
			
			Map<String,JSONObject> nameModelMap = null;
			try {
				if (jsonObjectMap.containsKey(analysisId))
					nameModelMap = jsonObjectMap.get(analysisId);
				else
					jsonObjectMap.clear(); // never store more than one analysis in the cache
			} catch (Exception e) {
				showWarningInResultsDisplayPanel("ResultsPoller.WrapperResultsDisplayHandler.broadcastResults: WARNING - problem getting nameModelMap");
				e.printStackTrace();
			}
			if (nameModelMap == null)
				nameModelMap = new HashMap<String,JSONObject>();
						
			nameModelMap.put(analysisName, jsonObject);
			jsonObjectMap.put(analysisId, nameModelMap);
		}

		@Override
		public void showWarningInResultsDisplayPanel(String message) {
			resultsDisplayHandler.showWarningInResultsDisplayPanel(message);
		}
	}
}