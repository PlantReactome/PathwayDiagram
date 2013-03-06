/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.model.ReactomeObject;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
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
public class CanvasPopupMenu extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    private MenuBar menuBar;
    private List<MenuItem> menuItems;
    private org.reactome.diagram.model.Node selected;
    
    public CanvasPopupMenu() {
        super(true);
        init();
    }
    
    private void init() {
        menuBar = new MenuBar(true);
        menuItems = new ArrayList<MenuItem>();
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
        menuItems.add(
        	menuBar.addItem(new MenuItem("Go to Pathway", new Command() {
        		@Override
        		public void execute() {
        			diagramPane.setPathway(selected.getReactomeId());
        			hide();
        		}
        	}))
        );
    }   
    
    // Complex Entity Menu
    private void createComplexMenu(boolean expressionData) {
    	createPhysicalEntityMenu();
    	retrievePMs(expressionData); 
    }

    // Participating molecules menu	
    private void retrievePMs(final boolean expressionData) { 
    	final PathwayDiagramController controller = diagramPane.getController();
    	    	
    	controller.getParticipatingMolecules(selected.getReactomeId(), new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					try {
						Document pmDom = XMLParser.parse(response.getText());
						Element pmElement = pmDom.getDocumentElement();
						XMLParser.removeWhitespace(pmElement);
						
						NodeList nodeList = pmElement.getChildNodes();
						
						List<ReactomeObject> molecules = new ArrayList<ReactomeObject>();
						for (int i = 0; i < nodeList.getLength(); i++) {
							Node node = nodeList.item(i);
							
							Element peElement = (Element) node;
							
							Node idNode = peElement.getElementsByTagName("dbId").item(0);
							Long molId = Long.parseLong(idNode.getChildNodes().item(0).getNodeValue());
							
							Node nameNode = peElement.getElementsByTagName("displayName").item(0);
							String molName = nameNode.getChildNodes().item(0).getNodeValue();
							
							ReactomeObject molecule = new ReactomeObject();
							molecule.setDisplayName(molName);
							molecule.setReactomeId(molId);
							molecules.add(molecule);
						}
						setPMMenu(molecules, expressionData);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					controller.requestFailed("Could not get participating molecules");					
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				controller.requestFailed(exception);
			}
    		
    	});    	
    }
    
    // Set participating molecules menu
    private void setPMMenu(final List<ReactomeObject> molecules, boolean expressionData) {
    	if (expressionData) {
    		menuItems.add(
    			menuBar.addItem("Display Participating Molecules", new Command() {
					@Override
					public void execute() {
						diagramPane.getExpressionComplexPopup().showPopup(selected, molecules);
						hide();							
					}    					
    			})
    		);
    	} else {
    		MenuBar pmMenu = new MenuBar(true);
    		pmMenu.setAutoOpen(true);
    				    				
    		for (ReactomeObject molecule : molecules) {			
    			pmMenu.addItem(molecule.getDisplayName(), new Command() {
    				@Override
    				public void execute() {
    					// TODO Auto-generated method stub
    					hide();
    				}    					
    			});    				    		
    		}	
    	
    		pmMenu.setStyleName(diagramPane.getStyle().subMenu());
    		pmMenu.setAnimationEnabled(true);
    		pmMenu.addSeparator(new MenuItemSeparator());
    		menuItems.add(
    				menuBar.addItem("Participating Molecules", pmMenu)
    		);
    	}	
   		show();
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
    	
    	menuItems.add(
    		menuBar.addItem(new MenuItem(action + " Interactors", new Command() {
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
    		}))
    	);
    	
    	
    	
    	menuBar.addItem(new MenuItem("Export Interactors", new Command() { 
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
    	diagramPane.getController().getOtherPathways(selected.getReactomeId());
    	
    	menuItems.add(
    		menuBar.addItem(new MenuItem("Retrieving other Pathways...", new Command() {
    			
    			@Override
    			public void execute() {
				
    			}
    	
    		}))
    	); 
    }
        
    public void setPathwayMenu(String xml) {
    	// Assumes the pathway menu item is the first menu item -- need to revisit this
    	MenuItem pathwayMenuItem = menuItems.get(0);
    	
    	MenuBar pathwaySubMenu = new MenuBar(true);
    	pathwaySubMenu.setAutoOpen(true);    	
    	
    	List<MenuItem> pathwaySubMenuItems = new ArrayList<MenuItem>();
    	final CanvasPathway currentPathway = diagramPane.getPathway();
    	
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
    				
    				if (pathway.getDisplayName() == null || pathway.getReactomeId() == null	
    					|| pathway.getReactomeId().longValue() == currentPathway.getReactomeId().longValue())
    					continue;
    				
    				pathwaySubMenuItems.add(pathwaySubMenu.addItem(pathway.getDisplayName(), new Command() {

    					@Override
    					public void execute() {
    						diagramPane.setPathway(pathway.getReactomeId());
    						hide();
    					}
    					
    				}));
    			}
    		}    			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	pathwaySubMenu.setStyleName(diagramPane.getStyle().subMenu());
    	pathwaySubMenu.setAnimationEnabled(true);
    	pathwaySubMenu.addSeparator(new MenuItemSeparator());
    	
    	menuBar.removeItem(pathwayMenuItem);
    	if (pathwaySubMenuItems.size() == 0) {
    		pathwayMenuItem = new MenuItem("No other pathways", new Command() {
    			public void execute() {
    				hide();
    			}
    		});
    	} else {
    		pathwayMenuItem = new MenuItem("Other Pathways", pathwaySubMenu);
    	}
    	menuBar.insertItem(pathwayMenuItem, 0);
    	show();
    }
    
    /**
     * Override to remove any popup menu.
     */
    @Override
    public void hide() {
        menuBar.clearItems();
        menuItems.clear();
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
        selected = (org.reactome.diagram.model.Node) getSelectedObject();    	
        
        GraphObjectType type = selected.getType();
        
        if (type == GraphObjectType.ProcessNode) {
            createProcessNodeMenu();            
        } else if (type == GraphObjectType.RenderableComplex) {
        	boolean expressionData;
        	
        	if (diagramPane.getExpressionCanvas().getPathway() == null) {
        		expressionData = false;
        	} else {
        		expressionData = true;
        	}
        	
        	createComplexMenu(expressionData);
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
