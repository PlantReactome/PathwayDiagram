/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.event.ParticipatingMoleculeSelectionEvent;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;
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
public class NodeOptionsMenu extends MenuBar  {
    private PathwayDiagramPanel diagramPane;
    private org.reactome.diagram.model.Node selected;
    private Integer menuItemsBeingCreated; // Count of menu items waiting for a callback before they can be created
    
    public NodeOptionsMenu(PathwayDiagramPanel diagramPane) {
    	this(diagramPane, false);
    }
    
    public NodeOptionsMenu(PathwayDiagramPanel diagramPane, Boolean vertical) {
        super(vertical);
        this.diagramPane = diagramPane;
        init();
    }
    
    private void init() {
    	menuItemsBeingCreated = 0;
    	setAutoOpen(true);
    }
    
    // Pathway Node Menu
    private void createProcessNodeMenu() {
        addItem(new MenuItem("Go to Pathway", new Command() {
        	@Override
        	public void execute() {
        		diagramPane.setPathway(selected.getReactomeId());
        		hide();
        	}
        }));
    }   
    
    // Complex Entity Menu
    private void createComplexMenu(boolean expressionData) {
    	createPhysicalEntityMenu();
    	retrievePMs(expressionData); 
    }

    // Participating molecules menu	
    private void retrievePMs(final boolean expressionData) {
    	final PathwayDiagramController controller = diagramPane.getController();
    	
    	menuItemsBeingCreated += 1;
    	
    	controller.getParticipatingMolecules(selected.getReactomeId(), new RequestCallback() {
    		
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					try {
						Document pmDom = XMLParser.parse(response.getText());
						Element pmElement = pmDom.getDocumentElement();
						XMLParser.removeWhitespace(pmElement);
						
						NodeList nodeList = pmElement.getChildNodes();
						
						for (int i = 0; i < nodeList.getLength(); i++) {
							Node node = nodeList.item(i);
							
							Element peElement = (Element) node;
							
							Node idNode = peElement.getElementsByTagName("dbId").item(0);
							Long molId = Long.parseLong(idNode.getChildNodes().item(0).getNodeValue());
							
							Node nameNode = peElement.getElementsByTagName("displayName").item(0);
							String molName = nameNode.getChildNodes().item(0).getNodeValue();

							Node schemaClassNode = peElement.getElementsByTagName("schemaClass").item(0);
							String molSchemaClass = schemaClassNode.getChildNodes().item(0).getNodeValue();
							
							Component component;
							
							Long refId = null;
							Node refEntityNode = peElement.getElementsByTagName("referenceEntity").item(0);							
							if (refEntityNode != null) {
								Node refIdNode = ((Element) refEntityNode).getElementsByTagName("dbId").item(0);
								refId = Long.parseLong(refIdNode.getChildNodes().item(0).getNodeValue());
								component = ((ComplexNode) selected).addComponent(refId);
							} else {
								component = ((ComplexNode) selected).addComponentByDBId(molId);
							}
								
							component.setDisplayName(molName);
							component.setReactomeId(molId);
							component.setSchemaClass(molSchemaClass);
						}
						setPMMenu(expressionData);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					controller.requestFailed("Could not get participating molecules");					
				}
				
				menuItemsBeingCreated -= 1;
			}

			@Override
			public void onError(Request request, Throwable exception) {
				controller.requestFailed(exception);
				menuItemsBeingCreated -= 1;
			}
    		
    	});    	
    }
    
    // Set participating molecules menu
    private void setPMMenu(boolean expressionData) {
    	if (expressionData) {
   			addItem("Display Participating Molecules", new Command() {
				@Override
				public void execute() {
					diagramPane.getComplexComponentPopup().showPopup((ComplexNode) selected);
					hide();							
				}    					
   			});
    	} else {
    		MenuBar pmMenu = new MenuBar(true);
    		pmMenu.setAutoOpen(true);
    		pmMenu.setAnimationEnabled(true);
    		
    		addItemsToMenu(pmMenu, getParticipatingMoleculesMenuItems());
    		
    		styleSubMenu(pmMenu);
    		
    		addItem("Participating Molecules", pmMenu);
    	}
    }
    
    private List<MenuItem> getParticipatingMoleculesMenuItems() {
   		List<MenuItem> pmMenuItems = new ArrayList<MenuItem>();
   		
   		for (final Component component : ((ComplexNode) selected).getComponents()) {			
   			pmMenuItems.add(new MenuItem(
   				component.getDisplayName(), new Command() {
   				
   					@Override
   					public void execute() {
   						ParticipatingMoleculeSelectionEvent pmSelectionEvent = new ParticipatingMoleculeSelectionEvent();
   						pmSelectionEvent.setSelectedParticipatingMoleculeId(component.getReactomeId());
   						diagramPane.fireEvent(pmSelectionEvent);
   					
   						hide();
   					}    					
   				}
   			));
   		}	
    	
   		return pmMenuItems;
   	}	
         
    // Protein/RNA/DNA Entity Menu    
    private void createGEEMenu() {
    	createPhysicalEntityMenu();
    	
    	String action;
    	final ProteinNode pSelected = (ProteinNode) selected;
    	final boolean displaying = pSelected.isDisplayingInteractors(); 
    	if (displaying) {
    		action = "Hide";
    	} else {
    		action = "Display";
    	}
    	
    	
    	addItem(new MenuItem(action + " Interactors", new Command() {
    		@Override
    		public void execute() {
    			if (displaying) {
    				diagramPane.getInteractorCanvas().removeProtein(pSelected);
    				pSelected.setDisplayingInteractors(false);
    			} else {	
    				diagramPane.getController().getInteractors(pSelected);
    				pSelected.setDisplayingInteractors(true);
    			}
    			hide();
    		}	
    	}));
    	
    	
    	
    	addItem(new MenuItem("Export Interactors", new Command() { 
    		@Override
    		public void execute() {
    			diagramPane.getController().openInteractionExportPage(pSelected.getReactomeId());
    			hide();
    		}
    	}));
    }

    // Small Molecule Menu	
    private void createSmallMoleculeMenu() {
    	createPhysicalEntityMenu();
    }
    
    // Menu items common to all physical entities
    private void createPhysicalEntityMenu() {
   		MenuItem pathwayMenuItem = addItem(new MenuItem("Retrieving other Pathways...", new Command() {
    			
    			@Override
    			public void execute() {
				
    			}
    	
    	}));
    	
    	diagramPane.getController().getOtherPathways(selected.getReactomeId(), setPathwayMenu(pathwayMenuItem));
    }
        
    private RequestCallback setPathwayMenu(final MenuItem pathwayMenuItem) {
    	menuItemsBeingCreated += 1;
    	
    	RequestCallback setPathwayMenu = new RequestCallback() {
    		
    		public void onResponseReceived(Request request, Response response) {
    			if (response.getStatusCode() == 200) {    				
    				MenuBar pathwaySubMenu = new MenuBar(true);
    				pathwaySubMenu.setAutoOpen(true);
    				pathwaySubMenu.setAnimationEnabled(true);
    			
    				List<MenuItem> otherPathwayMenuItems = getOtherPathwayMenuItems(response.getText());
    				
    				addItemsToMenu(pathwaySubMenu, otherPathwayMenuItems);    				
    				addPathwayMenu(pathwayMenuItem, pathwaySubMenu, otherPathwayMenuItems.size());
    				
    				styleSubMenu(pathwaySubMenu);
    			} else {
    				diagramPane.getController().requestFailed("Could not retrieve other pathways");
    			}
    			
    			menuItemsBeingCreated -= 1;
    		}
    		
    		public void onError(Request request, Throwable exception) {
    			diagramPane.getController().requestFailed(exception);
    			menuItemsBeingCreated -= 1;
    		}
    	};
    	
    	return setPathwayMenu;
    }
    
    private void addItemsToMenu(MenuBar menu, List<MenuItem> items) {
    	for (MenuItem item : items) {
    		menu.addItem(item);
    		menu.addSeparator();
    	}
    }
    		
    private List<MenuItem> getOtherPathwayMenuItems(String xml) {
    	List<MenuItem> pathwaySubMenuItems = new ArrayList<MenuItem>();
    	final CanvasPathway currentPathway = diagramPane.getPathway();
    	List<String> processedPathways = new ArrayList<String>();
    	
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
    			
    				final ReactomeObject pathway = new ReactomeObject();
    			
    				for (int j = 0; j < pathwayNodes.getLength(); j++) {
    					Node pathwayAttribute = pathwayNodes.item(j);
    					String attributeName = pathwayAttribute.getNodeName();
    				
    					if (attributeName.equals("dbId")) {
    						Long pathwayId = Long.parseLong(pathwayAttribute.getChildNodes().item(0).getNodeValue());
    						pathway.setReactomeId(pathwayId);
    					} else if (attributeName.equals("displayName")) {	
    						String pathwayName = pathwayAttribute.getChildNodes().item(0).getNodeValue();
    						pathway.setDisplayName(pathwayName);
    					} else {
    						continue;
    					}
    				}
    				
    				if (pathway.getDisplayName() == null ||
    					pathway.getReactomeId() == null	||
    					pathway.getReactomeId().longValue() == currentPathway.getReactomeId().longValue() ||
    					processedPathways.contains(pathway))
    					continue;
    				
    				MenuItem pathwaySubMenuItem = new MenuItem(pathway.getDisplayName(), new Command() {

    					@Override
   						public void execute() {
   							diagramPane.setPathway(pathway.getReactomeId());
    						hide();
    					}
    					
    				});
    				
    				pathwaySubMenuItems.add(pathwaySubMenuItem);
    				processedPathways.add(pathway.getDisplayName());
    			}
    		}    			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return pathwaySubMenuItems;
    }
            	
    private void addPathwayMenu(MenuItem oldPathwayMenuItem, MenuBar pathwaySubMenu, int numberOfOtherPathways) {	
    	Integer index = getItemIndex(oldPathwayMenuItem);
    	
    	removeItem(oldPathwayMenuItem);
    	
    	MenuItem newPathwayMenuItem;
    	if (numberOfOtherPathways == 0) {
    		newPathwayMenuItem = new MenuItem("No other pathways", new Command() {
    			public void execute() {
    				hide();
    			}
    		});
    	} else {
    		newPathwayMenuItem = new MenuItem("Other Pathways", pathwaySubMenu);
    	}
    	
    	insertItem(newPathwayMenuItem, index);
    }

    public void createMenu(org.reactome.diagram.model.Node selectedNode) {
        
    	// Prevents re-creating a menu that is part-way through construction
    	if (menuItemsBeingCreated > 0)
    		return;
    	
    	clearItems();
    	
    	selected = selectedNode;
        
        GraphObjectType type = selected.getType();
        
        if (type == GraphObjectType.ProcessNode) {
            createProcessNodeMenu();
        } else if (type == GraphObjectType.RenderableComplex) {
            boolean expressionData = !(
                    diagramPane.getExpressionCanvas() == null ||
                    diagramPane.getExpressionCanvas().getPathway() == null
            );
        	createComplexMenu(expressionData);
        } else if (type == GraphObjectType.RenderableProtein) {
        	createGEEMenu();
        } else if (type == GraphObjectType.RenderableChemical) {
        	createSmallMoleculeMenu();
        }
    }
    
    private void hide() {
    	if (parentIsPopupPanel()) 
    		((PopupPanel) getParent()).hide();
    }
    
    private Boolean parentIsPopupPanel() {
    	return getParent() != null && getParent() instanceof PopupPanel;
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
}
