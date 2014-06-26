/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.analysis.factory.AnalysisModelFactory;
import org.reactome.diagram.analysis.model.AnalysisResult;
import org.reactome.diagram.analysis.model.PathwayIdentifiers;
import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.AnalysisController;
import org.reactome.diagram.client.PathwayDiagramController;
import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEvent;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEventHandler;
import org.reactome.diagram.expression.model.PathwayOverlay;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * This customized GUI is used to present the user a way to select a data point, and
 * show a color bars for expression values. 
 * @author gwu
 *
 */
public abstract class DataController implements ResizeHandler {
    // Keep HandlerRegistration in order to remove it
    protected HandlerRegistration handlerRegistration;
    protected List<DataPointChangeEventHandler> dataPointChangeEventHandlers;
    protected List<ExpressionOverlayStopEventHandler> expressionOverlayStopEventHandlers;
    // Customized widget to show controls used to navigation data points
    protected NavigationPane navigationPane;
    // Size of this panel
    protected int navPaneWidth = 275;
    protected int navPaneHeight = 17;
    // Container holding the above two controls
    protected AbsolutePanel container;
    // Offset between container and the actual size
    protected int offsetWidth;
    protected int offsetHeight;
    // For fetching icons
    private static Resources resources;
    // Data models handled by this controller
    
    protected Map<String, Map<Long, PathwayOverlay>> pathwayOverlayMap;
    private CanvasPathway pathway;
    
    private AnalysisResult analysisResult;
    private String resourceName;
    
    /**
     * Some icons
     * @author gwu
     *
     */
    interface Resources extends ClientBundle {
        @Source("whiteclose.png")
        ImageResource close();
    }
    
    public DataController(AnalysisResult analysisResult) {
        pathwayOverlayMap = new HashMap<String, Map<Long,PathwayOverlay>>();
        setAnalysisResult(analysisResult);
    }
    
