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
    public static final int RECTANGLE_DIST = 10;
    
    public static final double ARROW_ANGLE = Math.PI / 6;
    public static final int ARROW_LENGTH = 8; 
    public static final int EDGE_TYPE_WIDGET_WIDTH = 12;
    public static final int EDGE_MODULATION_WIDGET_WIDTH = 8;
    // Make sure bold should be placed before 10px. Have to check if Monospaced work!
    public static final String WIDGET_FONT = "bold 10px Monospaced";
    
    /**
     * Draw graphObject in a Canvas context2d.
     * @param g2d
     * @param graphObject
     */
    public void render(Context2d g2d, 
                       T graphObject);
    
}
