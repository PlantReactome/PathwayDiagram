/*
 * Created on Oct 18, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * Customized NodeRenderer to draw Compartment.
 * @author gwu
 *
 */
public class CompartmentRenderer extends NodeRenderer {
    //TODO: Need to set line weights for compartment
    public CompartmentRenderer() {
        defaultLineColor = CssColor.make(255, 153, 102); 
    }

    @Override
    protected int getRadius() {
        return 2 * super.getRadius();
    }

    @Override
    protected void drawRectangle(Bounds bounds, Context2d context) {
        CssColor color = (CssColor) context.getStrokeStyle();
        System.out.println("Compartment color: " + color.value());
        super.drawRectangle(bounds, context);
    }
    
    
    
    
}
