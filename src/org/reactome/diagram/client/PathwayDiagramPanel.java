/*
 * Created on Sep 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.event.HoverEventHandler;
import org.reactome.diagram.event.PathwayChangeEvent;
import org.reactome.diagram.event.PathwayChangeEventHandler;
import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.expression.ExpressionDataController;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.Node;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This customized widget used to draw pathway diagram.
 * @author gwu
 *
 */
public class PathwayDiagramPanel extends Composite implements ContextMenuHandler, RequiresResize {    // Use an AbsolutePanel so that controls can be placed onto on a canvas
    protected AbsolutePanel contentPane;
    
    private List<DiagramCanvas> canvasList;
    // Pathway diagram should be drawn here
    private PathwayCanvas pathwayCanvas;
    // Interactors shown here
    private InteractorCanvas interactorCanvas;
    // For overview
    private OverviewCanvas overview;
    // Used with a back-end RESTful API server
    private PathwayDiagramController controller;
    // To show popup menu
    private CanvasPopupMenu popupMenu;
    // Loading icon
    private Image loadingIcon;

    private PathwayCanvasControls controls;
    
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
                
        controller = new PathwayDiagramController(this);
        
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvasList = new ArrayList<DiagramCanvas>();
        
        overview = new OverviewCanvas(this);
        
        pathwayCanvas = new PathwayCanvas(this);         
        // Keep the original information
        contentPane.add(pathwayCanvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName(style.mainCanvas());
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        canvasList.add(pathwayCanvas);        
        
        interactorCanvas = new InteractorCanvas(this);
        //interactorCanvas.setStyleName(style.interactorCanvas());
        contentPane.add(interactorCanvas, 4, 4);
        //interactorCanvas.setVisible(false);
        canvasList.add(interactorCanvas);
        
        // Set up overview
        //overview = new OverviewCanvas();
        // the width should be fixed
        overview.setCoordinateSpaceWidth(200);
        overview.setCoordinateSpaceHeight(1); // This is temporary
        overview.setStyleName(style.overViewCanvas());
        contentPane.add(overview, 1, 4);
        overview.setVisible(false); // Don't show it!
        
        // Controls
        controls = new PathwayCanvasControls(this);
        controls.setStyleName(style.controlPane());
        contentPane.add(controls, 4, 4);
        
        // Loading Icon
        loadingIcon = new Image(IMAGES.loading());
        loadingIcon.setVisible(false);
        contentPane.add(loadingIcon, 725, 340);
        
        initWidget(contentPane);

        // Add behaviors
        CanvasEventInstaller eventInstaller = canvasList.get(canvasList.size() - 1).getEventInstaller();
        eventInstaller.installUserInputHandlers();    
        eventInstaller.installDiagramEventHandlers();
        
        popupMenu = new CanvasPopupMenu();
        popupMenu.setPathwayDiagramPanel(this);
        popupMenu.setStyleName(style.canvasPopup());
        addDomHandler(this, ContextMenuEvent.getType());
        
        addTestCode();
    }
    
