/*
 * Created on Nov 9, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.HashMap;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.DiseaseCanvasPathway;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.ProteinNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
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
    // The root RESTful URL
	private String hostUrl = null;
		
    @SuppressWarnings("FieldCanBeLocal")
    private final String RESTFUL_URL = "RESTfulWS/";

    private PathwayDiagramPanel diagramPane;
    
    public PathwayDiagramController(PathwayDiagramPanel pane) {
        this.diagramPane = pane;
    }
    
    @SuppressWarnings("UnusedDeclaration")
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
    
    public void setInteractorDBList() {
    	addPSICQUICList();
    	
    	String url = GWT.getHostPageBaseURL() + "InteractorEdgeUrls.txt";
    	RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    	
    	try {
    		builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						diagramPane.getInteractorCanvas().addToInteractorDBMap(getInteractionDBMap(response.getText()));
						//InteractorEdge.setUrl(response.getText(), interactionDatabase);
					} else {
						requestFailed("Could not retrieve InteractorEdgeUrls.txt");
					}
					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);					
				}
    		});
    	} catch (RequestException e) {
    		requestFailed(e);
    	}
    }
    
    private void addPSICQUICList() {
        String url = this.getHostUrl() + "psicquicList";
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						diagramPane.getInteractorCanvas().setPSICQUICMap(response.getText());
					} else {
						requestFailed("Could not retrieve psiquicList");
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);					
				}
    			
    		});
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }
    
    private HashMap<String, String> getInteractionDBMap(String list) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	String [] records = list.split("\n");
    	
    	for (String record : records) {
    		String [] columns = record.split("\t");
    				
    		map.put(columns[0], columns[1]);		
    	}
    	
    	return map;    	
    }
    
    
    public void openInteractionPage(final InteractorEdge selected) {
    	final ProteinNode protein = selected.getProtein();

        String url = this.getHostUrl() + "referenceEntity/" + protein.getReactomeId();
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {				
						protein.setRefId(response.getText());
						Window.open(selected.getUrl(), null, null);
					} else {
						requestFailed("Failed to get protein's uniprot id");
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);
				}
    			
    			
    		}); 
    	} catch (RequestException ex) 	{    
    		requestFailed(ex);
    	}
   	}
    
    public void getInteractors(final ProteinNode selected) {
    	Long dbId = selected.getReactomeId();
    	final InteractorCanvas ic = diagramPane.getInteractorCanvas();

        String url = this.getHostUrl() + "psiquicInteractions/" + dbId + "/" + ic.getInteractorDatabase();
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");

    	ic.setLoadingInteractors(true);
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						selected.setInteractors(response.getText());
												
						ic.addProtein(selected);
					} else {
						requestFailed("Failed to get interactors - " + response.getStatusText());
						selected.setDisplayingInteractors(false);						
					}
					
					ic.setLoadingInteractors(false);
				}

				public void onError(Request request, Throwable exception) {
					requestFailed(exception);
					ic.setLoadingInteractors(false);
				}
    			
    		});
    	} catch (RequestException ex) {
    		requestFailed(ex);
    		ic.setLoadingInteractors(false);
    	}    	
    }
    
    public void getParticipatingMolecules(final Long dbId) {
        String url = this.getHostUrl() + "complexSubunits/" + dbId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {
    			public void onError(Request request, Throwable exception) {
    				requestFailed(exception);
    			}
    			
    			public void onResponseReceived(Request request, Response response) {
    				if (response.getStatusCode() == 200) {
    					diagramPane.getPopupMenu().setPMMenu(response.getText());
    				} else {
    					requestFailed("response failed");
    				}
    			}
    		});    		    		
    	} catch (RequestException ex) {
    		requestFailed(ex);    		
    	}
    }
    
    
    /**
     * Load a pathway diagram for a specified Pathway DB_ID.
     * @param dbId db_id for a pathway.
     */
    public void loadDiagramForDBId(final Long dbId) {
        String url = this.getHostUrl() + "pathwayDiagram/" + dbId + "/xml";
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
        //Image loadingIcon = diagramPane.getLoadingIcon();
    	//loadingIcon.setVisible(true);
        
    	diagramPane.setCursor(Cursor.WAIT);
    	
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
            diagramPane.setCanvasPathway(pathway);
        }
        catch(Exception e) {
            Window.alert("Error in parsing XML: " + e);
            e.printStackTrace();
        }
    	
    	diagramPane.setCursor(Cursor.DEFAULT);
    	
    	//loadingIcon.setVisible(false);
    }
    
    /**
     * Create a CanvasPathway object based on the passed XML element.
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
    	if (hostUrl == null) {
            String aux = GWT.getHostPageBaseURL();
            int lastIndex =  aux.lastIndexOf("/", aux.length() - 2);
            this.hostUrl = aux.substring(0, lastIndex + 1) + RESTFUL_URL;
    	}
        return this.hostUrl;
    }
    
}
