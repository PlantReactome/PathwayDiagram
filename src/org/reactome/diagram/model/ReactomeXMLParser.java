/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class ReactomeXMLParser {
	private Element documentElement;
	
	public ReactomeXMLParser(String xml) {
		setDocumentElement(xml);
	}
	
	public void setDocumentElement(String xml) {
		Document document;
		try {
			document = XMLParser.parse(xml);
		} catch (DOMParseException e) {
			GWT.log("Could not parse xml");
			return;
		}	
			
		Element element = document.getDocumentElement();
		XMLParser.removeWhitespace(element);
		
		documentElement = element;
	}	
	
	public Element getDocumentElement() {
		return documentElement;
	}
	
	public NodeList getNodeList(String containerTag, String desiredTag) {
		if (getDocumentElement() == null)
			return null;
		
		return getNodeList(getDocumentElement(), containerTag, desiredTag);
	}
	
	public NodeList getNodeList(Element xmlElement, String containerTag, String desiredTag) {		
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
	
	public String getXMLNodeValue(String tagName) {
		if (getDocumentElement() == null)
			return null;
		
		return getXMLNodeValue(getDocumentElement(), tagName);
	}
	
	public String getXMLNodeValue(Element xmlElement, String tagName) {
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
}

