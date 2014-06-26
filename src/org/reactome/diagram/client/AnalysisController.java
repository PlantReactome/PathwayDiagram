/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.client;


import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;


/**
 * @author weiserj
 *
 */
public class AnalysisController {
	private static final String BASE_URL = "/AnalysisService/";
	
    public AnalysisController() {
    }
    
    public void retrieveAnalysisResult(String token, RequestCallback callback) {
    	final String url = BASE_URL + "token/" + token + "?pageSize=0&page=1";
    			
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/json");
    	
    	try {
    		requestBuilder.sendRequest(null, callback);
    	} catch (RequestException ex) {
    		AlertPopup.alert("Error in retrieving analysis information:" + ex);
    	}
    }
    
    public Request retrievePathwaySummary(String token, Long pathwayId, String resource, RequestCallback callback) {
    	final String url = BASE_URL + "token/" + token + "/summary/" + pathwayId + "?resource=" + resource;
	    
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            return requestBuilder.sendRequest(null, callback);
        } 
        catch (RequestException ex) {
            AlertPopup.alert("Error in retrieving : " + ex);
            return null;
        } 	 
    }
    
    public Request retrievePathwayResults(String token, List<Long> pathwayIds, String resource, RequestCallback callback) {
    	final String url = BASE_URL + "token/" + token + "/filter/pathways?resource=" + resource;
    	
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
    	requestBuilder.setHeader("Accept", "application/json");
    	try {
    		return requestBuilder.sendRequest(getCommaSeparatedList(pathwayIds), callback);
    	} catch (RequestException ex) {
    		AlertPopup.alert("Error in retrieving pathway results: " + ex);
    		return null;
    	}
    }

	private String getCommaSeparatedList(List<Long> pathwayIds) {
		final String delimiter = ",";
		
		StringBuilder stringBuilder = new StringBuilder();
		for (Long pathwayId : pathwayIds) {
			stringBuilder.append(pathwayId);
			stringBuilder.append(delimiter);
		}
		
		if (stringBuilder.lastIndexOf(delimiter) != -1)
			stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(delimiter));
		
		return stringBuilder.toString();
	}
}
