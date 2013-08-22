/* 
 * Created on August 22, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.touch.client.Point;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * This class is used to create a clickable diagram options menu icon.
 * @author weiserj
 *
 */
public class OptionsMenuIcon extends Image {   
	private Resources resources;
	private PathwayDiagramPanel diagramPane;
	
	public OptionsMenuIcon(PathwayDiagramPanel diagramPane) {
		this.diagramPane = diagramPane;
		resources = GWT.create(Resources.class);
		setResource(resources.optionsIcon());
		
		addClickHandler();
		addMouseOverHandler();
	}
	
	private void addClickHandler() {   
    	addClickHandler(new ClickHandler() {
        	
        	public void onClick(ClickEvent event) {
        		final OptionsMenu optionsMenu = new OptionsMenu(diagramPane, false);
        		
        		optionsMenu.setPopupPositionAndShow(new PositionCallback() {

					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						final Integer left = OptionsMenuIcon.this.getAbsoluteLeft() + 
											 OptionsMenuIcon.this.getOffsetWidth() -
											 offsetWidth;
						final Integer top = OptionsMenuIcon.this.getAbsoluteTop() +  
											OptionsMenuIcon.this.getOffsetHeight();
						
						optionsMenu.showPopup(new Point(left, top));
					}        			
        		});
        	}
        });        
	}
	
	private void addMouseOverHandler() {
		addMouseOverHandler(new MouseOverHandler() {
		
			public void onMouseOver(MouseOverEvent event) {
				getElement().getStyle().setCursor(Cursor.POINTER);
			}
			
		});
	}
	
	public void updatePosition(Integer containerWidth, Integer containerHeight) {
		AbsolutePanel container = (AbsolutePanel) getParent();
		
		final Integer BUFFER = 2;
		
		final Integer left = containerWidth - resources.optionsIcon().getWidth() - BUFFER;
		final Integer top = BUFFER;
		
		container.setWidgetPosition(this, left, top);
	}
	
    interface Resources extends ClientBundle {
    	@Source("Options.png")
    	ImageResource optionsIcon();
    }   
}
