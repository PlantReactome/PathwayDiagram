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

import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

public class ComplexNode extends Node {
    private ArrayList<Component> components;
    private boolean participatingMoleculesObtained;
		
	/**
	 * Default constructor.
	 */
	public ComplexNode() {
		super();
		components = new ArrayList<Component>();
		participatingMoleculesObtained = false;
	}
	
	/**
	 * Get all components in the complex associated with reference entity id
	 * 
	 * @param refId Component's reference entity id
	 * @return List of components with the given reference entity id or null if no component matches the id
	 */
	public List<Component> getComponentsByRefId(Long refId) {
		List<Component> componentsWithRefId = new ArrayList<Component>();
		
		if (refId == null) 
			return componentsWithRefId;
		
		for (Component component : getComponents()) {
			if (refId.equals(component.getRefEntityId()))
				componentsWithRefId.add(component);
		}
		
		
		return componentsWithRefId;		
	}
	
	/**
	 * Get a component in the complex by the component's reactome internal id
	 * 
	 * @param dbId Component's reactome internal id
	 * @return Component with the given reactome internal id or null if no component matches the id
	 */	
	public Component getComponentByDBId(Long dbId) {
		if (dbId != null) {
			for (Component component : components) {
				if (dbId.equals(component.getReactomeId()))
					return component;
			}		
		}
		
		return null;
	}
	
	/**
	 * Get all components for the complex	
	 * 
	 * @return List of all complex components sorted ascendingly by gene expression levels, if present
	 */
	public List<Component> getComponents() {
		Collections.sort(components);
		Collections.reverse(components);
		return components;
	}
	
	/**
	 * Adds a new component object with the reactome internal id given,
	 * if no component object with the reactome internal id already exists
	 * 
	 * @param dbId Reactome internal id of the component
	 * @return A component object with the reference id given, either newly created or
	 * returned from the existing components in the complex
	 */
	public Component addComponentByDBId(Long dbId) {
		if (getComponentByDBId(dbId) != null)
			return getComponentByDBId(dbId);
		
		Component component = new Component();
		component.setReactomeId(dbId);
		components.add(component);
		
		return component;				
	}
	
	/**
	 * Adds a new component with the reference entity id given (the new component's reactome
	 * internal id will be null).  
	 * 
	 * The component will NOT be added or returned if one or more components with the
	 * same reference id already exists within the complex.
	 * 
	 * 
	 * @param refId Component's reference entity id
	 * @return The newly created component or null if one or more components with the provided 
	 * reference id already exists
	 */
	public Component addComponentByRefId(Long refId) {
		if (!getComponentsByRefId(refId).isEmpty())
			return null;
		
		Component component = addComponentByDBId(null);
		component.setRefEntityId(refId);
		
		return component;
	}

	/**
	 * Remove component from the complex node by its reactome internal id 
	 * 	
	 * @param dbId Reactome internal id of the component
	 */
	public void removeComponentByDBId(Long dbId) {
		if (getComponentByDBId(dbId) != null)
			components.remove(getComponentByDBId(dbId));
	}
	
	/**
	 * Remove all components from the complex
	 */
	public void removeComponents() {
		components.clear();
	}

	/**
	 * Get list of expression colors for all complex components ordered by expression level
	 * from lowest to highest
	 * 
	 * @return List of expression colors as RGB values
	 */
	public List<String> getComponentColors() {
		List<String> colors = new ArrayList<String>();
		
		for (Component component : getComponents()) {
//			System.out.println(component);
			if (component.getExpressionColor() != null)
				colors.add(component.getExpressionColor());
		}
		
		if (colors.isEmpty())  
			colors.add(Parameters.defaultExpressionColor.value());
		
		return colors;
	}

    public RequestCallback setParticipatingMolecules() {
    	RequestCallback setParticipatingMolecules = new RequestCallback() {
    		private final String ERROR_MSG = "Unable to get participating molecules for " + getDisplayName(); 
    		
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					AlertPopup.alert(ERROR_MSG);
					return;
				}
										
				ReactomeXMLParser pmXMLParser = new ReactomeXMLParser(response.getText());
				Element pmElement = pmXMLParser.getDocumentElement();
				
				if (pmElement == null) {
					AlertPopup.alert(ERROR_MSG);
					return;
				}
					
				NodeList nodeList = pmElement.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					parseParticipatingMoleculeNode((Element) nodeList.item(i));
				}
				
