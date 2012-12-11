/*
 * Created on Sep 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.event.HoverEventHandler;
import org.reactome.diagram.event.PathwayChangeEvent;
import org.reactome.diagram.event.PathwayChangeEventHandler;
import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This customized widget used to draw pathway diagram.
 * @author gwu
 *
 */
public class PathwayDiagramPanel extends Composite implements ContextMenuHandler, RequiresResize {    // Use an AbsolutePanel so that controls can be placed onto on a canvas
    protected AbsolutePanel contentPane;
    // Pathway diagram should be drawn here
    private PathwayCanvas canvas;
    // Interactors shown here
    private InteractorCanvas interactorCanvas;
    // For overview
    private OverviewCanvas overview;
    // For all selection related stuff.
    private SelectionHandler selectionHandler;
    // For all hovering related stuff.
    private HoverHandler hoverHandler;
    // Used with a back-end RESTful API server
    private PathwayDiagramController controller;
    // To show popup menu
    private CanvasPopupMenu popupMenu;
    // Loading icon
    private Image loadingIcon;

    private Style style; 
    
    interface ImageResources extends ClientBundle {
    	@Source("ajax-loader.gif")
    	ImageResource loading();

    }
    
    public static final ImageResources IMAGES = GWT.create(ImageResources.class);
    
    public PathwayDiagramPanel() {
        init();
    }
    
