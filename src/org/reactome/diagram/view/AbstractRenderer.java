/*
 * Created on Sep 22, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * The top-level renderer for rendering GraphObject.
 * @author gwu
 *
 */
public abstract class AbstractRenderer<T extends GraphObject> implements GraphObjectRenderer<T> {
    
    /**
     * Converts the Stroke and Fill Colors to Standard CssColor Objects and sets it on the Canvas
     * @param context Context2d object for which the Color Settings are initialized
     */
    public void setColors(Context2d context,
                          String fgColor,
                          String bgColor) {
        CssColor strokeStyleColor = CssColor.make(fgColor);
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(bgColor);
        context.setFillStyle(fillStyleColor);
    }
    
}
