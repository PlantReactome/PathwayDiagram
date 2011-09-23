/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * An interface for rendering a GraphicObject in a canvas 2d context.
 * @author gwu
 *
 */
public interface GraphObjectRenderer<T extends GraphObject> {
    
    /**
     * Draw graphObject in a Canvas context2d.
     * @param g2d
     * @param graphObject
     */
    public void render(Context2d g2d, 
                       T graphObject);
    
}
