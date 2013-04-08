/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import java.util.HashMap;

import org.reactome.gwt.client.FormUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Creates a panel for a search box with associated button.
 * 
 * @author David Croft, Antonio Fabregat Mundo
 */
public class SearchBarPanel extends HorizontalPanel {
    private Panel panel = this; // Needed by anonymous classes
    private TextBox queryBox = new TextBox();
    private PushButton searchButton = null;
	// These are the parameters that will be sent as "hidden" by the
	// POST request.
    private HashMap<String, String> params = new HashMap<String, String>();
    FlexTable flextable = null;

	public SearchBarPanel() {
		super();
		
		setStyleName("menu_bar");
		
		setStylePrimaryName("textbox"); // CSS
		
		queryBox.setTitle("write your query here");
		DOM.setStyleAttribute(queryBox.getElement(), "verticalAlign", "top");
		DOM.setStyleAttribute(queryBox.getElement(), "paddingLeft", "5px");
		DOM.setStyleAttribute(queryBox.getElement(), "paddingRight", "5px");
		
		Image searchImage = new Image("images/search.gif");
		searchImage.setPixelSize(15, 15);
		searchButton = new PushButton(searchImage);
		searchButton.setTitle("Search");
		DOM.setStyleAttribute(searchButton.getElement(), "cursor", "hand");

	    FlexTable flextable = new FlexTable();
	    flextable.setWidget(0, 0, queryBox);
	    flextable.getCellFormatter().getElement(0, 0).setAttribute("width", "130px");
	    flextable.setWidget(0, 1, searchButton);
	    flextable.getCellFormatter().getElement(0, 1).setAttribute("width", "15px");
	    add(flextable);
	    	    	    
		String db = getReactomeDb();
		if (db != null)
			params.put("DB", db);
		// Instruct the search mechanism to get all instance
		// types, rather than restricting them to, say pathways.
		params.put("OPERATOR", "ALL");
		// Sending an empty SPECIES value means "All species".
		// Sending no SPECIES parameter at all theoretically means "default
		// species", which is Homo sapiens for the main Reactome
		// website.
//		params.put("SPECIES", "");
		params.put("SPECIES", "48887"); // TODO: we need to get species from calling URL
				
		// Listen for keyboard events in the input box.
		queryBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					String value = queryBox.getValue();
					if (value.length() > 0 && !(value.matches("^ +$")) && !(value.matches("^[ \t\n]*$"))) {
						// Add the query to the form immediately before
						// submitting it, since we can't know when we
						// construct the form what query a user is going
						// to enter.
						params.put("QUERY", value);
						FormUtils.formCreator(panel, "/cgi-bin/search2", params, null, null, FormPanel.METHOD_POST, true).submit();
					}
				}
			}
		});
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String value = queryBox.getValue();
				if (value.length() > 0 && !(value.matches("^ +$")) && !(value.matches("^[ \t\n]*$"))) {
					// Add the query to the form immediately before
					// submitting it, since we can't know when we
					// construct the form what query a user is going
					// to enter.
					params.put("QUERY", value);
					FormUtils.formCreator(panel, "/cgi-bin/search2", params, null, null, FormPanel.METHOD_POST, true).submit();
				}
			}
		});
	}
	
	/**
	 * Gets the name of the database currently being accessed by the user.
	 */
	public static String getReactomeDb() {
		String requestURL = Window.Location.getHref();
		String reactomeDb = null;
		
		// First try to get the information from the request URL
		if (requestURL != null) {
			String[] parts = requestURL.split("\\?");
			if (parts.length == 2) {
				String[] pairs = parts[1].split("&");
				for (String pair: pairs) {
					String[] keyVal = pair.split("=");
					if (keyVal[0].equals("DB")) {
						reactomeDb = keyVal[1];
						break;
					}
				}
			}
		}
		
		return reactomeDb;
	}
	
	public void setWidth(String width) {
		if (flextable != null)
			flextable.getCellFormatter().getElement(0, 0).setAttribute("width", width);
	}
}
