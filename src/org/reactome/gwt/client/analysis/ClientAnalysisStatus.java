/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis;


import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


/**
 * Stores progress information for an analysis.
 * 
 * This is the client-side version of this class.  There is an equivalent
 * server-side version under:
 * 
 * org.reactome.dataanalysis
 * 
 * @author David Croft
 */
public class ClientAnalysisStatus {
	protected String name = null;
	protected String title = null;
	protected int progress = (-1);
	
//	public final static String FINISHED_ANALYSIS_NAME = "Finished";
//	public final static String WARNING_ANALYSIS_NAME = "Warning";
	
	public ClientAnalysisStatus(String name, String title, int progress) {
		this.name = name;
		this.title = title;
		this.progress = progress;
		
		System.err.println("AnalysisStatus: done");
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public int getProgress() {
		return progress;
	}
	
	public String toString() {
		return "Status: " + name + ", " + title + ", " + progress + "%";
	}
	
	public static ClientAnalysisStatus deserializeJson(String jsonString) {
		if (jsonString == null) {
			System.err.println("AnalysisStatus.deserializeJson: WARNING - jsonString == null");
			return null;
		}
		if (jsonString.isEmpty()) {
			System.err.println("AnalysisStatus.deserializeJson: WARNING - jsonString is empty!");
			return null;
		}
		ClientAnalysisStatus status = null;
		
		try {
			JSONObject json = (JSONObject)JSONParser.parseStrict(jsonString);
			if (json != null && json.containsKey("name")) {
				String name = AnalysisUtils.stripQuotes(json.get("name").toString());
				if (name == null) {
					System.err.println("AnalysisStatus.deserializeJson: WARNING - name == null");
					return null;
				}
				String title = AnalysisUtils.stripQuotes(json.get("title").toString());
				String progressString = AnalysisUtils.stripQuotes(json.get("progress").toString());
				if (progressString == null) {
					System.err.println("AnalysisStatus.deserializeJson: WARNING - progressString == null");
					return null;
				}
				try {
					int progress = Integer.parseInt(progressString);
					status = new ClientAnalysisStatus(name, title, progress);
				} catch (NumberFormatException e) {
					System.err.println("AnalysisStatus.deserializeJson: WARNING - progressString=" + progressString + " is not an integer!");
					e.printStackTrace();
					return null;
				}
			}
		} catch (Exception e) {
			System.err.println("AnalysisStatus.deserializeJson: WARNING - problem deserializing JSON string");
			e.printStackTrace();
		}

		return status;
	}
//	
//	public static boolean isStatusFinished(String jsonStatusPacketString) {
//		return isStatus(jsonStatusPacketString, FINISHED_ANALYSIS_NAME);
//	}
//	
//	public static boolean isStatusWarning(String jsonStatusPacketString) {
//		return isStatus(jsonStatusPacketString, WARNING_ANALYSIS_NAME);
//	}
//	
//	private static boolean isStatus(String jsonStatusString, String statusName) {
//		try {
//			JSONObject jsonStatus = (JSONObject)JSONParser.parseStrict(jsonStatusString);
//			if (jsonStatus != null && jsonStatus.containsKey("name")) {
//				String name = AnalysisUtils.stripQuotes(jsonStatus.get("name").toString());
//				if (name.equals(statusName))
//					return true;
//			}
//		} catch (Exception e) {
//		}
//
//		return false;
//	}
}
