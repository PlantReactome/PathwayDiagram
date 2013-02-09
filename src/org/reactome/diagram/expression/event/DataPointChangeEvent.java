/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression.event;

import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This customized GwtEvent is used to describe a data point change event. For example,
 * the user choose a different time point in a series of gene expression data.
 * @author gwu
 *
 */
public class DataPointChangeEvent extends GwtEvent<DataPointChangeEventHandler> {
    public static Type<DataPointChangeEventHandler> TYPE = new Type<DataPointChangeEventHandler>();
    private Long pathwayId;
    private Map<Long, String> pathwayComponentIdToColor;
    
    public DataPointChangeEvent() {
    }
    
    public Long getPathwayId() {
        return pathwayId;
    }

    public void setPathwayId(Long pathwayId) {
        this.pathwayId = pathwayId;
    }

    public Map<Long, String> getPathwayComponentIdToColor() {
        return pathwayComponentIdToColor;
    }

    public void setPathwayComponentIdToColor(Map<Long, String> pathwayComponentIdToColor) {
        this.pathwayComponentIdToColor = pathwayComponentIdToColor;
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
