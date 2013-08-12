/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.Node;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class CanvasPopupMenu extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    private NodeOptionsMenuBar menuBar;
    private GraphObject selected;
    
    public CanvasPopupMenu(PathwayDiagramPanel diagramPane) {
        super(true);
        this.diagramPane = diagramPane;
        init();
    }
    
    private void init() {
        menuBar = new NodeOptionsMenuBar(diagramPane);    
        setWidget(menuBar.getMenuBar());
    }

    public NodeOptionsMenuBar getNodeOptionsMenuBar() {
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
        
        selected = getSelectedObject();
        
        hide();
        
        menuBar.createMenu(selected);
                
        WidgetStyle.bringToFront(this);
        
        showIfMenuHasItems(event);
    }
        
    private GraphObject getSelectedObject() {
    	return diagramPane.getSelectedObjects().get(0);
    }
    
    private void showIfMenuHasItems(final MouseEvent<? extends EventHandler> event) {
    	if (menuBar.getItems() == null || menuBar.getItems().isEmpty())
    		return;
    	
    	setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (selected instanceof Node) {
					final PathwayCanvas pathwayCanvas = diagramPane.getPathwayCanvas();
															
					final Integer left = adjustCoordinate(
											((Node) selected).getBounds().getX(),
											(int) pathwayCanvas.getTranslateX(),
											(int) pathwayCanvas.getScale()
										 ) + pathwayCanvas.getAbsoluteLeft();	
											
					final Integer bottom = adjustCoordinate(  
											((Node) selected).getBounds().getY() + ((Node) selected).getBounds().getHeight(),
											(int) pathwayCanvas.getTranslateY(),
											(int) pathwayCanvas.getScale()
										   ) + pathwayCanvas.getAbsoluteTop();
					
					setPopupPosition(left, bottom);
					
				} else {
					final Integer OFFSET = 2;
					setPopupPosition(event.getClientX() + OFFSET, event.getClientY() + OFFSET);
				}
			}
    					
			private Integer adjustCoordinate(Integer coordinate, Integer translate, Integer scale) {
				return (coordinate * scale) + translate;
			}
    	});
    }
    
    public class NodeOptionsMenuBar extends NodeOptionsMenu {
    	private MenuBar menuBar;
    	private List<MenuItem> menuItems; 
    	private Map<Integer, MenuItem> menuItemLookup;
    	
    	public NodeOptionsMenuBar(PathwayDiagramPanel diagramPane) {
    		super(diagramPane);
    		
    		menuBar = new MenuBar(true);
    		menuBar.setAutoOpen(true);
    		
    		menuItems = new ArrayList<MenuItem>();
    		menuItemLookup = new HashMap<Integer, MenuItem>();
    	}
    	
    	public MenuBar getMenuBar() {
    		return menuBar;
    	}
    	
    	public List<MenuItem> getItems() {
    		return menuItems;
    	}
    	    	
    	public MenuOption addItem(String label, Command command) {
    		return addItem(createItem(label, command));
    	}
    	
    	public MenuOption addItem(String label, MenuBar subMenu) {
    		return addItem(createItem(label, subMenu));
    	}

    	private MenuOption addItem(MenuOption menuOption) {
    		MenuItem menuItem = getMenuItem(menuOption); 
    		
    		menuBar.addItem(menuItem);
    		menuItems.add(menuItem);
    		menuItemLookup.put(menuOption.getId(), menuItem);
    		
    		return menuOption;
    	}
    	
    	public Widget getParent() {
    		return menuBar.getParent();
    	}    	

    	public void enableItem(MenuOption menuOption, Boolean enable) {
    		MenuItem menuItem = menuItemLookup.get(menuOption.getId());
    		
    		if (menuItem != null)
    			menuItem.setEnabled(enable);
    	}
    	
    	public Integer getItemIndex(MenuOption menuOption) {    		
    		return menuBar.getItemIndex(menuItemLookup.get(menuOption.getId()));
    	}    	    	
    	
    	public void removeItem(MenuOption menuOption) {
    		MenuItem item = menuItemLookup.get(menuOption.getId());
    		
    		menuItemLookup.remove(item);
    		menuItems.remove(item);
    		menuBar.removeItem(item);
    	}
    	    	
    	public void insertItem(MenuOption menuOption, Integer index) {
    		MenuItem item = getMenuItem(menuOption);
    		
    		menuItemLookup.put(menuOption.getId(), item);
    		menuItems.add(index, item);
    		menuBar.insertItem(item, index);    		
    	}
    	
    	public void clearItems() {
    		menuItems.clear();
    		menuItemLookup.clear();
    		menuBar.clearItems();
    	}
    	
    	protected void hideIfWithinPopupPanel() {
    		super.hideIfWithinPopupPanel(menuBar);
    	}
    	
    	private MenuItem getMenuItem(MenuOption menuOption) {
    		if (menuOption.getCommand() != null)
    			return new MenuItem(menuOption.getLabel(), menuOption.getCommand());
    		else
    			return new MenuItem(menuOption.getLabel(), menuOption.getSubMenu());
    	}
    }    
}
