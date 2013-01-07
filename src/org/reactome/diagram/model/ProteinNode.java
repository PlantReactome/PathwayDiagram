/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.List;

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
	}

	public List<InteractorNode> getInteractors() {
		return interactors;
	}

	public void setInteractors(List<InteractorNode> interactors) {
		this.interactors = interactors;
	}
	
	public void setInteractors(String xml) {
		interactors = new ArrayList<InteractorNode>();
		
		Document iDom = XMLParser.parse(xml);
		Element iElement = iDom.getDocumentElement();
		XMLParser.removeWhitespace(iElement);
		
		NodeList interactionList = 
				((Element) iElement.getElementsByTagName("resultList").item(0)).getElementsByTagName("interactionList").item(0).getChildNodes();
		
		InteractorNode iNode;
		for (int i = 0; i < interactionList.getLength(); i++) {
			com.google.gwt.xml.client.Node node = interactionList.item(i);
			String name = node.getNodeName();
			
			if (name.equals("interactors")) {
				Element interactorElement = (Element) node;
				
				com.google.gwt.xml.client.Node accNode = interactorElement.getElementsByTagName("accession").item(0);
				String acc = accNode.getChildNodes().item(0).getNodeValue();
				
				com.google.gwt.xml.client.Node genenameNode = interactorElement.getElementsByTagName("genename").item(0);
				String geneName = genenameNode.getChildNodes().item(0).getNodeValue();
				
				iNode = new InteractorNode();
				iNode.setRefId(acc);
				iNode.setDisplayName(geneName);
				
				interactors.add(iNode);
			}
			
		}
		
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
				String name = node.getNodeName();
									
				if (name.equals("referenceEntity")) {
					Element reElement = (Element) node;
					
					com.google.gwt.xml.client.Node nameNode = reElement.getElementsByTagName("displayName").item(0);
					String displayName = nameNode.getChildNodes().item(0).getNodeValue();
															
					int start = "UniProt:".length();
					int idLength = 6;
								
					this.refId = displayName.substring(start, start + idLength);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
