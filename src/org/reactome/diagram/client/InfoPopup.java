/*
 * Created on June 28, 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.ProteinNode;
import org.reactome.diagram.model.ReactomeXMLParser;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This customized PopupPanel is used to hold an information box describing a selected graph object.
 * @author gwu
 *
 */
public class InfoPopup extends PopupPanel {
    private PathwayDiagramPanel diagramPane;
    private ExpressionCanvas expressionCanvas;
	private GraphObject selectedObject;
    
    private VerticalPanel container;
    private HorizontalPanel header;
    private FlexTable informationPanel;
    private NodeOptionsFooterMenu menuBar;
    
    
    public InfoPopup(PathwayDiagramPanel diagramPane) {
        super(true);
        this.diagramPane = diagramPane;
    }
    
    public void showPopup(GraphObject selected) {
    	selectedObject = selected;
    	makePopupForSelectedObject(selected);
    	center();
    }
    
    private void makePopupForSelectedObject(GraphObject selected) {
    	container = new VerticalPanel();

    	setObjectTypeAsHeader();
    	setInformationPanelAsContent();

    	setWidget(container);
    	
    	getElement().getStyle().setZIndex(2);
    }  
    
    
    private void setObjectTypeAsHeader() {
    	header = new HorizontalPanel();
    	
    	header.add(getObjectTypeLabel());
    	header.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    	
    	container.add(header);
    }
    
    private Label getObjectTypeLabel() {
    	Label objectTypeLabel = new Label(objectType());
    
    	Style labelStyle = objectTypeLabel.getElement().getStyle();
    	labelStyle.setTextDecoration(TextDecoration.UNDERLINE);
    	labelStyle.setFontSize(18, Unit.PX);
    	
    	return objectTypeLabel;
    }
    
    private void setInformationPanelAsContent() {
    	informationPanel = new FlexTable();
 
    	informationPanel.setText(0, 0, "Name");
    	informationPanel.setWidget(0, 1, getNameAnchor());
    	
    	informationPanel.setText(1, 0, "Reference ID");
    	setExternalIdWidget();
    	
    	if (expressionDataAvailable() && selectedObject instanceof ProteinNode) {
    		informationPanel.setText(2, 0, "Expression Data");
    		informationPanel.getFlexCellFormatter().setRowSpan(2, 0, 2);
    		informationPanel.setText(2, 1, getProbeExpressionIdText());
    		informationPanel.setText(3, 0, getProbeExpressionValueText());    		
    	}
    	
    	informationPanel.setBorderWidth(1);
    	
    	container.add(informationPanel);
    	setMenuBarAsFooter();
    }
    
    private Anchor getNameAnchor() {
    	Anchor nameAnchor = new Anchor(selectedObject.getDisplayName(), getUrlForSelectedObject());    	    	
    	nameAnchor.setWordWrap(true);
    	nameAnchor.setTarget("_blank");
    	
    	return nameAnchor;
    }
    
    private String getUrlForSelectedObject() {
    	String cgiScript;
    	Long identifier;
    	
    	if (selectedObjectHasReferenceEntity()) {      	
    	 	cgiScript = "eventbrowser";
    	 	identifier = diagramPane.getPathway()
    	 							.getDbIdToRefEntityId()
    	 							.get(selectedObject.getReactomeId())
    	 							.get(0)
    	 							.getDbId();
    	} else {    		
    		cgiScript = "instancebrowser";
    		identifier = selectedObject.getReactomeId();
    	}	
    
    	return "http://www.reactome.org/cgi-bin/" + cgiScript + "?DB=gk_current&ID=" + identifier;
    }
    
    private Boolean selectedObjectHasReferenceEntity() {
    	switch (selectedObject.getType()) {
    		case RenderableProtein: 
    		case RenderableChemical: 
    		case RenderableGene:
    		case RenderableRNA:
    			return true;
    			
    		default:
    			return false;
    	}
    }
    
    private void setExternalIdWidget() {
    	if (selectedObjectHasReferenceEntity())
    		diagramPane.getController().getReferenceEntity(selectedObject.getReactomeId(), addIdWidgetToInformationPanel());
    	else {
    		informationPanel.setWidget(1, 1, getExternalIdWidget(null));
    	}
    }
    
