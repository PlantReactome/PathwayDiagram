/*
 * Created on Nov 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.model.ReactomeObject;


import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
    private org.reactome.diagram.model.Node selected;
    
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
    	retrievePMs(); 
    }

    // Participating molecules menu	
    private void retrievePMs() {
    	diagramPane.getController().getParticipatingMolecules(selected.getReactomeId());    	
    }
    
    // Set participating molecules menu
    public void setPMMenu(String xml) {
    	MenuBar pmMenu = new MenuBar(true);
    	pmMenu.setAutoOpen(true);
    	
    	try {
    		Document pmDom = XMLParser.parse(xml);
    		Element pmElement = pmDom.getDocumentElement();
    		XMLParser.removeWhitespace(pmElement);
    		
    		NodeList nodeList = pmElement.getChildNodes();
    		
    		for (int i = 0; i < nodeList.getLength(); i++) {
    			Node node = nodeList.item(i);
    			//String name = node.getNodeName();
    			
    			//if (name.equals("physicalEntity")) {
    				Element peElement = (Element) node;
    				
    				Node idNode = peElement.getElementsByTagName("dbId").item(0);
    				Long molId = Long.parseLong(idNode.getChildNodes().item(0).getNodeValue());
    				
    				Node nameNode = peElement.getElementsByTagName("displayName").item(0);
    				String molName = nameNode.getChildNodes().item(0).getNodeValue();
    				
    				pmMenu.addItem(molName, new Command() {
						@Override
						public void execute() {
							// TODO Auto-generated method stub
							hide();
						}    					
    				});    				
    			//}
    		}
    		
    		pmMenu.setStyleName(diagramPane.getStyle().subMenu());
    		pmMenu.setAnimationEnabled(true);
    		pmMenu.addSeparator(new MenuItemSeparator());
    		menuBar.addItem("Participating Molecules", pmMenu);
    		show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
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
   		}));
    	
    	
    	
    	menuBar.addItem(new MenuItem("Export Interactors", new Command() { 
    		@Override
    		public void execute() {
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
        selected = (org.reactome.diagram.model.Node) getSelectedObject();    	
        //Window.alert(selected.getDisplayName());
        
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
