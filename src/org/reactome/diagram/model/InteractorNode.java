/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.List;

public class InteractorNode extends Node {
    // 
    private int count; 
	private List<InteractorEdge> edges;
	private String refId;
	private boolean isDragging;
	
	/**
	 * Default constructor.
	 */
	public InteractorNode() {
		count = 1;
		edges = new ArrayList<InteractorEdge>();
		setType(GraphObjectType.RenderableInteractor); 
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

	public List<InteractorEdge> getEdges() {
		return this.edges;
	}

	public void addEdge(InteractorEdge edge) {
		this.edges.add(edge);
	}
	
	public void removeEdge(InteractorEdge edge) {
		this.edges.remove(edge);
	}
	
	public String getUrl() {
		return "http://www.uniprot.org/uniprot/" + this.refId;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}
}
