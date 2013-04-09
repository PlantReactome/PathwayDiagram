/*
 * Created on Dec 9, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * This class is used for test only.
 * @author gwu
 *
 */
public class PathwayTreeBrowser {
    private DockPanel contentPane;
    private Tree pathwayTree;
    private ScrollPanel sp;
    private PathwayDiagramPanel diagramPane;
    // Track if sub-pathways have been loaded
    private Set<Long> loadedIds;
    
    public PathwayTreeBrowser(PathwayDiagramPanel pathwayPane) {
        this.diagramPane = pathwayPane;
        init();
    }

    protected void init() {
        contentPane = new DockPanel();
        pathwayTree = new Tree();
        sp = new ScrollPanel(pathwayTree);
        contentPane.setStyleName("dialogPane");
        sp.setStyleName("pathwayTree");
        int spWidth = 300;
        int spHeight = 400;
        sp.setSize(spWidth + "px", spHeight + "px");
        contentPane.add(sp, DockPanel.CENTER);
        Button close = new Button("Close");
        contentPane.add(close, DockPanel.SOUTH);
        contentPane.setCellHorizontalAlignment(close, 
                                               DockPanel.ALIGN_CENTER);
        close.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                diagramPane.contentPane.remove(contentPane);
            }
        });
        // Need to install listener to tree
        pathwayTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem treeItem = event.getSelectedItem();
                ReactomeEvent re = (ReactomeEvent) treeItem.getUserObject();
                Long dbId = re.dbId;
                String type = re.type;
                if (type.equals("pathway")) {
                	diagramPane.setPathway(dbId);
                } else if (type.equals("reaction")) {
                	// Open diagram and centre reaction
                }
            }
        });
        pathwayTree.addOpenHandler(new OpenHandler<TreeItem>() {
            
            @Override
            public void onOpen(OpenEvent<TreeItem> event) {
                TreeItem treeItem = event.getTarget();
                ReactomeEvent re = (ReactomeEvent) treeItem.getUserObject();
                showSubPathways(treeItem, re);
                treeItem.setState(true, false);
            }
        });
    }
    
    private void showSubPathways(final TreeItem item,
                                 ReactomeEvent re) {
        if (loadedIds == null)
            loadedIds = new HashSet<Long>();
        if (loadedIds.contains(re.dbId))
            return;
        loadedIds.add(re.dbId);
        // Need to load subpathway
        String url = getRESTfulURL() + "queryById/Pathway/" + re.dbId;
//        System.out.println("Call " + url);
        RequestBuilder request = new RequestBuilder(RequestBuilder.GET, url);
        request.setHeader("Accept", "application/xml");
        try {
            request.sendRequest(null, new RequestCallback() {
                
                @Override
                public void onResponseReceived(Request request, Response response) {
                    showSubPathways(response.getText(), item);
                }
                
                @Override
                public void onError(Request request, Throwable exception) {
                    diagramPane.getController().requestFailed(exception);
                }
            });
        }
        catch(RequestException ex) {
            diagramPane.getController().requestFailed(ex);
        }
    }
    
    private void showSubPathways(String xml, TreeItem parentItem) {
        Document dom = XMLParser.parse(xml);
        Element pathwayElm = dom.getDocumentElement();
        XMLParser.removeWhitespace(pathwayElm);
        NodeList list = pathwayElm.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node child = list.item(i);
            if (child.getNodeName().equals("hasEvent")) {
                Element elm = (Element) child;
                String type = elm.getAttribute("xsi:type");
                TreeItem pathwayItem = createPathwayItem(elm, type);
                parentItem.addItem(pathwayItem);
            }
        }
        parentItem.getChild(0).remove();
    }
    
    private String getRESTfulURL() {
        PathwayDiagramController controller = diagramPane.getController();
        String hostUrl = controller.getHostUrl();
              System.out.println("Host url: " + hostUrl);
        // Do some simple parsing
        int lastIndex = hostUrl.lastIndexOf("/", hostUrl.length() - 2);
        String url = hostUrl.substring(0, lastIndex + 1) + "RESTfulWS/";
        return url;
    }
    
    public void initTree() {
        final PathwayDiagramController controller = diagramPane.getController();
        
//        String url = getRESTfulURL() + "participatingMolecules/export/109581";
//        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
//        requestBuilder.setHeader("Accept", "text/plain");
////        requestBuilder.setRequestData("Talk back!");
//        try {
//            requestBuilder.sendRequest("Talk back!", new RequestCallback() {
//                public void onError(Request request, Throwable exception) {
//                    controller.requestFailed(exception);
//                }
//                public void onResponseReceived(Request request, Response response) {
//                    System.out.println("Request: " + request.toString());
//                    System.out.println("Reponse: " + response.getText());
//                }
//            });
//        } 
//        catch (RequestException ex) {
//            controller.requestFailed(ex);
//        } 
        
                String url = getRESTfulURL() + "frontPageItems/homo+sapiens";
//                String url = getRESTfulURL() + "frontPageItems/canis+familiaris";
                RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
                requestBuilder.setHeader("Accept", "application/xml");
                try {
                    requestBuilder.sendRequest(null, new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            controller.requestFailed(exception);
                        }
                        public void onResponseReceived(Request request, Response response) {
                            setUpTree(response.getText());
                        }
                    });
                } 
                catch (RequestException ex) {
                    controller.requestFailed(ex);
                } 
    }
    
    private void setUpTree(String text) {
//        Window.alert(text);
//         System.out.println(text);
    	if (text == null || text.isEmpty()) {
            PathwayDiagramController controller = diagramPane.getController();
            controller.requestFailed("Null or empty hierarchy description string, this may be due to connection problems with the web services");
    		return;
    	}
        Document dom = XMLParser.parse(text);
        Element pathwaysElm = dom.getDocumentElement();
        XMLParser.removeWhitespace(pathwaysElm);
        NodeList nodeList = pathwaysElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (child.getNodeName().equals("pathway")) {
                Element elm = (Element) nodeList.item(i);
                TreeItem pathwayItem = createPathwayItem(elm, "pathway");
                pathwayTree.addItem(pathwayItem);
            }
        }
//        int width = diagramPane.getOffsetWidth();
//        int height = diagramPane.getOffsetHeight();
        diagramPane.contentPane.add(contentPane, 
                                    20, 
                                    50);
    }
    
    private TreeItem createPathwayItem(Element pathwayElm, String type) {
        ReactomeEvent re = new ReactomeEvent();
        re.type = type;
        
        NodeList nodeList = pathwayElm.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String name = node.getNodeName();
            if (name.equals("dbId"))
                re.dbId = new Long(node.getFirstChild().getNodeValue());
            else if (name.equals("displayName"))
                re.name = node.getFirstChild().getNodeValue();
        }
        TreeItem treeItem = new TreeItem(re.name);
        treeItem.addItem(""); // Add a place holder so that this item can be opened.
        treeItem.setUserObject(re);
        return treeItem;
    }
    
    private class ReactomeEvent {
        Long dbId;
        String name;
        String type;
        
        public ReactomeEvent() {
        }
        
    }

}
