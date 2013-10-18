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
	private String geneName;
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

	@Override
	public String getDisplayName() {
		return getRefType() == InteractorType.Protein ? getGeneName() + " (" + getAccession() + ")" : getGeneName(); 
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
	
	public String getGeneName() {
		return geneName;
	}
	
	public void setGeneName(String geneName) {
		this.geneName = geneName;
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
		
		if (getRefType() == InteractorType.Chemical) {
			final String defaultUrl = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/";
			if (getChemicalId() != null) {
				url = defaultUrl + getChemicalId();
			} else if (getChemblId() != null) {
				url = defaultUrl + "CHEMBL" + getChemblId();
			} else {
				url = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=" + getAccession();
			}		
		} else if (getRefType() == InteractorType.Protein) {
			url = "http://www.uniprot.org/uniprot/" + getAccession();
		} else {
			url = "http://www.ebi.ac.uk/chembldb/index.php/compound/inspect/" + getAccession();
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
		if (obj instanceof InteractorNode && sameInteractorRefType((InteractorNode) obj)) { 
			if (proteinWithSameNameAndId((InteractorNode) obj) || chemicalWithSameNameAndId((InteractorNode) obj))
				return true;
		}
		
		return false;
	}
	private boolean sameInteractorRefType(InteractorNode interactor) {
		return interactor.getRefType() == this.getRefType();
	}
	
	private boolean proteinWithSameNameAndId(InteractorNode interactor) {
		if (getRefType() == InteractorType.Protein) {
			if (geneNameTheSame(interactor) && interactor.getAccession().equals(this.getAccession()))
				return true;
		}
		
		return false;
	}
	
	private boolean chemicalWithSameNameAndId(InteractorNode interactor) {
		if (getRefType() == InteractorType.Chemical) {
			if (geneNameTheSame(interactor) && (chemicalIdentifierTheSame(interactor) || chemicalAccessionTheSame(interactor)))
				return true;
		}
		
		return false;
	}
	
	private boolean chemicalIdentifierTheSame(InteractorNode interactor) {
		if (interactor.getChemicalId() != null && interactor.getChemicalId().equals(getChemicalId()))
			return true;
		
		return false;
	}
	
	private boolean chemicalAccessionTheSame(InteractorNode interactor) {
		if (interactor.getAccession() != null && interactor.getAccession().equals(getAccession()))
			return true;
		
		return false;
	}	
	
	private String getChemblId() {
		return getGeneName().matches("^\\d+$") ? getGeneName() : null;
	}
	
	private boolean geneNameTheSame(InteractorNode interactor) {
		return interactor.getGeneName().equals(this.getGeneName());
	}
	
}
