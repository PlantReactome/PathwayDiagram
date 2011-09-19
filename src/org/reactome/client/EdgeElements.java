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
import com.google.gwt.xml.client.NodeList;

/**
 * Separates the edges into the reaction main branch, input branch, output branch and various other attachment branches
 * Builds a HashMap with all the information about the edges like the Points on the edge, the Position and Stoichiometry of the Reaction
 */

public class EdgeElements{

	private String nameTag = "org.gk.render.RenderableReaction";
	private int numberOfReactions;
	private NodeList reactions;
	private final HashMap<Integer, List<String>> reactionAttributeHashMap = new HashMap<Integer, List<String>>();
	private final HashMap<Integer, List<String>> inputAttributesHashMap = new HashMap<Integer, List<String>>();
	private final HashMap<Integer, List<String>> outputAttributesHashMap = new HashMap<Integer, List<String>>();
	private final HashMap<Integer, List<String>> catalystAttributesHashMap = new HashMap<Integer, List<String>>();
	private final HashMap<Integer, List<String>> inhibitorAttributesHashMap = new HashMap<Integer, List<String>>();
//	private final HashMap<Double, List<String>> activatorAttributesHashmap = new HashMap<Double, List<String>>();
	
	public EdgeElements(Element allEdges) {
		reactions = allEdges.getElementsByTagName(this.nameTag);
		numberOfReactions = reactions.getLength();
	}

