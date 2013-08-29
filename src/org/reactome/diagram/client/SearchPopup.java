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
import org.reactome.diagram.model.Node;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Timer;
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
	private List<GraphObject> matchingEntityIds;
	private SearchTimer searchTimer;
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
		matchingEntityIds = new ArrayList<GraphObject>();
		
		Button closeButton = new Button("X", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SearchPopup.this.setVisible(false);
			}
		});
		
		Label searchLabel = new Label("Find Reaction/Entity:");
		
		searchTimer = new SearchTimer();			
		
		searchBox = new TextBox();
		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {					
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && searchTimer.isActive()) {
					searchTimer.cancel();
					doSearch(searchString);
					return;
				} 
				
				String newSearchString = searchBox.getText();
				if (!newSearchString.equalsIgnoreCase(searchString)) {
					searchString = newSearchString;
					searchTimer.schedule(1000);
				}
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
				//diagramPane.setSelectionObjects(matchingEntityIds);
				highlightEntities(matchingEntityIds);
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
	private List<GraphObject> searchDiagram(String query) {
		List<GraphObject> matchingObjects = new ArrayList<GraphObject>();
		
		if (!query.isEmpty() && diagramPane.getPathway() != null) {
		
			List<GraphObject> pathwayObjects = diagramPane.getPathway().getGraphObjects(); 			
			for (GraphObject pathwayObject: pathwayObjects) {
				if (pathwayObject.getDisplayName() == null || pathwayObject.getType() == GraphObjectType.RenderableCompartment)
					continue;
				
				if (pathwayObject.getDisplayName().toLowerCase().contains(query.toLowerCase()))
					matchingObjects.add(pathwayObject);
			}						
		}
			
		return matchingObjects;			
	}

	private void doSearch(String query) {		
		matchingEntityIds = searchDiagram(query);
		
		if (matchingEntityIds.isEmpty()) {
			enableButtons(Boolean.FALSE);
			
			String labelText = null;				
			if (!query.isEmpty())
				labelText = "No matches found for " + query;
			resultsLabel.setText(labelText);
			
			highlightEntities(matchingEntityIds);
			
			return;
		} else {
			enableButtons(Boolean.TRUE);
		}
		
		selectEntity(0);
		focus();
	}
		
	private void selectEntity(Integer index) {
		if (index >= matchingEntityIds.size()) {
			index = 0;
		} else if (index < 0) {
			index = matchingEntityIds.size() - 1;
		}
		
		selectedIndex = index;
		
		resultsLabel.setText("Focused on: " + (index + 1) + " of " + matchingEntityIds.size());
		
		highlightEntity(matchingEntityIds.get(index));
		
	}
	
	private void highlightEntities(List<GraphObject> entities) {
		removeAllHighlighting();
		removeAllSelections();
		
		for (GraphObject entity : entities) {
			entity.setHighlighted(true);
		}
		
		Boolean doCentring = !(diagramPane.getPathwayCanvas().currentViewContainsAtLeastOneGraphObject(entities));
		
		if (doCentring && !entities.isEmpty()) {
			GraphObject entity = entities.get(0);
			
			Point entityCentre;
			if (entity instanceof Node)
				entityCentre = ((Node) entity).getBounds().getCentre();
			else
				entityCentre = entity.getPosition();
			
			diagramPane.center(diagramPane.getPathwayCanvas()
										  .getAbsoluteCoordinates(entityCentre.getX(), entityCentre.getY()));
		}
		
		diagramPane.update();
	}
	
	private void highlightEntity(GraphObject entity) {
		List<GraphObject> entities = new ArrayList<GraphObject>(0);
		entities.add(entity);
		
		highlightEntities(entities);
	}
	
	private void removeAllHighlighting() {
		for (GraphObject entity : getPathwayGraphObjects()) {
			entity.setHighlighted(false);
		}
	}
	
	private void removeAllSelections() {
		for (GraphObject entity : getPathwayGraphObjects())
			entity.setIsSelected(false);
	}
	
	private List<GraphObject> getPathwayGraphObjects() {
		return diagramPane.getPathway().getGraphObjects();
	}
	
	private void enableButtons(Boolean enable) {
		Iterator<Widget> buttonIterator = navigationButtons.iterator();
		
		while (buttonIterator.hasNext()) {
			Button button = (Button) buttonIterator.next();
			button.setEnabled(enable);
		}
		
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

	public void focus() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			public void execute() {
				searchBox.setFocus(true);
			}
		});
	}
	
	private class SearchTimer extends Timer {
		private Boolean active;
		
		@Override
		public void run() {
			final String query = SearchPopup.this.searchString;
			
			doSearch(query);
			active = false;						
		}
		
		public void schedule(int milliSeconds) {
			super.schedule(milliSeconds);
			active = true;
		}
		
		public void cancel() {
			super.cancel();
			active = false;
		}		
		
		public Boolean isActive() {
			return active;
		}		
	}
}
    
