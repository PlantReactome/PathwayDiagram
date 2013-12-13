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
    private Integer ENTITY_SET_INSET = 2;
	
    public EntitySetRenderer() {
    }
    
    /**
     * This is a template that should be implemented by a sub-class.
     * @param bounds
     * @param context
     * @param node
     */
    @Override
    protected void drawNode(Context2d context,
                            Node node) {
    	
    	
    	Bounds bounds = node.getBounds();
    	Bounds innerBounds = new Bounds(bounds.getX() + ENTITY_SET_INSET,
    									bounds.getY() + ENTITY_SET_INSET,
    									bounds.getWidth() - (2 * ENTITY_SET_INSET),
    									bounds.getHeight() - (2 * ENTITY_SET_INSET)
    			
    	);
    	
    	drawRectangle(bounds, context, true, true);
    	drawRectangle(innerBounds, context, true, true);
    	drawName(innerBounds, context, node);
    	drawNodeAttachments(context, node);
    }
}
