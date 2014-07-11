/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized PopupPanel is used to hold an alert message for the user.
 * @author weiserj
 *
 */
public class AlertPopup {
	private static DialogBox existingAlert;
    private static HTML existingLabel;
    
    public static DialogBox alert(String alertText) {
    	DialogBox popUp = init(alertText);
    	popUp.center();
    	return popUp;
    }
    
    private static DialogBox init(final String labelText) {
    	if (existingAlert == null)  
    		existingAlert = new DialogBox(false, false);
    	
        if (existingLabel == null) 
        	existingLabel = new HTML(labelText);
    	else {
    		if (!existingLabel.getHTML().contains(labelText))
    			existingLabel.setHTML(existingLabel.getHTML() + "<br />" + labelText);
    	}
    
        Button button = new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				existingAlert.hide();
				existingAlert = null;
				existingLabel = null;
			}
        	
        });
        
        VerticalPanel vPane = new VerticalPanel();
        vPane.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        vPane.add(existingLabel);
        vPane.add(button);
        
        setStyle(existingAlert);
        existingAlert.setWidget(vPane);
        existingAlert.setText("Alert!");
        
        return existingAlert;
    }

	private static void setStyle(DialogBox popUp) {
		Style style = popUp.getElement().getStyle();
		
		style.setZIndex(30);
		style.setBackgroundColor("rgb(255, 255, 255)");
		style.setBorderWidth(1, Style.Unit.PX);
		style.setBorderColor("rgb(0, 0, 0)");
	}
}    