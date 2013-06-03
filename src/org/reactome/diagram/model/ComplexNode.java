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

public class ComplexNode extends Node {
    private Map<Long, Component> components; 
		
	/**
	 * Default constructor.
	 */
	public ComplexNode() {		
		super();
		components = new HashMap<Long, Component>();
	}

	public Component getComponent(Long refId) {
		return components.get(refId);
	}
	
	public List<Component> getComponents() {
		List<Component> componentList = new ArrayList<Component>(components.values());
		Collections.sort(componentList);
		return componentList;		
	}

	public void addComponent(Long refId, String exprId, Double exprLevel, String exprColor) {
		components.put(refId, new Component(exprId, exprLevel, exprColor));
	}

	public void removeComponent(Long refId) {
		components.remove(refId);
	}
	
	public void removeComponents() {
		components.clear();
	}
	
	public List<String> getComponentColors() {
		List<String> colors = new ArrayList<String>();
	
		for (Component component : getComponents())
			colors.add(component.getExpressionColor());
		
		return colors;
	}
	

	public class Component extends ReactomeObject implements Comparable<Component> {
		private String expressionId;
		private Double expressionLevel;
		private String expressionColor;
			
		public Component(String exprId, Double exprLevel, String exprColor) {			
			setExpressionId(exprId);
			setExpressionLevel(exprLevel);
			setExpressionColor(exprColor);
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
		
	}
	
}

