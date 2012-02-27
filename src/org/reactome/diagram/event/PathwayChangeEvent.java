/*
 * Created on Feb 27, 2012
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is used to describe the pathway displayed in a canvas has been changed. The client
 * to this Event can get the previous displayed pathway diagram DB_ID and the curretn display DB_ID.
 * @author gwu
 *
 */
public class PathwayChangeEvent extends GwtEvent<PathwayChangeEventHandler> {
    public static final Type<PathwayChangeEventHandler> TYPE = new Type<PathwayChangeEventHandler>();
    private Long previousPathwayDBId;
    private Long currentPathwayDBId;
    
    public PathwayChangeEvent() {
    }
    
    public Long getPreviousPathwayDBId() {
        return previousPathwayDBId;
    }


    public void setPreviousPathwayDBId(Long previousPathwayDBId) {
        this.previousPathwayDBId = previousPathwayDBId;
    }



    public Long getCurrentPathwayDBId() {
        return currentPathwayDBId;
    }



    public void setCurrentPathwayDBId(Long currentPathwayDBId) {
        this.currentPathwayDBId = currentPathwayDBId;
    }



    @Override
    protected void dispatch(PathwayChangeEventHandler handler) {
        handler.onPathwayChange(this);
    }

    @Override
    public Type<PathwayChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    
    
}
