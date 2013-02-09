/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized GUI is used to present the user a way to select a data point, and
 * show a color bars for expression values. 
 * @author gwu
 *
 */
public class ExpressionDataController implements ResizeHandler {
    // Keep HandlerRegistration in order to remove it
    private HandlerRegistration handlerRegistration;
    private List<DataPointChangeEventHandler> dataPointChangeEventHandlers;
    // Customized widget to show controls used to navigation data points
    private NavigationPane navigationPane;
    // Size of this panel
    private int navPaneWidth = 275;
    private int navPaneHeight = 17;
    // Customized widget to show a color gradient
    private ColorSpectrumPane colorPane;
    private int colorPaneWidth = 36;
    private int colorPaneHeight = 325;
    // Container holding the above two controls
    private AbsolutePanel container;
    // Offset between container and the actual size
    private int offsetWidth;
    private int offsetHeight;
    // For fetching icons
    private static Resources resources;
    // Data models handled by this controller
    private ReactomeExpressionValue dataModel;
    private Long pathwayId; // Displayed pathway id
    
    /**
     * Some icons
     * @author gwu
     *
     */
    interface Resources extends ClientBundle {
        @Source("left.png")
        ImageResource previous();
        @Source("right.png")
        ImageResource next();
        @Source("whiteclose.png")
        ImageResource close();
        @Source("ColorSpectrum.png")
        ImageResource colorSpectrum();
    }
    
    public ExpressionDataController() {
        init();
    }
    
