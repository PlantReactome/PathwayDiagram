/*

 * Created on Feb 22, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.ExpressionCanvas;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.model.ComplexNode;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class ComplexComponentPopup extends DialogBox {
    private ExpressionCanvas expressionCanvas;
	private ScrollPanel scrollPanel;
    private List<ComplexComponent> complexComponents;
	private FlexTable componentTable;
	
	private final Integer tableWidth = 300;
    
    public ComplexComponentPopup(ExpressionCanvas expressionCanvas) {
        super(true);
        this.expressionCanvas = expressionCanvas;
        addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					ComplexComponentPopup.this.expressionCanvas.setGreyOutCanvas(false);
				}       	
        });
        init();
    }
    
    private void init() {
    	this.scrollPanel = new ScrollPanel();
    	this.componentTable = new FlexTable();
    	
    	this.scrollPanel.add(this.componentTable);
        setWidget(this.scrollPanel);
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
    public void showPopup(ComplexNode selectedComplex) {
        hide();
        
        String complexName = selectedComplex.getDisplayName();
               	
        addComplexComponentsToFlexTable(selectedComplex);
        if (this.complexComponents == null || this.complexComponents.isEmpty()) {   	
        	AlertPopup.alert(complexName + " has no genome encoded components with data");        	
        	return;
        } 
        
        setText("Components for " + complexName + ": ");
        
        expressionCanvas.setGreyOutCanvas(true);
        bringToFront();
        center();
        setScrollPanelSize();
    }
    
    private void addComplexComponentsToFlexTable(ComplexNode complex) {
    	this.componentTable.removeAllRows();
    	this.componentTable.setBorderWidth(1);
    	
    	this.complexComponents = getComplexComponents(complex);
    	    	
    	for (int i = 0; i < this.complexComponents.size(); i++) {
    		ComplexComponent component = this.complexComponents.get(i);
    		
    		String label = component.getDisplayName();
    		
    		if (expressionCanvas.getAnalysisType() == AnalysisType.Expression && (component.getExpressionId() != null || component.getExpressionLevel() != null)) {		
    			label = label + " (";
    			
    			if (component.getExpressionId() != null) {
    				label = label + "ID: " + component.getExpressionId();
    			}
    			
    			if (component.getExpressionLevel() != null) {	
    				label = label + " Level: " + component.getExpressionLevel();
    			}
    				
    			label = label + ")";
    		}			   
    		Label componentName = new Label(label);
    		componentName.setWidth(tableWidth + "px");
    		componentName.getElement().getStyle().setBackgroundColor(component.getExpressionColor());
    		
    		this.componentTable.setWidget(i, 0, componentName);    		
    		this.componentTable.getFlexCellFormatter().setColSpan(i, 0, 1);
    		
    	}  
    }
    	
    private List<ComplexComponent> getComplexComponents(ComplexNode complex) {
    	List<ComplexComponent> complexComponents = new ArrayList<ComplexComponent>();
    	
    	for (ComplexNode.Component component : complex.getComponents()) {
    		ComplexComponent complexComponent = new ComplexComponent();
    		
    		Long refId = component.getRefEntityId();
    		if (refId == null)
    			continue;
    		    		
    		complexComponent.setDbId(refId);
    		complexComponent.setDisplayName(component.getDisplayName());
    		
    		String color = component.getExpressionColor();	
    		String expressionId = component.getExpressionId();
    		Double expressionLevel = component.getExpressionLevel();
    		
    		complexComponent.setExpressionId(expressionId);
    		complexComponent.setExpressionLevel(expressionLevel);
    		complexComponent.setExpressionColor(color);
    		
    		complexComponents.add(complexComponent);
    	}
    		
    		
    	
    	return complexComponents;
    }
    
    private void bringToFront() {
    	getElement().getStyle().setZIndex(2);
    }
    
    private void setScrollPanelSize() {    	
    	final Integer numberOfComponentsToShow = Math.min(complexComponents.size(), 10);
    	final Integer componentHeight = 25;
    	final Integer buffer = 5;
    	
    	Integer width = tableWidth + 40;
    	Integer height = numberOfComponentsToShow * componentHeight + buffer;
    	
    	scrollPanel.setPixelSize(width, height);
    	
    	setPixelSize(width, height);
    }
    
    private class ComplexComponent {
    	private Long dbId;
    	private String displayName;
    	private String expressionId;
    	private Double expressionLevel;
    	private String expressionColor;
    	
		public Long getDbId() {
			return dbId;
		}
		public void setDbId(Long dbId) {
			this.dbId = dbId;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public String getExpressionId() {
			return expressionId;
		}
		public void setExpressionId(String expressionId) {
			this.expressionId = expressionId;
		}
		public Double getExpressionLevel() {
			return expressionLevel;
		}
		public void setExpressionLevel(Double expressionLevel) {
			this.expressionLevel = expressionLevel;
		}
		public String getExpressionColor() {
			return expressionColor;
		}
		public void setExpressionColor(String expressionColor) {
			this.expressionColor = expressionColor;
		}
    }
}
