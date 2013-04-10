/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
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
        
        Resources resources = GWT.create(Resources.class);
        Style style = resources.alertPopupStyle();
        style.ensureInjected();
        
        setStyleName(style.alertPopup());
        
        vPane = new VerticalPanel();
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
	
	interface Resources extends ClientBundle {
		@Source(Style.DEFAULT_CSS)
		Style alertPopupStyle();
	}
	
	interface Style extends CssResource {
		String DEFAULT_CSS = "org/reactome/diagram/client/PathwayDiagram.css";
		
		String alertPopup();		
	}	
}    