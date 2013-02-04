/**
 * 
 * @author weiserj
 * 
 */

package org.reactome.diagram.model;

import java.util.Map;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;

/**
 * Sets and initializes all the Edge Elements for Render on the Canvas
 */
public class InteractorEdge extends HyperEdge {
    private ProteinNode protein;
    private InteractorNode interactor;
    private static String url;
    
    /**
     * Default constructor.
     */
    public InteractorEdge() {
        this.setLineColor("rgba(0, 0, 255, 1)");
        this.setLineWidth(3);
        setType(GraphObjectType.RenderableInteraction);
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
		String url = InteractorEdge.url;
		
		url = url.replace("##ACC##", protein.getRefId());
		url = url.replace("##INT##", interactor.getAccession());
		return url;
	}

	public static void setUrl(Map<String, String> interactionDBMap, String interactionDatabase) {
		String url = interactionDBMap.get(interactionDatabase);
		InteractorEdge.url = url;			
	}

	public boolean isPicked(Point point) {
		Point start = this.getBackbone().get(0);
		Point end = this.getBackbone().get(1);
		
		double rise = -end.getY() - -start.getY();
		double run = end.getX() - start.getX();
	
		double slope = rise/run;
		double intercept = -start.getY() - (slope * start.getX());
				
		// If mouse y-coordinate within 5 pixels of line's corresponding y-coordinate at a certain
		// x-coordinate and mouse x-coordinate is within line segment domain   
		if (Math.abs(-point.getY() - intercept - (slope * point.getX())) < 5 && 
			(point.getX() >= start.getX() && point.getX() <= end.getX() ||
			point.getX() <= start.getX() && point.getX() >= end.getX() )) {
			
			return true;
		}
		
		return false;
	}
}
