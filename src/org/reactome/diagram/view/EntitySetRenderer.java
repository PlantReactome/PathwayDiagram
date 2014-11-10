/*
 * Created on Dec 13, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Renderer that is used to render entity sets.
 * @author weiserj
 *
 */
public class EntitySetRenderer extends ProteinRenderer {
    private final Integer ENTITY_SET_INSET = 3;
    
    public EntitySetRenderer() {
    }
    
    @Override
    protected void drawNode(Context2d context, Node node) {
    	
    	drawRectangle(context, node);
    	drawName(getInnerBounds(node), context, node);
    	drawNodeAttachments(getOuterBounds(node), context, node);
    }
    
    protected void drawRectangle(Context2d context, Node node) {
    	if (node.isNeedDashedBorder()) {
    		drawDashedRectangle(getOuterBounds(node), context, true);
    		drawDashedRectangle(getInnerBounds(node), context, true);
    	} else {
    		drawRectangle(getOuterBounds(node), context, true, true);
    		drawRectangle(getInnerBounds(node), context, true, true);
    	}
    }
    
    protected Bounds getInnerBounds(Node node) {
    	return node.getBounds();
    }
    	
    protected Bounds getOuterBounds(Node node) {
    	Bounds innerBounds = getInnerBounds(node);
    	
    	return new Bounds(innerBounds.getX() - ENTITY_SET_INSET,
    									innerBounds.getY() - ENTITY_SET_INSET,
    									innerBounds.getWidth() + (2 * ENTITY_SET_INSET),
    									innerBounds.getHeight() + (2 * ENTITY_SET_INSET)
    			
    	);
    }
}
