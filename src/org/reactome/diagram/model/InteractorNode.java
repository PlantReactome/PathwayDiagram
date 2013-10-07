/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;

public class InteractorNode extends Node implements Comparable<InteractorNode> {
    // 	
    private int count; 
	private List<InteractorEdge> edges;
	private InteractorType refType;
	private String accession;	
	private double score;
	private String chemicalId;
	private Image image;
	private boolean isDragging;
	private String defaultColour;
	
	/**
	 * Default constructor.
	 */
	public InteractorNode() {
		count = 1;
		edges = new ArrayList<InteractorEdge>();
		setType(GraphObjectType.RenderableInteractor); 
		setFont("12px Lucida Console");
		defaultColour = getBgColor();
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
	
	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getChemicalId() {
		return chemicalId;
	}

	public void setChemicalId(String chemicalId) {
		this.chemicalId = chemicalId;
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
			if (chemicalId != null || getDisplayName().matches("^\\d+$")) {
				url = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/";
				
				if (chemicalId != null) {				
					url = url + chemicalId;
				} else {
					url = url + "CHEMBL" + getDisplayName();
				}
			} else {
				url = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + this.accession;
			}		
		} else if (this.refType == InteractorType.Protein) { 
			url = "http://www.uniprot.org/uniprot/" + this.accession; 
		} else {
			url = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/" + this.accession;
		}
		
		return url;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}

	@Override
	public int compareTo(InteractorNode interactor) {
		if (interactor.getScore() < this.getScore()) {
			return -1;
		} else if (interactor.getScore() > this.getScore()) {
			return 1;
		}
			
		return 0;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(String url) {
		this.image = new Image(url);
	}

	public String getDefaultColour() {
		return defaultColour;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof InteractorNode && displayNameTheSame((InteractorNode) obj))
			return true;
		
		return false;
	}
	
	private boolean displayNameTheSame(InteractorNode interactor) {
		return interactor.getDisplayName().equals(this.getDisplayName());
	}
	
}
