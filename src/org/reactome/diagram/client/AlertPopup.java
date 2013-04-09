/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
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
    
    public AlertPopup(String labelText) {
        super();
        
        alertLabel = new Label(labelText);        
        button = new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
        	
        });
        
        vPane.add(alertLabel);
        vPane.add(button);
        
        setWidget(vPane);
    }

	public String getAlertText() {
		return alertLabel.getText();
	}

	public void setAlertText(String alertText) {
		this.alertLabel.setText(alertText);
	}
}    