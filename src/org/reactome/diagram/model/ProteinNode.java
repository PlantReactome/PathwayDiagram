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
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

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
		interactors.clear();
		
		Document iDom = XMLParser.parse(xml);
		Element iElement = iDom.getDocumentElement();
		XMLParser.removeWhitespace(iElement);
		
		NodeList interactionList; 	
		try {	
			interactionList = ((Element) iElement.getElementsByTagName("resultList").item(0)).getElementsByTagName("interactionList").item(0).getChildNodes();
		} catch (NullPointerException npe) {			
			return;
		}
		
		InteractorNode iNode;
		for (int i = 0; i < interactionList.getLength(); i++) {
			com.google.gwt.xml.client.Node node = interactionList.item(i);
			String name = node.getNodeName();
		
			if (name.equals("interactors")) {
				Element interactorElement = (Element) node;
			
				
				String acc;
				String geneName;
				String chemblId = null;
				String scoreString;
				double score;
				
				try {
					com.google.gwt.xml.client.Node accNode = interactorElement.getElementsByTagName("accession").item(0);								
					acc = accNode.getChildNodes().item(0).getNodeValue();
			
					com.google.gwt.xml.client.Node genenameNode = interactorElement.getElementsByTagName("genename").item(0);
					geneName = genenameNode.getChildNodes().item(0).getNodeValue();
				
					com.google.gwt.xml.client.Node scoreNode = interactorElement.getElementsByTagName("score").item(0);
					scoreString = scoreNode.getChildNodes().item(0).getNodeValue();
					score = Double.parseDouble(scoreString);
				} catch (NullPointerException e) {
					continue;
				}

				try {
					com.google.gwt.xml.client.Node extraNode = interactorElement.getElementsByTagName("extraFields").item(0);
					
					NodeList entryNodes = ((Element) extraNode).getElementsByTagName("entry");
					for (int j = 0; j < entryNodes.getLength(); j++) {
						com.google.gwt.xml.client.Node entryNode = entryNodes.item(0);
												
						String key = entryNode.getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
						if (key.equals("chemblid")) {
							String value = entryNode.getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
							chemblId = value;
						}
					}						
				} catch (NullPointerException e) {
					chemblId = null;
				}
				
				iNode = new InteractorNode();
				iNode.setAccession(acc);
				if (acc.matches("^(C[Hh]E(BI:|MBL))?\\d+") || chemblId != null) {
					iNode.setRefType(InteractorType.Chemical);
				} else {
					iNode.setRefType(InteractorType.Protein);
				}
				iNode.setDisplayName(geneName);
				iNode.setScore(score);
				iNode.setChemicalId(chemblId);
			
				interactors.add(iNode);
			}			
		}
		
		Collections.sort(interactors);
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

	public void setRefId(String xml) {
		try {
			Document refDom = XMLParser.parse(xml);
			Element refElement = refDom.getDocumentElement();
			XMLParser.removeWhitespace(refElement);
			
			NodeList nodeList = refElement.getChildNodes();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				com.google.gwt.xml.client.Node node = nodeList.item(i);
				//String name = node.getNodeName();
									
				//if (name.equals("referenceEntity")) {
					Element reElement = (Element) node;
					
					com.google.gwt.xml.client.Node nameNode = reElement.getElementsByTagName("displayName").item(0);
					String displayName = nameNode.getChildNodes().item(0).getNodeValue();
					
					int start = displayName.indexOf(":") + 1;
					int end = displayName.indexOf(" ");
								
					this.refId = displayName.substring(start, end);
				//}
			}
		} catch (Exception e) {
			GWT.log("Could not set reference id", e);			
		}
	}	
}
