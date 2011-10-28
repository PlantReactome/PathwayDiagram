/*
 * Created on Oct 27, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This customized Canvas is used as an overview.
 * @author gwu
 *
 */
public class OverviewCanvas extends PathwayCanvas {

    public OverviewCanvas() {
    }

    @Override
    public void setPathway(CanvasPathway pathway) {
        super.setPathway(pathway);
        // Need to set scale automatically so that the whole pathway can be
        // drawn in this canvas.
        Bounds size = pathway.getPreferredSize();
        int width = getCoordinateSpaceWidth();
        double scale = (double) width / size.getWidth();
        // resize the height
        double height = (double) size.getHeight() / size.getWidth() * width;
        setCoordinateSpaceHeight((int)height);
        scale(scale);
        updatePosition();
    }
    
    public void updatePosition() {
        // Need to make sure it is placed at the correct position
        //TODO: This is hard-coded and should be changed soon
        AbsolutePanel container = (AbsolutePanel) getParent();
        int height = getCoordinateSpaceHeight();
        container.setWidgetPosition(this, 
                                    4, 
                                    container.getOffsetHeight() - height - 7);

    }
    
    
}
