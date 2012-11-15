/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.ReactomeObject;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class CanvasPopupMenu extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    private MenuBar menuBar;
    private GraphObject selected;
    
    public CanvasPopupMenu() {
        super(true);
        init();
    }
    
    private void init() {
        menuBar = new MenuBar(true);
        setWidget(menuBar);
    }
    
    public void setPathwayDiagramPanel(PathwayDiagramPanel pane) {
        diagramPane = pane;
    }
    
    public PathwayDiagramPanel getPathwayDiagramPanel() {
        return this.diagramPane;
    }

    public MenuBar getMenuBar() {
    	return this.menuBar;
    }
    
    // Pathway Node Menu
    private void createProcessNodeMenu() {
        menuBar.addItem(new MenuItem("Go to Pathway", new Command() {
            @Override
            public void execute() {
                diagramPane.setPathway(selected.getReactomeId());
                hide();
            }
        }));
        
    }   
    
    // Complex Entity Menu
    private void createComplexMenu() {
    	createPhysicalEntityMenu();
    	setPMMenu(); 
    }

    // Participating molecules menu	
    private void setPMMenu() {
    	diagramPane.getController().getParticipatingMolecules(selected.getReactomeId());    	
    }
        
    // Protein/RNA/DNA Entity Menu    
    private void createGEEMenu() {
    	createPhysicalEntityMenu();
    	menuBar.addItem(new MenuItem("Display Interactors", new Command() {
    		@Override
    		public void execute() {
    			
    		}	
   		}));
    	
    	menuBar.addItem(new MenuItem("Export Interactors", new Command() { 
    		@Override
    		public void execute() {
    			
    		}
    	}));
    }

    // Small Molecule Menu	
    private void createSmallMoleculeMenu() {
    	createPhysicalEntityMenu();
    }
    
    // Menu items common to all physical entities
    private void createPhysicalEntityMenu() {
    	menuBar.addItem(new MenuItem("Other Pathways", getPathwayMenu())); 
    }
        
    private MenuBar getPathwayMenu() {
    	MenuBar pathwayMenu = new MenuBar(true);
    	pathwayMenu.setAutoOpen(true);    	
    	pathwayMenu.setStyleName(this.getStyleName());
    	
    	List<ReactomeObject> pathways = getOtherPathways();
    	
    	
    	return pathwayMenu;
    }
    
    private List<ReactomeObject> getOtherPathways() {
    	return null;
    }
    
    /**
     * Override to remove any popup menu.
     */
    @Override
    public void hide() {
        menuBar.clearItems();
        super.hide();
    }
    
    /**
     * Show popup menu
     * @param panel
     */
    public void showPopupMenu(MouseEvent<? extends EventHandler> event) {
       	event.preventDefault();
        event.stopPropagation();
        
        hide();
        createMenu(event);
        show();
    }
    
    private void createMenu(MouseEvent<? extends EventHandler> event) {
        selected = getSelectedObject();    	
    	GraphObjectType type = selected.getType();
        
        if (type == GraphObjectType.ProcessNode) {
            createProcessNodeMenu();            
        } else if (type == GraphObjectType.RenderableComplex) {
        	createComplexMenu();
        } else if (type == GraphObjectType.RenderableProtein) {
        	createGEEMenu();
        } else if (type == GraphObjectType.RenderableChemical) {
        	createSmallMoleculeMenu();
        }
         
        setPopupPosition(event.getNativeEvent().getClientX() + 2, 
                         event.getNativeEvent().getClientY() + 2); // A little shift if actually better                
    }
    
    private GraphObject getSelectedObject() {
    	return diagramPane.getSelectedObjects().get(0);
    }
    
}
