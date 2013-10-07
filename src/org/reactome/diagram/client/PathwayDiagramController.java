/*

 * Created on Nov 9, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.HashMap;
import java.util.List;

import org.reactome.diagram.event.SubpathwaySelectionEvent;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.DiseaseCanvasPathway;
import org.reactome.diagram.model.InteractorCanvasModel;
import org.reactome.diagram.model.InteractorEdge;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.view.DefaultColorScheme;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

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

    public void setInteractorDBList(final InteractorCanvasModel interactorCanvasModel) {
        String url = GWT.getHostPageBaseURL() + "InteractorEdgeUrls.txt";
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

        try {
            builder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request,	Response response) {
                    if (response.getStatusCode() == 200) {
                        interactorCanvasModel.addToInteractorDBMap(getInteractionDBMap(response.getText()));                        
                        addPSICQUICList(interactorCanvasModel);

                        InteractorEdge.setUrl(interactorCanvasModel.getInteractorDBMap(),
                        					  interactorCanvasModel.getInteractorDatabase());
                        //diagramPane.getControls().addInteractionOverlayButton();
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

    private void addPSICQUICList(final InteractorCanvasModel interactorCanvasModel) {
        String url = this.getHostUrl() + "psicquicList";
 
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/xml");

        try {
            requestBuilder.sendRequest(null, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
                        interactorCanvasModel.setPSICQUICMap(response.getText());
					} else {
						requestFailed("Could not retrieve psiquicList.  HTML Status code - " + response.getStatusCode());
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


    public void getReferenceEntity(final Long dbId, RequestCallback callback) {
        String url = this.getHostUrl() + "referenceEntity/" + dbId;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        try {
            requestBuilder.sendRequest(null, callback);
        } catch (RequestException ex) 	{
            requestFailed(ex);
        }
    }
    
    
    public void getInteractors(final ProteinNode selected, RequestCallback callback) {
        Long dbId = selected.getReactomeId();
        
        diagramPane.initInteractorCanvas(); // Does nothing if the canvas already exists
        final InteractorCanvas ic = diagramPane.getInteractorCanvas();

        String url = this.getHostUrl() + "psiquicInteractions/" + dbId + "/" + diagramPane.getInteractorCanvasModel().getInteractorDatabase();
        
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/xml");

        ic.setLoadingInteractors(true);
        try {
            requestBuilder.sendRequest(null, callback);
        } catch (RequestException ex) {
            requestFailed(ex);
            ic.setLoadingInteractors(false);
        }
    }
    
    public void getPathwayInteractors(CanvasPathway pathway, String interactionDatabase, RequestCallback callback) {
    	String url = getHostUrl() + "psiquicInteractions/" + pathway.getReactomeId() + "/" + interactionDatabase;
    	
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	try {
    		requestBuilder.sendRequest(null, callback);
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }
    
    public void openInteractionExportPage(Long dbId) {
    	String hostUrl = getHostUrl();
    	
    	String serviceName = diagramPane.getInteractorCanvasModel().getInteractorDatabase();
    	
    	int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
    	String url = hostUrl.substring(0, lastIndex + 1) + RESTFUL_URL + "exportPsiquicInteractions/" + dbId + "/" + serviceName;

    	Window.open(url, null, null);
    }

    public void getParticipatingMolecules(Long dbId, RequestCallback callback) {
        String url = this.getHostUrl() + "complexSubunits/" + dbId;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/xml");

        try {
            requestBuilder.sendRequest(null, callback);
        } catch (RequestException ex) {
            requestFailed(ex);
        }
    }

    public void getPhysicalToReferenceEntityMap(CanvasPathway pathway, RequestCallback callback) {
    	getPhysicalToReferenceEntityMap(pathway.getReactomeId(), callback);
    }
    
    public void getPhysicalToReferenceEntityMap(Long pathwayId, RequestCallback callback) {
    	String url = this.getHostUrl() + "getPhysicalToReferenceEntityMaps/" + pathwayId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/json");
    	
    	try {
    		requestBuilder.sendRequest(null, callback);
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }
    
    public void queryByIds(String dbIds, String className, RequestCallback callback) {
    	String hostUrl = getHostUrl();
    	
    	int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
    	String url = hostUrl.substring(0, lastIndex + 1) + RESTFUL_URL + "queryByIds";
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	StringBuffer postData = new StringBuffer();
    	postData.append(URL.encode("ID"));
    	postData.append("=");
    	postData.append(URL.encode(dbIds));
    	
    	try {
    		requestBuilder.sendRequest(postData.toString(), callback);
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }
    
    public void getOtherPathways(Long dbId, RequestCallback callback) {
    	String hostUrl = getHostUrl();
    	
    	int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
    	String url = hostUrl.substring(0, lastIndex + 1) + RESTFUL_URL + "pathwaysForEntities"; 
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	StringBuffer postData = new StringBuffer();
    	postData.append(URL.encode("ID"));
    	postData.append("=");
    	postData.append(URL.encode(dbId.toString()));
    	//requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
    	
    	try {
    		requestBuilder.sendRequest(postData.toString(), callback);
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }

    public void getPathwayDiagram(Long dbId, RequestCallback callback) {
    	getPathwayDiagramData(dbId, callback, "png");    	
    }
    
    public void getCanvasPathwayXML(Long dbId, RequestCallback callback) {
    	getPathwayDiagramData(dbId, callback, "xml");
    }
    
    private void getPathwayDiagramData(Long dbId, RequestCallback callback, String format) {
    	sendRequest(getPathwayDiagramRequestBuilder(dbId, format), callback);
    }
    
    /**
     * Load a pathway diagram for a specified Pathway DB_ID.
     * @param dbId db_id for a pathway.
     */
    public void loadDiagramForDBId(final Long dbId) {
        RequestBuilder requestBuilder = getPathwayDiagramRequestBuilder(dbId, "xml");

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
    
    private RequestBuilder getPathwayDiagramRequestBuilder(Long dbId, String format) {
       	String url = this.getHostUrl() + "pathwayDiagram/" + dbId + "/" + format;
    	return new RequestBuilder(RequestBuilder.GET, url);
    }
    
    /**
     *
     * @param exception Exception whenever the XML file is not load
     */
    protected void requestFailed(Throwable exception) {
        AlertPopup.alert("Failed to send the message: " + exception.getMessage());
    }

    /**
     *
     * @param message An error message to alert the user of problems
     */
    protected void requestFailed(String message) {
        AlertPopup.alert("WARNING: " + message);
    }    
    

    /**
     * Parses the XML Text and Builds a HashMap of the nodes and the edges. Renders the Canvas Visualization.
     * @param xmlText The XML Text to be parsed
     */
    private void renderXML(String xmlText, Long dbId) {
        //System.out.println(xmlText);
        //Image loadingIcon = diagramPane.getLoadingIcon();
        //loadingIcon.setVisible(true);

        Document pathwayDom; 
        try {
        	pathwayDom = XMLParser.parse(xmlText);            
        } catch (DOMParseException e) {
        	requestFailed(e);
        	e.printStackTrace();
        	return;
        }        
        
        diagramPane.setCursor(Cursor.WAIT);
        Element pathwayElement = pathwayDom.getDocumentElement();
        XMLParser.removeWhitespace(pathwayElement);
        CanvasPathway pathway = createPathway(pathwayElement);
        pathway.buildPathway(pathwayElement);

        // A PathwayDiagram can be shared by more than one pathway.
        // So reactomeId in the XML text is not reliable at all if it is
        // there. An external dbId for pathway is needed to set the correct
        // pathway id.
            
        // Apply the default color schemes
//            DefaultColorScheme colorScheme = new DefaultColorScheme();
//            colorScheme.applyScheme(pathway);
            
        pathway.setReactomeId(dbId);
        getPhysicalToReferenceEntityMap(pathway, setCanvasPathway(pathway));
        diagramPane.setCursor(Cursor.DEFAULT);
        
        //loadingIcon.setVisible(false);
    }

    private RequestCallback setCanvasPathway(final CanvasPathway pathway) {
    	RequestCallback setCanvasPathway = new RequestCallback() {

    		public void onResponseReceived(Request request, Response response) {
    			if (response.getStatusCode() == 200) {
    				pathway.setDbIdToRefEntity(response.getText());
    				diagramPane.setCanvasPathway(pathway);
    			} else {
    				requestFailed("Unable to get physical to reference entity map");
    			}
    		}
    		
    		public void onError(Request request, Throwable exception) {
    			requestFailed(exception);
    		}
    	};
    	
    	return setCanvasPathway;
    }
    
    public void getDiagramPathwayId(final Long dbId) {
    	String url = getHostUrl() + "queryEventAncestors/" + dbId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/xml");
    	
    	try {
    		//System.out.println("Looking for parent diagram id");
    		requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					final String ERROR = "Unable to obtain diagram for pathway " + dbId;
					
					if (response.getStatusCode() != 200) {
						requestFailed(ERROR);
						return;
					}
					
					Document ancestorDom;
					try {
						ancestorDom = XMLParser.parse(response.getText());
					} catch (DOMParseException e) {
						requestFailed(e);
						e.printStackTrace();
						return;
					}
					
					Element ancestorElement = ancestorDom.getDocumentElement();
					XMLParser.removeWhitespace(ancestorElement);
										
					SubpathwaySelectionEvent subPathwayEvent = new SubpathwaySelectionEvent();
					subPathwayEvent.setSubpathwayId(dbId);
					
					Long pathwayDiagramId = null;
					
					NodeList ancestorLists = ancestorElement.getElementsByTagName("DatabaseObjects"); 
					
					ancestorLists:
					for (int i = 0; i < ancestorLists.getLength(); i++) { 
						
						Node ancestorListNode = ancestorLists.item(i);
						
						NodeList ancestorList = ((Element) ancestorListNode).getElementsByTagName("databaseObject");
						
						// Most related ancestor pathway at bottom, so counting down
						for (int j = ancestorList.getLength() - 1; j >= 0; j--) {							
							Node ancestor = ancestorList.item(j);
																									
							String diagramBoolean = ((Element) ancestor).getElementsByTagName("hasDiagram").item(0).getFirstChild().getNodeValue();								
							Boolean hasDiagram = new Boolean(diagramBoolean);
							
							if (hasDiagram) {
								pathwayDiagramId = new Long(((Element) ancestor).getElementsByTagName("dbId").item(0).getFirstChild().getNodeValue());
								break ancestorLists;
							}
						}
					}	
						
				//		System.out.println("Pathway Id - " + dbId);
				//		System.out.println("Diagram Id - " + pathwayDiagramId);
						
					if (pathwayDiagramId != null) {
						subPathwayEvent.setDiagramPathwayId(pathwayDiagramId);
						diagramPane.fireEvent(subPathwayEvent);
					} else {
						requestFailed(ERROR);
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
    
    private void sendRequest(RequestBuilder requestBuilder, RequestCallback callback) {
    	try {
    		requestBuilder.sendRequest(null, callback);
    	} catch (RequestException ex) {
    		requestFailed(ex);
    	}
    }

}
