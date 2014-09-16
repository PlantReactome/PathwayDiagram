/*
 * Created on July 16, 2014
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;import java.util.Map;

import org.reactome.diagram.client.WidgetStyle;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.model.CompositionalNode;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

/**
 * A class to manage the expression information pop-ups of complexes and sets.
 * @author weiserj
 *
 */
public class ComplexComponentPopupManager {  
    private Map<CompositionalNode, ComplexComponentPopup> complexComponentPopupMap;
    private ComplexComponentPopup activePopup; 
    private AnalysisType analysisType;

    public ComplexComponentPopupManager(AnalysisType analysisType) {
    	complexComponentPopupMap = new HashMap<CompositionalNode, ComplexComponentPopup>();
    	this.analysisType = analysisType;
    }
    
    public void showPopup(CompositionalNode complex) {
    	if (complexComponentPopupMap.get(complex) == null) {
    		complexComponentPopupMap.put(complex, new ComplexComponentPopup(complex, analysisType));
    		addMouseDownHandler(complexComponentPopupMap.get(complex));
    	}
    	complexComponentPopupMap.get(complex).showPopup();
    }
    
    public void updatePopups() {
    	for (ComplexComponentPopup popup : complexComponentPopupMap.values()) {
    		if (popup.isShowing())
    			popup.createTable();
    	}
    }
    
    public void removePopups() {
    	for (CompositionalNode complex : complexComponentPopupMap.keySet()) {
    		complexComponentPopupMap.get(complex).hide();
    	}
    	complexComponentPopupMap.clear();
    }
    
    private void addMouseDownHandler(final ComplexComponentPopup popup) {
    	popup.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				setActivePopup(popup);
			}
    	}, MouseDownEvent.getType());
    }
    
    private void setActivePopup(ComplexComponentPopup popup) {
    	if (activePopup == popup)
    		return;
    	
    	if (activePopup != null)
    		changeZIndexBy(activePopup, -1);
    	
    	changeZIndexBy(popup, 1);
    	activePopup = popup;
    }
    
    private void changeZIndexBy(ComplexComponentPopup popup, int change) {
    	int zIndex = Integer.parseInt(popup.getElement().getStyle().getZIndex());
    	WidgetStyle.setZIndex(popup, zIndex + change);
    }
}
