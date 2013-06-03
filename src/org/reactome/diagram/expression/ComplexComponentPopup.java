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
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ReactomeObject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized PopupPanel is used to hold a list of popup menu.
 * @author gwu
 *
 */
public class ComplexComponentPopup extends PopupPanel {
    private ExpressionCanvas expressionCanvas;
	private VerticalPanel vPanel;
    private List<ComplexComponent> complexComponents;
	private FlexTable componentTable;
	private Label complexLabel;
    
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
    	this.vPanel = new VerticalPanel();
    	this.componentTable = new FlexTable();
    	this.complexLabel = new Label();
    	
    	this.vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    	this.vPanel.add(this.complexLabel);
    	this.vPanel.add(this.componentTable);
        setWidget(this.vPanel);
    }
   
    private void setComplexLabel(String text) {
    	this.complexLabel.setText(text);
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
    public void showPopup(ComplexNode selectedComplex, List<ReactomeObject> components) {
        hide();
        
        String complexName = selectedComplex.getDisplayName();
               	
        addComplexComponentsToFlexTable(selectedComplex, components);
        if (this.complexComponents == null || this.complexComponents.isEmpty()) {   	
        	AlertPopup.alert(complexName + " has no genome encoded components with data");        	
        	return;
        } 
        
        setComplexLabel("Components for " + complexName + ": ");
        
        expressionCanvas.setGreyOutCanvas(true);
        center();
    }
    
    private void addComplexComponentsToFlexTable(ComplexNode complex, List<ReactomeObject> components) {
    	this.componentTable.removeAllRows();
    	this.componentTable.setBorderWidth(1);
    	
    	this.complexComponents = createComplexComponents(complex, components);
    	
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
    		componentName.getElement().getStyle().setBackgroundColor(component.getExpressionColor());
    		
    		this.componentTable.setWidget(i, 0, componentName);    		
    		this.componentTable.getFlexCellFormatter().setColSpan(i, 0, 1);
    	}    	
    }
    	
    private List<ComplexComponent> createComplexComponents(ComplexNode complex, List<ReactomeObject> components) {
    	List<ComplexComponent> complexComponents = new ArrayList<ComplexComponent>();
    	
    	ExpressionCanvasModel expressionCanvasModel = expressionCanvas.getExpressionCanvasModel();
    	for (ReactomeObject component : components) {
    		ComplexComponent complexComponent = new ComplexComponent();
    		
    		Long dbId = component.getReactomeId();
    		List<Long> refIds = expressionCanvasModel.getPhysicalToReferenceEntityMap().get(dbId);
    		if (refIds == null)
    			continue;
    		Long refId = refIds.get(0);
    		
    		complexComponent.setDbId(refId);
    		complexComponent.setDisplayName(component.getDisplayName());
    		
    		String color = complex.getComponent(refId).getExpressionColor();
    		
   // 		if (color == null)
  //  			continue;
    		
    		String expressionId = complex.getComponent(refId).getExpressionId();    		
    		Double expressionLevel = complex.getComponent(refId).getExpressionLevel();
    		
    		complexComponent.setExpressionId(expressionId);
    		complexComponent.setExpressionLevel(expressionLevel);
    		complexComponent.setExpressionColor(color);
    		
    		complexComponents.add(complexComponent);
    	}
    		
    		
    	
    	return complexComponents;
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
