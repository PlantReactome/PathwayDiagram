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
public interface SelectionEventHandler extends EventHandler {
    
    public void onSelectionChanged(SelectionEvent e);
    
}
