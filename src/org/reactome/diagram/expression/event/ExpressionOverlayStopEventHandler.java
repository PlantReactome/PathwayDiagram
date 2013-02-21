/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * An interface is used to handle DataPointChangeEvent.
 * @author gwu
 *
 */
public interface ExpressionOverlayStopEventHandler extends EventHandler {
    
    public void onExpressionOverlayStopped(ExpressionOverlayStopEvent e);
    
}
