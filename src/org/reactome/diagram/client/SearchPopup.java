/*
 * Created on May 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Designed to mimic the 'find' functionality found within applications and browsers for the pathway
 * diagram.
 * @author weiserj
 *
 */
public class SearchPopup extends HorizontalPanel {
	private PathwayDiagramPanel diagramPane;
	private List<Long> matchingEntityIds;
	private TextBox searchBox;
	private Label resultsLabel;
	private HorizontalPanel navigationButtons;
	private Integer selectedIndex;
	private String searchString;
	
	public SearchPopup(PathwayDiagramPanel dPane) {
		super();
		diagramPane = dPane;
		init();
	}
	
	private void init() {
		matchingEntityIds = new ArrayList<Long>();
		
		Button closeButton = new Button("X", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SearchPopup.this.setVisible(false);
			}
		});
		
		Label searchLabel = new Label("Find Reaction/Entity:");
		
		searchBox = new TextBox();
		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {	
				String newSearchString = searchBox.getText();
				
				if (!newSearchString.equalsIgnoreCase(searchString))
					doSearch(newSearchString);
				
				searchString = newSearchString;
			}
		});
			
		resultsLabel = new Label();
		
		navigationButtons = new HorizontalPanel();
		Button previousButton = new Button("Previous", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectEntity(selectedIndex - 1);					
			}
				
		});
			
		Button nextButton = new Button("Next", new ClickHandler() {
	
			@Override
			public void onClick(ClickEvent event) {
				selectEntity(selectedIndex + 1);
			}
				
		});
			
		Button selectAllButton = new Button("Select All", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				diagramPane.setSelectionIds(matchingEntityIds);
				selectedIndex = -1;
				resultsLabel.setText("Focused on: All " + matchingEntityIds.size());
			}
			
		});
		navigationButtons.add(previousButton);
		navigationButtons.add(nextButton);
		navigationButtons.add(selectAllButton);
		
		
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		add(closeButton);
		add(searchLabel);
		add(searchBox);						
		add(navigationButtons);
		add(resultsLabel);
			
		enableButtons(Boolean.FALSE);
		
		this.setStyleName(diagramPane.getStyle().searchPopup());
		
		//Style style = this.getElement().getStyle();
		//style.setPadding(1, Style.Unit.PX);
		//style.setBorderColor("rgb(0, 0, 0)");
		//style.setBorderWidth(1, Style.Unit.PX);	
		//style.setOpacity(1);
		//style.setZIndex(2);
	}	
		
	// Returns a list of db ids for objects in the pathway diagram
	// which match the given query
	private List<Long> searchDiagram(String query) {
		List<Long> matchingObjectIds = new ArrayList<Long>();
		
		if (!query.isEmpty() && diagramPane.getPathway() != null) {
		
			List<GraphObject> pathwayObjects = diagramPane.getPathway().getGraphObjects(); 			
			for (GraphObject pathwayObject: pathwayObjects) {
				if (pathwayObject.getDisplayName() == null || pathwayObject.getType() == GraphObjectType.RenderableCompartment)
					continue;
				
				if (pathwayObject.getDisplayName().toLowerCase().contains(query.toLowerCase()))
					matchingObjectIds.add(pathwayObject.getReactomeId());
			}						
		}
			
		return matchingObjectIds;			
	}

	private void doSearch(String query) {			
		matchingEntityIds = searchDiagram(query);
		
		if (matchingEntityIds.isEmpty()) {
			enableButtons(Boolean.FALSE);
			
			String labelText = null;				
			if (!query.isEmpty())
				labelText = "No matches found for " + query;
			resultsLabel.setText(labelText);
			
			diagramPane.setSelectionId(null);
			
			return;
		} else {
			enableButtons(Boolean.TRUE);
		}
		
		selectEntity(0);
	}
		
	private void selectEntity(Integer index) {
		if (index >= matchingEntityIds.size()) {
			index = 0;
		} else if (index < 0) {
			index = matchingEntityIds.size() - 1;
		}
		
		selectedIndex = index;
		
		resultsLabel.setText("Focused on: " + (index + 1) + " of " + matchingEntityIds.size());
		
		diagramPane.setSelectionId(matchingEntityIds.get(index));
	}
	
	private void enableButtons(Boolean enable) {
		Iterator<Widget> buttonIterator = navigationButtons.iterator();
		
		while (buttonIterator.hasNext()) {
			Button button = (Button) buttonIterator.next();
			button.setEnabled(enable);
		}
		
	}
	
	public TextBox getTextBox() {
		return searchBox;
	}
	
	public void updatePosition() {
		AbsolutePanel container = (AbsolutePanel) getParent();
		
		// Search box is placed next to the overview canvas
		OverviewCanvas overview = diagramPane.getOverview();
		Integer overviewLeft = container.getWidgetLeft(overview);
		Integer overviewWidth =  overview.getCoordinateSpaceWidth();
		Integer buffer = 4;
		
		Integer top = container.getOffsetHeight() - getOffsetHeight() - buffer;
		Integer left = overviewLeft + overviewWidth + buffer;
		container.setWidgetPosition(this, left, top);
	}
}
    
