/*
 * Created on June 16, 2014
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Customized renderer for rendering expression overlay of RenderableGenes.
 * @author weiserj
 *
 */
public class ExpressionGeneRenderer extends GeneRenderer {

    @Override
    protected void drawRectangle(Bounds bounds, Context2d context, Node node) {
    	drawRectangle(getTextBounds(bounds, context, node), context, true, true);
    }
}
