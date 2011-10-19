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
    
    public static final int ROUND_RECT_ARC_WIDTH = 6;
    public static final int COMPLEX_RECT_ARC_WIDTH = 6;
    public static final int THINK_LINE_WIDTH = 2;
    public static final int RECTANGLE_DIST = 10;
    
    /**
     * Draw graphObject in a Canvas context2d.
     * @param g2d
     * @param graphObject
     */
    public void render(Context2d g2d, 
                       T graphObject);
    
}
