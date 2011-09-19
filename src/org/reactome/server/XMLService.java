/**
 * Code written by Maulik Kamdar.
 * Google Summer of Code Project 
 * Canvas Based Pathway Visualization Tool
 * 
 * Parts of Code have been derived from the tutorials and samples provided at Google Web Toolkit Website 
 * http://code.google.com/webtoolkit/doc/latest/tutorial/
 * 
 * Ideas for canvas based initiation functions derived and modified from Google Canvas API Demo
 * http://code.google.com/p/gwtcanvasdemo/source/browse/
 * 
 */


package org.reactome.server;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of RTPJ file handling
 */

@SuppressWarnings("serial")
public class XMLService extends RemoteServiceServlet {
	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,"sample.rtpj");
	{
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);
				}
				public void onResponseReceived(Request request, Response response) {
					renderXML(response.getText());
				}
			});
		  } 
		catch (RequestException ex) {
			requestFailed(ex);
		}
  
	}
	private void requestFailed(Throwable exception) {
	  Window.alert("Failed to send the message: " + exception.getMessage());
	}
	protected void renderXML(String text) {
		// TODO Auto-generated method stub
		
	}
}
	  