    static Resources getResource() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }
    
    private void init() {
        initNavigationPane();
        initColorPane();
    }
    
    public void setDataModel(ReactomeExpressionValue model) {
        this.dataModel = model;
        navigationPane.setDataModel(model);
    }
    
    public ReactomeExpressionValue getDataModel() {
        return this.dataModel;
    }
    
    public void setPathwayId(Long pathwayId) {
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
        // Set up position for the color panel
        colorPane.setSize(colorPaneWidth + "px",
                          colorPaneHeight + "px");
        x = (width - colorPaneWidth - 10); // Some extra space
        y = 50; 
        container.add(colorPane, x, y);
        offsetWidth = container.getOffsetWidth() - width;
        offsetHeight = container.getOffsetHeight() - height;
        handlerRegistration = this.container.addHandler(this, ResizeEvent.getType());
    }
    
    @Override
    public void onResize(ResizeEvent event) {
        if (!navigationPane.isVisible() || !colorPane.isVisible())
            return;
        updatePosition();
    }
    
    private void updatePosition() {
        int width = container.getOffsetWidth() - offsetWidth;
        int height = container.getOffsetHeight() - offsetHeight;
        int x = (width - navPaneWidth) / 2;
        int y = height - navPaneHeight;
        container.setWidgetPosition(navigationPane, x, y);
        x = (width - colorPaneWidth - 10); // Some extra space
        y = 50; 
        container.setWidgetPosition(colorPane, x, y);
    }
    
    public void dispose() {
        if (container == null)
            return; // It has not been displayed
        container.remove(navigationPane);
        container.remove(colorPane);
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        container = null; // Null it so that it can be displayed again.
    }
    
    public void addDataPointChangeEventHandler(DataPointChangeEventHandler handler) {
        if (dataPointChangeEventHandlers == null)
            dataPointChangeEventHandlers = new ArrayList<DataPointChangeEventHandler>();
        if (dataPointChangeEventHandlers.contains(handler))
            return;
        dataPointChangeEventHandlers.add(handler);
    }
    
    private void onDataPointChange(Integer dataIndex) {
        if (pathwayId == null)
            return; // Nothing to do. No pathway has been displayed.
        PathwayExpressionValue pathwayValues = dataModel.getPathwayExpressionValue(pathwayId);
        Map<Long, Double> compIdToValue = pathwayValues.getExpressionValueForDataPoint(dataIndex);
        List<Double> values = new ArrayList<Double>(compIdToValue.values());
        Collections.sort(values);
        Double min = values.get(0);
        Double max = values.get(values.size() - 1);
//        colorPane.setValues(min, max);
    }
    
    protected void fireDataPointChangeEvent() {
        if (dataPointChangeEventHandlers == null)
            return;
        DataPointChangeEvent event = new DataPointChangeEvent();
        for (DataPointChangeEventHandler handler : dataPointChangeEventHandlers)
            handler.onDataPointChanged(event);
    }
    
    public void setNavigationPaneStyle(String style) {
        navigationPane.setStyleName(style);
    }
    
    public void setColorPaneStyle(String style) {
        colorPane.setStyleName(style);
    }
    
    private void initNavigationPane() {
        navigationPane = new NavigationPane();
        navigationPane.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                onDataPointChange(event.getValue());
            }
        });
    }
    
    private void initColorPane() {
        colorPane = new ColorSpectrumPane();
    }
    
    private class ColorSpectrumPane extends AbsolutePanel {
        // Labels with some dumb values for debugging purposes
        private Label bottomLabel = new Label("3.50");
        private Label middleLabel = new Label("6.75");
        private Label topLabel = new Label("10.00");
        
        public ColorSpectrumPane() {
            init();
        }
        
        private void init() {
            Image image = new Image(getResource().colorSpectrum());
            add(image, 0, 0);
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.setSize(image.getWidth() + "px", 
                                  image.getHeight() + "px");
            verticalPanel.add(topLabel);
            verticalPanel.setCellVerticalAlignment(topLabel, HasVerticalAlignment.ALIGN_TOP);
            verticalPanel.setCellHorizontalAlignment(topLabel, HasHorizontalAlignment.ALIGN_CENTER);
            verticalPanel.add(middleLabel);
            verticalPanel.setCellVerticalAlignment(middleLabel, HasVerticalAlignment.ALIGN_MIDDLE);
            verticalPanel.setCellHorizontalAlignment(middleLabel, HasHorizontalAlignment.ALIGN_CENTER);
            verticalPanel.add(bottomLabel);
            verticalPanel.setCellVerticalAlignment(bottomLabel, HasVerticalAlignment.ALIGN_BOTTOM);
            verticalPanel.setCellHorizontalAlignment(bottomLabel, HasHorizontalAlignment.ALIGN_CENTER);
            add(verticalPanel, 0, 0);
        }
    }
    
    private class NavigationPane extends HorizontalPanel implements HasValue<Integer> {
        private Label dataLabel;
        private Image previous;
        private Image next;
        private Image close;
        private List<String> dataPoints;
        private Integer currentDataIndex = 0;
        
        public NavigationPane() {
            init();
        }
        
        private void init() {
            Resources resources = ExpressionDataController.getResource();
            previous = new Image(resources.previous());
            previous.setAltText("previous");
            previous.setTitle("previous");
            previous.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    System.out.println("Previous is clicked!");
                }
            });
            next = new Image(resources.next());
            next.setAltText("next");
            next.setTitle("next");
            next.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    System.out.println("Next is clicked!");
                }
            });
            close = new Image(resources.close());
            close.setAltText("close");
            close.setTitle("close");
            close.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    System.out.println("Close is clicked!");
                }
            });
            dataLabel = new Label("Data Point");
            
            add(previous);
            setCellVerticalAlignment(previous,
                                     HasVerticalAlignment.ALIGN_MIDDLE);
            add(dataLabel);
            setCellVerticalAlignment(dataLabel,
                                     HasVerticalAlignment.ALIGN_MIDDLE);
            setCellHorizontalAlignment(dataLabel, 
                                       HasHorizontalAlignment.ALIGN_CENTER);
            HorizontalPanel panel1 = new HorizontalPanel();
            add(panel1);
            setCellVerticalAlignment(panel1,
                                     HasVerticalAlignment.ALIGN_MIDDLE);
            setCellHorizontalAlignment(panel1, 
                                       HasHorizontalAlignment.ALIGN_RIGHT);
            panel1.add(next);
            panel1.setCellVerticalAlignment(next,
                                            HasVerticalAlignment.ALIGN_MIDDLE);
            panel1.setCellHorizontalAlignment(next, 
                                              HasHorizontalAlignment.ALIGN_RIGHT);
            panel1.add(close);
            panel1.setCellVerticalAlignment(close,
                                            HasVerticalAlignment.ALIGN_MIDDLE);
            setCellHorizontalAlignment(close, 
                                       HasHorizontalAlignment.ALIGN_RIGHT);
            // To avoid null exception
            dataPoints = new ArrayList<String>();
            // test code
            for (int i = 0; i < 5; i ++) {
                dataPoints.add(i + " hr");
            }
            installHandlers();
        }
        
        private void installHandlers() {
            close.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    ExpressionDataController.this.dispose();
                }
            });
            
            previous.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    setDataPoint(--currentDataIndex);
                }
            });
            
            next.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {
                    setDataPoint(++currentDataIndex);
                }
            });
        }
        
        private void setDataPoint(int dataPoint) {
            // Two checks to cycle all data points
            if (dataPoint < 0)
                dataPoint = dataPoints.size() - 1;
            else if (dataPoint > dataPoints.size() - 1)
                dataPoint = 0;
            if (currentDataIndex == dataPoint)
                return;
            Integer old = this.currentDataIndex;
            this.currentDataIndex = dataPoint;
            dataLabel.setText((currentDataIndex + 1) + "/" + dataPoints.size() + ": " + 
                              dataPoints.get(currentDataIndex));
            ValueChangeEvent.fireIfNotEqual(this, 
                                            old, 
                                            currentDataIndex);
        }
        
        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
            return addHandler(handler, 
                              ValueChangeEvent.getType());
        }

        @Override
        public Integer getValue() {
            return currentDataIndex;
        }

        @Override
        public void setValue(Integer value) {
            setDataPoint(value);
        }

        @Override
        public void setValue(Integer value, boolean fireEvents) {
            setDataPoint(value);
        }

        public void setDataModel(ReactomeExpressionValue dataModel) {
            List<String> values = dataModel.getExpressionColumnNames();
            dataPoints.clear();
            dataPoints.addAll(values);
            setDataPoint(0); // Start from the first point
        }
    }
    
}
