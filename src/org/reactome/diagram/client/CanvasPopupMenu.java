/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class CanvasPopupMenu extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    // Used to hold any buttons
    private VerticalPanel contentPane;
    
    public CanvasPopupMenu() {
        super(true);
        init();
    }
    
    private void init() {
        contentPane = new VerticalPanel();
        setWidget(contentPane);
    }
    
    public void setPathwayDiagramPanel(PathwayDiagramPanel pane) {
        diagramPane = pane;
    }
    
    public PathwayDiagramPanel getPathwayDiagramPanel() {
        return this.diagramPane;
    }
    
    private Button createGoToPathwayButton(final GraphObject subPathway) {
        Button goToPathwayBtn = new Button("Go to Pathway");
        goToPathwayBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                goToPathway(subPathway);
                hide();
            }
        });
        return goToPathwayBtn;
    }
    
    private void goToPathway(GraphObject pathway) {
        // Get the pathway DB_ID
        Long dbId = pathway.getReactomeId();
        diagramPane.setPathway(dbId);
    }
    
    /**
     * Override to remove any popup menu.
     */
    @Override
    public void hide() {
        contentPane.clear();
        super.hide();
    }
    
    /**
     * Show popup menu
     * @param panel
     */
    public void showPopupMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
        List<GraphObject> selectedObjects = diagramPane.getSelectedObjects();
        // Support single selection only currently
        if (selectedObjects == null || selectedObjects.size() != 1)
            return;
        GraphObject selected = selectedObjects.get(0);
        
        
        // For showing sub pathway diagram
        if (selected.getType() == GraphObjectType.ProcessNode) {
            Button btn = createGoToPathwayButton(selected);
            // Add the button if it is not already there
            if (contentPane.getWidgetCount() == 0)
            	contentPane.add(btn);
        }

        
        
        
        if (contentPane.getWidgetCount() == 0)
            return;
        setPopupPosition(event.getNativeEvent().getClientX() + 2, 
                         event.getNativeEvent().getClientY() + 2); // A little shift if actually better
        show();
    }
    
}