    private RequestCallback addIdWidgetToInformationPanel() {
    	RequestCallback addIdWidgetToInformationPanel = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {					
					Widget externalIdWidget = getExternalIdWidget(getIdentifier(response.getText()));				
					informationPanel.setWidget(1, 1, externalIdWidget);
				} else {
					diagramPane.getController().requestFailed("Unable to retrieve reference entity");
				}
				setMenuBarAsFooter();
			}

			@Override
			public void onError(Request request, Throwable exception) {
				diagramPane.getController().requestFailed(exception);				
				setMenuBarAsFooter();
			}
    		
    	};
    	
    	return addIdWidgetToInformationPanel;
    }
    
    private String getIdentifier(String xml) {
    	ReactomeXMLParser referenceEntityXmlParser = new ReactomeXMLParser(xml);
  
    	return referenceEntityXmlParser.getXMLNodeValue("displayName");
    }
    
    private Widget getExternalIdWidget(String identifier) {
    	if (identifier == null)
    		return new Label("N/A");
    	else
    		return new Label(identifier);
    }
    
    private Boolean expressionDataAvailable() {
    	expressionCanvas = diagramPane.getExpressionCanvas();
    	
    	return expressionCanvas != null && expressionCanvas.getPathway() != null;
    }
    
    private String getProbeExpressionIdText() {
    	final Long proteinReferenceId = diagramPane.getPathway().getReferenceIdForProtein(selectedObject.getReactomeId());
    	
    	String probeId = expressionCanvas.getExpressionInfo(proteinReferenceId).getIdentifiersAsString();
    	
    	if (probeId == null)
    		probeId = "N/A";
    	    		
    	return "Probe Id: " + probeId;
    }
    
    private String getProbeExpressionValueText() {
    	final Long proteinReferenceId = diagramPane.getPathway().getReferenceIdForProtein(selectedObject.getReactomeId());
    	
    	Double expressionValue = expressionCanvas.getEntityExpressionLevel(proteinReferenceId);
    	
    	return "Probe Expression Level: " + (expressionValue == null ? "N/A" : expressionValue);
    }
    
    private void setMenuBarAsFooter() {
    	if (selectedObject instanceof Node) {
    		if (menuBar != null) {
    			container.remove(menuBar.getMenuContainer());
    		}
    		
    		menuBar = new NodeOptionsFooterMenu(diagramPane);
    		menuBar.createMenu((Node) selectedObject);
    		//styleMenu();
    		container.add(menuBar.getMenuContainer());
    		center();
    	}
    }
    
    private void styleMenu() {
    	//if (menuBar.getOffsetWidth() > informationPanel.getOffsetWidth())
    	    	
    	int menuBarWidth = informationPanel.getOffsetWidth();
    	
    	menuBar.getMenuContainer().setWidth(menuBarWidth + "px");
    	styleMenuItems(menuBarWidth);
    }
    
    private void styleMenuItems(int menuBarWidth) {    
    	Double menuItemWidth = (double) menuBarWidth / menuBar.getMenuButtons().size();
    	
    	for (Button menuButton : menuBar.getMenuButtons()) {
    		Style buttonStyle = menuButton.getElement().getStyle();
    		
    		buttonStyle.setFontSize(12, Unit.PX);
    		menuButton.setWidth(menuItemWidth + "px");
    	}
   
    }
    
    private String objectType() {
    	return selectedObject.getObjectType();
    }
    
    protected class NodeOptionsFooterMenu extends NodeOptionsMenu {
    	private HorizontalPanel menuContainer;
    	private List<Button> menuButtons;
    	private Map<Integer, Button> menuButtonLookup;
    	
    	public NodeOptionsFooterMenu(PathwayDiagramPanel diagramPane) {
    		super(diagramPane);
    		
    		menuContainer = new HorizontalPanel();
    		menuButtons = new ArrayList<Button>();
    		menuButtonLookup = new HashMap<Integer, Button>(); 
    	}
    	
    	public HorizontalPanel getMenuContainer() {
    		return menuContainer;
    	}
    	
    	public List<Button> getMenuButtons() {
    		return menuButtons;
    	}
    	
    	public MenuOption addItem(String label, Command command) {
    		return addItem(createItem(label, command));
    	}
    	
    	public MenuOption addItem(String label, MenuBar subMenu) {
    		return addItem(createItem(label, subMenu));
    	}
    	
    	private MenuOption addItem(MenuOption menuOption) {
    		Button menuButton = getMenuButton(menuOption);
    		
    		menuContainer.add(menuButton);
    		menuButtons.add(menuButton);
    		menuButtonLookup.put(menuOption.getId(), menuButton);
    	
    		return menuOption;
    	}
    	
    	public Widget getParent() {
    		return menuContainer.getParent();
    	}
    	
    	public void enableItem(MenuOption menuOption, Boolean enable) {
    		Button menuButton = menuButtonLookup.get(menuOption.getId());
    		
    		if (menuButton != null)
    			menuButton.setEnabled(enable);
    	}
    	
    	public Integer getItemIndex(MenuOption menuOption) {
    		return menuContainer.getWidgetIndex(menuButtonLookup.get(menuOption.getId()));
    	}
    	
    	public void removeItem(MenuOption menuOption) {
    		Button menuButton = menuButtonLookup.get(menuOption.getId());
    		
    		menuContainer.remove(menuButton);
    		menuButtons.remove(menuButton);
    		menuButtonLookup.remove(menuButton);
    	}
    	
    	public void insertItem(MenuOption menuOption, Integer index) {
    		Button button = getMenuButton(menuOption);
    		
    		menuContainer.insert(button, index);
    		menuButtons.add(index, button);
    		menuButtonLookup.put(menuOption.getId(), button);
    	}
    	
    	public void clearItems() {
    		menuContainer.clear();
    		menuButtons.clear();
    		menuButtonLookup.clear();
    	}
    	
    	public void hideIfWithinPopupPanel() {
    		super.hideIfWithinPopupPanel(menuContainer);
    	}
    	
    	private Button getMenuButton(final MenuOption menuOption) {
    		final Button menuButton = new Button(menuOption.getLabel());
    		menuButton.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
    		
    		if (menuOption.getCommand() != null) {
    			menuButton.addClickHandler(new ClickHandler() {
    			
    				public void onClick(ClickEvent event) {    				
    					menuOption.getCommand().execute();   				
    				}
    			});
    		} else {
    			menuButton.setText(menuButton.getText() + " \u25BC"); // Downward arrow added to indicate sub-menu
    			
    			addMenuButtonMouseHandlers(menuButton, createSubMenuPopup(menuOption));
    		}
    		
    		return menuButton;
    	}
    	
    	private void addMenuButtonMouseHandlers(final Button menuButton, final PopupPanel subMenuPopup) {
    		menuButton.addDomHandler(new MouseOverHandler() {
    		
    			public void onMouseOver(MouseOverEvent event) {
    				subMenuPopup.setPopupPosition(menuButton.getAbsoluteLeft(), 
    											  menuButton.getAbsoluteTop() + menuButton.getOffsetHeight());
    				subMenuPopup.show();
    			}
    			
    		}, MouseOverEvent.getType());
    			
    		menuButton.addDomHandler(new MouseOutHandler() {
    
    			public void onMouseOut(MouseOutEvent event) {
    				if (subMenuPopup.isShowing() && outsideSubMenu(event))
    					subMenuPopup.hide();
    			}
    			
    			private Boolean outsideSubMenu(MouseOutEvent event) {
    				final Integer mouseX = event.getClientX();
    				final Integer mouseY = event.getClientY();
    				
    				final Integer menuLeft = subMenuPopup.getAbsoluteLeft();
    				final Integer menuRight = menuLeft + subMenuPopup.getOffsetWidth();
    				final Integer menuTop = subMenuPopup.getAbsoluteTop() - 5;
    				final Integer menuBottom = menuTop + subMenuPopup.getOffsetHeight();

    				//System.out.println("Item width: " );
    				System.out.println("Mouse X: " + mouseX + " Boundaries: " + menuLeft + " " + menuRight);
    				System.out.println("Mouse Y: " + mouseY + " Boundaries: " + menuTop + " " + menuBottom);
    				
    				return (mouseX < menuLeft || mouseX > menuRight || mouseY < menuTop || mouseY > menuBottom);
    			}
    				
    		}, MouseOutEvent.getType());    		
    	}
    	
    	private PopupPanel createSubMenuPopup(MenuOption menuOption) {
    		PopupPanel subMenuPopup = new PopupPanel(true);
    		subMenuPopup.setWidget(menuOption.getSubMenu());    		
    		return subMenuPopup;
    	}
    }
}
