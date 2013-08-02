/*
 * Created on Feb 13, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;

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
        setFillStyle(c2d, node);
        setStroke(c2d,
                  node);
        drawName(c2d, node);
    }
    
}
