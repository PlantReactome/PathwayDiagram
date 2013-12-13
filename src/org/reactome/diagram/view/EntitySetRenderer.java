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
    
    public EntitySetRenderer() {
    }
    
    /**
     * This is a template that should be implemented by a sub-class.
     * @param bounds
     * @param context
     * @param node
     */
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
    }
}
