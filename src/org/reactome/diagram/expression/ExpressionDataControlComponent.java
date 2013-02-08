/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This customized GUI is used to present the user a way to select a data point, and
 * show a color bars for expression values. 
 * @author gwu
 *
 */
public class ExpressionDataControlComponent {
    private List<DataPointChangeEventHandler> dataPointChangeEventHandlers;
    // Customized widget to show controls used to navigation data points
    private HorizontalPanel navigationPane;
    // Customized widget to show a color gradient
    private VerticalPanel colorPane;
    // Container holding the above two controls
    private AbsolutePanel container;
    
    public ExpressionDataControlComponent() {
        init();
    }
    
    private void init() {
        initNavigationPane();
        initColorPane();
    }
    
    /**
     * Display this component in the specified composite.
     * @param composite
     */
    public void display(AbsolutePanel container) {
        this.container = container;
        int width = container.getOffsetWidth();
        int height = container.getOffsetHeight();
        // Set up position for the navigation pane
        int x = (width - navigationPane.getOffsetWidth()) / 2;
        int y = height - navigationPane.getOffsetHeight();
        container.add(navigationPane, x, y);
        // Set up position for the color panel
        x = (width - colorPane.getOffsetWidth() - 10); // Some extra space
        y = 50; 
        container.add(colorPane, x, y);
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
    
    private void initNavigationPane() {
        navigationPane = new HorizontalPanel();
    }
    
    private void initColorPane() {
        colorPane = new VerticalPanel();
    }
    
}
