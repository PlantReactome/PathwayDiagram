/*
 * Created on June 28, 2013
 *
 */
package org.reactome.diagram.client;

import org.reactome.diagram.model.GraphObject;

import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This customized PopupPanel is used to hold an information box describing a selected graph object.
 * @author gwu
 *
 */
public class InfoPopup extends PopupPanel {
    private GraphObject selectedObject;
    
    private HeaderPanel container;
    private Label header;
    private Panel informationPanel;
    private MenuBar menuBar;
    
    
    public InfoPopup(GraphObject selectedObject) {
        super(true);
        this.selectedObject = selectedObject;
        init();
    }
    
    private void init() {
    	container = new HeaderPanel();

    	header = getObjectTypeLabel();
    	
    	
    	container.setHeaderWidget(header);
    	container.setContentWidget(informationPanel);
    	container.setFooterWidget(menuBar);
    	
    	setWidget(container);
    	//menuBar = new CanvasPopupMenu();
    }  
    
    
    private Label getObjectTypeLabel() {
    	Label objectType = new Label(selectedObject.getObjectType());
    	
    	return objectType;
    }
}
