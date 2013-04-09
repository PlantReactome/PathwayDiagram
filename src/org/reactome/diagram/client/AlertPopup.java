/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This customized PopupPanel is used to hold an alert message for the user.
 * @author weiserj
 *
 */
public class AlertPopup extends PopupPanel {
    private Label alertLabel;
    
    public AlertPopup(String labelText) {
        super(true);
        alertLabel = new Label(labelText);        
        setWidget(alertLabel);
    }

	public String getAlertText() {
		return alertLabel.getText();
	}

	public void setAlertText(String alertText) {
		this.alertLabel.setText(alertText);
	}
}    