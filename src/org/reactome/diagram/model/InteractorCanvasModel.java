/*
 * Created on April 17, 2013
 *
 */
package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.reactome.diagram.client.InteractorCanvas;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * This class is used to represent interaction database information.
 * @author weiserj
 *
 */
public class InteractorCanvasModel {
    private final String defaultInteractorDatabase = "IntAct";    
    private String interactorDatabase;    
   	private Map<String, String> psicquicMap;   	
	private Map<String, String> interactorDBMap; 
	private InteractorCanvas interactorCanvas;
	
	public InteractorCanvasModel() {
		interactorDatabase = defaultInteractorDatabase;
	}
		
    public InteractorCanvasModel(InteractorCanvas interactorCanvas) {
    	this();    	
    	this.interactorCanvas = interactorCanvas;
    }
    
    public void setInteractorDatabase(String interactorDatabase) {
    	setInteractorDatabase(interactorDatabase, Boolean.FALSE);
    }
    
    public void setInteractorDatabase(String interactorDatabase, Boolean initializing) {
    	// Already set to the chosen interactor database
    	if (this.interactorDatabase == interactorDatabase)
    		return;
    	
    	// if the interactor database contains 'Interaction File', this is a user
    	// uploaded file id to be used as the interactorDatabase name
    	String uploadIdFlag = "Interaction_File";
    	if (interactorDatabase.contains(uploadIdFlag)) {
    		Integer uploadIdStart = interactorDatabase.indexOf(uploadIdFlag); 
    		String uploadId = interactorDatabase.substring(uploadIdStart);
    		interactorDatabase = new String(uploadId);
    	}
    	
    	
    	this.interactorDatabase = interactorDatabase;
    	InteractorEdge.setUrl(interactorDBMap, interactorDatabase);
    	if (interactorCanvas != null) {
    		interactorCanvas.setInteractorDatabase(interactorDatabase, initializing);
    	}
    }
    
	public String getInteractorDatabase() {
		return interactorDatabase;
	}
	
	public Map<String, String> getPSICQUICMap() {
		return psicquicMap;
	}
	
	public void setPSICQUICMap(String xml) {
		psicquicMap = new HashMap<String, String>();
		
		Document psicquicDom = XMLParser.parse(xml);
		Element psicquicElement = psicquicDom.getDocumentElement();
		XMLParser.removeWhitespace(psicquicElement);
		
		NodeList nodeList = psicquicElement.getChildNodes();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			
			if (name.equals("service")) {
				Element serviceElement = (Element) node;
				
				Node nameNode = serviceElement.getElementsByTagName("name").item(0);
				String serviceName = nameNode.getChildNodes().item(0).getNodeValue();
				
				Node urlNode = serviceElement.getElementsByTagName("restUrl").item(0);
				String serviceUrl = urlNode.getChildNodes().item(0).getNodeValue();
				
				psicquicMap.put(serviceName, serviceUrl);
			}
		}
		
		addToInteractorDBMap(psicquicMap);
	}
	
	public Map<String, String> getInteractorDBMap() {
		return interactorDBMap;
	}
	
	public InteractorCanvas getInteractorCanvas() {
		return interactorCanvas;
	}

	public void setInteractorCanvas(InteractorCanvas interactorCanvas) {
		this.interactorCanvas = interactorCanvas;
	}

	public void addNewPSICQUICService(String serviceName, String serviceUrl) {
		if (!interactorDBMap.containsKey(serviceName))
			interactorDBMap.put(serviceName, serviceUrl);
	}
	
	public void addToInteractorDBMap(Map<String, String> map) {
		if (interactorDBMap == null) {
			interactorDBMap = map;
		} else {
			for (String db : map.keySet()) {
				if (!interactorDBMap.containsKey(db))
					interactorDBMap.put(db, map.get(db));
			}
		}
	}
	
	public ListBox getInteractorDBListBox() {
		final ListBox interactorDBList = new ListBox();	
		
		List<String> dbs = new ArrayList<String>(interactorDBMap.keySet());
		Collections.sort(dbs);
		for (int i = 0; i < dbs.size(); i++) {
			String db = dbs.get(i);
			
			interactorDBList.addItem(db, interactorDBMap.get(db));
			if (db.equals(defaultInteractorDatabase)) {
				interactorDBList.setSelectedIndex(i);
				//setInteractorDatabase(db, true);				
			}
		}
		
		interactorDBList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {				
				setInteractorDatabase(interactorDBList.getItemText(interactorDBList.getSelectedIndex()));				
			}
			
		});
		
		return interactorDBList;				
	}
}
