/*

 * Created on Nov 9, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.HashMap;

import org.reactome.diagram.event.SubpathwaySelectionEvent;
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
        
        diagramPane.initInteractorCanvas();
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
                        
                        if (selected.getInteractors() == null || selected.getInteractors().isEmpty()) {
                        	AlertPopup alert = new AlertPopup(selected.getDisplayName() + " has no interactors for the selected interaction database");
                        	alert.center();
                        }                        	
                        	
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
    
    public void openInteractionExportPage(Long dbId) {
    	String hostUrl = getHostUrl();
    	
    	String serviceName = diagramPane.getInteractorCanvas().getInteractorDatabase();
    	
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

    public void getPhysicalToReferenceEntityMap(Long pathwayId, final boolean updateCanvas) {
    	String url = this.getHostUrl() + "getPhysicalToReferenceEntityMaps/" + pathwayId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	requestBuilder.setHeader("Accept", "application/json");
    	
    	final ExpressionCanvas ec = diagramPane.getExpressionCanvas();
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						ec.getExpressionCanvasModel().setPhysicalToReferenceEntityMap(response.getText(), updateCanvas);
					} else {
						requestFailed("Could not retrieve physical to reference entity map");
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
    
    public void getOtherPathways(final Long dbId) {
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
    		requestBuilder.sendRequest(postData.toString(), new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						diagramPane.getPopupMenu().setPathwayMenu(response.getText());
					} else {
						requestFailed("Could not retrieve other pathways");
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
        AlertPopup alert = new AlertPopup("Failed to send the message: " + exception.getMessage());
        alert.center();
    }

    /**
     *
     * @param message An error message to alert the user of problems
     */
    protected void requestFailed(String message) {
        AlertPopup alert = new AlertPopup("WARNING: " + message);
        alert.center();
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
        } catch (DOMParseException e) {
            // Could be a subpathway with no diagram -- try to get parent pathway diagram instead
        	getDiagramPathwayId(dbId, e);
        } finally {
        	diagramPane.setCursor(Cursor.DEFAULT);
        }
        //loadingIcon.setVisible(false);
    }

    private void getDiagramPathwayId(final Long dbId, final Exception e) {
    	String url = getHostUrl() + "queryEventAncestors/" + dbId;
    	RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
    	
    	try {
    		requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					try {
						Document ancestorDom = XMLParser.parse(response.getText());
						
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
						
						if (pathwayDiagramId != null) {
							subPathwayEvent.setDiagramPathwayId(pathwayDiagramId);
							diagramPane.fireEvent(subPathwayEvent);
						} else {
							throw e;
						}	
					} catch (Exception e) {
						AlertPopup alert = new AlertPopup("Error in parsing XML: " + e);
						alert.center();
						e.printStackTrace();
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

}