    private void addTestDataPointDisplay() {
        final PushButton testBtn = new PushButton("Show Data Point");
        contentPane.add(testBtn, 700, 4);
        testBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                ExpressionDataController control = new ExpressionDataController();
                control.setColorPaneStyle(style.colorBar());
                control.setNavigationPaneStyle(style.dataPointControl());
                control.display(contentPane,
                                pathwayCanvas.getCoordinateSpaceWidth(),
                                pathwayCanvas.getCoordinateSpaceHeight());
            }
        });
    }
    
    private void addTestCode() {
        addTestDataPointDisplay();
        PushButton testBtn = new PushButton("Build Tree");
        contentPane.add(testBtn, 500, 4);
    	testBtn.addClickHandler(new ClickHandler() {
            
    		//ExpressionProcessor ep = new ExpressionProcessor(GWT.getHostPageBaseURL() + "ExpressionLevelJsonForJoel.txt");	
    		//  @Override
    		public void onClick(ClickEvent event) {
        		//ep.retrieveExpressionData();
                //controller.listPathways();
                PathwayTreeBrowser treeBrowser = new PathwayTreeBrowser(PathwayDiagramPanel.this);
                treeBrowser.initTree();
            }
        });
    	
    	PushButton removeAllBtn = new PushButton("Remove All");
    	contentPane.add(removeAllBtn, 600, 4);
    	removeAllBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                removeAll();
            }
        });
                
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

        //Check hovered object change
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

    public PathwayCanvasControls getControls() {
    	return controls;
    }
    
    public void setSize(int windowWidth, int windowHeight) {
        int width = (int) (windowWidth - 40);
        int height = (int) (windowHeight - 40);
        super.setSize(width + "px", height + "px");
        onResize(width, height);
    }
    
    public void onResize() {
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        onResize(width, height);
    }
    
    private void onResize(int width, int height) {
        for (DiagramCanvas canvas : canvasList) {
    	   	canvas.setSize(width - 8 + "px", height - 8 + "px");
        	canvas.setCoordinateSpaceWidth(width - 8);
        	canvas.setCoordinateSpaceHeight(height - 8);
        }	
        
        
        // Need to reset the overview position so that it stays at the bottom-left corner
        if (!overview.isVisible())
            overview.setVisible(true);
        overview.updatePosition();
        update();
        LocalResizeEvent event = new LocalResizeEvent(width, height);
        contentPane.fireEvent(event);
    }
    
    protected void setCanvasPathway(CanvasPathway pathway) {
        // Get the old displayed pathway
    	CanvasPathway old = pathwayCanvas.getPathway();
    	//        System.out.println("Set pathway: " + pathway.getReactomeId());
        // Set up the overview first so that it can draw correct rectangle.
        overview.setPathway(pathway);
//        overview.update();
        interactorCanvas.removeAllProteins();
        pathwayCanvas.setPathway(pathway);
        //pathwayCanvas.update();
        update();
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
     * Call this method to remove all displayed objects in this component.
     */
    public void removeAll() {
        setCanvasPathway(null);
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
        return pathwayCanvas.getPathway();
    }
    
    public Image getLoadingIcon() {
    	return this.loadingIcon;
    }
    
    public void translate(double dx, double dy, boolean fromOverview) {
    	if (!fromOverview)
    		translate(dx, dy);
    }
    
    public void translate(double dx, double dy) {
        for (DiagramCanvas canvas : canvasList) 	
        	canvas.translate(dx, dy);
    }
    
    public void scale(double scale) {
        for (DiagramCanvas canvas : canvasList)	
    		canvas.scale(scale);
    }
    
    public void reset() {
    	for (DiagramCanvas canvas : canvasList)
        	canvas.reset();
    }
    
    public PathwayCanvas getPathwayCanvas() {
        return this.pathwayCanvas;
    }
    
    public OverviewCanvas getOverview() {
        return this.overview;
    }

    public void drag(Node node, int dx, int dy) {
    	this.interactorCanvas.drag((InteractorNode) node, dx, dy);
    }
    
    public void hover(int x, int y) {
       	Point hoveredPoint = pathwayCanvas.getCorrectedCoordinates(x, y);
    
       	Collections.reverse(canvasList);       	
       	
       	for (DiagramCanvas canvas : canvasList) {
       		if (canvas.getHoverHandler().hover(hoveredPoint) != null) {
       			stopHoveringExceptFor(canvas);
       			canvas.getHoverHandler().fireHoverEvent();
       			break;
       		}	
       	}
       	
       	Collections.reverse(canvasList);
       	
    }

    public void hideTooltip() {
    	for (DiagramCanvas canvas : canvasList)
    		canvas.getHoverHandler().getTooltip().hide();
    }
    
    public void stopHoveringExceptFor(DiagramCanvas c) {
    	for (DiagramCanvas canvas : canvasList) {
    		if (canvas == c)
    			continue;
    		HoverHandler hh = canvas.getHoverHandler();
    		hh.getTooltip().hide();
    		hh.clearHoveredObject();
    	}
    }
    
    
    /**
     * Do selection based on a mouse click or a touch event.
     * @param x
     * @param y
     */
    public void select(GwtEvent<? extends EventHandler> event, int x, int y) {
        // Need to consider both scale and translate
        Point correctedPoint = pathwayCanvas.getCorrectedCoordinates(x, y);
    	        
        Collections.reverse(canvasList);
        
        for (DiagramCanvas canvas : canvasList) {
        	if (canvas.getSelectionHandler().select(event, correctedPoint) != null) {
        		canvas.getSelectionHandler().fireSelectionEvent();
        		break;        		
        	}
                      
        	if (canvas == canvasList.get(canvasList.size() - 1)) {
        		canvas.getSelectionHandler().fireSelectionEvent();
        	}
        }
        	
        Collections.reverse(canvasList);
    }
        
    public void addSelectionEventHandler(SelectionEventHandler handler) {
        addHandler(handler, 
                          SelectionEvent.TYPE);
    }
    
    public void addHoverEventHandler(HoverEventHandler handler) {
    	addHandler(handler, 
    					  HoverEvent.TYPE);
    }
    
    public void addPathwayChangeEventHandler(PathwayChangeEventHandler handler) {
        addHandler(handler,
                   PathwayChangeEvent.TYPE);
    }
    
    protected void fireSelectionEvent(SelectionEvent event) {
        fireEvent(event);
        pathwayCanvas.update();
    }
    
    /**
     * Get a list of selected objects displayed in this PathwayDiagramPanel.
     * @return
     */
    public List<GraphObject> getSelectedObjects() {
        List<GraphObject> selectedObjects = null;
        
        for (DiagramCanvas canvas : canvasList) {
        	selectedObjects = canvas.getSelectionHandler().getSelectedObjects();     
        	if (!selectedObjects.isEmpty())
        		break;
        }
    	
    	return selectedObjects;
    }
    
    /**
     * Set a list of objects using their DB_IDs.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
        for (DiagramCanvas canvas : canvasList)
    		canvas.getSelectionHandler().setSelectionIds(dbIds);
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
    	for (DiagramCanvas canvas : canvasList)
    		canvas.getSelectionHandler().clearSelection();
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        for (DiagramCanvas canvas : canvasList)
    		canvas.update();
    }
    
    public void setCursor(Cursor cursor) {
    	getElement().getStyle().setCursor(cursor);    	
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

        String interactorCanvas();
        
        String controlPane();
        
        String overViewCanvas();
        
        String canvasPopup();
        
        String subMenu();
        
        String tooltip();
        
        String dataPointControl();
        
        String colorBar();
    }

    public ListBox getInteractorDBList() {
    	ListBox interactorDBList = new ListBox();
    	    	
    	for (String db : InteractorCanvas.getInteractorDBMap().keySet()) 
    		interactorDBList.addItem(db, InteractorCanvas.getInteractorDBMap().get(db));	
    		
    	return interactorDBList;
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
	
	private class LocalResizeEvent extends ResizeEvent {
	    
	    public LocalResizeEvent(int width, int height) {
	        super(width, height);
	    }
	    
	}
}
