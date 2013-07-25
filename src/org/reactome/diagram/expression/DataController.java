/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEvent;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEventHandler;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
    protected ReactomeExpressionValue dataModel;
    protected Long pathwayId; // Displayed pathway id
    
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
        init();
    }
    
    static Resources getDataControllerResources() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }
    
    protected abstract void init();
    
    
    public void setDataModel(ReactomeExpressionValue model) {
        this.dataModel = model;
    }
    
    public ReactomeExpressionValue getDataModel() {
        return this.dataModel;
    }
    
    public void setPathwayId(Long pathwayId) {
        if (this.pathwayId != null) {
        	onDataPointChange(0);
        }
        
        this.pathwayId = pathwayId;
    }
    
    public Long getPathwayId() {
        return this.pathwayId;
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
//        System.out.println("Data index: " + dataIndex);
        if (pathwayId == null)
            return; // Nothing to do. No pathway has been displayed.
        PathwayExpressionValue pathwayValues = dataModel.getPathwayExpressionValue(pathwayId);
        
        Map<Long, Double> compIdToValue = null;
        Map<Long, String> compIdToColor = null;
        Map<Long, String> compIdToExpressionId = null;
        
        if (pathwayValues != null) {
        	compIdToValue = pathwayValues.getExpressionValueForDataPoint(dataIndex);
        	compIdToColor = convertValueToColor(compIdToValue);
        	compIdToExpressionId = pathwayValues.getDbIdsToExpressionIds();
        }        
        
        DataPointChangeEvent event = new DataPointChangeEvent();
        event.setPathwayId(pathwayId);
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
