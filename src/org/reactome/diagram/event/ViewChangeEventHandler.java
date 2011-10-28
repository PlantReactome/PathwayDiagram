/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author gwu
 *
 */
public interface ViewChangeEventHandler extends EventHandler {
    
    public void onViewChange(ViewChangeEvent event);
    
}
