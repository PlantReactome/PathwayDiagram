/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This customized GwtEvent is used to describe a data point change event. For example,
 * the user choose a different time point in a series of gene expression data.
 * @author gwu
 *
 */
public class ExpressionOverlayStopEvent extends GwtEvent<ExpressionOverlayStopEventHandler> {
    public static Type<ExpressionOverlayStopEventHandler> TYPE = new Type<ExpressionOverlayStopEventHandler>();
    
    public ExpressionOverlayStopEvent() {
    }
    
    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<ExpressionOverlayStopEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(ExpressionOverlayStopEventHandler handler) {
        handler.onExpressionOverlayStopped(this);
    }
    
}
