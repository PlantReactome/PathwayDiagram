/*
 * Created on April 9, 2013
 *
 */
package org.reactome.diagram.client;

import java.util.HashMap;
import java.util.Map;

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
    private static Map<String, DialogBox> existingAlerts = new HashMap<String, DialogBox>();
    
    public static DialogBox alert(String alertText) {
    	DialogBox popUp = init(alertText);
    	popUp.center();
    	return popUp;
    }
    
    private static DialogBox init(final String labelText) { 
        if (existingAlerts.containsKey(labelText))
        	return existingAlerts.get(labelText);
    	
    	final DialogBox popUp = new DialogBox(false, false);
    	VerticalPanel vPane = new VerticalPanel();
        HTML alertLabel = new HTML(labelText);        
        Button button = new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popUp.hide();
				existingAlerts.remove(labelText);
			}
        	
        });
        
        vPane.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        vPane.add(alertLabel);
        vPane.add(button);
        
        setStyle(popUp);
        popUp.setWidget(vPane);
        popUp.setText("Alert!");
        
        existingAlerts.put(labelText, popUp);
        return popUp;
    }

	private static void setStyle(DialogBox popUp) {
		Style style = popUp.getElement().getStyle();
		
		style.setZIndex(30);
		style.setBackgroundColor("rgb(255, 255, 255)");
		style.setBorderWidth(1, Style.Unit.PX);
		style.setBorderColor("rgb(0, 0, 0)");
	}
}    