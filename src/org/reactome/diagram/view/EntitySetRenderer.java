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
    protected void drawNode(Context2d context,
                            Node node) {
    	
    	
    	Bounds innerBounds = node.getBounds();
    	Bounds outerBounds = new Bounds(innerBounds.getX() - ENTITY_SET_INSET,
    									innerBounds.getY() - ENTITY_SET_INSET,
    									innerBounds.getWidth() + (2 * ENTITY_SET_INSET),
    									innerBounds.getHeight() + (2 * ENTITY_SET_INSET)
    			
    	);
    	
    	
    	drawRectangle(outerBounds, context, true, true);
    	drawRectangle(innerBounds, context, true, true);
    	drawName(innerBounds, context, node);
    	
    	node.setBounds(outerBounds);
    	drawNodeAttachments(context, node);
    }
}
