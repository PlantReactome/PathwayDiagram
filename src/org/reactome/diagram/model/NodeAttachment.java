/**
 * 
 * Created on October 19, 2011.
 * 
 */

package org.reactome.diagram.model;


/**
 * A simple data structure to catch NodeAttachment information from XML.
 * @author wgm
 */
public class NodeAttachment {

	private double relativeX;
	private double relativeY;
	private String label;
	private String description;
	
	public NodeAttachment() {
	}

    public double getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(double relativeX) {
        this.relativeX = relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(double relativeY) {
        this.relativeY = relativeY;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
	
}
