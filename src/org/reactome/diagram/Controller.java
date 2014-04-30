/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram;

import org.reactome.diagram.client.AlertPopup;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;


/**
 * @author weiserj
 *
 */
public class Controller {
	private static final String BASE_URL = "/AnalysisService/";
	
    public Controller() {
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
    
    public void retrievePathwaySummary(String token, Long pathwayId, RequestCallback callback) {
    	final String url = BASE_URL + "token/" + token + "/summary/" + pathwayId;
	    
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            requestBuilder.sendRequest(null, callback);
        } 
        catch (RequestException ex) {
            AlertPopup.alert("Error in retrieving : " + ex);
        } 	 
    }
    
}
