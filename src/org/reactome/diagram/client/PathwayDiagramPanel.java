/*
 * Created on Sep 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.List;

import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This customized wiget is used to draw pathway diagram.
 * @author gwu
 *
 */
public class PathwayDiagramPanel extends Composite implements ContextMenuHandler, RequiresResize {
    // Use an AbsolutePanel so that controls can be placed onto on a canvas
    protected AbsolutePanel contentPane;
    // Pathway diagram should be drawn here
    private PathwayCanvas canvas;
    // For overview
    private OverviewCanvas overview;
    // For all selection related stuff.
    private SelectionHandler selectionHandler;
    // Used with a back-end RESTful API server
    private PathwayDiagramController controller;
    // To show popup menu
    private CanvasPopupMenu popupMenu;
    
    public PathwayDiagramPanel() {
        init();
    }
    
    private void init() {
        // Set up style
        Resources resources = GWT.create(Resources.class);
        Style style = resources.pathwayDiagramStyle();
        style.ensureInjected();
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvas = new PathwayCanvas();
        // Keep the original information
        contentPane.add(canvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName(style.mainCanvas());
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        // Set up overview
        overview = new OverviewCanvas();
        // the width should be fixed
        overview.setCoordinateSpaceWidth(200);
        overview.setCoordinateSpaceHeight(1); // This is temporary
        overview.setStyleName(style.overViewCanvas());
        contentPane.add(overview, 1, 4);
        overview.setVisible(false); // Don't show it!
        // Controls
        PathwayCanvasControls controls = new PathwayCanvasControls(canvas);
        controls.setStyleName(style.controlPane());
        contentPane.add(controls, 4, 4);
        initWidget(contentPane);
        // Add behaviors
        CanvasEventInstaller eventInstaller = new CanvasEventInstaller(this);
        eventInstaller.installHandlers();
        
        selectionHandler = new SelectionHandler(this);
        controller = new PathwayDiagramController(this);
        
        popupMenu = new CanvasPopupMenu();
        popupMenu.setPathwayDiagramPanel(this);
        popupMenu.setStyleName(style.canvasPopup());
        addDomHandler(this, ContextMenuEvent.getType());
        
//        addTestCode();
    }
    
    private void addTestCode() {
        PushButton testBtn = new PushButton("Test");
        contentPane.add(testBtn, 400, 4);
        testBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
//                controller.listPathways();
                PathwayTreeBrowser treeBrowser = new PathwayTreeBrowser(PathwayDiagramPanel.this);
                treeBrowser.initTree();
            }
        });
        // Test selections
        SelectionEventHandler selectionHandler = new SelectionEventHandler() {
            
            @Override
            public void onSelectionChanged(SelectionEvent e) {
                List<Long> selectedIds = e.getSelectedDBIds();
                System.out.println("Selection: " + selectedIds);
            }
        };
        addSelectionEventHandler(selectionHandler);
    }
    
    public PathwayDiagramController getController() {
        return this.controller;
    }
    
    public void onContextMenu(ContextMenuEvent event) {
        popupMenu.showPopupMenu(event);
    }
    
    public void setSize(int windowWidth, int windowHeight) {
        int width = (int) (windowWidth - 40);
        int height = (int) (windowHeight - 40);
        super.setSize(width + "px", height + "px");
        onResize(width, height);
//        canvas.setSize(width - 8 + "px", height - 8 + "px");
//        canvas.setCoordinateSpaceWidth(width - 8);
//        canvas.setCoordinateSpaceHeight(height - 8);
//        // Need to reset the overview position so that it stays at the bottom-left corner
//        if (!overview.isVisible())
//            overview.setVisible(true);
//        overview.updatePosition();
    }
    
    public void onResize() {
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        onResize(width, height);
    }
    
    private void onResize(int width, int height) {
        canvas.setSize(width - 8 + "px", height - 8 + "px");
        canvas.setCoordinateSpaceWidth(width - 8);
        canvas.setCoordinateSpaceHeight(height - 8);
        // Need to reset the overview position so that it stays at the bottom-left corner
        if (!overview.isVisible())
            overview.setVisible(true);
        overview.updatePosition();
        update();
    }
    
    public void setPathway(CanvasPathway pathway) {
//        System.out.println("Set pathway: " + pathway.getReactomeId());
        // Set up the overview first so that it can draw correct rectangle.
        overview.setPathway(pathway);
        overview.update();
        canvas.setPathway(pathway);
        canvas.update();
    }
    
    /**
     * Set the pathway to be displayed whose DB_ID is the same as the specified dbId parametmer.
     * @param dbId
     */
    public void setPathway(Long dbId) {
        controller.loadDiagramForDBId(dbId);
    }
    
    /**
     * Set the pathway to be displayed.
     * @param xml
     */
    public void setPathway(String xml){
    	controller.loadDiagramForXML(xml);
    }
    
    /**
     * Set the RESTfulService URL
     * @param restURL the REST URL
     */
    public void setRestServiceURL(String restURL){
    	controller.setHostUrl(restURL);
    }
    
    public CanvasPathway getPathway() {
        return canvas.getPathway();
    }
    
    public void translate(double dx, double dy) {
        canvas.translate(dx, dy);
    }
    
    public void scale(double scale) {
        canvas.scale(scale);
    }
    
    public void reset() {
        canvas.reset();
    }
    
    public PathwayCanvas getCanvas() {
        return this.canvas;
    }
    
    public OverviewCanvas getOverview() {
        return this.overview;
    }
    
    /**
     * Do selection based on a mouse click or a touch event.
     * @param x
     * @param y
     */
    public void select(int x, int y) {
        // Need to consider both scale and translate
        double correctedX = x - canvas.getTranslateX();
        correctedX /= canvas.getScale();
        double correctedY = y - canvas.getTranslateY();
        correctedY /= canvas.getScale();
        selectionHandler.select(correctedX, 
                                correctedY);
    }
    
    public void addSelectionEventHandler(SelectionEventHandler handler) {
        canvas.addHandler(handler, 
                          SelectionEvent.TYPE);
    }
    
    protected void fireSelectionEvent(SelectionEvent event) {
        canvas.fireEvent(event);
    }
    
    /**
     * Get a list of selected objects displayed in this PathwayDiagramPanel.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        return selectionHandler.getSelectedObjects();
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        canvas.update();
    }
    
    /**
     * Mainly to load Style
     */
    interface Resources extends ClientBundle {
        @Source(Style.DEFAULT_CSS)
        Style pathwayDiagramStyle();
    }
    
    /**
     * Used to provide CSS style
     */
    interface Style extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String DEFAULT_CSS = "org/reactome/diagram/client/PathwayDiagram.css";
        
        String mainCanvas();
        
        String controlPane();
        
        String overViewCanvas();
        
        String canvasPopup();
    }
}
