/**
 * 
 * @author Maulik Kamdar
 * Google Summer of Code Project 
 * Canvas Based Pathway Visualization Tool
 * 
 * Parts of Code have been derived from the tutorials and samples provided at Google Web Toolkit Website 
 * http://code.google.com/webtoolkit/doc/latest/tutorial/
 * 
 * Ideas for canvas based initiation functions derived and modified from Google Canvas API Demo
 * http://code.google.com/p/gwtcanvasdemo/source/browse/
 * 
 * Interactivity ideas taken from HTML5 canvas tutorials
 * http://www.html5canvastutorials.com/
 * 
 * 
 */

package org.reactome.client;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * String & XML Parser Functions
 */
public class Parser {
	NamedNodeMap attributes;
	NodeList childNodes;
	
	public Parser() {
		
	}
	 
	public Parser(NamedNodeMap attributesToParse) {
		this.attributes = attributesToParse;
	}
	
	public Parser(NodeList nodestoParse) {
		this.childNodes = nodestoParse;
	}
	
	/**Splits string on " " character
	 * 
	 * @param string The String to be split
	 * @return An array of split words or phrases
	 */
	public String[] splitbySpace(String string) {
		String[] Values = string.split(" ");
		return Values;
	}

	/**Builds a CSS Color String out of the RGB value of the bgColor Attribute
	 * 
	 * @param colorAtt Value of the bgColor Attribute
	 * @return Color String
	 */
	public String makeColor(String[] colorAtt) {
		// TODO Auto-generated method stub
		int rndAlpha = 1;
		String bgColor = "rgba(" + colorAtt[0] + "," + colorAtt[1] + "," + colorAtt[2] + "," + rndAlpha + ")";
		return bgColor;
	}
	
	/**Obtain the value of the specified attribute
	 * 
	 * @param queryTag The attribute whose value needs to be obtained
	 * @return The Value of the attribute
	 */
	public String getStringAttributes(String queryTag){
		String value = "";
		try {
			Node referenceNode = attributes.getNamedItem(queryTag);
			// Have to check if referenceNode is null. Otherwise, a null exception
			// will be thrown in the compiled code, which blocks the execution of the
			// browser.
			if (referenceNode != null)
			    value = referenceNode.getNodeValue();
		} catch (NullPointerException nullpointer) {
			
		}
		return value;
	}
	
	/**Obtains the Display name of any particular Node
	 * 
	 * @return The Display name of the Node
	 */
	public String getDisplayNames() {
		String complexName = "";
		try{
			Element childElement = (Element) childNodes.item(0);
			Element displayNames = (Element) childElement.getFirstChild();
			complexName = displayNames.getFirstChild().getNodeValue();			
		} catch(NullPointerException nullpointer) {
			
		}
		return complexName;
	}
	
	/**Obtains the Node Attachments linked to any particular Node.
	 * 
	 * @return NodeList containing Node Attachments
	 */
	public NodeList getNodeAttachments() {
		NodeList nodeAttachments = null;
		try {
			for(int i = 0; i < 2; i++) {
				Element childElement = (Element) childNodes.item(i);
				try {
				  nodeAttachments = childElement.getElementsByTagName("org.gk.render.RenderableFeature");
				} catch (NullPointerException nullpointer) {
					
				}
			}
		} catch (NullPointerException nullpointer) {
			
		}
		return nodeAttachments;
	}
	
}
