/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class ProteinNode extends Node {
	// Uniprot id
	private String refId;
	// If this node has interactors
    private List<InteractorNode> interactors; 
    // A flag to indicate if interactors are being displayed 
    private boolean displayingInteractors;
		
	/**
	 * Default constructor.
	 */
	public ProteinNode() {		
		interactors = new ArrayList<InteractorNode>();		
	}

	public List<InteractorNode> getInteractors() {
		return interactors;
	}

	public void setInteractors(List<InteractorNode> interactors) {
		this.interactors = interactors;
	}
	
	public void setInteractors(String xml) {
		Document iDom;		
		try {
			iDom = XMLParser.parse(xml);
		} catch (DOMParseException e) {
			GWT.log("Interactors could not be parsed from xml", e);
			return;
		}
		
		Element iElement = iDom.getDocumentElement();
		XMLParser.removeWhitespace(iElement);
	
		NodeList interactionList = getNodeList(iElement, "resultList", "interactionList");
	
		interactors = parseInteractorList(interactionList);
	}
	
	public void setInteractors(NodeList interactionList) {
		interactors = parseInteractorList(interactionList);
	}
		
	private List<InteractorNode> parseInteractorList(NodeList interactionList) {			
		List<InteractorNode> interactorNodes = new ArrayList<InteractorNode>();
		
		if (interactionList == null)
			return interactorNodes;
		
		for (int i = 0; i < interactionList.getLength(); i++) {
			com.google.gwt.xml.client.Node node = interactionList.item(i);
			String name = node.getNodeName();
		
			if (name.equals("interactors")) {
				Element interactorElement = (Element) node;
				
				String acc = getXMLNodeValue(interactorElement,"accession");
									
				String geneName = getXMLNodeValue(interactorElement, "genename");
				if (geneName.isEmpty()) {
					continue; // Gene name or some kind of display name is required
				}
			
				Double score;				
				try {
					score = Double.parseDouble(getXMLNodeValue(interactorElement, "score"));				
				} catch (IllegalArgumentException e) {
					score = 0d;
				}

								
				String chemblId = getChemblId(getNodeList(interactorElement, "extraFields", "entry"));								
				
				interactorNodes.add(createInteractor(acc, geneName, score, chemblId));
			}			
		}
		
		Collections.sort(interactorNodes);
		
		return interactorNodes;
	}

	public boolean isDisplayingInteractors() {
		return displayingInteractors;
	}

	public void setDisplayingInteractors(boolean displayingInteractors) {
		this.displayingInteractors = displayingInteractors;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String json) {		
		JSONValue refEntityJSON = JSONParser.parseStrict(json);
		
		if (refEntityJSON == null || refEntityJSON.isArray() == null)
			return;
		
		for (int i = 0; i < refEntityJSON.isArray().size(); i++) {	
			JSONObject refEntity = refEntityJSON.isArray().get(i).isObject(); 
						
			JSONValue displayNameJSONValue = refEntity.get("displayName");
			
			if (displayNameJSONValue != null) {			
				String displayName = displayNameJSONValue.isString().stringValue();
				if (displayName != null) {
					int start = displayName.indexOf(":") + 1;
					int end = displayName.indexOf(" ");
						
					String name = displayName.substring(start, end);
				
					if (name != null && !name.isEmpty()) {
						this.refId = name;
						break;
					}
				}
			}
		}		
	}	
	
	private NodeList getNodeList(Element xmlElement, String containerTag, String desiredTag) {		
		NodeList containerCollection = xmlElement.getElementsByTagName(containerTag); 
		if (containerCollection == null || containerCollection.item(0) == null)
			return null;
		
		NodeList desiredTagCollection = ((Element) containerCollection.item(0)).getElementsByTagName(desiredTag);
		if (desiredTagCollection == null)
			return null;
		
		com.google.gwt.xml.client.Node desiredTagNode = desiredTagCollection.item(0);
		if (desiredTagNode == null)
			return null;
		
		return desiredTagNode.getChildNodes();		
	}
	
	private String getXMLNodeValue(Element xmlElement, String tagName) {
		String nodeValue = null;
		
		NodeList nodes = xmlElement.getElementsByTagName(tagName);		
		if (nodes != null && nodes.item(0) != null) { 
			NodeList nodeChildren = nodes.item(0).getChildNodes();
			
			if (nodeChildren != null && nodeChildren.item(0) != null) {
				nodeValue = nodeChildren.item(0).getNodeValue();
			}
		} 
		
		if (nodeValue == null)
			nodeValue = ""; 
				
		return nodeValue;		
	}
	
	private InteractorNode createInteractor(String acc, String geneName, Double score, String chemblID) {
		InteractorNode interactor = new InteractorNode();
		interactor.setAccession(acc);
		interactor.setDisplayName(geneName);
		interactor.setScore(score);
		interactor.setChemicalId(chemblID);
		
		if (acc.matches("^(C[Hh]E(BI:|MBL))?\\d+") || !chemblID.isEmpty() ) {
			interactor.setRefType(InteractorType.Chemical);
		} else {
			interactor.setRefType(InteractorType.Protein);
		}
		
		return interactor;		
	}
	
	private String getChemblId(NodeList entryNodes) {
		if (entryNodes == null)
			return "";
		
		com.google.gwt.xml.client.Node keyNode = entryNodes.item(0);
		com.google.gwt.xml.client.Node valueNode = entryNodes.item(1);
			
		if (keyNode != null) {			
			com.google.gwt.xml.client.Node keyNodeValue = keyNode.getChildNodes().item(0);
			if (keyNodeValue != null && keyNodeValue.getNodeValue().equals("chemblid")) {
				com.google.gwt.xml.client.Node valueNodeValue = valueNode.getChildNodes().item(0);
				if (valueNodeValue != null) {
					return valueNodeValue.getNodeValue();
				}
			}
		}
	
		return "";
	}
}

