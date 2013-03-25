/*
 * Created on Nov 30, 2011
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author gwu
 *
 */
public interface SubpathwaySelectionEventHandler extends EventHandler {
    
    public void onSubpathwaySelection(SubpathwaySelectionEvent e);
    
}
