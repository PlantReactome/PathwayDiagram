/*
 * Created on Nov 30, 2011
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author gwu
 *
 */
public class SubpathwaySelectionEvent extends GwtEvent<SubpathwaySelectionEventHandler> {
	public static Type<SubpathwaySelectionEventHandler> TYPE = new Type<SubpathwaySelectionEventHandler>();
    // A list of selected objects
    private Long subpathwayId;
    private Long diagramPathwayId;
    
    public SubpathwaySelectionEvent() {
    }
    
    public Long getSubpathwayId() {
		return subpathwayId;
	}

	public void setSubpathwayId(Long subpathwayId) {
		this.subpathwayId = subpathwayId;
	}

	public Long getDiagramPathwayId() {
		return diagramPathwayId;
	}

	public void setDiagramPathwayId(Long diagramPathwayId) {
		this.diagramPathwayId = diagramPathwayId;
	}

	/* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<SubpathwaySelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(SubpathwaySelectionEventHandler handler) {
        handler.onSubpathwaySelection(this);
    }
    
}