    static Resources getDataControllerResources() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }

    public void setResourceName(String resourceName) {
    	this.resourceName = resourceName;
    	if (pathwayOverlayMap.get(getResourceName()) == null)
    		pathwayOverlayMap.put(getResourceName(), new HashMap<Long, PathwayOverlay>());
    	
    	setPathway(pathway);
    }
    
    public String getResourceName() {
    	return resourceName;
    }
    
    public void setPathway(CanvasPathway pathway) {
    	this.pathway = pathway;
    	
    	if (pathway == null)
    		return;
    	
        PathwayOverlay selectedPathwayOverlay = this.pathwayOverlayMap.get(getResourceName()).get(pathway.getReactomeId());
    	if (selectedPathwayOverlay == null) {
        	setPathwaySummary(pathway);
        } else {
        	setPathwayOverlay(selectedPathwayOverlay);
        }
    }
    
        
    private void setPathwayOverlay(PathwayOverlay pathwayOverlay) { 
        this.pathwayOverlayMap.get(getResourceName()).put(pathway.getReactomeId(), pathwayOverlay);
        onDataPointChange(getCurrentDataPoint());
    }
    
    private PathwayOverlay getPathwayOverlay() {
        return this.pathwayOverlayMap.get(getResourceName()).get(pathway.getReactomeId());
    }
    
    private void setPathwaySummary(final CanvasPathway pathway) {
    	AnalysisController analysisController = new AnalysisController();  
      
    	final Long pathwayId = pathway.getReactomeId();
    	analysisController.retrievePathwaySummary(getToken(), pathwayId, getResourceName(), new RequestCallback() {
    		public void onError(Request request, Throwable exception) {
                AlertPopup.alert("Error in retrieving pathway summary results: " + exception);
    		}
                
    		public void onResponseReceived(Request request, Response response) {
    			if (response.getStatusCode() != Response.SC_OK && response.getStatusCode() != Response.SC_NOT_FOUND) {
    				AlertPopup.alert("Error in retrieving pathway summary results. Response code: " + response.getStatusCode());
    				return;
    			}
    			
    			PathwayIdentifiers pathwayIdentifiers;
    			try {
    				pathwayIdentifiers = AnalysisModelFactory.getModelObject(PathwayIdentifiers.class, response.getText());
    			} catch (Exception e) {
    					e.printStackTrace();
    					setPathwayOverlay(null);
    					return;
    			}
    			PathwayOverlay pathwayOverlay = new PathwayOverlay(pathway, pathwayIdentifiers);
    			addDbIdToRefEntityMapToPathway(pathway, pathwayOverlay);
    		}
    	});
    }
    	
    private void addDbIdToRefEntityMapToPathway(final CanvasPathway pathway, final PathwayOverlay pathwayOverlay) {
    	if (pathway.getDbIdToRefEntity() == null || pathway.getDbIdToRefEntity().isEmpty()) {
    		PathwayDiagramController.getInstance().getPhysicalToReferenceEntityMap(pathway, new RequestCallback() {
    			private final String ERROR = "Error in retrieving db id to reference entity map. ";
    			
    			@Override
    			public void onResponseReceived(Request request,	Response response) {
                   	if (response.getStatusCode() != Response.SC_OK) {
                   		AlertPopup.alert(ERROR + 
                   				"Pathway id: " + pathway.getReactomeId() + " " +
                   				"Response code: " + response.getStatusCode());
                   		return;
                   	}
                   	
                   	pathway.setDbIdToRefEntity(response.getText());
                   	setPathwayOverlay(pathwayOverlay);
				}
							
				@Override
				public void onError(Request request,Throwable exception) {
					AlertPopup.alert(ERROR + exception);
				}
			});
    	} else {
    		setPathwayOverlay(pathwayOverlay);
    	}
    } 
    
    public String getToken() {
    	return analysisResult.getSummary().getToken();
    }
    
    /**
     * Display this component in the specified composite.
     * @param composite
     */
    public void display(AbsolutePanel container,
                        int width,
                        int height) {
        this.container = container;
        // Set up position for the navigation pane
        navigationPane.setSize(navPaneWidth + "px",
                               navPaneHeight + "px");
        int x = (width - navPaneWidth) / 2;
        int y = height - navPaneHeight;

        if (container.getWidgetIndex(navigationPane) == -1)
        	container.add(navigationPane, x, y);
        else
        	container.setWidgetPosition(navigationPane, x, y);
        
        offsetWidth = container.getOffsetWidth() - width;
        offsetHeight = container.getOffsetHeight() - height;
        
        if (handlerRegistration == null)
        	handlerRegistration = this.container.addHandler(this, ResizeEvent.getType());
    }
    
    @Override
    public void onResize(ResizeEvent event) {
        if (!navigationPane.isVisible())
            return;
        updatePosition();
    }
    
    private void updatePosition() {
        int width = container.getOffsetWidth() - offsetWidth;
        int height = container.getOffsetHeight() - offsetHeight;
        int x = (width - navPaneWidth) / 2;
        int y = height - navPaneHeight;
        container.setWidgetPosition(navigationPane, x, y);
    }
    
    public void dispose() {
        if (container == null)
            return; // It has not been displayed
        container.remove(navigationPane);
     
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        container = null; // Null it so that it can be displayed again.
    }
    
    public void addExpressionOverlayStopEventHandler(ExpressionOverlayStopEventHandler handler) {
    	if (expressionOverlayStopEventHandlers == null)
    		expressionOverlayStopEventHandlers = new ArrayList<ExpressionOverlayStopEventHandler>();
    	if (expressionOverlayStopEventHandlers.contains(handler))
    		return;
    	expressionOverlayStopEventHandlers.add(handler);
    }
    
    public void addDataPointChangeEventHandler(DataPointChangeEventHandler handler) {
        if (dataPointChangeEventHandlers == null)
            dataPointChangeEventHandlers = new ArrayList<DataPointChangeEventHandler>();
        if (dataPointChangeEventHandlers.contains(handler))
            return;
        dataPointChangeEventHandlers.add(handler);
    }
    
    protected void onDataPointChange(Integer dataIndex) {
        PathwayOverlay currentPathwayOverlay = getPathwayOverlay();
        
    	if (pathway == null)
            return; // Nothing to do. No pathway has been displayed.
        
        Map<Long, Double> compIdToValue = null;
        Map<Long, String> compIdToColor = null;
        Map<Long, List<String>> compIdToExpressionId = null;
        
        if (currentPathwayOverlay != null && resourceName != null) {
        	compIdToValue = currentPathwayOverlay.getExpressionValuesForDataPoint(dataIndex, resourceName);
        	compIdToColor = convertValueToColor(compIdToValue);
        	compIdToExpressionId = currentPathwayOverlay.getDbIdToExpressionId(resourceName);
        }        
        
        DataPointChangeEvent event = new DataPointChangeEvent();
        event.setPathwayId(pathway.getReactomeId());
        event.setPathwayComponentIdToColor(compIdToColor);
        event.setPathwayComponentIdToExpressionLevel(compIdToValue);
        event.setPathwayComponentIdToExpressionId(compIdToExpressionId);
       
        fireDataPointChangeEvent(event);
    }
    
    public Integer getCurrentDataPoint() {
    	return 0;
    }

    public String convertValueToColor(Double value) {
    	final Long dummyId = 0L;
    	
    	Map<Long, Double> idToValue = new HashMap<Long, Double>();
    	idToValue.put(dummyId, value);
    			
    	Map<Long, String> idToColor = convertValueToColor(idToValue);
    	if (idToColor == null)
    		return null;
    	return idToColor.get(dummyId);
    }
    
    public abstract Map<Long, String> convertValueToColor(Map<Long, Double> compIdToValue);
    
    private void fireExpressionOverlayStopEvent() {
    	if (expressionOverlayStopEventHandlers == null)
    		return;
    	ExpressionOverlayStopEvent event = new ExpressionOverlayStopEvent();
    	for (ExpressionOverlayStopEventHandler handler : expressionOverlayStopEventHandlers)
    		handler.onExpressionOverlayStopped(event);
    		
    }
    
    private void fireDataPointChangeEvent(DataPointChangeEvent event) {
        if (dataPointChangeEventHandlers == null)
            return;
        for (DataPointChangeEventHandler handler : dataPointChangeEventHandlers)
            handler.onDataPointChanged(event);
    }
    
    public void setNavigationPaneStyle(String style) {
        navigationPane.setStyleName(style);
    }
    
    public AnalysisResult getAnalysisResult() {
		return analysisResult;
	}

	public void setAnalysisResult(AnalysisResult analysisResult) {
		this.analysisResult = analysisResult;
	}

	protected abstract class NavigationPane extends HorizontalPanel {
        protected Label dataLabel;
        private Image close;
        
        public NavigationPane() {
            Resources resources = DataController.getDataControllerResources();
            close = new Image(resources.close());
            close.setAltText("close");
            close.setTitle("close");
            dataLabel = new Label();
        }
        
        protected abstract void init();
                	
        protected void addDataLabel() {	
        	add(dataLabel);
            setCellVerticalAlignment(dataLabel,
                                     HasVerticalAlignment.ALIGN_MIDDLE);
            setCellHorizontalAlignment(dataLabel, 
                                       HasHorizontalAlignment.ALIGN_CENTER);
        }    
        
        protected void addCloseButton() {
        	HorizontalPanel panel = new HorizontalPanel();
        	add(panel);
        	setCellVerticalAlignment(panel, HasVerticalAlignment.ALIGN_MIDDLE);
        	setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_RIGHT);
        	addCloseButton(panel);
        }
        
        protected void addCloseButton(HorizontalPanel panel) {    
            panel.add(close);
            panel.setCellVerticalAlignment(close,
                                            HasVerticalAlignment.ALIGN_MIDDLE);
            setCellHorizontalAlignment(close, 
                                       HasHorizontalAlignment.ALIGN_RIGHT);
        }
        
        protected void installHandlers() {
            close.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    DataController.this.dispose();
                    DataController.this.fireExpressionOverlayStopEvent();
                }
            });
        } 
    }
}
