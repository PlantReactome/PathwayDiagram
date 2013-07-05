/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.Node;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class CanvasPopupMenu extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    private NodeOptionsMenu menuBar;
    
    public CanvasPopupMenu(PathwayDiagramPanel diagramPane) {
        super(true);
        this.diagramPane = diagramPane;
        init();
    }
    
    private void init() {
        menuBar = new NodeOptionsMenu(diagramPane, true);    
        setWidget(menuBar);
    }

    public NodeOptionsMenu getMenuBar() {
    	return this.menuBar;
    }
    
    /**
     * Override to remove any popup menu.
     */
    @Override
    public void hide() {
        super.hide();
    }
    
    /**
     * Show popup menu
     * @param event
     */
    public void showPopupMenu(MouseEvent<? extends EventHandler> event) {
       	event.preventDefault();
        event.stopPropagation();
        
        hide();
        
        menuBar.createMenu((Node) getSelectedObject());
        
        setPopupPosition(event.getNativeEvent().getClientX() + 2,
        				 event.getNativeEvent().getClientY() + 2);
        
        WidgetStyle.bringToFront(this);
        
        show();
    }
        
    private GraphObject getSelectedObject() {
    	return diagramPane.getSelectedObjects().get(0);
    }
    
}