	/**
	 * Separates the edges into the reaction main branch, input branch, output branch and various other attachment branches
	 * Builds a HashMap with all the information about the edges like the Points on the edge, the Position and Stoichiometry of the Reaction
	 */
	public void buildHashMap() {
		Integer inputKey = 0;
		Integer outputKey = 0;
		Integer catalystKey = 0;
		Integer inhibitorKey = 0;
//		Double activatorKey = (double) 0;
		for(int i = 0; i < numberOfReactions ; i++) {
		    Element renderableReaction = (Element) reactions.item(i);
			NodeList reactionComponents = renderableReaction.getChildNodes();
			String reactionId = renderableReaction.getAttribute("id");
			String reactionPoints = renderableReaction.getAttribute("points");
			String reactionPosition = renderableReaction.getAttribute("position");
			
			// ------------ Reactions
			List<String> reactionAttributesList = new ArrayList<String>();
			reactionAttributesList.add(reactionId);
			reactionAttributesList.add(reactionPoints);
			reactionAttributesList.add(reactionPosition);
			reactionAttributeHashMap.put(new Integer(reactionId), 
			                             reactionAttributesList);
			
			// ---------- Inputs
			for(int reactionComponent = 0; reactionComponent < reactionComponents.getLength(); reactionComponent++) {
				Element component = (Element) reactionComponents.item(reactionComponent);
				String componentName = component.getNodeName();
				if (componentName.equals("Inputs")) {
					NodeList input = component.getElementsByTagName("Input");
					for(int j = 0; j < input.getLength(); j++){
						Element renderableInput = (Element) input.item(j);
						String inputId = renderableInput.getAttribute("id");
						String inputPoints = renderableInput.getAttribute("points");
						String stoichiometry = renderableInput.getAttribute("stoichiometry");
						
						List<String> inputAttributesList = new ArrayList<String>();
						inputAttributesList.add(inputId);
						inputAttributesList.add(inputPoints);
						inputAttributesList.add(reactionId);
						inputAttributesList.add(reactionPoints);
						inputAttributesList.add(reactionPosition);
						inputAttributesList.add(stoichiometry);
						
						inputAttributesHashMap.put(inputKey, inputAttributesList);
						inputKey++;
					}
				} else if (componentName.equals("Outputs")) {
					NodeList output = component.getElementsByTagName("Output");
					for(int j = 0; j < output.getLength(); j++){
						Element renderableOutput = (Element) output.item(j);
						String outputId = renderableOutput.getAttribute("id");
						String outputPoints = renderableOutput.getAttribute("points");
						String stoichiometry = renderableOutput.getAttribute("stoichiometry");
						
						List<String> outputAttributesList = new ArrayList<String>();
						outputAttributesList.add(outputId);
						outputAttributesList.add(outputPoints);
						outputAttributesList.add(reactionId);
						outputAttributesList.add(reactionPoints);
						outputAttributesList.add(reactionPosition);
						outputAttributesList.add(stoichiometry);
						
						outputAttributesHashMap.put(outputKey, outputAttributesList);
						outputKey++;
					}
				} else if (componentName.equals("Catalysts")) {
					NodeList catalyst = component.getElementsByTagName("Catalyst");
					for(int j = 0; j < catalyst.getLength(); j++){
						Element renderableCatalyst = (Element) catalyst.item(j);
						String catalystId = renderableCatalyst.getAttribute("id");
						String catalystPoints = renderableCatalyst.getAttribute("points");
						// No stoichiometry
						
						List<String> catalystAttributesList = new ArrayList<String>();
						catalystAttributesList.add(catalystId);
						catalystAttributesList.add(catalystPoints);
						catalystAttributesList.add(reactionId);
						catalystAttributesList.add(reactionPoints);
						catalystAttributesList.add(reactionPosition);
						
						catalystAttributesHashMap.put(catalystKey, catalystAttributesList);
						catalystKey++;
					}
				} else if (componentName.equals("Inhibitors")) {
					NodeList inhibitor = component.getElementsByTagName("Inhibitor");
					int noofInhibitors = inhibitor.getLength();
					for(int j = 0; j < noofInhibitors; j++){
						Element renderableInhibitor = (Element) inhibitor.item(j);
						String inhibitoridno = renderableInhibitor.getAttribute("id");
						String inhibitorPoints = renderableInhibitor.getAttribute("points");
						// There should be no stoichiometry for inhibitors
						
						List<String> inhibitorAttributesList = new ArrayList<String>();
						inhibitorAttributesList.add(inhibitoridno);
						inhibitorAttributesList.add(inhibitorPoints);
						inhibitorAttributesList.add(reactionId);
						inhibitorAttributesList.add(reactionPoints);
						inhibitorAttributesList.add(reactionPosition);
						
						inhibitorAttributesHashMap.put(inhibitorKey, inhibitorAttributesList);
						inhibitorKey++;
					}
				} else if (componentName.equals("Activators")) {

				}
			}
		}
	}

	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateReactionGroup(Context2d context) {
		for (Integer key : reactionAttributeHashMap.keySet()) {
			List<String> attributes = reactionAttributeHashMap.get(key);
			ReactionGroup reactiongroup = new ReactionGroup(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(1));
			reactiongroup.plotReactionGroup(context);
		}
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateInputGroup(Context2d context) {
	   for (Integer key : inputAttributesHashMap.keySet()) {
			List<String> attributes = inputAttributesHashMap.get(key);
			String inputPoints = attributes.get(1);
			if (inputPoints != null && inputPoints.length() > 0) {
				InputGroup inputgroup = new InputGroup(attributes.get(0), attributes.get(3), attributes.get(4), attributes.get(1));
				inputgroup.plotInputGroup(context);
			}
		}
	}
	
	/**
	 * 
	 * @param context The Context2d object to plot the edge elements
	 */
	public void updateOutputGroup(Context2d context) {
		for (Integer key : outputAttributesHashMap.keySet()) {
			List<String> attributes = outputAttributesHashMap.get(key);
			if(attributes.get(1) != null && attributes.get(1).length() > 0){
				OutputGroup outputgroup = new OutputGroup(attributes.get(0), 
				                                          attributes.get(3),
				                                          attributes.get(4), 
				                                          attributes.get(1));
				outputgroup.plotOutputGroup(context);
			}
			else { // There is no points for output. Only arrow should be drawn
			    Integer reactionId = new Integer(attributes.get(2));
			    List<String> reactionAttributes = reactionAttributeHashMap.get(reactionId);
			    // Get the last point
			    String posText = reactionAttributes.get(2);
			    double id = Double.parseDouble(attributes.get(0));
			    String outputBounds = NodeGroup.BoundsHashmap.get(id);
			    // Get the center of the bounds
			    String[] tokens = outputBounds.split(" ");
			    int centerX = Integer.parseInt(tokens[0]) + Integer.parseInt(tokens[2]) / 2;
			    int centerY = Integer.parseInt(tokens[1]) + Integer.parseInt(tokens[3]) / 2;
			    HyperEdge edge = new HyperEdge(centerX + " " + centerY,
                                               posText);
			    edge.updateArrow(context, outputBounds);
			}
		}
	}
	
	/**
	 * 
	 * @param context The Context2d object to plot the edge elements
	 */
	public void updateCatalystGroup(Context2d context) {
		for (Integer key : catalystAttributesHashMap.keySet()) {
			List<String> attributes = catalystAttributesHashMap.get(key);
			if(attributes.get(1) != null && attributes.get(1).length() > 0){
				AttachmentGroup catalystgroup = new AttachmentGroup(attributes.get(0), attributes.get(3), attributes.get(4), attributes.get(1));
				catalystgroup.plotAttachmentGroup(context);
			}
		}
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateInhibitorGroup(Context2d context) {
		for (Integer key : inhibitorAttributesHashMap.keySet()) {
			List<String> attributes = inhibitorAttributesHashMap.get(key);
			if(attributes.get(1) != null && attributes.get(1).length() > 0){
				AttachmentGroup inhibitorgroup = new AttachmentGroup(attributes.get(0), attributes.get(3), attributes.get(4), attributes.get(1));
				inhibitorgroup.plotAttachmentGroup(context);
			}
		}
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateActivatorGroup(Context2d context) {
		
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateCatalystNode(Context2d context) {
		for (Integer key : catalystAttributesHashMap.keySet()) {
			List<String> attributes = catalystAttributesHashMap.get(key);
			AttachmentGroup catalystgroup = new AttachmentGroup(attributes.get(0), attributes.get(3), attributes.get(4), attributes.get(1));
			catalystgroup.plotCatalystNodeGroup(context);
		}
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateInhibitorNode(Context2d context) {
		
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateActivatorNode(Context2d context) {
		
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateReactionNode(Context2d context) {
		for (Integer key : reactionAttributeHashMap.keySet()) {
			List<String> attributes = reactionAttributeHashMap.get(key);
			ReactionGroup reactiongroup = new ReactionGroup(attributes.get(0), attributes.get(1), attributes.get(2), attributes.get(1));
			reactiongroup.plotReactionNodeGroup(context);
			//GwtEvent<MouseDownEvent> event = new GwtEvent<MouseDownEvent>();
			//reactiongroup.fireEvent(event);
		}
	}
	
	/**
	 * 
	 * @param context  The Context2d object to plot the edge elements
	 */
	public void updateStoichiometricNode(Context2d context) {
		int flag = 1;
		Iterator<Integer> inputIterator = inputAttributesHashMap.keySet().iterator();
		while(inputIterator.hasNext()) {
		    Integer key = inputIterator.next();
		    List<String> attributes = inputAttributesHashMap.get(key);
		    if (attributes.get(1) != null && attributes.get(1).length() > 0 &&
		        attributes.get(5) != null && attributes.get(5).length() > 0) {
		        HyperEdge stoichNode = new HyperEdge(attributes.get(3), attributes.get(1));
		        stoichNode.updateStoichNode(context, attributes.get(5),flag);
		    }
		}
		
		flag = 2;
		Iterator<Integer> outputIterator = outputAttributesHashMap.keySet().iterator();
		while(outputIterator.hasNext()) {
		    Integer key = outputIterator.next();
		    List<String> attributes = outputAttributesHashMap.get(key);
		    if (attributes.get(1) != null && attributes.get(1).length() > 0 &&
	            attributes.get(5) != null && attributes.get(5).length() > 0) {
		        HyperEdge stoichNode = new HyperEdge(attributes.get(3), attributes.get(1));
		        stoichNode.updateStoichNode(context, attributes.get(5),flag);
		    }
		}
	}
}
