/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This customized FlexTable is used to set up controls for PathwayCanvas: e.g. 
 * zooming, translation. The set up basically is copied from Maulik's original code.
 * @author gwu
 *
 */
public class PathwayCanvasControls extends FlexTable {
	// The following resources is used to load images for controls
    interface Resources extends ClientBundle {
        @Source("Reset.png")
        ImageResource reset();
        @Source("Plus.png")
        ImageResource plus();
        @Source("Minus.png")
        ImageResource minus();
        @Source("Left.png")
        ImageResource left();
        @Source("Right.png")
        ImageResource right();
        @Source("Up.png")
        ImageResource up();
        @Source("Down.png")
        ImageResource down();
    }
    
    private PathwayDiagramPanel diagramPane;
    private static Resources resources;
    
    public PathwayCanvasControls(PathwayDiagramPanel diagramPane) {
        this.diagramPane = diagramPane;
        init();
    }
    
    private static Resources getResource() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }
    
    private void init() {
        Resources resources = getResource();
        Image refresh = new Image(resources.reset());
        refresh.setAltText("reset");
        refresh.setTitle("reset");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.reset();
                diagramPane.update();
            }
        });
        
        Image zoomPlus = new Image(resources.plus());
        zoomPlus.setAltText("zoom in");
        zoomPlus.setTitle("zoom in");
        zoomPlus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.scale(Parameters.ZOOMIN);
                diagramPane.update();
            }
        });
        
        Image zoomMinus = new Image(resources.minus());
        zoomMinus.setAltText("zoom out");
        zoomMinus.setTitle("zoom out");
        zoomMinus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.scale(Parameters.ZOOMOUT);
                diagramPane.update();
            }
        });
        
        Image scrollLeft = new Image(resources.left());
        scrollLeft.setAltText("move left");
        scrollLeft.setTitle("move left");
        scrollLeft.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(Parameters.MOVEX, 0);
                diagramPane.update();
            }
        });
        
        Image scrollTop = new Image(resources.up());
        scrollTop.setAltText("move up");
        scrollTop.setTitle("move up");
        scrollTop.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(0, Parameters.MOVEY);
                diagramPane.update();
            }
        });
        
        Image scrollBottom = new Image(resources.down());
        scrollBottom.setAltText("move down");
        scrollBottom.setTitle("move down");
        scrollBottom.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(0, -Parameters.MOVEY);
                diagramPane.update();
            }
        });
        
        Image scrollRight = new Image(resources.right());
        scrollRight.setAltText("move right");
        scrollRight.setTitle("move right");
        scrollRight.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                diagramPane.translate(-Parameters.MOVEX, 0);
                diagramPane.update();
            }
        });
    
        SearchPanel searchPanel = new SearchPanel();
        
        Button downloadDiagram = new Button("Download Diagram", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
			}        	
        });
        	
        Button interactionOverlay = new Button("Interaction Overlay Options...", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
			}        	
        });
        
               
//        setButtonSize(refresh);
//        setButtonSize(zoomPlus);
//        setButtonSize(zoomMinus);
//        setButtonSize(scrollLeft);
//        setButtonSize(scrollRight);
//        setButtonSize(scrollBottom);
//        setButtonSize(scrollTop);
        
        FlexCellFormatter cellFormatter = getFlexCellFormatter();
        setWidget(0, 0, refresh);
        cellFormatter.setRowSpan(0, 0, 2);
        setWidget(0, 1, zoomPlus);
        cellFormatter.setRowSpan(0, 1, 2);
        setWidget(0, 2, zoomMinus);
        cellFormatter.setRowSpan(0, 2, 2);
        setWidget(0, 3, scrollLeft);
        cellFormatter.setRowSpan(0, 3, 2);
        setWidget(0, 4, scrollTop);
        setWidget(1, 0, scrollBottom);
        setWidget(0, 5, scrollRight);
        cellFormatter.setRowSpan(0, 5, 2);				
		setWidget(0, 6, searchPanel);
		cellFormatter.setRowSpan(0, 6, 2);
		setWidget(0, 7, downloadDiagram);
		cellFormatter.setRowSpan(0, 7, 2);
		setWidget(0, 8, interactionOverlay);
		cellFormatter.setRowSpan(0, 8, 2);
	}
	
	private class SearchPanel extends HorizontalPanel {
		private Label searchLabel;
		private TextBox searchBox;
		private Button doSearch; 
		
		public SearchPanel() {
			super();
			init();
		}
		
		private void init() {
			searchLabel = new Label("Find Reaction/Entity:");
			searchBox = new TextBox();
			doSearch = new Button("Search Diagram", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (searchBox.getText().isEmpty() || diagramPane.getPathway() == null)
						return;
					
					List<Long> matchingResults = searchDiagram(searchBox.getText());
					
					if (matchingResults.isEmpty()) {
						@SuppressWarnings("unused")
						AlertPopup alert = new AlertPopup(searchBox.getText() + " can not be found for the current pathway");
					}
					
					diagramPane.setSelectionIds(matchingResults);
				}
				
			});
			
			setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			add(searchLabel);
			add(searchBox);			
			add(doSearch);						
			
			Style style = this.getElement().getStyle();
			style.setPadding(1, Style.Unit.PX);
			style.setBorderColor("rgb(0, 0, 0)");
			style.setBorderWidth(1, Style.Unit.PX);	
		}
		
		// Returns a list of db ids for objects in the pathway diagram
		// which match the given query
		private List<Long> searchDiagram(String query) {
			List<GraphObject> pathwayObjects = diagramPane.getPathway().getGraphObjects();

			List<Long> matchingObjectIds = new ArrayList<Long>(); 
			
			for (GraphObject pathwayObject: pathwayObjects) {
				if (pathwayObject.getDisplayName().toLowerCase().contains(query.toLowerCase()))
					matchingObjectIds.add(pathwayObject.getReactomeId());
			}
			
			return matchingObjectIds;
			
		}
	}
    
//    private void setButtonSize(PushButton btn) {
//        btn.setSize("10px", "10px");
//    }
    
}
