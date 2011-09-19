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
 * Separates the Chemical Nodes from the remaining nodes, stores the attribute information in a hashmap, and renders them.
 */
public class ChemicalGroup extends NodeGroup{

	HashMap<Double, String> chemicalHashmap;
	static HashMap<Double, List<String>> chemicalValuesHashmap;
	static String nameTag = "org.gk.render.RenderableChemical";
	
	/**Separates the Chemical Nodes from the remaining nodes
	 * 
	 * @param allNodes XML ELement containing the Nodes of the Network
	 */
	public ChemicalGroup(Element allNodes) {
		super(allNodes,nameTag);
		chemicalHashmap = new HashMap<Double, String>();
		chemicalValuesHashmap = new HashMap<Double, List<String>>();
	}

	/**
	 * Builds a HashMap containing the information of the ID, Bounds, Position of the Chemical Nodes
	 * Builds a HashMap containing the information of DisplayName and Node Id
	 */
	public void buildHashMap() {
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
			
			String DisplayName = "";
			if(RenderableNode.hasChildNodes()){
				NodeList ChildNodes = RenderableNode.getChildNodes();
				Parser NodeParser = new Parser(ChildNodes);
				DisplayName = NodeParser.getDisplayNames();
				chemicalHashmap.put(reactomeIdNo, DisplayName);
			} else {
				if (chemicalHashmap.containsKey(reactomeIdNo)) {
					DisplayName = chemicalHashmap.get(reactomeIdNo);
				} else {
					System.out.println("No name for" + reactomeIdNo);
				}
			}
			
			attributes.add(DisplayName);
			chemicalValuesHashmap.put(idNo, attributes);
			BoundsHashmap.put(idNo, Attributeparser.getStringAttributes("bounds"));
		}
	}
	
	/**Renders the Chemical Nodes on the Context
	 * 
	 * @param context The Context2d Object on which the Nodes are rendered
	 */
	public void update(Context2d context) {
		Iterator<Double> iterator = chemicalValuesHashmap.keySet().iterator();
		while(iterator.hasNext()) {
			Double key = (Double) iterator.next();
			List<String> attributes = chemicalValuesHashmap.get(key);
			ChemicalNode chemical = new ChemicalNode(attributes.get(0), attributes.get(2), attributes.get(3), attributes.get(4));
			chemical.draw(context);
			int fontSize = 13;
			Annotations TagName = new Annotations(attributes.get(0), attributes.get(2), attributes.get(3), fontSize, attributes.get(5));
			TagName.writeText(context);
		}
		
	}

}
