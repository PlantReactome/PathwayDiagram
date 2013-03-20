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
public class ParticipatingMoleculeSelectionEvent extends GwtEvent<ParticipatingMoleculeSelectionEventHandler> {
    public static Type<ParticipatingMoleculeSelectionEventHandler> TYPE = new Type<ParticipatingMoleculeSelectionEventHandler>();
    // A list of selected objects
    private Long selectedParticipatingMoleculeId;
    
    public ParticipatingMoleculeSelectionEvent() {
    }
    
    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<ParticipatingMoleculeSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(ParticipatingMoleculeSelectionEventHandler handler) {
        handler.onPMSelectionChanged(this);
    }

	public Long getSelectedParticipatingMoleculeId() {
		return selectedParticipatingMoleculeId;
	}

	public void setSelectedParticipatingMoleculeId(
			Long selectedParticipatingMoleculeId) {
		this.selectedParticipatingMoleculeId = selectedParticipatingMoleculeId;
	}
    
}
