/*
 * Created on Oct 4, 2012
 *
 */
package org.reactome.diagram.event;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jweiser
 *
 */
public class HoverEvent extends GwtEvent<HoverEventHandler> {
    public static Type<HoverEventHandler> TYPE = new Type<HoverEventHandler>();
    
    private GraphObject hoveredObject;
    
    public HoverEvent() {
    }
    
    /**
     * Set a graph object that has been hovered over.
     * @param GraphObject
     */
    public void setHoveredObject(GraphObject obj) {
    	this.hoveredObject = obj;
    }
    
    /**
     * Get the graph object that has been hovered over.
     * @return GraphObject
     */
    public GraphObject getHoveredObject() {
        return this.hoveredObject;
    }
    
    /**
     * Get a list of seletcted DB_IDs. Usually it is easy to use DB_IDs for easy mapping
     * among different components.
     * @return
     */

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<HoverEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(HoverEventHandler handler) {
        handler.onHover(this);
    }
    
}
