/*
 * Created on Nov 9, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * This class is related to communicating activites between the front end and the RESTful APIs.
 * @author gwu
 *
 */
public class PathwayDiagramController {
	private String hostUrl = null;
	
    public static final String RESTFUL_URL = "RESTfulWS/pathwaydiagram/";
//    public static final String RESTFUL_URL = "http://localhost:8080/ReactomeRESTfulAPI/RESTfulWS/pathwaydiagram/";
    private PathwayDiagramPanel diagramPane;
    
    public PathwayDiagramController(PathwayDiagramPanel pane) {
        this.diagramPane = pane;
    }
    
    public void listPathways() {
        String url = GWT.getHostPageBaseURL() + "ListOfPathways.txt";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                
                @Override
                public void onResponseReceived(Request request, Response response) {
                    showListOfPathways(response.getText());
                }
                
                @Override
                public void onError(Request request, Throwable exception) {
                    requestFailed(exception);
                }
            });
        }
        catch(RequestException e) {
            requestFailed(e);
        }
    }
    
    private void showListOfPathways(String text) {
        VerticalPanel vPane = new VerticalPanel();
        String[] lines = text.split("\n");
        final ScrollPanel sp = new ScrollPanel(vPane);
//        vPane.setStyleName("dialogPane");
        sp.setStyleName("dialogPane");
        int spWidth = 400;
        int spHeight = 300;
        sp.setSize(spWidth + "px", spHeight + "px");
        for (String line : lines) {
            final Button label = new Button(line);
            label.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    String text = label.getText();
                    int index1 = text.indexOf("[");
                    int index2 = text.lastIndexOf("]");
                    String dbId = text.substring(index1 + 1, index2);
                    loadDiagramForDBId(new Long(dbId));
                    diagramPane.contentPane.remove(sp);
                }
            });
            vPane.add(label);
        }
        int width = diagramPane.getOffsetWidth();
        int height = diagramPane.getOffsetHeight();
        diagramPane.contentPane.add(sp, 
                                    (width - spWidth) / 2, 
                                    (height - spHeight) / 2);
    }
    
    /**
     * Load a pathway diagram for a specified Pathway DB_ID.
     * @param dbId db_id for a pathway.
     * @return
     */
    public void loadDiagramForDBId(Long dbId) {
        String hostUrl = getHostUrl();
//        System.out.println("Host url: " + hostUrl);
        // Do some simple parsing
        int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
        String url = hostUrl.substring(0, lastIndex + 1) + RESTFUL_URL + dbId + "/xml";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    requestFailed(exception);
                }
                public void onResponseReceived(Request request, Response response) {
                    renderXML(response.getText());
                }
            });
        } catch (RequestException ex) {
            requestFailed(ex);
        } 
    }
    
    /**
     * Load a pathway diagram for a specified Pathway XML
     * @param xml the XML data for a pathway
     */
    public void loadDiagramForXML(String xml){
    	renderXML(xml);
    }
    
    /**
     * 
     * @param exception Exception whenever the XML file is not load
     */
    private void requestFailed(Throwable exception) {
        Window.alert("Failed to send the message: " + exception.getMessage());
    }
    
    /** 
     * Parses the XML Text and Builds a HashMap of the nodes and the edges. Renders the Canvas Visualization.
     * @param xmlText The XML Text to be parsed
     */
    private void renderXML(String xmlText) {
//        System.out.println(xmlText);
        try {
            Document pathwayDom = XMLParser.parse(xmlText);
            Element pathwayElement = pathwayDom.getDocumentElement();
            XMLParser.removeWhitespace(pathwayElement);
            CanvasPathway pathway = new CanvasPathway();
            pathway.buildPathway(pathwayElement);
            diagramPane.setPathway(pathway);
        }
        catch(Exception e) {
            Window.alert("Error in parsing XML: " + e);
        }
    }
    
    public void setHostUrl(String hostUrl){
    	this.hostUrl = hostUrl;
    }
    
    public String getHostUrl(){
    	if(hostUrl!=null){
    		return this.hostUrl;
    	}else{
    		return GWT.getHostPageBaseURL();
    	}
    }
    
}
