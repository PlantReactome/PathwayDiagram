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
public class AlertPopup {
    private static PopupPanel popUp;
    
    public static void alert(String alertText) {
    	init(alertText);
    	popUp.center();
    }
    
    private static void init(String labelText) { 
        popUp = new PopupPanel(false, true);
    	VerticalPanel vPane = new VerticalPanel();
        Label alertLabel = new Label(labelText);        
        Button button = new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popUp.hide();
			}
        	
        });
        
        vPane.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        vPane.add(alertLabel);
        vPane.add(button);
        
        setStyle();
        popUp.setWidget(vPane);
    }

	private static void setStyle() {
		Style style = popUp.getElement().getStyle();
		
		style.setZIndex(2);
		style.setBackgroundColor("rgb(255, 255, 255)");
		style.setBorderWidth(1, Style.Unit.PX);
		style.setBorderColor("rgb(0, 0, 0)");
	}
}    