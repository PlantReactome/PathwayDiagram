/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.diagram.event.ParticipatingMoleculeSelectionEvent;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.CompositionalNode;
import org.reactome.diagram.model.CompositionalNode.Component;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.model.ReactomeObject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public abstract class NodeOptionsMenu {
    private PathwayDiagramPanel diagramPane;
    private GraphObject selected;
    private Integer menuItemsBeingCreated; // Count of menu items waiting for a callback before they can be created
    //private Integer numberOfMenuItems;
    private Integer currentMenuItemId;
    
    public NodeOptionsMenu(PathwayDiagramPanel diagramPane) {  
        this.diagramPane = diagramPane;
        init();
    }
    
    private void init() {
    	menuItemsBeingCreated = 0;
    	currentMenuItemId = 0;
    }
    
    private PathwayDiagramController getController() {
    	return PathwayDiagramController.getInstance();
    }
    
    // Pathway Node Menu
    private void createProcessNodeMenu() {
    	addItem("Go to Pathway", new Command() {
        	@Override
        	public void execute() {
        		diagramPane.setPathway(selected.getReactomeId());
        		hideIfWithinPopupPanel();
        	}
        });
    }   
    
    // Complex Entity Menu
    private void createComplexMenu() {
    	createPhysicalEntityMenu();
    	
    	if (((CompositionalNode) selected).participatingMoleculesObtained()) {
    		setPMMenu();
    	} else {
    		getController().getParticipatingMolecules(selected.getReactomeId(), setParticipatingMolecules());
    	}
    }
    	
    private RequestCallback setParticipatingMolecules() {
    	RequestCallback setParticipatingMolecules = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				((CompositionalNode) selected).setParticipatingMolecules().onResponseReceived(request, response);
				setPMMenu();
			}

			@Override
			public void onError(Request request, Throwable exception) {
				((CompositionalNode) selected).setParticipatingMolecules().onError(request, exception);
			}
    		
    	};
    	
		return setParticipatingMolecules;
    }
    
    // Set participating molecules menu
    private void setPMMenu() {
    	menuItemsBeingCreated += 1;
    	if (diagramPane.getOverlayDataController() != null) {
   			addItem("Display Participating Molecules", new Command() {
				@Override
				public void execute() {
					diagramPane.getOverlayDataController().showPopup((CompositionalNode) selected);
					hide();							
				}    					
   			});   			
    	} else {
    		SubMenuBar pmMenu = new SubMenuBar(true);
    		pmMenu.setAutoOpen(true);
    		pmMenu.setAnimationEnabled(true);
    		
    		addItemsToMenu(pmMenu, getParticipatingMoleculesMenuItems());
    		
    		styleSubMenu(pmMenu);
    		
    		addItem("Participating Molecules", pmMenu);
    	}
    	menuItemsBeingCreated -= 1;
    }
    
    private List<MenuItem> getParticipatingMoleculesMenuItems() {
   		List<MenuItem> pmMenuItems = new ArrayList<MenuItem>();
   		
   		for (final Component component : ((CompositionalNode) selected).getComponents()) {			
   			final MenuItem pmMenuItem = new MenuItem(component.getDisplayName(), nullCommand());
   			
   			pmMenuItem.setScheduledCommand(new Command() {
   				
   					@Override
   					public void execute() {
   						ParticipatingMoleculeSelectionEvent pmSelectionEvent = new ParticipatingMoleculeSelectionEvent();
   						pmSelectionEvent.setSelectedParticipatingMoleculeId(component.getReactomeId());
   						diagramPane.fireEvent(pmSelectionEvent);
   		
   						hideParentMenu(pmMenuItem);
   						hideIfWithinPopupPanel();
   					}    					
   				}
   			);
   			
   			pmMenuItems.add(pmMenuItem);
   		}	
    	
   		return pmMenuItems;
   	}	
         
    // Protein/RNA/DNA Entity Menu    
    private void createGEEMenu() {
    	createPhysicalEntityMenu();
    	
    	final ProteinNode pSelected = (ProteinNode) selected;     	     	
    	
    	final MenuOption toggleInteractors = addItem(toggleInteractorsLabel(pSelected.isDisplayingInteractors()));    			
    	toggleInteractors.setCommand(new Command() {
    		    		
    		@Override
    		public void execute() {    			
    			if (pSelected.isDisplayingInteractors()) {
    				diagramPane.getInteractorCanvas().removeProtein(pSelected);
    				toggleInteractors.setLabel(toggleInteractorsLabel(pSelected.isDisplayingInteractors()));
    			} else {	
    				getController().getInteractors(pSelected, 
    												setInteractors(pSelected, toggleInteractors), diagramPane
    												);
    			}   			
    		
    			hide();
    		}    		
    	});
    	    	   	
    	addItem("Export Interactors", new Command() { 
    		@Override
    		public void execute() {
    			getController().openInteractionExportPage(pSelected.getReactomeId(), 
    					diagramPane.getInteractorCanvasModel().getInteractorDatabase());
    			hide();
    		}
    	});    	
    }
    
    private String toggleInteractorsLabel(Boolean interactorsDisplaying) {
    	return (interactorsDisplaying ? "Hide" : "Display") + " interactors";
    }

    private RequestCallback setInteractors(final ProteinNode protein, final MenuOption toggleInteractors) {
    	toggleInteractors.setEnabled(false);
    	    	
    	RequestCallback setInteractors = new RequestCallback() {
    		    		
    		public void onResponseReceived(Request request, Response response) {    			
    			diagramPane.getInteractorCanvas().setInteractors(protein)
    							.onResponseReceived(request, response);
    			
    			toggleInteractors.setLabel(toggleInteractorsLabel(protein.isDisplayingInteractors()));
    			toggleInteractors.setEnabled(true);    			
    		}
    		
    		public void onError(Request request, Throwable exception) {
    			diagramPane.getInteractorCanvas().setInteractors(protein).onError(request, exception);
    			toggleInteractors.setEnabled(true);
    		}
    	};
    	
    	return setInteractors;
    }
    
    // Small Molecule Menu	
    private void createSmallMoleculeMenu() {
    	createPhysicalEntityMenu();
    }
    
    // Menu items common to all physical entities
    private void createPhysicalEntityMenu() {
   		MenuOption pathwayMenuItem = addItem("Retrieving other Pathways...", nullCommand());
    	
    	getController().getOtherPathways(selected.getReactomeId(), setPathwayMenu(pathwayMenuItem));
    }
        
    private RequestCallback setPathwayMenu(final MenuOption pathwayMenuItem) {
    	menuItemsBeingCreated += 1;
    	
    	RequestCallback setPathwayMenu = new RequestCallback() {
    		
    		public void onResponseReceived(Request request, Response response) {
    			if (response.getStatusCode() == 200) {    				
    				SubMenuBar pathwaySubMenu = new SubMenuBar(true);
    				pathwaySubMenu.setAutoOpen(true);
    				pathwaySubMenu.setAnimationEnabled(true);
    			
    				List<MenuItem> otherPathwayMenuItems = getOtherPathwayMenuItems(response.getText());
    				
    				addItemsToMenu(pathwaySubMenu, otherPathwayMenuItems);    				
    				addPathwayMenu(pathwayMenuItem, pathwaySubMenu, otherPathwayMenuItems.size());
    				
    				styleSubMenu(pathwaySubMenu);
    			} else {
    				getController().requestFailed("Could not retrieve other pathways");
    			}
    			
    			menuItemsBeingCreated -= 1;
    		}
    		
    		public void onError(Request request, Throwable exception) {
    			getController().requestFailed(exception);
    			menuItemsBeingCreated -= 1;
    		}
    	};
    	
    	return setPathwayMenu;
    }
    
    private void addItemsToMenu(MenuBar menu, List<MenuItem> items) {    	
    	for (MenuItem item : items) {
    		menu.addItem(item);
    		    		
    		if (!isLastItemInList(item, items))
    			menu.addSeparator();
    	}
    }
    
    private Boolean isLastItemInList(MenuItem item, List<MenuItem> listItems) {
    	return item == listItems.get(listItems.size() - 1);
    }
    		
    private List<MenuItem> getOtherPathwayMenuItems(String xml) {
    	List<MenuItem> pathwaySubMenuItems = new ArrayList<MenuItem>();
    	final CanvasPathway currentPathway = diagramPane.getPathway();
    	Set<String> processedPathways = new HashSet<String>();
    	
    	try {
    		Document pathwayDom = XMLParser.parse(xml);
    		Element otherPathwayElement = pathwayDom.getDocumentElement();
    		XMLParser.removeWhitespace(otherPathwayElement);
    	
    		NodeList nodeList = otherPathwayElement.getChildNodes();
    	
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node node = nodeList.item(i);
    			String name = node.getNodeName();
    				
    			if (name.equals("pathway")) {
    				NodeList pathwayNodes = node.getChildNodes();
    			
    				final PathwayObject pathway = new PathwayObject();
    			
    				for (int j = 0; j < pathwayNodes.getLength(); j++) {
    					Node pathwayAttribute = pathwayNodes.item(j);
    					String attributeName = pathwayAttribute.getNodeName();
    				
    					//System.out.println("Attribute Name - " + attributeName);
    					
    					if (attributeName.equals("dbId")) {
    						Long pathwayId = Long.parseLong(pathwayAttribute.getChildNodes().item(0).getNodeValue());
    						pathway.setReactomeId(pathwayId);
    					} else if (attributeName.equals("displayName")) {	
    						String pathwayName = pathwayAttribute.getChildNodes().item(0).getNodeValue();
    						pathway.setDisplayName(pathwayName);
    					} else if (attributeName.equals("hasDiagram")) { 
    						String hasDiagram = pathwayAttribute.getChildNodes().item(0).getNodeValue();
    						pathway.setHasDiagram(new Boolean(hasDiagram));
    					} else {
    						continue;
    					}
    				}
    				
    				if (pathway.getDisplayName() == null ||
    					pathway.getReactomeId() == null	||
    					pathway.getReactomeId().longValue() == currentPathway.getReactomeId().longValue() ||
    					!processedPathways.add(pathway.getDisplayName()))
    					continue;
    				
    				final MenuItem pathwaySubMenuItem = new MenuItem(pathway.getDisplayName(), nullCommand());
    						
    				pathwaySubMenuItem.setScheduledCommand(new Command() {

    					@Override
   						public void execute() {
   							if (pathway.hasDiagram())
    							diagramPane.setPathway(pathway.getReactomeId());
   							else
   								getController().getDiagramPathwayId(pathway.getReactomeId(), diagramPane);
   							
   							hideParentMenu(pathwaySubMenuItem);
   							hideIfWithinPopupPanel();
    					}
    					
    				});
    				
    				pathwaySubMenuItems.add(pathwaySubMenuItem);    				
    			}
    		}    			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return pathwaySubMenuItems;
    }
           
    protected void addPathwayMenu(MenuOption oldPathwayMenuItem, SubMenuBar pathwaySubMenu, int numberOfOtherPathways) {	
    	Integer index = getItemIndex(oldPathwayMenuItem);
    	
    	removeItem(oldPathwayMenuItem);
    	
    	MenuOption newPathwayMenuItem;
    	if (numberOfOtherPathways == 0) {
    		newPathwayMenuItem = createItem("No other pathways", new Command() {
    			public void execute() {
    				hide();
    			}
    		});
    		newPathwayMenuItem.setEnabled(false);
    	} else {
    		newPathwayMenuItem = createItem("Other Pathways", pathwaySubMenu);
    	}
    	
    	insertItem(newPathwayMenuItem, index);
    }

    public void createMenu(GraphObject selectedObject) {
        
    	// Prevents re-creating a menu that is part-way through construction
    	if (menuBeingConstructed())
    		return;
    	
    	clearItems();
    	
    	selected = selectedObject;
        
        GraphObjectType type = selected.getType();
        
        if (type == GraphObjectType.ProcessNode) {
            //numberOfMenuItems = 1;
        	createProcessNodeMenu();            
        } else if (type == GraphObjectType.RenderableComplex ||
        		   type == GraphObjectType.RenderableEntitySet) {
            //numberOfMenuItems = 2;
        	createComplexMenu();
        } else if (type == GraphObjectType.RenderableProtein) {
        	//numberOfMenuItems = 3;
        	createGEEMenu();
        } else if (type == GraphObjectType.RenderableChemical) {
        	//numberOfMenuItems = 1;
        	createSmallMoleculeMenu();
        } else if (type == GraphObjectType.RenderableEntity ||
        		   type == GraphObjectType.RenderableGene ||
        		   type == GraphObjectType.RenderableRNA) {
        	//numberOfMenuItems = 1;
        	createPhysicalEntityMenu();
        }
    }

    public boolean menuBeingConstructed() {
    	return menuItemsBeingCreated > 0;
    }
    
    private void hide() {
    	if (parentIsPopupPanel(getParent())) 
    		((PopupPanel) getParent()).hide();
    }
    
    protected abstract void hideIfWithinPopupPanel();
    
    protected void hideIfWithinPopupPanel(Widget widget) {
    	Widget currentWidget = widget;
    	
    	while (true) {
    		if (currentWidget == null)
    			break;
    		
    		Widget parent = currentWidget.getParent();
    		
    		if (parentIsPopupPanel(parent)) {
    			((PopupPanel) parent).hide();
    			break;
    		}
    		
    		currentWidget = parent;
    	}
    }
    
    private void hideParentMenu(MenuItem menuItem) {
    	if (menuItem != null)
    		hideIfWithinPopupPanel(menuItem.getParentMenu());
    }
    
    private Boolean parentIsPopupPanel(UIObject parent) {
    	return parent != null && parent instanceof PopupPanel;
    }
       
    private void styleSubMenu(MenuBar subMenu) {
    	final String BLACK = "rgb(0, 0, 0)";
    	
    	Style subMenuStyle = subMenu.getElement().getStyle();
    	
    	subMenuStyle.setBorderWidth(1, Unit.PX);
    	subMenuStyle.setBorderColor(BLACK);
    	subMenuStyle.setPosition(Position.ABSOLUTE);
    	subMenuStyle.setZIndex(2);
    	subMenuStyle.setLeft(-1, Unit.PX);
    	subMenuStyle.setTop(-1, Unit.PX);
    	subMenuStyle.setWhiteSpace(WhiteSpace.NOWRAP);
    }
    
    private Command nullCommand() {
    	return new Command() {    		
    		public void execute() {}    		
    	};
    }
    
    protected MenuOption createItem(String label, Command command) {
    	return new MenuOption(label, command);
    }
    
    protected MenuOption createItem(String label, SubMenuBar menu) {
    	return new MenuOption(label, menu);
    }    

    public MenuOption addItem(String label) {
    	return addItem(label, new Command() {
    		public void execute() {
    			hide();
    		}
    	});
    }
    
    protected abstract MenuOption addItem(String label, Command command);
    
    protected abstract MenuOption addItem(String label, SubMenuBar menu);
    
    protected abstract Widget getParent();
    
    protected abstract void enableItem(MenuOption item, Boolean enable);
    
    protected abstract Integer getItemIndex(MenuOption item);
    
    protected abstract void removeItem(MenuOption item);
    
    protected abstract void insertItem(MenuOption item, Integer index);
    
    protected abstract void clearItems();

    protected class MenuOption {    		
    	private Integer id;
    	private String label;
    	private Command command;
    	private SubMenuBar subMenu;
    	private Boolean enabled;
    	
    	private MenuOption(String label) {
    		currentMenuItemId++;
    		this.id = currentMenuItemId;
    		this.label = label;
    		this.enabled = true;
    	}
  
    	public MenuOption() {
    		this(null);
    	}
    	
    	public MenuOption(String label, Command command) {
    		this(label);    		
    		this.command = command;
    	}
    	
    	public MenuOption(String label, SubMenuBar subMenu) {
    		this(label);
    		this.subMenu = subMenu;
    	}
    	    	
    	public Integer getId() {
    		return id;
    	}
    	
    	public String getLabel() {
    		return label;
    	}
    	
    	public void setLabel(String label) {
    		this.label = label;    		
    		updateItem();    		
    	}
    	
    	public Command getCommand() {
    		return command;
    	}
    	
    	public void setCommand(Command command) {
    		this.command = command;
    		updateItem();
    	}
    	
    	public MenuBar getSubMenu() {
    		return subMenu;
    	}
    	
    	public void setSubMenu(SubMenuBar subMenu) {
    		this.subMenu = subMenu;
    		updateItem();
    	}

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			enableItem(this, enabled);
			this.enabled = enabled;
		}
		
		private void updateItem() {
			Integer index = getItemIndex(this);
			
			if (index >= 0) {
				removeItem(this);
				insertItem(this, index);
			}
		}
    }    
    
    protected class SubMenuBar extends MenuBar {
    	
    	public SubMenuBar(boolean vertical) {
    		super(vertical);
    	}
    	
    	@Override
    	public List<MenuItem> getItems() {
    		return super.getItems();
    	}
    }
    
    private class PathwayObject extends ReactomeObject {
    	private boolean hasDiagram;
    	
    	public boolean hasDiagram() {
    		return hasDiagram;
    	}
    	
    	public void setHasDiagram(boolean hasDiagram) {
    		this.hasDiagram = hasDiagram;
    	}    	    	
    }
}