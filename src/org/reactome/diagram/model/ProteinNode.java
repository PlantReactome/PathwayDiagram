/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.List;

public class ProteinNode extends Node {
    // If this node has interactors
    private List<InteractorNode> interactors; 
    // A flag to indicate if interactors are being displayed 
    private boolean displayingInteractors;
		
	/**
	 * Default constructor.
	 */
	public ProteinNode() {
	}

	public List<InteractorNode> getInteractors() {
		return interactors;
	}

	public void setInteractors(List<InteractorNode> interactors) {
		this.interactors = interactors;
	}
	
	public void setInteractors(String xml) {
		
	}

	public boolean isDisplayingInteractors() {
		return displayingInteractors;
	}

	public void setDisplayingInteractors(boolean displayingInteractors) {
		this.displayingInteractors = displayingInteractors;
	}	
}
