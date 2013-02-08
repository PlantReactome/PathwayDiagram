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
public class DataPointChangeEvent extends GwtEvent<DataPointChangeEventHandler> {
    public static Type<DataPointChangeEventHandler> TYPE = new Type<DataPointChangeEventHandler>();
    
    public DataPointChangeEvent() {
        
    }
    
    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<DataPointChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(DataPointChangeEventHandler handler) {
        handler.onDataPointChanged(this);
    }
    
}
