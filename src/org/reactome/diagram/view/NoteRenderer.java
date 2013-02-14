/*
 * Created on Feb 13, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * @author gwu
 *
 */
public class NoteRenderer extends NodeRenderer {
   
    public NoteRenderer() {
        
    }

    /**
     * We only need to draw text for notes
     */
    @Override
    public void render(Context2d c2d, Node node) {
        setColors(c2d, node.getFgColor(), node.getBgColor());
        
        String color = node.getBgColor();
        if (color == null) {
            c2d.setFillStyle(Parameters.defaultbgColor);
        }
        else {
            c2d.setFillStyle(CssColor.make(color));
        }
        
        setStroke(c2d,
                  node);
        drawName(c2d, node);
    }
    
}
