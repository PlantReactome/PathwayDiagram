/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

/**
 * Utility methods for client-side analysis code
 *
 * @author David Croft
 */
public class AnalysisUtils {
	public static String extractResultsFromSubmitCompleteEvent(SubmitCompleteEvent event) {
		String results = event.getResults();
		if (results != null) {
			results = results.replaceAll("</*[pP][rR][eE][^>]*>", "");
			if (!results.isEmpty())
				results = results.trim();
		}

		return results;
	}
	
	/**
	 * If you do a "toString" on arguments extracted from JSONObjects, they
	 * tend to be surrounded in double quotes, which messes things up when
	 * you want to use them as hash keys, amongst other things.  This
	 * trivially simple little method strips leading and trailing double
	 * quotes from a string.
	 * 
	 * @param string
	 * @return
	 */
	public static String stripQuotes(String string) {
		String nakedString = string.replaceAll("^\"", "");
		nakedString = nakedString.replaceAll("\"$", "");
		
		return nakedString;
	}
	
	public static String getWebsiteBaseUrl() {
		return GWT.getModuleBaseURL().replaceAll("/site", "");
	}
}
