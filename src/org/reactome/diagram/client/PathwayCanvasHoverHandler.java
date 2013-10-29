/*
 * Created on Oct 11, 2012
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.Node;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * This class is used to handle hovering over objects for PathwayDiagramPanel.
 * @author jweiser
 *
 */
public class PathwayCanvasHoverHandler extends HoverHandler {
    private PathwayCanvas pc;
	private InfoIcon infoIcon;
    
    public PathwayCanvasHoverHandler(PathwayDiagramPanel diagramPanel, PathwayCanvas pathwayCanvas) {
        super(diagramPanel, pathwayCanvas);
        pc = pathwayCanvas; 
        infoIcon = new InfoIcon();
    }

    public InfoIcon getInfoIconPopup() {
    	return infoIcon;
    }
    
    public GraphObject hover(Point hoverPoint) {
        this.hoverPoint = hoverPoint;
    	
    	if (pc.getPathway() == null || pc.getPathway().getObjectsForRendering() == null)
            return null;
                
        List<GraphObject> objects = pc.getPathway().getObjectsForRendering();
        super.hover(objects);        
        
        if (hoveredObject != null) {
        	infoIcon.hide();
        	showPopups();
        } else {
        	infoIcon.startHideTimer();
        }
        	
        return hoveredObject;        
    }
    
    private void showPopups() {
    	if (hoveredObject instanceof Node)
    		showInfoIcon((Node) hoveredObject);
    	showTooltip();
    }
    
    private void showInfoIcon(final Node entity) {
    	infoIcon.stopHideTimer();
    	infoIcon.setEntity(entity);
    	
    	Image infoIconImage = infoIcon.getInfoIconImage();
    	infoIconImage.setPixelSize((int) (infoIconImage.getWidth() * pc.getScale()), 
    							   (int) (infoIconImage.getHeight() * pc.getScale()));
    	
    	
    	
    	infoIcon.setPopupPositionAndShow(new PositionCallback(){
			
    		@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				final Integer OFFSET = (int) (7 / pc.getScale());
				
				final Integer popupLeft = pc.getAbsoluteXCoordinate(
											(double) (entity.getBounds().getX() + entity.getBounds().getWidth() - OFFSET)
										  ).intValue();
				final Integer popupTop = (int) (pc.getAbsoluteYCoordinate(
											(double) (entity.getBounds().getY() + OFFSET)
										 ) - offsetHeight);
				infoIcon.setPopupPosition(popupLeft, popupTop);
			}    		
    	});
    }
    
    protected void showTooltip() {
    	if (hoveredObject.getType() == GraphObjectType.RenderableReaction) {
    		String label = super.getLabel();
    	 	
    		tooltip.setWidget(new Label(label));
    
    		super.showTooltip();
    	}
    }
    
    interface Resources extends ClientBundle {
		@Source("InfoIcon.png")
		DataResource infoIcon();
	}
	
	protected class InfoIcon extends PopupPanel {
		private HTMLPanel infoIconContainer;
		private Image infoIconImage;
		private Node entity;
		private HideTimer hideTimer;
		private ClickEvent clickEvent;
		
		public InfoIcon() {
			final DataResource infoIconResource = ((Resources) GWT.create(Resources.class)).infoIcon();
			
			infoIconImage = new Image(infoIconResource.getSafeUri());
			infoIconContainer = new HTMLPanel(infoIconImage.toString());

			setWidget(infoIconContainer);
			
			WidgetStyle.bringToFront(this);
			WidgetStyle.removeBorder(this);
			WidgetStyle.setTransparentBackground(this);
			
			addHandlers();
			addHideTimer();
		}
		
		public Image getInfoIconImage() {
			return infoIconImage;
		}
		
		public void setEntity(Node entity) {
			this.entity = entity;
		}
		
		public Node getEntity() {
			return entity;
		}
					
		public void startHideTimer() {
			hideTimer.schedule(300);
		}
		
		public void stopHideTimer() {
			hideTimer.cancel();
		}
				
		private void addHandlers() {
			addClickHandler();
			addMouseOverHandler();
		}
		
		private void addClickHandler() {
			infoIconContainer.sinkEvents(Event.ONCLICK);
			infoIconContainer.addHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					clickEvent = event;
					diagramPanel.setSelectionObject(getEntity());
					
					//diagramPanel.getPopupMenu().showPopupMenu(getEntity(), event);
					//diagramPanel.setSelectionObject(getEntity());
					hide();
				}	
			}, ClickEvent.getType());
			
			diagramPanel.addSelectionEventHandler(new SelectionEventHandler() {

				@Override
				public void onSelectionChanged(SelectionEvent e) {
					if (clickEvent != null)
						diagramPanel.getPopupMenu().showPopupMenu(getEntity(), clickEvent);
					
					clickEvent = null;
				}
			});
		}
		
		private void addMouseOverHandler() {
			infoIconContainer.sinkEvents(Event.ONMOUSEOVER);
			infoIconContainer.addHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					WidgetStyle.setCursor(InfoIcon.this, Cursor.POINTER);
					stopHideTimer();
				}				
			}, MouseOverEvent.getType());
		}
		
		private void addHideTimer() {
			hideTimer = new HideTimer(); 
		}					
					
		private class HideTimer extends Timer {
			//private Boolean active = false;
				
			@Override
			public void run() {
				hide();
				//active = false;
			}
			
			public void schedule(int delayMillis) {
				//if (!active) {	
					//active = true;
					super.schedule(delayMillis);
				//}
			}		
		}
	}	
}
