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
	private InteractorType refType;
	private String refId;	
	private boolean isDragging;
	
	/**
	 * Default constructor.
	 */
	public InteractorNode() {
		count = 1;
		edges = new ArrayList<InteractorEdge>();
		setType(GraphObjectType.RenderableInteractor); 
		setFont("12px Lucida Console");
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public InteractorType getRefType() {
		return refType;
	}
	
	public void setRefType(InteractorType refType) {
		this.refType = refType;
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

	public void setEdges(List<InteractorEdge> edges) {
		this.edges.clear();
		this.edges.addAll(edges);
	}

	public void addEdge(InteractorEdge edge) { 
		this.edges.add(edge);
	}
	
	public void removeEdge(InteractorEdge edge) {
		this.edges.remove(edge);
	}
	
	public String getUrl() {
		String url;
		
		if (this.refType == InteractorType.Chemical) {
			url = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/CHEMBL";
		} else if (this.refType == InteractorType.Protein) { 
			url = "http://www.uniprot.org/uniprot/"; 
		} else {
			url = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/";
		}
		
		return url + this.refId;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}
}
