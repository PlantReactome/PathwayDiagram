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

import org.reactome.diagram.view.Parameters;

public class ComplexNode extends Node {
    private ArrayList<Component> components;
		
	/**
	 * Default constructor.
	 */
	public ComplexNode() {
		super();
		components = new ArrayList<Component>();
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
		Collections.sort(components);
		Collections.reverse(components);
		return components;
	}
	
	/**
	 * Adds a new component object using the reference id given.  It's reactome internal
	 * id is undefined
	 * 
	 * @param refId Reference id of the component 
	 * @return A component object with the reference id given, either newly created or
	 * returned from the existing components in the complex 
	 */
	
	
	//public Component addComponentByRefId(Long refId) {
		//Component component = new Component();
		//component.setRefEntityId(refId);
		////components.add(component);
		
		//if (getComponentsByRefId(refId).isEmpty()) {
		//	components.add(component);
		//} else {
			//if (getComponentsByRefId(refId).contains(component))	
			//	refIdToComponentsMap.get(refId).add(component);
			//else {
			//	Integer componentIndex = refIdToComponentsMap.get(refId).indexOf(component);
			//	component = refIdToComponentsMap.get(refId).get(componentIndex);
			//}
		//}
		//
		//return component;
	//}
	

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
	//public void removeComponent(Long refId) {
	//	Component component = refIdToComponentsMap.remove(refId);
	//	components.remove(component);
	//}

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
		//refIdToComponentsMap.clear();
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
						return 0;
					}
				}
				return 0;
			} else if (expressionLevel == null)
				return -1;
			else if (c.getExpressionLevel() == null)
				return 1;
			else if (expressionLevel > c.getExpressionLevel())
				return 1;
			else if (expressionLevel < c.getExpressionLevel())
				return -1;
						
			return 0;
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

