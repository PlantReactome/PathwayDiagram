/*

 * Created on Feb 22, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.List;
import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.PathwayDiagramController;
import org.reactome.diagram.client.WidgetStyle;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;
import org.reactome.diagram.model.ReactomeXMLParser;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class ComplexComponentPopup extends DialogBox {
    private ComplexNode complexNode;
    private AnalysisType analysisType;
	private VerticalPanel verticalPanel;
	private ScrollPanel scrollPanel;
	private FlexTable componentTable;
	private Button closeButton;

    public ComplexComponentPopup(ComplexNode complexNode, AnalysisType analysisType) {
        super(false, false);
        this.complexNode = complexNode;
        this.analysisType = analysisType;
        init();
    }
    
    private void init() {
    	this.verticalPanel = new VerticalPanel();
    	
    	this.scrollPanel = new ScrollPanel();
    	this.componentTable = new FlexTable();
    	this.scrollPanel.add(this.componentTable);
    	
    	this.closeButton = new Button("Close");
    	this.closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
    		
    	});
    	
    	this.verticalPanel.add(scrollPanel);
    	this.verticalPanel.add(closeButton);
    	this.verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        setWidget(this.verticalPanel);
        
        WidgetStyle.setZIndex(this, 2);
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
     * @param panel
     */
    public void showPopup() {
        String componentsWithoutDisplayNames = getRefIdsForComponentsWithNoDisplayName();
        
        if (!componentsWithoutDisplayNames.isEmpty()) {
        	PathwayDiagramController.getInstance().queryByIds(componentsWithoutDisplayNames, "ReferenceEntity", setDisplayNamesAndMakePopup());
        } else {
        	makePopup();
        }
     }
    
    /*
     * Returns a comma delimited string of complex component reference ids lacking a display name
     */
    private String getRefIdsForComponentsWithNoDisplayName() {
    	final String delimiter = ",";
    	
    	StringBuffer refIds = new StringBuffer();
    	
    	for (Component component : complexNode.getComponents()) {
    		if (component.getDisplayName() == null || component.getDisplayName().isEmpty()) {
    			refIds.append(component.getRefEntityId());
    			refIds.append(delimiter);
    		}
    	}
    	
    	if (refIds.toString().endsWith(delimiter)) 
    		refIds.deleteCharAt(refIds.lastIndexOf(delimiter));
    	
    	return refIds.toString();
    }
    
    private RequestCallback setDisplayNamesAndMakePopup() {
    	RequestCallback setDisplayNamesAndMakePopup = new RequestCallback() { 
    		final private String ERROR_MSG = "Could not get display names for all components ";
    		
    		
    		public void onResponseReceived(Request request,	Response response) {
    			if (response.getStatusCode() != 200) {
    				AlertPopup.alert(ERROR_MSG + response.getStatusCode() + ": " + response.getStatusText());
    				return;
    			}
    			
    			setDisplayNamesForComponents(response.getText());
    			makePopup();
    		}
    		
    		public void onError(Request request, Throwable exception) {
    			AlertPopup.alert(ERROR_MSG);
    		}
    	};
    	
    	return setDisplayNamesAndMakePopup;
    }			
    
    private void setDisplayNamesForComponents(String componentXML) {
    	ReactomeXMLParser complexComponentParser = new ReactomeXMLParser(componentXML);
    	
    	Element complexComponentElement = ((ReactomeXMLParser) complexComponentParser).getDocumentElement();
    	
    	NodeList componentReferenceEntities = complexComponentElement.getChildNodes();
    	for (int i = 0; i < componentReferenceEntities.getLength(); i++) {
    		Element componentEntity = (Element) componentReferenceEntities.item(i);
    		
    		String dbId = complexComponentParser.getXMLNodeValue(componentEntity, "dbId");
    		if (dbId != null) {
    			List<Component> components = complexNode.getComponentsByRefId(Long.parseLong(dbId));
    			
    			for (Component component : components) {
    				if (component != null) {
    					String name = complexComponentParser.getXMLNodeValue(componentEntity, "name");
    					component.setDisplayName(name);
    				}
    			}
    		}
    	}    	
    }
    
    private void makePopup() {	
   		createTable();
    	if (tableIsEmpty()) {   	
    		AlertPopup.alert(complexNode.getDisplayName() + " has no genome encoded components with data");        	
    		return;
    	} 
        
    	setText("Components for " + complexNode.getDisplayName() + ": ");
        
    	if (isShowing() == false)
    		center();
    	setScrollPanelSize();    		     	
    }
        
    private boolean tableIsEmpty() {
    	Integer numberOfComponents = componentTable.getRowCount() - 1; // Subtract one row for the header
		
    	return numberOfComponents == 0;
	}

	public void createTable() {
    	this.componentTable.removeAllRows();
    	this.componentTable.setBorderWidth(1);
    	
    	addHeader();
    	
    	for (Component component : complexNode.getComponents()) {
    		addRow(component);
    	}
    }	
    		
    private void addHeader() {
    	final String BLUE = "rgb(0, 0, 255)";
    	final String WHITE = "rgb(255, 255, 255)";
    	
    	addLabel("Component Name", 0, 0, WHITE, BLUE);
    	
    	if (isExpressionAnalysis()) {
    		addLabel("Expression ID", 0, 1, WHITE, BLUE);
    		addLabel("Expression Level", 0, 2, WHITE, BLUE);
    	}
    }
    	
	private void addRow(Component component) {		
		final Integer rowIndex = componentTable.getRowCount();	
    	final String bgColor = component.getExpressionColor();

    	String label = (component.getDisplayName() != null && !component.getDisplayName().isEmpty()) ? 
    					component.getDisplayName() : component.getRefEntityId().toString();
    	
    	addLabel(label, rowIndex, 0, bgColor);
    		
    	if (isExpressionAnalysis()) {    		   			
    		addLabel(getText(component.getExpressionIdentifiers()), rowIndex, 1, bgColor);
    		addLabel(getText(component.getExpressionLevel()), rowIndex, 2, bgColor);
    	}		
    }
	
	private boolean isExpressionAnalysis() {
		return analysisType == AnalysisType.Expression;
	}
	
	private String getText(Object object) {
		return ((object != null) ? object.toString() : "N/A");
	}
    
	private Label addLabel(String text, Integer row, Integer column, String bgColor) {		
		return addLabel(text, row, column, bgColor, complexNode.getVisibleFgColor(bgColor));
	}
	
    private Label addLabel(String text, Integer row, Integer column, String bgColor, String textColor) {
    	Label label = new Label(text);

    	label.setWordWrap(false);
    	label.getElement().getStyle().setColor(textColor);
    	label.getElement().getStyle().setBackgroundColor(bgColor);    	
    	this.componentTable.setWidget(row, column, label);
    	
    	return label;
    }
    
    private void setScrollPanelSize() {
    	final Integer COLUMNBUFFER = 17;
    	
    	Integer width = componentTable.getOffsetWidth() + COLUMNBUFFER;
    	Integer height = getHeightOfFirstTenComponents();
    	
    	scrollPanel.setPixelSize(width, height);
    	
    	setPixelSize(width, height);
    }

	private Integer getHeightOfFirstTenComponents() {
		final Integer ROWBUFFER = 8;
		
		Integer height = 0;		
		for (Integer rowIndex = 0; rowIndex < numberOfRows(); rowIndex++) {
			Integer rowHeight = componentTable.getWidget(rowIndex, 0).getOffsetHeight() + ROWBUFFER;
			height += rowHeight;
		}
		
		return height;
	}
    
	private Integer numberOfRows() {
		return Math.min(10, componentTable.getRowCount());
	}
    
}
