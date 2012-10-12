/*
 * Created on Nov 30, 2011
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author jweiser
 *
 */
public interface HoverEventHandler extends EventHandler {
    
    public void onHover(HoverEvent e);
    
}
