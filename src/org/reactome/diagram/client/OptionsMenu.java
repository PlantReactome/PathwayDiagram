/*
 * Created May 2013
 *
 */
package org.reactome.diagram.client;



import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author weiserj
 *
 */
public class OptionsMenu extends PopupPanel {
       
    private PathwayDiagramPanel diagramPane;
    private Image optionsIcon;
    private Boolean showing;
    
    public OptionsMenu(PathwayDiagramPanel diagramPane) {
        super(true); // Hide when clicking outside pop-up
    	this.diagramPane = diagramPane;
    	this.optionsIcon = new Image (getResources().options());
        this.showing = false;
    	init();
    }
    
    private void init() {
    	setWidget(getOptionsMenu());
    	optionsIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (showing) {  
					hide();					
				} else {
					showMenuUnderOptionsIcon();					
				}
			}				
    	});
    	
    	bringToFront(this);
    }
    	
    private void bringToFront(Widget widget) {
    	widget.getElement().getStyle().setZIndex(2);
    }    	   

    public void hide() {
    	super.hide();
    	showing = false;
    }
    
    private MenuBar getOptionsMenu() {
    	MenuBar optionsMenu = new MenuBar(true);
    	
    	optionsMenu.addItem(getSearchBarMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getDownloadDiagramMenuItem());
    	optionsMenu.addSeparator();
    	optionsMenu.addItem(getInteractionOverlayMenuItem());
    	
    	setOptionsMenuStyle(optionsMenu);
    	
    	return optionsMenu;
    }
    
    
    private void setOptionsMenuStyle(MenuBar optionsMenu) {
		Style menuStyle = optionsMenu.getElement().getStyle();
		menuStyle.setOpacity(1);		
	}

	private MenuItem getSearchBarMenuItem() {
    	MenuItem searchBar = new MenuItem("Search Diagram (Ctrl-f/\u2318-f)", new Command() {
    		
    		@Override
    		public void execute() {
    			diagramPane.showSearchPopup();
    			hide();
    		}
    	});
    	
    	return searchBar;
    }
    
    private MenuItem getDownloadDiagramMenuItem() {
        MenuItem downloadDiagram = new MenuItem("Download Diagram", new Command() {

			@Override
			public void execute() {							
				if (diagramPane.getPathway() == null) {
					AlertPopup.alert("Please choose a pathway to download");
					hide();
					return;
				}
				
				Canvas downloadCanvas = createDownloadCanvas(diagramPane.getCanvasList().get(0));
				
				Context2d context = downloadCanvas.getContext2d();
				
				for (DiagramCanvas canvasLayer : diagramPane.getCanvasList())					
					context.drawImage(canvasLayer.getCanvasElement(),0,0);				
				
				String canvasUrl = downloadCanvas.toDataUrl("application/octet-stream");
				Window.open(canvasUrl, null, null);
				
				hide();
			}

			private Canvas createDownloadCanvas(DiagramCanvas diagramCanvas) {				
				Integer width = diagramCanvas.getCanvasElement().getWidth();
				Integer height = diagramCanvas.getCanvasElement().getHeight();
				
				Canvas downloadCanvas = Canvas.createIfSupported();				
				downloadCanvas.setWidth(width + "px");
				downloadCanvas.setHeight(height + "px");
				downloadCanvas.setCoordinateSpaceWidth(width);
				downloadCanvas.setCoordinateSpaceHeight(height);
				
				return downloadCanvas;				
			}        	
        });
        
        return downloadDiagram;        
	}
	
    private MenuItem getInteractionOverlayMenuItem() {
    	MenuItem interactionOverlay = new MenuItem("Interaction Overlay Options...", new Command() {

			@Override
			public void execute() {
				InteractionOverlayOptionsPopup optionsPopup = new InteractionOverlayOptionsPopup(diagramPane);
				optionsPopup.center();
				
				hide();
			}
    		
    	});
		return interactionOverlay;    	
    }
        
    interface Resources extends ClientBundle {
    	@Source("Options.png")
    	ImageResource options();
    }
    
    private Resources getResources() {
    	return GWT.create(Resources.class);
    }
    
    private void showMenuUnderOptionsIcon() {
    	
    	setPopupPositionAndShow(new PopupPanel.PositionCallback() {
    		
    		@Override
    		public void setPosition(int offsetWidth, int offsetHeight) { 
    			// 'Right-justify' menu under the options icon
    			Integer menuX = getOptionsIcon().getAbsoluteLeft() + getOptionsIcon().getOffsetWidth() - offsetWidth; 
    			Integer menuY = getOptionsIcon().getAbsoluteTop() + getOptionsIcon().getOffsetHeight();
    	
    			setPopupPosition(menuX, menuY);
    			
    			showing = true;
    		}					
		});
    }
    
	public Image getOptionsIcon() {
		return optionsIcon;
	}

	public void setOptionsIcon(Image optionsIcon) {
		this.optionsIcon = optionsIcon;
	}

	public void updateIconPosition() {
		AbsolutePanel container = (AbsolutePanel) getOptionsIcon().getParent();
		Integer buffer = 4;
		
		// Top-right corner
		Integer left = container.getOffsetWidth() - getOptionsIcon().getOffsetWidth() - buffer;

		// TODO This is a hack to get correct initial placement of the icon when the container width
		// 		and icon width are unavailable making the left equal to the negative buffer.
		// 		THIS SHOULD BE REPLACED WITH DYNAMIC CALCULATION OF INITIAL POSITION
		final Integer CONTAINERBUFFER = 40;
		final Integer ICONWIDTH = 30;
		if (left == -buffer)
			left = Window.getClientWidth() - CONTAINERBUFFER - ICONWIDTH - buffer;
		
		
		Integer top = buffer;
		
		container.setWidgetPosition(getOptionsIcon(), left, top);
	}
}
