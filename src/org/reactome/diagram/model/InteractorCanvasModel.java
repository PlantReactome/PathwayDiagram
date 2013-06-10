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
    private String interactorDatabase; // The currently selected interaction database   
   	private Map<String, String> psicquicMap; // PSICQUIC service names mapped to urls obtained from the online registry  	
	private Map<String, String> userFileMap; // User file uploads mapping a label provided by the user to the file id on the server
   	private Map<String, String> interactorDBMap; // Represents the psicquicMap with some values overridden from a configuration file  
	private InteractorCanvas interactorCanvas; // The canvas using the interaction database info provided here
	private InteractorConfidenceScoreColourModel confidenceScoreColourModel;
	
	public InteractorCanvasModel() {
		interactorDatabase = defaultInteractorDatabase;
		psicquicMap = new HashMap<String, String>();
		userFileMap = new HashMap<String, String>();
		confidenceScoreColourModel = new InteractorConfidenceScoreColourModel();
	}
		
    public InteractorCanvasModel(InteractorCanvas interactorCanvas) {
    	this();    	
    	this.interactorCanvas = interactorCanvas;
    }
    
    public void setInteractorDatabase(String interactorDatabase) {
    	// Already set to the chosen interactor database
    	if (this.interactorDatabase.equals(interactorDatabase))
    		return;
    	    	
    	this.interactorDatabase = interactorDatabase;
    	InteractorEdge.setUrl(interactorDBMap, interactorDatabase);
    	if (interactorCanvas != null) {
    		interactorCanvas.reObtainProteinsForNewInteractorDatabase();
    	}
    }
    
	public String getInteractorDatabase() {
		return interactorDatabase;
	}
	
	public Map<String, String> getPSICQUICMap() {
		return psicquicMap;
	}
	
	public void setPSICQUICMap(String xml) {
		psicquicMap.clear();
		
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
	
	public InteractorConfidenceScoreColourModel getConfidenceScoreColourModel() {
		return confidenceScoreColourModel;		
	}

	public void addNewPSICQUICService(String serviceName, String serviceUrl) {
		if (!psicquicMap.containsKey(serviceName))
			psicquicMap.put(serviceName, serviceUrl);
	}
	
	public void addNewUploadedUserFile(String labelName, String fileId) {
		labelName = labelName + " (file upload)";
		userFileMap.put(labelName, fileId);
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
		//final String separator = "--------------------";
		
		addMapToListBox(userFileMap, interactorDBList);
		//interactorDBList.addItem(separator);
		addMapToListBox(psicquicMap, interactorDBList);
		
		interactorDBList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {				
				String selection = interactorDBList.getItemText(interactorDBList.getSelectedIndex());
				
				//if (selection.equals(separator))
								
				if (userFileMap.containsKey(selection)) 
					setInteractorDatabase(userFileMap.get(selection));
				else
					setInteractorDatabase(selection);
			}
			
		});
				
		return interactorDBList;						
	}

	private void addMapToListBox(Map<String, String> map, ListBox interactorDBList) {
		List<String> dbs = new ArrayList<String>(map.keySet());
		Collections.sort(dbs);
		
		for (int i = 0; i < dbs.size(); i++) {
			String db = dbs.get(i);
			
			interactorDBList.addItem(db, map.get(db));
			if (db.equals(defaultInteractorDatabase))
				interactorDBList.setSelectedIndex(interactorDBList.getItemCount() - 1);
		}
			
	}
	
	public class InteractorConfidenceScoreColourModel {
		private Double confidenceLevelThreshold;
		private String colourAboveThreshold;
		private String colourBelowThreshold;
		private Boolean coloringModeOn;
		
		public InteractorConfidenceScoreColourModel() {
			this(0.5, // Default threshold
				 "rgb(0, 255, 0)", // Green default color for above threshold
				 "rgb(255, 0, 0)"); // Red default color for below threshold
		}
		
		public InteractorConfidenceScoreColourModel(Double confidenceLevelThreshold, 
													String colourAboveThreshold,
													String colourBelowThreshold) {
			this.confidenceLevelThreshold = confidenceLevelThreshold;
			this.colourAboveThreshold = colourAboveThreshold;
			this.colourBelowThreshold = colourBelowThreshold;
			
			this.coloringModeOn = false;
		}

		public Double getConfidenceLevelThreshold() {
			return confidenceLevelThreshold;
		}
		
		public void setConfidenceLevelThreshold(Double confidenceLevelThreshold) {
			this.confidenceLevelThreshold = confidenceLevelThreshold;
			sendUpdateToCanvasIfColoringOn();
		}
		
		public String getColourAboveThreshold() {
			return colourAboveThreshold;
		}
		
		public void setColourAboveThreshold(String colourAboveThreshold) {
			this.colourAboveThreshold = colourAboveThreshold;
			sendUpdateToCanvasIfColoringOn();
		}
		
		public String getColourBelowThreshold() {
			return colourBelowThreshold;
		}
		
		public void setColourBelowThreshold(String colourBelowThreshold) {
			this.colourBelowThreshold = colourBelowThreshold;
			sendUpdateToCanvasIfColoringOn();
		}

		public Boolean getColoringModeOn() {
			return coloringModeOn;
		}

		public void setColoringModeOn(Boolean coloringModeOn) {
			this.coloringModeOn = coloringModeOn;
			sendUpdateToCanvas();
		}
		
		private void sendUpdateToCanvasIfColoringOn() {
			if (coloringModeOn)
				sendUpdateToCanvas();
		}
		
		private void sendUpdateToCanvas() {
			if (interactorCanvas != null)
				InteractorCanvasModel.this.interactorCanvas.setInteractorColouring(this);
		}
		
	}
}
