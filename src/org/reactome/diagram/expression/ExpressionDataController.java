/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
public class ExpressionDataController {
    private List<DataPointChangeEventHandler> dataPointChangeEventHandlers;
    // Customized widget to show controls used to navigation data points
    private HorizontalPanel navigationPane;
    // Size of this panel
    private int navPaneWidth = 275;
    private int navPaneHeight = 17;
    // Customized widget to show a color gradient
    private VerticalPanel colorPane;
    private int colorPaneWidth = 36;
    private int colorPaneHeight = 325;
    // Container holding the above two controls
    private AbsolutePanel container;
    // Offset between container and the actual size
    private int offsetWidth;
    private int offsetHeight;
    // For fetching icons
    private static Resources resources;
    
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
    
    private static Resources getResource() {
        if (resources == null)
            resources = GWT.create(Resources.class);
        return resources;
    }
    
    private void init() {
        initNavigationPane();
        initColorPane();
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
        container.addHandler(new ResizeHandler() {
            
            @Override
            public void onResize(ResizeEvent event) {
                if (!navigationPane.isVisible() || !colorPane.isVisible())
                    return;
                updatePosition();
            }
        }, ResizeEvent.getType());
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
        container = null; // Null it so that it can be displayed again.
    }
    
    public void addDataPointChangeEventHandler(DataPointChangeEventHandler handler) {
        if (dataPointChangeEventHandlers == null)
            dataPointChangeEventHandlers = new ArrayList<DataPointChangeEventHandler>();
        if (dataPointChangeEventHandlers.contains(handler))
            return;
        dataPointChangeEventHandlers.add(handler);
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
        navigationPane = new HorizontalPanel();
        String size = "25px";
        Resources resources = getResource();
        Image previous = new Image(resources.previous());
        previous.setSize(size, size);
        previous.setAltText("previous");
        previous.setTitle("previous");
        previous.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                System.out.println("Previous is clicked!");
            }
        });
        Image next = new Image(resources.next());
        next.setAltText("next");
        next.setTitle("next");
        next.setSize(size, size);
        next.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                System.out.println("Next is clicked!");
            }
        });
        Image close = new Image(resources.close());
        close.setAltText("close");
        close.setTitle("close");
        close.setSize(size, size);
        close.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                System.out.println("Close is clicked!");
            }
        });
        Label dataLabel = new Label("Data Point");
        
        navigationPane.add(previous);
        navigationPane.setCellVerticalAlignment(previous,
                                                HasVerticalAlignment.ALIGN_MIDDLE);
        navigationPane.add(dataLabel);
        navigationPane.setCellVerticalAlignment(dataLabel,
                                                HasVerticalAlignment.ALIGN_MIDDLE);
        navigationPane.setCellHorizontalAlignment(dataLabel, 
                                                  HasHorizontalAlignment.ALIGN_CENTER);
        HorizontalPanel panel1 = new HorizontalPanel();
        navigationPane.add(panel1);
        navigationPane.setCellVerticalAlignment(panel1,
                                                HasVerticalAlignment.ALIGN_MIDDLE);
        navigationPane.setCellHorizontalAlignment(panel1, 
                                                  HasHorizontalAlignment.ALIGN_RIGHT);
        panel1.add(next);
        panel1.setCellVerticalAlignment(next,
                                                HasVerticalAlignment.ALIGN_MIDDLE);
        panel1.setCellHorizontalAlignment(next, 
                                                  HasHorizontalAlignment.ALIGN_RIGHT);
        panel1.add(close);
        panel1.setCellVerticalAlignment(close,
                                                HasVerticalAlignment.ALIGN_MIDDLE);
        navigationPane.setCellHorizontalAlignment(close, 
                                                  HasHorizontalAlignment.ALIGN_RIGHT);
    }
    
    private void initColorPane() {
        colorPane = new VerticalPanel();
        
        Image image = new Image(getResource().colorSpectrum());
        colorPane.add(image);
    }
    
}
