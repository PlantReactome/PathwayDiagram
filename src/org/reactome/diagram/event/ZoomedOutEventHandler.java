/*
 * Created on February 4, 2015
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author weiserj
 *
 */
public interface ZoomedOutEventHandler extends EventHandler {
    
    public void onZoomedOut(ZoomedOutEvent event);
    
}
