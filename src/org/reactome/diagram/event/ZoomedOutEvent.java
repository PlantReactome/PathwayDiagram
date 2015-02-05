/*
 * Created on February 4, 2015
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to monitor being zoomed out from a pathway.
 * @author weiserj
 *
 */
public class ZoomedOutEvent extends GwtEvent<ZoomedOutEventHandler> {
    public static Type<ZoomedOutEventHandler> TYPE = new Type<ZoomedOutEventHandler>();
    private long pathwayId;

    public ZoomedOutEvent(long pathwayId) {
    	this.setPathwayId(pathwayId);
    }

	public long getPathwayId() {
		return pathwayId;
	}

	private void setPathwayId(long pathwayId) {
		this.pathwayId = pathwayId;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ZoomedOutEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ZoomedOutEventHandler handler) {
		handler.onZoomedOut(this);
	}
	
	public String toString() {
		return getClass().getSimpleName() + " for pathway " + getPathwayId();
	}
}
