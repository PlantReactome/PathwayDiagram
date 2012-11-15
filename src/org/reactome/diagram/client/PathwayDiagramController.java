/*
 * Created on Nov 9, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.DiseaseCanvasPathway;
import org.reactome.diagram.model.ReactomeObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;

/**
 * This class is related to communicating activites between the front end and the RESTful APIs.
 * @author gwu
 *
 */
public class PathwayDiagramController {
    // The root RESTful URL
	private String hostUrl = null;
		
    private final String RESTFUL_URL = "RESTfulWS/";
    private final String PATHWAY_DIAGRAM = RESTFUL_URL + "pathwayDiagram/";
    
    //    public static final String RESTFUL_URL = "http://localhost:8080/ReactomeRESTfulAPI/RESTfulWS/pathwaydiagram/";
    private PathwayDiagramPanel diagramPane;
    private List<ReactomeObject> molecules;
    
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
    
    
    public void getParticipatingMolecules(final Long dbId) {
    	String hostUrl = getHostUrl();
    	
    	this.molecules = new ArrayList<ReactomeObject>();
    	int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
    	String url = hostUrl.substring(0, lastIndex + 1) + RESTFUL_URL + "complexSubunits/" + dbId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {
    			public void onError(Request request, Throwable exception) {
    				requestFailed(exception);
    			}
    			
    			public void onResponseReceived(Request request, Response response) {
    				if (response.getStatusCode() == 200) {
    					setPMList(response.getText());
    				} else {
    					requestFailed("response failed");
    				}
    			}
    		});    		    		
    	} catch (RequestException ex) {
    		requestFailed(ex);    		
    	}
    }
    
    private void setPMList(String xml) {
    	MenuBar pmMenu = new MenuBar(true);
    	pmMenu.setAutoOpen(true);    	
    	try {
    		Document pmDom = XMLParser.parse(xml);
    		Element pmElement = pmDom.getDocumentElement();
    		XMLParser.removeWhitespace(pmElement);
    		
    		NodeList nodeList = pmElement.getChildNodes();
    		
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node node = nodeList.item(i);
    			String name = node.getNodeName();
   			
    			if (name.equals("physicalEntity")) {
    				Element peElement = (Element) node;
   				    					
    				Node idNode = peElement.getElementsByTagName("dbId").item(0);
    				Long molId = Long.parseLong(idNode.getChildNodes().item(0).getNodeValue());
    					
    				Node nameNode = peElement.getElementsByTagName("displayName").item(0);
    				String molName = nameNode.getChildNodes().item(0).getNodeValue();
    					
    				ReactomeObject pm = new ReactomeObject();
    				pm.setDisplayName(molName);
    				pm.setReactomeId(molId);   				
    		
    				pmMenu.addItem(molName, new Command() {

						@Override
						public void execute() {
														
						}    					
    				});
    			}	
    		}
    		CanvasPopupMenu popup = diagramPane.getPopupMenu();    		
    		pmMenu.setStyleName(diagramPane.getStyle().subMenu());
    		pmMenu.addSeparator();
    		popup.getMenuBar().addItem("Participating Molecules", pmMenu);
    		popup.show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * Load a pathway diagram for a specified Pathway DB_ID.
     * @param dbId db_id for a pathway.
     * @return
     */
    public void loadDiagramForDBId(final Long dbId) {
        String hostUrl = getHostUrl();
//        System.out.println("Host url: " + hostUrl);
        // Do some simple parsing
        int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
        String url = hostUrl.substring(0, lastIndex + 1) + PATHWAY_DIAGRAM + dbId + "/xml";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    requestFailed(exception);
                }
                public void onResponseReceived(Request request, Response response) {
                    renderXML(response.getText(), dbId);
                }
            });
        } catch (RequestException ex) {
            requestFailed(ex);
        } 
        
    }
    
    /**
     * Load a pathway diagram for a specified Pathway XML
     * @param xml the XML data for a pathway
     * @param dbId the pathway dbId associated with this XML.
     */
    public void loadDiagramForXML(String xml, Long dbId){
    	renderXML(xml, dbId);
    }
    
    /**
     * 
     * @param exception Exception whenever the XML file is not load
     */
    protected void requestFailed(Throwable exception) {
        Window.alert("Failed to send the message: " + exception.getMessage());
    }
    
    /**
     * 
     * @param message An error message to alert the user of problems
     */
    protected void requestFailed(String message) {
        Window.alert("WARNING: " + message);
    }
    
    /** 
     * Parses the XML Text and Builds a HashMap of the nodes and the edges. Renders the Canvas Visualization.
     * @param xmlText The XML Text to be parsed
     */
    private void renderXML(String xmlText, Long dbId) {
        //System.out.println(xmlText);
        Image loadingIcon = diagramPane.getLoadingIcon();
    	loadingIcon.setVisible(true);
        try {
            Document pathwayDom = XMLParser.parse(xmlText);
            Element pathwayElement = pathwayDom.getDocumentElement();
            XMLParser.removeWhitespace(pathwayElement);
            CanvasPathway pathway = createPathway(pathwayElement);
            pathway.buildPathway(pathwayElement);
            // A PathwayDiagram can be shared by more than one pathway. 
            // So reactomeId in the XML text is not reliable at all if it is
            // there. An external dbId for pathway is needed to set the correct
            // pathway id.
            pathway.setReactomeId(dbId); 
            diagramPane.setPathway(pathway);
        }
        catch(Exception e) {
            Window.alert("Error in parsing XML: " + e);
            e.printStackTrace();
        }
    	loadingIcon.setVisible(false);
    }
    
    /**
     * Create a CanvasPathway object based on the passed XML element.
     * @param pathwayElm
     * @return
     */
    private CanvasPathway createPathway(Element pathwayElm) {
        String isDiseaseRelated = pathwayElm.getAttribute("isDisease");
        if (isDiseaseRelated != null && isDiseaseRelated.equals("true"))
            return new DiseaseCanvasPathway();
        return new CanvasPathway();
    }
    
    public void setHostUrl(String hostUrl){
    	this.hostUrl = hostUrl;
    }
    
    public String getHostUrl() {
    	if (hostUrl != null) {
    		return this.hostUrl;
    	}
    	else{
    		return GWT.getHostPageBaseURL();
    	}
    }
    
}
