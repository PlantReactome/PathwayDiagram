/*
 * Created on Aug 1, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.touch.client.Point;


/**
 * Customized renderer for rendering RenderableGenes.
 * @author gwu
 *
 */
public class GeneRenderer extends NodeRenderer {
    // Two final values for gene rendering
    private static final int GENE_SYMBOL_PAD = 4;
    private static final int GENE_SYMBOL_WIDTH = 50;
    
    
    /**
     * Default constructor.
     */
    public GeneRenderer() {
        defaultLineWidth = 2.5d;
    }

    @Override
    protected void drawRectangle(Bounds bounds, Context2d context, Node node) {
        // Draw bounds when selected
        if (node.isSelected()) {
            drawRectangle(getTextBounds(bounds, context, node), context, false, true);
        }
        drawGeneSymbol(context, 
                       bounds,
                       node);
    }

    @Override
	protected void hideObject(Context2d c2d, Node node) {
		super.hideObject(c2d, node);
		hideArea(c2d, getTextBounds(node.getBounds(), c2d, node));
	}

	private double getTextPositionY(Bounds bounds) {
		return getTextPositionY(bounds, 0) - 2;
	}

	@Override
    protected double getTextPositionY(Bounds bounds, int totalHeight) {
        return bounds.getY() + GENE_SYMBOL_WIDTH / 2 + 2;
    }

	protected Bounds getTextBounds(Bounds bounds, Context2d context, Node node) {
		Bounds textBounds = new Bounds(bounds);
		textBounds.setY(getTextPositionY(bounds));
		
		List<String> textLines = splitName(node.getDisplayName(), context, (int) bounds.getWidth());
		textBounds.setHeight(textLines.size() * Parameters.LINE_HEIGHT + 6);
		
		return textBounds;
	}
	
    /**
     * Draw a gene symbol just above the gene's name.
     * @param context
     * @param y
     */
    private void drawGeneSymbol(Context2d context,
                                Bounds bounds,
                                Node node) {
        // Draw the horizontal line
        double x1 = bounds.getX();
        double y1 = getTextPositionY(bounds);
        double x2 = bounds.getX() + bounds.getWidth();
        double y2 = getTextPositionY(bounds);
        // Draw a line
        context.beginPath();
        context.moveTo(x1, y1);
        context.lineTo(x2, y2);
        // Draw the vertical line
        x1 = x2 - GENE_SYMBOL_PAD;
        x2 = x1;
        y2 = (int)(y1 - GENE_SYMBOL_WIDTH / 2.0) + 2; // Need an extra 2 pixel. Not sure why!
        // Looks nice with one pixel offset
        context.moveTo(x1, y1);
        context.lineTo(x2, y2);
        // another very short horizontal line
        x1 += GENE_SYMBOL_PAD;
        context.lineTo(x1, y2);
//        context.closePath();
        context.stroke();
        // draw the arrow
        String color = node.getLineColor();
        if (color == null)
            color = "rgba(0, 0, 0, 1)";
        // Keep it to reset it to the original style is always a good practice
        FillStrokeStyle oldStyle = context.getFillStyle();
        context.setFillStyle(CssColor.make(color));
        drawArrow(context, 
                  new Point(x1, y2), 
                  new Point(x1 + ARROW_LENGTH, y2), 
                  false);
        context.setFillStyle(oldStyle); // Reset it back
    }
    
}
