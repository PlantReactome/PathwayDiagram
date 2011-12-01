/*
 * Created on Nov 30, 2011
 *
 */
package org.reactome.diagram.event;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author gwu
 *
 */
public class SelectionEvent extends GwtEvent<SelectionEventHandler> {
    public static Type<SelectionEventHandler> TYPE = new Type<SelectionEventHandler>();
    // A list of selected objects
    private List<GraphObject> selectedObjects;
    
    public SelectionEvent() {
    }
    
    /**
     * Set a list of objects that have been selected.
     * @param objects
     */
    public void setSelectedObjects(List<GraphObject> objects) {
        if (selectedObjects == null)
            selectedObjects = new ArrayList<GraphObject>();
        else
            selectedObjects.clear();
        if (objects != null)
            selectedObjects.addAll(objects);
    }
    
    /**
     * Get a list of selected objects.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        if (selectedObjects == null)
            selectedObjects = new ArrayList<GraphObject>();
        return this.selectedObjects;
    }
    
    /**
     * Get a list of seletcted DB_IDs. Usually it is easy to use DB_IDs for easy mapping
     * among different components.
     * @return
     */
    public List<Long> getSelectedDBIds() {
        List<Long> dbIds = new ArrayList<Long>();
        if (selectedObjects != null) {
            for (GraphObject obj : selectedObjects) {
                if (obj.getReactomeId() != null)
                    dbIds.add(obj.getReactomeId());
            }
        }
        return dbIds;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<SelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(SelectionEventHandler handler) {
        handler.onSelectionChanged(this);
    }
    
}
