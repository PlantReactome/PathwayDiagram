/*
 * Created on Feb 27, 2012
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Used to handle a pathway displayed in a CanvasPanel has been changed to another one.
 * @author gwu
 *
 */
public interface PathwayChangeEventHandler extends EventHandler {
    
    public void onPathwayChange(PathwayChangeEvent event);
    
}