				participatingMoleculesObtained = true;
			}
    	    
			private void parseParticipatingMoleculeNode(Element peElement) {
				try {
					Component component = addComponentByDBId(getPMDbId(peElement));
					component.setDisplayName(getPMDisplayName(peElement));
					component.setSchemaClass(getPMSchemaClass(peElement));
					component.setRefEntityId(getPMRefEntityId(peElement));
				} catch(Exception e) {
					e.printStackTrace();
				}
    		}
	
			private Long getPMDbId(Element moleculeElement) {
				return Long.parseLong(getPMAttributeValue("dbId", moleculeElement));
			}
			
			private String getPMDisplayName(Element moleculeElement) {
				return getPMAttributeValue("displayName", moleculeElement);
			}
			
			private String getPMSchemaClass(Element moleculeElement) {
				return getPMAttributeValue("schemaClass", moleculeElement);
			}
			
			private Long getPMRefEntityId(Element moleculeElement) {
				com.google.gwt.xml.client.Node refEntityNode = moleculeElement.getElementsByTagName("referenceEntity").item(0);
				
				if (refEntityNode != null)
					return getPMDbId((Element) refEntityNode);
				
				return null;
			}
			
			private String getPMAttributeValue(String attribute, Element moleculeElement) {
				com.google.gwt.xml.client.Node attributeNode = moleculeElement.getElementsByTagName(attribute).item(0);
						
				return attributeNode.getChildNodes().item(0).getNodeValue();
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				AlertPopup.alert(ERROR_MSG + exception);
			}
    	};
    	
    	return setParticipatingMolecules;
    }
	
	public boolean participatingMoleculesObtained() {
		return participatingMoleculesObtained;
	}

	public class Component extends ReactomeObject implements Comparable<Component> {
		private Long referenceEntityId;
		private List<String> expressionIds;
		private Double expressionLevel;
		private String expressionColor;
			
		public Component() {			
			
		}

		public Long getRefEntityId() {
			return referenceEntityId;
		}

		public void setRefEntityId(Long referenceEntityId) {
			this.referenceEntityId = referenceEntityId;
		}

		public List<String> getExpressionIdentifiers() {
			return expressionIds;
		}

		public void setExpressionId(List<String> componentExpressionIds) {
			this.expressionIds = componentExpressionIds;
		}

		public Double getExpressionLevel() {
			return expressionLevel;
		}

		public void setExpressionLevel(Double expressionLevel) {
			this.expressionLevel = expressionLevel;
		}

		public String getExpressionColor() {
			return expressionColor;
		}

		public void setExpressionColor(String color) {
			this.expressionColor = color;
		}

		@Override
		public int compareTo(Component c) {
			if (expressionLevel == c.getExpressionLevel()) {
				if (expressionLevel == null && c.getExpressionLevel() == null) {					
					// Default expression color is always first 
					if (expressionColor == null || expressionColor.equals(Parameters.defaultExpressionColor.value())) {
						return -1;
					} else if (c.getExpressionColor() == null || c.getExpressionColor().equals(Parameters.defaultExpressionColor.value())) {
						return 1;
					} else {
						return compareToAlphabetically(c);
					}
				}
				return compareToAlphabetically(c);
			} else if (expressionLevel == null)
				return -1;
			else if (c.getExpressionLevel() == null)
				return 1;
			else if (expressionLevel > c.getExpressionLevel())
				return 1;
			else if (expressionLevel < c.getExpressionLevel())
				return -1;
						
			return compareToAlphabetically(c);
		}		
		
		private int compareToAlphabetically(Component c) {
			if (getDisplayName() == null)
				return -1;
			
			return getDisplayName().compareToIgnoreCase(c.getDisplayName());
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof Component && reactomeIdsEqual(((Component) obj).getReactomeId()))
				return true;
			
			return false;
		}
		
		private boolean reactomeIdsEqual(Long reactomeId) {
			if (reactomeId == null || this.getReactomeId() == null)
				return false;
			
			return this.getReactomeId().equals(reactomeId);
		}
		
		public String toString() {
			return "Name - " + getDisplayName() + "\n DB ID - " + getReactomeId() + 
					"\n Expression Level - " + getExpressionLevel() + "\n Expression Color - " +
					getExpressionColor();
		}
	}
	
}

