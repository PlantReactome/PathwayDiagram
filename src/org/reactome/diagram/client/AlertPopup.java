/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized PopupPanel is used to hold an alert message for the user.
 * @author weiserj
 *
 */
public class AlertPopup extends PopupPanel {
    private VerticalPanel vPane;
	private Button button;
    private Label alertLabel;
    
    public AlertPopup(String labelText, Boolean hide) {	
   		this(labelText);
   		if (hide)
   			hide();
    }
    
    public AlertPopup(String labelText) {
        super();
        init(labelText);
        center();
    }
    
    private void init(String labelText) { 
        vPane = new VerticalPanel();
        alertLabel = new Label(labelText);        
        button = new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
        	
        });
        
        vPane.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        vPane.add(alertLabel);
        vPane.add(button);
        
        setStyle();
        setWidget(vPane);
    }

	public String getAlertText() {
		return alertLabel.getText();
	}

	public void setAlertText(String alertText) {
		this.alertLabel.setText(alertText);
	}	
	
	private void setStyle() {
		Style style = getElement().getStyle();
		
		style.setZIndex(2);
		style.setBackgroundColor("rgb(255, 255, 255)");
		style.setBorderWidth(1, Style.Unit.PX);
		style.setBorderColor("rgb(0, 0, 0)");
	}
}    