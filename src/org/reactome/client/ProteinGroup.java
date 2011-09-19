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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Separates the Protein Nodes from the remaining nodes, stores the attribute information in a hashmap, and renders them.
 */

public class ProteinGroup extends NodeGroup{

	HashMap<Double, String> proteinHashmap;
	static HashMap<Double, List<String>> proteinValuesHashmap;
	HashMap<Double, List<Double>> proteinNodeAttachmentIdHashmap;
	HashMap<Double, List<String>> proteinNodeAttachmentHashmap;
	static String nameTag = "org.gk.render.RenderableProtein";
	
	/**Separates the Protein Nodes from the remaining nodes
	 * 
	 * @param allNodes XML ELement containing the Nodes of the Network
	 */
	public ProteinGroup(Element allNodes) {
		super(allNodes,nameTag);
		proteinHashmap = new HashMap<Double, String>();
		proteinValuesHashmap = new HashMap<Double, List<String>>();
		proteinNodeAttachmentHashmap = new HashMap<Double, List<String>>();
		proteinNodeAttachmentIdHashmap = new HashMap<Double, List<Double>>();
	}
	
	/**
	 * Builds a HashMap containing the information of the ID, Bounds, Position of the Protein Nodes
	 * Builds a HashMap containing the information of DisplayName and Node Id
	 * Builds a HashMap containing the information about Bounds of Node Attachments if any
	 */
	
	public void buildHashMap() {
		double attachmentID = 0;
		for (int i = 0; i < noofnodes ; i++){
			Node RenderableNode = nodes.item(i);
			List<String> attributes = new ArrayList<String>();
			NamedNodeMap RenderableAttributes = RenderableNode.getAttributes();
			Parser Attributeparser = new Parser(RenderableAttributes);
			
			String id = Attributeparser.getStringAttributes("id");
			double idNo = Double.parseDouble(id);
			String reactomeId = Attributeparser.getStringAttributes("reactomeId");
			double reactomeIdNo = Double.parseDouble(reactomeId);
			
			attributes.add(id);
			attributes.add(reactomeId);
			attributes.add(Attributeparser.getStringAttributes("bounds"));
			attributes.add(Attributeparser.getStringAttributes("position"));
			attributes.add(Attributeparser.getStringAttributes("bgColor"));
			
			String displayName = "";
			if(RenderableNode.hasChildNodes()){
				NodeList childNodes = RenderableNode.getChildNodes();
				for(int j = 0; j < childNodes.getLength(); j++) {
					Element details = (Element) childNodes.item(j);
					String detailsType = details.getNodeName();
					if(detailsType.equals("Properties")){
						Element displayNames = (Element) details.getFirstChild();
						displayName = displayNames.getFirstChild().getNodeValue();
						proteinHashmap.put(reactomeIdNo, displayName);
					} else if(detailsType.equals("NodeAttachments")) {
						if (proteinHashmap.containsKey(reactomeIdNo)) {
							displayName = proteinHashmap.get(reactomeIdNo);
						}
						NodeList NodeAttachments = details.getElementsByTagName("org.gk.render.RenderableFeature");
						int noofAttachments = NodeAttachments.getLength();
						List<Double> attachmentIdList = new ArrayList<Double>();
						for(int k = 0; k < noofAttachments ; k++) {
							Node NodeAttachment = NodeAttachments.item(k);
							NamedNodeMap AttachmentAttributes = NodeAttachment.getAttributes();
							Parser AttachmentParser = new Parser(AttachmentAttributes);
							String relativeX = AttachmentParser.getStringAttributes("relativeX");
							String relativeY = AttachmentParser.getStringAttributes("relativeY");
							String label = "";
							label = AttachmentParser.getStringAttributes("label");
							List<String> nodeAttachmentAttributes = new ArrayList<String>();
							nodeAttachmentAttributes.add(relativeX);
							nodeAttachmentAttributes.add(relativeY);
							nodeAttachmentAttributes.add(label);
							proteinNodeAttachmentHashmap.put(attachmentID, nodeAttachmentAttributes);
							attachmentIdList.add(attachmentID);
							attachmentID++;
						}
						proteinNodeAttachmentIdHashmap.put(idNo, attachmentIdList);
					}
				}
			} else {
				if (proteinHashmap.containsKey(reactomeIdNo)) {
					displayName = proteinHashmap.get(reactomeIdNo);
				} else {
					System.out.println("No name for" + reactomeIdNo);
				}
			}
			
			attributes.add(displayName);
			proteinValuesHashmap.put(idNo, attributes);
			BoundsHashmap.put(idNo, Attributeparser.getStringAttributes("bounds"));
		}
	}
	
	/**Renders the Protein Nodes & their Node Attachments on the Context
	 * 
	 * @param context The Context2d Object on which the Nodes are rendered
	 */
	
	public void update(Context2d context) {
		Iterator<Double> iterator = proteinValuesHashmap.keySet().iterator();
		while(iterator.hasNext()) {
			Double key = (Double) iterator.next();
			List<String> attributes = proteinValuesHashmap.get(key);
			ProteinNode protein = new ProteinNode(attributes.get(0), attributes.get(2), attributes.get(3), attributes.get(4));
			protein.draw(context);
			
			int fontSize = 13;
			Annotations TagName = new Annotations(attributes.get(0), attributes.get(2), attributes.get(3), fontSize, attributes.get(5));
			TagName.writeText(context);
			if(proteinNodeAttachmentIdHashmap.containsKey(key)) {
				List<Double> attachmentIdList = proteinNodeAttachmentIdHashmap.get(key);
				for(int i = 0; i < attachmentIdList.size(); i++) {
					if(proteinNodeAttachmentHashmap.containsKey(attachmentIdList.get(i))) {
						List<String> attachmentAttributes = proteinNodeAttachmentHashmap.get(attachmentIdList.get(i));
						NodeAttachment Attachment = new NodeAttachment(attributes.get(0), attributes.get(2), attributes.get(3), attachmentAttributes.get(0), attachmentAttributes.get(1));
						Attachment.updateNodeAttachment(context,attachmentAttributes.get(2));
					}
				}
				
			}	
		}
	}
	
}
