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
		
	/**
	 * Default constructor.
	 */
	public InteractorNode() {
		count = 1;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
		
}
