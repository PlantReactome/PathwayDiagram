/**
 * 
 * @author weiserj
 * 
 */

package org.reactome.diagram.model;

import com.google.gwt.touch.client.Point;

/**
 * Sets and initializes all the Edge Elements for Render on the Canvas
 */
public class InteractorEdge extends HyperEdge {
    // 
    private ProteinNode protein;
    // 
    private InteractorNode interactor;
   
    /**
     * Default constructor.
     */
    public InteractorEdge() {
        this.setLineColor("rgba(0, 0, 255, 1)");
        this.setLineWidth(3);
    }    
    
    public ProteinNode getProtein() {
    	return this.protein;
    }
    
    public void setProtein(ProteinNode prot) {
    	this.protein = prot;
    }

	public InteractorNode getInteractor() {
		return interactor;
	}

	public void setInteractor(InteractorNode interactor) {
		this.interactor = interactor;
	}    
	
	public String getUrl() {
		return "http://www.ebi.ac.uk/intact/pages/interactions/interactions.xhtml?query=" +
		protein + " AND " + interactor.getRefId(); 
				
	}
	
	public boolean isPicked(Point point) {
		Point start = this.getBackbone().get(0);
		Point end = this.getBackbone().get(1);
		
		double rise = end.getY() - start.getY();
		double run = end.getX() - start.getX();
	
		double slope = -rise/run;
		double intercept = start.getY() - (slope * start.getX());
		
		if (point.getY() == (slope * point.getX()) - intercept) {
			return true;
		}
		
		return false;
	}
}
