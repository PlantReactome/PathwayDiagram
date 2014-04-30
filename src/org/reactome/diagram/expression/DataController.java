/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.analysis.factory.AnalysisModelException;
import org.reactome.analysis.factory.AnalysisModelFactory;
import org.reactome.analysis.model.AnalysisResult;
import org.reactome.analysis.model.PathwayIdentifiers;
import org.reactome.diagram.Controller;
import org.reactome.diagram.client.AlertPopup;
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
    protected Map<Long, PathwayOverlay> pathwayOverlayMap;
    protected PathwayOverlay pathwayOverlay; // Displayed pathway
    private AnalysisResult analysisResult;
    
    /**
     * Some icons
     * @author gwu
     *
     */
    interface Resources extends ClientBundle {
        @Source("whiteclose.png")
        ImageResource close();
    }
    
    public DataController() {
        pathwayOverlayMap = new HashMap<Long,PathwayOverlay>();
    }
    
    static Resources getDataControllerResources() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }

    public void setPathway(String token, CanvasPathway pathway) {
        PathwayOverlay selectedPathwayOverlay = this.pathwayOverlayMap.get(pathway.getReactomeId());
    	if (selectedPathwayOverlay == null) {
        	setPathwaySummary(token, pathway);
        } else {
        	setPathwayOverlay(selectedPathwayOverlay);
        }
    }
    
        
    private void setPathwayOverlay(PathwayOverlay pathwayOverlay) { 
        this.pathwayOverlay = pathwayOverlay;
    }
    
    public PathwayOverlay getPathwayOverlay() {
        return this.pathwayOverlay;
    }
    
    public void setPathwaySummary(final String token, final CanvasPathway pathway) {
    	Controller analysisController = new Controller();  
      
    	final Long pathwayId = pathway.getReactomeId();
    	analysisController.retrievePathwaySummary(token, pathwayId, new RequestCallback() {
    		public void onError(Request request, Throwable exception) {
                AlertPopup.alert("Error in retrieving pathway summary results: " + exception);
    		}
                
    		public void onResponseReceived(Request request, Response response) {
    			if (response.getStatusCode() == Response.SC_OK) {
    				try {
                   		PathwayIdentifiers pathwayIdentifiers = AnalysisModelFactory.getModelObject(PathwayIdentifiers.class, response.getText());
                   		PathwayOverlay pathwayOverlay = new PathwayOverlay(pathway, pathwayIdentifiers);
                   		
                   		setPathwayOverlay(pathwayOverlay);
                   		pathwayOverlayMap.put(pathwayId, pathwayOverlay);
    				} catch (AnalysisModelException e) {
    					e.printStackTrace();
    					AlertPopup.alert("Could not get pathway identifiers for token " + token);
    					return;
    				}
                   	
    				if (pathway.getDbIdToRefEntity() == null || pathway.getDbIdToRefEntity().isEmpty()) {
                   		PathwayDiagramController diagramController = new PathwayDiagramController();
                   		diagramController.getPhysicalToReferenceEntityMap(pathway, new RequestCallback() {

								@Override
								public void onResponseReceived(Request request,	Response response) {
									if (response.getStatusCode() == Response.SC_OK) {
										pathway.setDbIdToRefEntity(response.getText());
									}
									
								}

								@Override
								public void onError(Request request,Throwable exception) {
									
								}
                   				
                   			});
                   		}
                   			
                   		//onDataPointChange(0)
    			}
    		}
    	});
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
        container.add(navigationPane, x, y);
      
        offsetWidth = container.getOffsetWidth() - width;
        offsetHeight = container.getOffsetHeight() - height;
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
    	if (currentPathwayOverlay == null)
            return; // Nothing to do. No pathway has been displayed.
        
        Map<Long, Double> compIdToValue = null;
        Map<Long, String> compIdToColor = null;
        Map<Long, List<String>> compIdToExpressionId = null;
        
       // if (pathwayIdentifiersMap.get(pathwayId) != null) {
        	compIdToValue = currentPathwayOverlay.getExpressionValuesForDataPoint(dataIndex);
        	compIdToColor = convertValueToColor(compIdToValue);
        	compIdToExpressionId = currentPathwayOverlay.getDbIdToExpressionId();
        //}        
        
        DataPointChangeEvent event = new DataPointChangeEvent();
        event.setPathwayId(currentPathwayOverlay.getPathway().getReactomeId());
        event.setPathwayComponentIdToColor(compIdToColor);
        event.setPathwayComponentIdToExpressionLevel(compIdToValue);
        event.setPathwayComponentIdToExpressionId(compIdToExpressionId);
       
        fireDataPointChangeEvent(event);
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
