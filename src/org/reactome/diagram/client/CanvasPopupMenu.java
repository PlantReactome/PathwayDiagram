/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.Node;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.touch.client.Point;
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
    private GraphObject entity;
    private MenuBounds menuBounds;
    
    public CanvasPopupMenu(PathwayDiagramPanel diagramPane) {
        super(true);
        this.diagramPane = diagramPane;
        this.setAutoHideOnHistoryEventsEnabled(false);
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
    	showPopupMenu(getSelectedObject(), event);
    }    
    
    public void showPopupMenu(GraphObject entity, MouseEvent<? extends EventHandler> event) {
       	event.preventDefault();
        event.stopPropagation();
        
        this.entity = entity;

        if(isVisible()){
            hide();
        }
        
        menuBar.createMenu(entity);
                
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
				if (entity instanceof Node) {
					final MenuBounds menuBounds = new MenuBounds(
													getEntityBottomLeft((Node) entity, diagramPane.getPathwayCanvas()),
													offsetWidth,
													offsetHeight
												  );
					
					translateDiagramIfPopupOutsideBounds(menuBounds);
					setPopupPosition(menuBounds.getLeft(), 
									 menuBounds.getTop());
				} else {
					final Integer OFFSET = 2;
					setPopupPosition(event.getClientX() + OFFSET, event.getClientY() + OFFSET);
				}
			}
    	});
    }
    
    private void translateDiagramIfPopupOutsideBounds(MenuBounds menuBounds) {
    	Integer menuRight = menuBounds.getLeft() + menuBounds.getWidth() + menuBounds.getSubMenuWidth();
    	
    	if (menuRight > pathwayCanvasRight()) {
    		Integer deltaX = pathwayCanvasRight() - menuRight;
    		menuBounds.translate(deltaX, 0);
    		diagramPane.translate(deltaX, 0);
    		diagramPane.update();
    	}
    }
    
    private Integer pathwayCanvasRight() {
    	final PathwayCanvas pathwayCanvas = diagramPane.getPathwayCanvas();
    	
    	return pathwayCanvas.getAbsoluteLeft() + pathwayCanvas.getCoordinateSpaceWidth();
    }
    
    private Point getEntityBottomLeft(Node entity, PathwayCanvas pathwayCanvas) {
    	final Integer entityLeft = entity.getBounds().getX();
    	final Integer entityBottom = entity.getBounds().getY() + entity.getBounds().getHeight(); 
    	
    	return pathwayCanvas.getAbsoluteCoordinates(entityLeft.doubleValue(), entityBottom.doubleValue());
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
    
    private class MenuBounds {
    	private Point menuCoordinates;
    	private Integer menuWidth;
    	private Integer menuHeight;
    	
    	public MenuBounds(Point menuCoordinates, Integer menuWidth, Integer menuHeight) {
    		this.menuCoordinates = menuCoordinates;
    		this.menuWidth = menuWidth;
    		this.menuHeight = menuHeight;
    	}
    	
    	public Integer getLeft() {
    		return (int) menuCoordinates.getX();
    	}
    	
    	public Integer getTop() {
    		return (int) menuCoordinates.getY();
    	}
    	
    	public Integer getWidth() {
    		return menuWidth;
    	}
    	
    	public Integer getSubMenuWidth() {
    		Integer subMenuWidth = 0;
    		
    		for (MenuItem subMenuItem : menuBar.getItems()) {
    			subMenuWidth = Math.max(subMenuWidth, subMenuItem.getText().length() * menuItemFontSize(subMenuItem));
    		}
    		
    		return subMenuWidth;
    	}
    	
    	public void translate(Integer dx, Integer dy) {
    		menuCoordinates = menuCoordinates.plus(new Point(dx, dy));
    	}
    	
    	private Integer menuItemFontSize(MenuItem menuItem) {
    		try {
    			return Integer.parseInt(menuItem.getElement().getStyle().getFontSize());
    		} catch (NumberFormatException e) {
    			System.out.println();
    			return 0;
    		}
    	}
    }
}
