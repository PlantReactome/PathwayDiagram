/*

 * Created on Feb 22, 2013
 *
 */
package org.reactome.diagram.expression;

import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.ExpressionCanvas;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.ComplexNode.Component;

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
	private FlexTable componentTable;
    
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
        
        createTable(selectedComplex);
        if (tableIsEmpty()) {   	
        	AlertPopup.alert(selectedComplex.getDisplayName() + " has no genome encoded components with data");        	
        	return;
        } 
        
        setText("Components for " + selectedComplex.getDisplayName() + ": ");
        
        expressionCanvas.setGreyOutCanvas(true);
        bringToFront();
        center();
        setScrollPanelSize();
    }
    
    private boolean tableIsEmpty() {		
		return componentTable.getRowCount() == 0;
	}

	private void createTable(ComplexNode complex) {
    	this.componentTable.removeAllRows();
    	this.componentTable.setBorderWidth(1);
    	        	    
    	addHeader();
    	
    	for (Component component : complex.getComponents()) {     		
    		if (component.getRefEntityId() != null)
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
    	
    	addLabel(component.getDisplayName(), rowIndex, 0, bgColor);
    		
    	if (isExpressionAnalysis()) {    		   			
    		addLabel(getText(component.getExpressionId()), rowIndex, 1, bgColor);
    		addLabel(getText(component.getExpressionLevel()), rowIndex, 2, bgColor);
    	}		
    }
       
    private Boolean isExpressionAnalysis() {
    	return expressionCanvas.getAnalysisType() == AnalysisType.Expression;
    }
	
	private String getText(Object object) {
		return ((object != null) ? object.toString() : "N/A");
	}
    
	private Label addLabel(String text, Integer row, Integer column, String bgColor) {
		final String BLACK = "rgb(0, 0, 0)";
		
		return addLabel(text, row, column, bgColor, BLACK);				
	}
	
    private Label addLabel(String text, Integer row, Integer column, String bgColor, String textColor) {
    	Label label = new Label(text);

    	label.setWordWrap(false);
    	label.getElement().getStyle().setColor(textColor);
    	label.getElement().getStyle().setBackgroundColor(bgColor);    	
    	this.componentTable.setWidget(row, column, label);
    	
    	return label;
    }
    
    private void bringToFront() {
    	getElement().getStyle().setZIndex(2);
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
