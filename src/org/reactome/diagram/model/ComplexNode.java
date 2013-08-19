/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.view.Parameters;

public class ComplexNode extends Node {
    private ArrayList<Component> components;
	private Map<Long, Component> refIdToComponentsMap; 
		
	/**
	 * Default constructor.
	 */
	public ComplexNode() {		
		super();
		components = new ArrayList<Component>();
		refIdToComponentsMap = new HashMap<Long, Component>();		
	}
	
	/**
	 * Get a component in the complex by the component's reference entity id
	 * 
	 * @param refId Component's reference entity id
	 * @return Component with the given reference entity id or null if no component matches the id
	 */
	public Component getComponent(Long refId) {
		return refIdToComponentsMap.get(refId);		
	}
	
	/**
	 * Get a component in the complex by the component's reactome internal id
	 * 
	 * @param dbId Component's reactome internal id
	 * @return Component with the given reactome internal id or null if no component matches the id
	 */	
	public Component getComponentByDBId(Long dbId) {
		for (Component component : components) {
			if (dbId.equals(component.getReactomeId()))
				return component;
		}		
		
		return null;
	}
	
	/**
	 * Get all components for the complex	
	 * 
	 * @return List of all complex components sorted ascendingly by gene expression levels, if present
	 */
	public List<Component> getComponents() {
		//List<Component> componentList = new ArrayList<Component>(components);
		Collections.sort(components);
		return components;		
	}
	
	/**
	 * Adds a new component object with the reference id given, 
	 * if no component object with the reference id already exists
	 * 
	 * @param refId Reference id of the component 
	 * @return A component object with the reference id given, either newly created or
	 * returned from the existing components in the complex 
	 */
	public Component addComponent(Long refId) {
		if (!refIdToComponentsMap.containsKey(refId)) {
			Component component = new Component();
			component.setRefEntityId(refId);
			components.add(component);
			refIdToComponentsMap.put(refId, component);
		}
		
		return refIdToComponentsMap.get(refId);
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
	 * Remove component from the complex node by its reference entity id
	 * 
	 * @param refId Reference entity id of the component
	 */
	public void removeComponent(Long refId) {
		Component component = refIdToComponentsMap.remove(refId);
		components.remove(component);
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
		refIdToComponentsMap.clear();
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
			if (component.getExpressionColor() != null)
				colors.add(component.getExpressionColor());
		}
		
		if (colors.isEmpty())  
			colors.add(Parameters.defaultExpressionColor.value());
		
		return colors;
	}

	public class Component extends ReactomeObject implements Comparable<Component> {
		private Long referenceEntityId;
		private String expressionId;
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

		public String getExpressionId() {
			return expressionId;
		}

		public void setExpressionId(String expressionId) {
			this.expressionId = expressionId;
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
			if (expressionLevel == c.getExpressionLevel())
				return 0;
			else if (expressionLevel == null)
				return -1;
			else if (c.getExpressionLevel() == null)
				return 1;
			else if (expressionLevel > c.getExpressionLevel())
				return 1;
			else if (expressionLevel < c.getExpressionLevel())
				return -1;
						
			return 0;
		}		
		
		public String toString() {
			return "Name - " + getDisplayName() + "\n DB ID - " + getReactomeId() + 
					"\n Expression Level - " + getExpressionLevel() + "\n Expression Color - " +
					getExpressionColor();
		}
	}
	
}

