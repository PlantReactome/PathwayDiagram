/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

public class InteractorNode extends Node {
    // 
    private int count; 
	private boolean showing;
	private String refId;
    
	/**
	 * Default constructor.
	 */
	public InteractorNode() {
		count = 1;
		showing = false;
		setType(GraphObjectType.RenderableProtein); 
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}
		
}