    private void init() {
        // Set up style
        Resources resources = GWT.create(Resources.class);
        style = resources.pathwayDiagramStyle();
        style.ensureInjected();
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvas = new PathwayCanvas();
        
        // Keep the original information
        contentPane.add(canvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName(style.mainCanvas());
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        
        interactorCanvas = new InteractorCanvas();
        contentPane.add(interactorCanvas, 4, 4);
        interactorCanvas.setVisible(false);
        
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
        
        // Loading Icon
        loadingIcon = new Image(IMAGES.loading());
        loadingIcon.setVisible(false);
        contentPane.add(loadingIcon, 725, 340);
        
        initWidget(contentPane);
        // Add behaviors
        CanvasEventInstaller eventInstaller = new CanvasEventInstaller(this);
        eventInstaller.installHandlers();
        
        selectionHandler = new SelectionHandler(this);
        hoverHandler = new HoverHandler(this);
        hoverHandler.getTooltip().setStyleName(style.tooltip());
        controller = new PathwayDiagramController(this);
        
        popupMenu = new CanvasPopupMenu();
        popupMenu.setPathwayDiagramPanel(this);
        popupMenu.setStyleName(style.canvasPopup());
        addDomHandler(this, ContextMenuEvent.getType());
        
        //addTestCode();
    }
    
    private void addTestCode() {
//        PushButton testBtn = new PushButton("Test");
 //       contentPane.add(testBtn, 400, 4);
//    	testBtn.addClickHandler(new ClickHandler() {
            
          //  @Override
//        public void onClick(ClickEvent event) {
//                controller.listPathways();
                PathwayTreeBrowser treeBrowser = new PathwayTreeBrowser(PathwayDiagramPanel.this);
                treeBrowser.initTree();
//            }
//        });
        // Test selections
        SelectionEventHandler selectionHandler = new SelectionEventHandler() {
            
            @Override
            public void onSelectionChanged(SelectionEvent e) {
//                List<Long> selectedIds = e.getSelectedDBIds();
//                System.out.println("Selection: " + selectedIds);
                List<GraphObject> selectedObjects = e.getSelectedObjects();
                System.out.println("Selected objects: " + selectedObjects.size());
                for (GraphObject obj : selectedObjects) {
                    if (obj instanceof Node) {
                        Node node = (Node) obj;

                        System.out.println("Node: " + node.getDisplayName());
                        List<HyperEdge> reactions = node.getConnectedReactions();
                        System.out.println(" connected reactions: " + reactions.size());
                        for (HyperEdge edge : reactions)
                            System.out.println(edge.getDisplayName());
                    }
                }
            }
        };
        addSelectionEventHandler(selectionHandler);

        // Check hovered object change
        HoverEventHandler hoverHandler = new HoverEventHandler() {

			@Override
			public void onHover(HoverEvent e) {
				System.out.println("Hovered object: " + e.getHoveredObject().getDisplayName());
			}
        };	
        addHoverEventHandler(hoverHandler);
        
        
        // Check displayed pathway change
        PathwayChangeEventHandler pathwayHandler = new PathwayChangeEventHandler() {
            
            @Override
            public void onPathwayChange(PathwayChangeEvent event) {
                System.out.println("Current displayed pathway: " + event.getCurrentPathwayDBId());
            }
        };
        addPathwayChangeEventHandler(pathwayHandler);
    }
    
    public PathwayDiagramController getController() {
        return this.controller;
    }
    
    public CanvasPopupMenu getPopupMenu() {
        return popupMenu;
    }
        
    public void setSize(int windowWidth, int windowHeight) {
        int width = (int) (windowWidth - 40);
        int height = (int) (windowHeight - 40);
        super.setSize(width + "px", height + "px");
        onResize(width, height);
        
        if (interactorCanvas != null) {
        	interactorCanvas.setSize(width - 8 + "px", height - 8 + "px");
        	interactorCanvas.setCoordinateSpaceWidth(width - 8);
        	interactorCanvas.setCoordinateSpaceHeight(height - 8);
        }	
        
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
        // Get the old displayed pathway
    	CanvasPathway old = canvas.getPathway();
//        System.out.println("Set pathway: " + pathway.getReactomeId());
        // Set up the overview first so that it can draw correct rectangle.
        overview.setPathway(pathway);
        overview.update();
        canvas.setPathway(pathway);
        canvas.update();
        PathwayChangeEvent event = new PathwayChangeEvent();
        if (old != null)
            event.setPreviousPathwayDBId(old.getReactomeId());
        if (pathway != null)
            event.setCurrentPathwayDBId(pathway.getReactomeId());
        fireEvent(event);
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
    public void setPathway(String xml, Long dbId){
    	controller.loadDiagramForXML(xml, dbId);
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
    
    public Image getLoadingIcon() {
    	return this.loadingIcon;
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
    
    public void hover(int x, int y) {
       	Point hoveredPoint = getCorrectedCoordinates(x, y);
    
       	hoverHandler.hover(hoveredPoint);
    }

    public void hideTooltip() {
    	hoverHandler.getTooltip().hide();
    }
    
    
    /**
     * Do selection based on a mouse click or a touch event.
     * @param x
     * @param y
     */
    public void select(GwtEvent<? extends EventHandler> event, int x, int y) {
        // Need to consider both scale and translate
        Point correctedPoint = getCorrectedCoordinates(x, y);
    	
        selectionHandler.select(event,
        						correctedPoint.getX(), 
                                correctedPoint.getY());
    }
    
    private Point getCorrectedCoordinates(int x, int y) {
    	double scale = canvas.getScale();
    	
    	double correctedX = x - canvas.getTranslateX();
    	correctedX /= scale;
    	
    	double correctedY = y - canvas.getTranslateY();
    	correctedY /= scale;
    	
    	return new Point(correctedX, correctedY);
    }
    
    public void addSelectionEventHandler(SelectionEventHandler handler) {
        canvas.addHandler(handler, 
                          SelectionEvent.TYPE);
    }
    
    public void addHoverEventHandler(HoverEventHandler handler) {
    	canvas.addHandler(handler, 
    					  HoverEvent.TYPE);
    }
    
    public void addPathwayChangeEventHandler(PathwayChangeEventHandler handler) {
        addHandler(handler,
                   PathwayChangeEvent.TYPE);
    }
    
    protected void fireSelectionEvent(SelectionEvent event) {
        canvas.fireEvent(event);
        canvas.update();
    }
    
    /**
     * Get a list of selected objects displayed in this PathwayDiagramPanel.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        return selectionHandler.getSelectedObjects();
    }
    
    /**
     * Set a list of objects using their DB_IDs.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
        selectionHandler.setSelectionIds(dbIds);
    }
    
    /**
     * Select a single Object based on its DB_ID
     * @param dbId
     */
    public void setSelectionId(Long dbId) {
        List<Long> selection = new ArrayList<Long>(1);
        selection.add(dbId);
        setSelectionIds(selection);
    }
    
    /**
     * Reset all selection.
     */
    public void clearSelection() {
        selectionHandler.clearSelection();
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        canvas.update();
    }
    
    public Style getStyle() {
    	return this.style;
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
        
        String subMenu();
        
        String tooltip();
    }

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// Suppress browser context menu so canvas pop-up menu can be seen
		event.preventDefault();
		event.stopPropagation();
	}

	public InteractorCanvas getInteractorCanvas() {
		return interactorCanvas;
	}

	public void setInteractorCanvas(InteractorCanvas interactorCanvas) {
				
	}
}
