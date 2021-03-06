/* 


 * Created on Sep 23, 2011
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.reactome.diagram.analysis.factory.AnalysisModelException;
import org.reactome.diagram.analysis.factory.AnalysisModelFactory;
import org.reactome.diagram.analysis.model.AnalysisResult;
import org.reactome.diagram.event.HoverEvent;
import org.reactome.diagram.event.HoverEventHandler;
import org.reactome.diagram.event.ParticipatingMoleculeSelectionEvent;
import org.reactome.diagram.event.ParticipatingMoleculeSelectionEventHandler;
import org.reactome.diagram.event.PathwayChangeEvent;
import org.reactome.diagram.event.PathwayChangeEventHandler;
import org.reactome.diagram.event.SelectionEvent;
import org.reactome.diagram.event.SelectionEventHandler;
import org.reactome.diagram.event.SubpathwaySelectionEvent;
import org.reactome.diagram.event.SubpathwaySelectionEventHandler;
import org.reactome.diagram.event.ZoomedOutEvent;
import org.reactome.diagram.event.ZoomedOutEventHandler;
import org.reactome.diagram.expression.DataController;
import org.reactome.diagram.expression.ExpressionDataController;
import org.reactome.diagram.expression.OverrepresentationDataController;
import org.reactome.diagram.expression.SpeciesComparisonDataController;
import org.reactome.diagram.expression.event.DataPointChangeEvent;
import org.reactome.diagram.expression.event.DataPointChangeEventHandler;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEvent;
import org.reactome.diagram.expression.event.ExpressionOverlayStopEventHandler;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.ExpressionCanvasModel;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.InteractorCanvasModel;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
    
    private List<DiagramCanvas> canvasList;
    // Pathway diagram should be drawn here
    private PathwayCanvas pathwayCanvas;
    // Expression and species comparison overlay shown here
    private ExpressionCanvas expressionCanvas;
    // Popup for showing complex component expression or species comparison data
    //private ComplexComponentPopup complexComponentPopup;
    // GUI component for expression or species comparison data
    private DataController overlayDataController;
    // Interactors shown here
    private InteractorCanvas interactorCanvas;
    // Interactor Database Model
    private InteractorCanvasModel interactorCanvasModel;
    // For overview
    private OverviewCanvas overview;
    // Used with a back-end RESTful API server
    private PathwayDiagramController controller;
    // To show popup menu
    private CanvasPopupMenu popupMenu;
    // Options Menu Icon 
    private OptionsMenuIcon optionsMenuIcon;
    // Loading icon
    private Image loadingIcon;

    private PathwayCanvasControls controls;

    private SearchPopup searchBar;
    
    private Style style; 
    
    interface ImageResources extends ClientBundle {
    	@Source("ajax-loader.gif")
    	ImageResource loading();    	   	
    }
    
    public static final ImageResources IMAGES = GWT.create(ImageResources.class);
    
    public PathwayDiagramPanel() {
        this(false);
    }
    
    public PathwayDiagramPanel(boolean testMode) {
    	init();
    	if (testMode)
    		addTestCode();
    }
    
    private void init() {
        // Set up style
        Resources resources = GWT.create(Resources.class);
        style = resources.pathwayDiagramStyle();
        style.ensureInjected();
                
        controller = PathwayDiagramController.getInstance();
        
        // Use an AbsolutePanel so that controls can be placed onto on a canvas
        contentPane = new AbsolutePanel();
        canvasList = new ArrayList<DiagramCanvas>();
        
        overview = new OverviewCanvas(this);
        
        pathwayCanvas = new PathwayCanvas(this);         
        CanvasEventInstaller eventInstaller = pathwayCanvas.getEventInstaller();
        eventInstaller.installOverviewEventHandler();
        eventInstaller.installUserInputHandlers();
        
        interactorCanvasModel = new InteractorCanvasModel();
        controller.setInteractorDBList(interactorCanvasModel);
        
// Keep the original information
        contentPane.add(pathwayCanvas, 4, 4); // Give it some buffer space
        contentPane.setStyleName(style.mainCanvas());
//        canvas.setSize("100%", "100%");
//        contentPane.setSize("100%", "100%");
        canvasList.add(pathwayCanvas);        
        
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
        
        searchBar = new SearchPopup(this);
        searchBar.getElement().getStyle().setMarginLeft(6, Unit.PX);
        addPathwayChangeEventHandler(searchBar);
        addSelectionEventHandler(searchBar);
        
        HorizontalPanel allControls = new HorizontalPanel();
        allControls.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        allControls.add(controls);
        allControls.add(searchBar);
        contentPane.add(allControls, 4, 4);
        
        // Options Menu Icon
        optionsMenuIcon = new OptionsMenuIcon(this);
        optionsMenuIcon.setVisible(false);
        contentPane.add(optionsMenuIcon);
        
        // Loading Icon
        loadingIcon = new Image(IMAGES.loading());
        loadingIcon.setVisible(false);
        contentPane.add(loadingIcon, 725, 340);
                
        
        initWidget(contentPane);        
                        
        popupMenu = new CanvasPopupMenu(this);
        //popupMenu.setStyleName(style.canvasPopup());
        addDomHandler(this, ContextMenuEvent.getType());
    }
    
    public List<DiagramCanvas> getCanvasList() {
		return canvasList;
	}

	private void addTestDataPointDisplay() {
        final PushButton testBtn = new PushButton("Show Data Point");
        contentPane.add(testBtn, 700, 4);
        testBtn.addClickHandler(new ClickHandler() {
            private int clicks = 0;
        	
            @Override
            public void onClick(ClickEvent event) {
                String token = clicks % 2 == 0 ? "MjAxNDA3MTYxNzUyMDNfMw%253D%253D" : "MDYxMzExMjQ1NV8x"; 
            	String resourceName = "TOTAL";
            	
            	showAnalysisData(token, resourceName);
            	clicks++;
            }
        });
    }
    
    private void addTestCode() {
        // Just want to load a pathway diagram
        addTestDataPointDisplay();
        PushButton testPathwayBtn = new PushButton("Load Pathway");
        contentPane.add(testPathwayBtn, 400, 4);
        testPathwayBtn.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                setPathway(1912422L); // Pre-Notch Expression and Processing for showing genes and miRNAs
            }
        });
        
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
        //HoverEventHandler hoverHandler = new HoverEventHandler() {

		//	@Override
        //	public void onHover(HoverEvent e) {
		//		System.out.println("Hovered object: " + e.getHoveredObject().getDisplayName());
		//	}
        //};	
        //addHoverEventHandler(hoverHandler);
        
        
        // Check displayed pathway change
        PathwayChangeEventHandler pathwayHandler = new PathwayChangeEventHandler() {
            
            @Override
            public void onPathwayChange(PathwayChangeEvent event) {
                System.out.println("Current displayed pathway: " + event.getCurrentPathwayDBId());
            }
        };
        addPathwayChangeEventHandler(pathwayHandler);
    }
    
    public InteractorCanvasModel getInteractorCanvasModel() {
		return interactorCanvasModel;
	}

	public CanvasPopupMenu getPopupMenu() {
        return popupMenu;
    }

	//public OptionsMenu getOptionsMenu() {
	//	return optionsMenu;
	//}
	
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
        setMinScale();
    }
    
    private void resizeCanvases(int width, int height) {
        final Integer BUFFER = 8;
    	
    	for (DiagramCanvas canvas : getExistingCanvases()) {
        	canvas.resize(width - BUFFER, height - BUFFER);
        }
    	fireViewChangeEvent();
    }    
        
    private void onResize(Integer width, Integer height) {
    	resizeCanvases(width, height);
    	
        // Need to reset the overview position so that it stays at the bottom-left corner
        if (!overview.isVisible())
            overview.setVisible(true);
        overview.updatePosition();
                
        optionsMenuIcon.setVisible(true);
        optionsMenuIcon.updatePosition(width, height);
        
        update();
                
        LocalResizeEvent event = new LocalResizeEvent(width, height);
        contentPane.fireEvent(event);
    }

    protected void setCanvasPathway(CanvasPathway pathway) {
        reset(true); // Resets scale and translation of all canvases
    	clearSelection();
        clearExpressionCanvas();
    	clearInteractorOverlay();
        
    	// Get the old displayed pathway
    	CanvasPathway old = pathwayCanvas.getPathway();
    	//System.out.println("Set pathway: " + pathway.getReactomeId());
            	    	
    	// Set up the overview first so that it can draw correct rectangle.
        overview.setPathway(pathway);

        pathwayCanvas.setPathway(pathway);
        
        showDefaultView(pathway);

        pathwayCanvas.update();
        
        if (overlayDataController != null && pathway != null) {
        	overlayDataController.display(contentPane,
        									expressionCanvas.getCoordinateSpaceWidth(),
        									expressionCanvas.getCoordinateSpaceHeight());
        	overlayDataController.setPathway(pathway);
        }
        
       	PathwayChangeEvent event = new PathwayChangeEvent();
       	if (old != null)
       		event.setPreviousPathwayDBId(old.getReactomeId());
       	if (pathway != null)
       		event.setCurrentPathwayDBId(pathway.getReactomeId());

        fireEvent(event);
    }

	public void showDefaultView(CanvasPathway pathway) {
        moveToViewArea(getProportionedViewArea(pathway.getPreferredSize(), 0), true);
        setMinScale();
	}
    
    /**
     * Set the pathway to be displayed whose DB_ID is the same as the specified dbId parametmer.
     * @param dbId
     */
    public void setPathway(Long dbId) {
    	controller.loadDiagramForDBId(dbId, this);
    }
    
    /**
     * Call this method to remove all displayed objects in this component.
     */
    public void removeAll() {
    	overview.setPathway(null);
        pathwayCanvas.setPathway(null);
        pathwayCanvas.update();
        clearOverlays();
    }
    
    /**
     * Set the pathway to be displayed.
     * @param xml
     */
    public void setPathway(String xml, Long dbId){
    	controller.loadDiagramForXML(xml, dbId, this);
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
    
    public void translate(double dx, double dy) {
    	translate(dx, dy, false);
    }
    
    public void translate(double dx, double dy, boolean gradual) {
        for (DiagramCanvas canvas : getExistingCanvases()) {
        	canvas.translate(dx, dy, gradual);
        }
        fireViewChangeEvent();
    }
    
    public void scale(double scaleFactor) {
        scale(scaleFactor, getCenterPoint());
    }
    
    private void scale(double scaleFactor, Point point) {
    	for (DiagramCanvas canvas : getExistingCanvases()) {
    		canvas.scale(scaleFactor, point);
    	}
    	fireViewChangeEvent();
    }

    public void zoomIn() {
    	zoomIn(getCenterPoint());
    }
    
    public void zoomIn(Point point) {
    	scale(Parameters.ZOOMFACTOR, point);
    }
    
    public void zoomOut() {
    	zoomOut(getCenterPoint());
    }
    
    public void zoomOut(Point point) {
    	scale(1 / Parameters.ZOOMFACTOR, point);
    	
    	if (pathwayCanvas.getCanvasTransformation().lessThanMinScale()) {
    		fireEvent(new ZoomedOutEvent(getPathway().getReactomeId()));
    	}
    }
    
    public void center(Point point) {
    	center(point, false);
    }
    
    private Point getCenterPoint() {
    	return new Point(
    		pathwayCanvas.getCoordinateSpaceWidth() / 2,
    		pathwayCanvas.getCoordinateSpaceHeight() / 2
    	);
    }
    
    public void center(Point point, boolean entityCoordinates) {
    	for (DiagramCanvas canvas : getExistingCanvases()) {
    		canvas.center(point, entityCoordinates);
    	}
    	fireViewChangeEvent();
    }
    
    private Bounds getProportionedViewArea(Bounds area, double buffer) {
    	double left = area.getX() - buffer;
    	double right = area.getRight() + buffer;
    	double top = area.getY() - buffer;
    	double bottom = area.getBottom() + buffer;
    	
    	double width = right - left;
    	double height = bottom - top;
    	
    	
    	double pathwayWidth = getPathwayCanvas().getCoordinateSpaceWidth();
    	double pathwayHeight = getPathwayCanvas().getCoordinateSpaceHeight();
    	double adjustedWidth, adjustedHeight;
    	if (width / height < pathwayWidth / pathwayHeight) {
    		adjustedWidth = pathwayWidth / pathwayHeight * height;
    		adjustedHeight = height;
    	} else if (width / height > pathwayWidth / pathwayHeight) {
    		adjustedWidth = width;
    		adjustedHeight = pathwayHeight / pathwayWidth * width;
    	} else {
    		adjustedWidth = width;
    		adjustedHeight = height;
    	}
    	
    	if (adjustedWidth - width > 0) {
    		double increase = adjustedWidth - width;
    		left -= increase / 2.0;
    		right += increase / 2.0;
    	} else if (adjustedHeight - height > 0) {
    		double increase = adjustedHeight - height;
    		top -= increase / 2.0;
    		bottom += increase/ 2.0;
    	}
    
    	return new Bounds(left, top, adjustedWidth, adjustedHeight);
    }
    
    private void moveToViewArea(Bounds viewArea, boolean allowZoomIn) {
    	final double newScale = getPathwayCanvas().getCoordinateSpaceWidth() / viewArea.getWidth();
    	final boolean zoomingOut = newScale < getPathwayCanvas().getScale();
    	
    	if (allowZoomIn || zoomingOut) {
    		reset();
    		center(viewArea.getCentre(), true);
    		scale(newScale);
    	} else {
    		center(viewArea.getCentre(), true);
    	}
    }
    
    private void setMinScale() {
    	final double pathwayWidth = getPathwayCanvas().getCoordinateSpaceWidth();
    	final double adjustedWidth = getProportionedViewArea(getPathway().getPreferredSize(), 0).getWidth();
    	
    	getPathwayCanvas().getCanvasTransformation().setMinScale(pathwayWidth / adjustedWidth);
    }
    
    public void reset() {
    	reset(false);
    }
    
    public void reset(boolean resetMinScale) {
    	for (DiagramCanvas canvas : getExistingCanvases()) {
    		canvas.reset();
    		
    		if (resetMinScale)
    			canvas.getCanvasTransformation().setMinScale(0);
    	}
    	fireViewChangeEvent();
    }
    
    public void resetTranslate() {
    	for (DiagramCanvas canvas : getExistingCanvases()) {
    		canvas.resetTranslate();
    	}
    	fireViewChangeEvent();
    }
    
    public void fireViewChangeEvent() {
    	for (DiagramCanvas canvas : getExistingCanvases()) {
    		canvas.fireViewChangeEvent();
    	}
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
       	
       	List<HoverHandler> hoverHandlers = getExistingHoverHandlers();
       	Collections.reverse(hoverHandlers);
       	
       	for (HoverHandler hh : hoverHandlers) {
       		if (hh.hover(hoveredPoint) != null) {       			
       			hh.fireHoverEvent();
       			
       			if (hh.overridesOtherHoverHandlers())
       				break;
       		}	
       	}       	
    }

    public void hideTooltip() {
    	for (HoverHandler hh : getExistingHoverHandlers()) {
    		if (hh != null)
    			hh.getTooltip().hide();
    	}	
    }
    
    //public void stopHoveringExceptFor(DiagramCanvas c) {
    //	for (DiagramCanvas canvas : canvasList) {
    //		if (canvas == null || canvas == c)
    //			continue;
    //		HoverHandler hh = canvas.getHoverHandler();
    //		
    //		if (hh != null) {
    //			hh.getTooltip().hide();
    //			hh.clearHoveredObject();
    //		}
    //	}
    //}
    
    
    /**
     * Do selection based on a mouse click or a touch event.
     * @param x
     * @param y
     */
    public void select(GwtEvent<? extends EventHandler> event, int x, int y) {
        // Need to consider both scale and translate
        Point correctedPoint = pathwayCanvas.getCorrectedCoordinates(x, y);
                
        // Loop through each canvas starting with the top layer and working backwards
        List<SelectionHandler> selectionHandlers = getExistingSelectionHandlers();
        Collections.reverse(selectionHandlers);
        
        for (SelectionHandler sh : selectionHandlers) {
        	if (sh.select(event, correctedPoint) != null) {
        		sh.fireSelectionEvent();
        		break;        		
        	}

        	// Nothing new has been selected, so fire an event to de-select old selections
        	if (sh instanceof PathwayCanvasSelectionHandler) {
        		sh.fireSelectionEvent();
        	}        	
        }
    }
        
    public void addSelectionEventHandler(SelectionEventHandler handler) {
        addHandler(handler, SelectionEvent.TYPE);
    }
    
    public void addHoverEventHandler(HoverEventHandler handler) {
    	addHandler(handler, HoverEvent.TYPE);
    }
    
    public void addZoomedOutEventHandler(ZoomedOutEventHandler handler) {
    	addHandler(handler, ZoomedOutEvent.TYPE);
    }
    
    public void addPathwayChangeEventHandler(PathwayChangeEventHandler handler) {
        addHandler(handler, PathwayChangeEvent.TYPE);
    }
    
    public void addParticipatingMoleculeSelectionEventHandler(ParticipatingMoleculeSelectionEventHandler handler) {
    	addHandler(handler, ParticipatingMoleculeSelectionEvent.TYPE);
    }
    
    public void addSubpathwaySelectionEventHandler(SubpathwaySelectionEventHandler handler) {
    	addHandler(handler, SubpathwaySelectionEvent.TYPE);
    }
    
    public void addExpressionOverlayStopHandler(ExpressionOverlayStopEventHandler handler) {
    	addHandler(handler, ExpressionOverlayStopEvent.TYPE);
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
        List<GraphObject> selectedObjects = new ArrayList<GraphObject>();
        
        for (SelectionHandler selectionHandler : getExistingSelectionHandlers()) {        	
        	selectedObjects = selectionHandler.getSelectedObjects();     
        	if (!selectedObjects.isEmpty())
        		break;
        }
    	
    	return selectedObjects;
    }
    
    public void setSelectionObjects(List<GraphObject> objects) {
    	for (SelectionHandler selectionHandler : getExistingSelectionHandlers())
    		selectionHandler.setSelectionObjects(objects);
    }
    
    public void setSelectionObject(GraphObject object) {
    	List<GraphObject> objectList = new ArrayList<GraphObject>(1);
    	objectList.add(object);
    	setSelectionObjects(objectList);
    }
    
    /**
     * Set a list of objects using their DB_IDs.
     * @param dbIds
     */
    public void setSelectionIds(List<Long> dbIds) {
        for (SelectionHandler selectionHandler : getExistingSelectionHandlers()) {
   			selectionHandler.setSelectionIds(dbIds);
        }
        
        List<GraphObject> selectedObjects = getSelectedObjects();
        if (!selectedObjects.isEmpty()) {
        	SelectionEvent event = new SelectionEvent();
        	event.setSelectedObjects(selectedObjects);
        	
        	List<GraphObject> objectsAndConnections = new ArrayList<GraphObject>();
        	for (GraphObject selectedObject : selectedObjects) {
        		objectsAndConnections.add(selectedObject);
        		if (selectedObject instanceof Node) {
        			objectsAndConnections.addAll(((Node) selectedObject).getConnectedReactions());
        		} else if (selectedObject instanceof HyperEdge) {
        			objectsAndConnections.addAll(((HyperEdge) selectedObject).getConnectedNodes());
        		}
        	}
    		moveToViewArea(getProportionedViewArea(getEncompassingBounds(objectsAndConnections), 50), false);
        	fireSelectionEvent(event);
        }
    }

	private Bounds getEncompassingBounds(List<GraphObject> objects) {
		List<GraphObject> objectList = new ArrayList<GraphObject>(objects);
		
		Collections.sort(objectList, GraphObject.getXCoordinateComparator());
		double left = objectList.get(0) instanceof Node ?
					((Node) objectList.get(0)).getBounds().getX() :
					objectList.get(0).getPosition().getX(); 
		
		Collections.reverse(objectList);
		double right = objectList.get(0) instanceof Node ?
					((Node) objectList.get(0)).getBounds().getRight() :
					objectList.get(0).getPosition().getX();
		
		Collections.sort(objectList, GraphObject.getYCoordinateComparator());
		double top = objectList.get(0) instanceof Node ?
					((Node) objectList.get(0)).getBounds().getY() :
					objectList.get(0).getPosition().getY();
		
		Collections.reverse(objectList);
		double bottom = objectList.get(0) instanceof Node ?
					((Node) objectList.get(0)).getBounds().getBottom() :
					objectList.get(0).getPosition().getY();
					
		return new Bounds(left, top, right - left, bottom - top);
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
    	for (SelectionHandler selectionHandler : getExistingSelectionHandlers()) {
   			selectionHandler.clearSelection();
    	}
    }
    
    public void clearOverlays() {    	
    	clearInteractorOverlay();
    	clearExpressionOverlay();
    }
    
    /**
     * Update drawing.
     */
    public void update() {
        for (DiagramCanvas canvas : getExistingCanvases()) {    		
        	canvas.update();
        }	
    }
    
    public void setCursor(Cursor cursor) {
    	getElement().getStyle().setCursor(cursor);    	
    }
    
    public Style getStyle() {
    	return this.style;
    }
    
    public void showAnalysisData(String token, final String resourceName) {
    	if (sameAsCurrentToken(token)) {
    		setResource(resourceName);
    		return;
    	}
    	
    	initExpressionCanvas();
    	
    	AnalysisController analysisController = new AnalysisController();
    	analysisController.retrieveAnalysisResult(token, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    AlertPopup.alert("Error in retrieving expression results: " + exception);
                }
                
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() != Response.SC_OK) {
                    	AlertPopup.alert("Error in retrieving expression results: " + response.getStatusText());
                    	return;
                    }
                    
                    clearExpressionOverlay();
                    setDataController(createDataController(response.getText(), resourceName));
                }
        });
    }

    public void setResource(String resourceName) {
    	if (overlayDataController == null)
    		return;
    	
    	overlayDataController.setResourceName(resourceName);
    }
    
    private boolean sameAsCurrentToken(String newToken) {
    	if (overlayDataController == null)
    		return false;
    	
    	return overlayDataController.getToken().equals(newToken);
    }
    
    private DataController createDataController(String analysisResultText, String resourceName) {
    	AnalysisResult analysisResult;
    	try {
    		analysisResult = AnalysisModelFactory.getModelObject(AnalysisResult.class, analysisResultText);
    	} catch (AnalysisModelException e) {
    		e.printStackTrace();
    		AlertPopup.alert("Unable to parse analysis results: " + e);
    		return null;
    	}
            
    	DataController dataController = getDataController(analysisResult);
            
        if (dataController == null) {
            AlertPopup.alert(analysisResult.getSummary().getType() + " is an unknown analysis type");
            return null;
        }
        dataController.setResourceName(resourceName);
        
        expressionCanvas.setAnalysisType(analysisResult.getSummary().getType());
        expressionCanvas.setDataController(dataController);
            
        if (dataController instanceof SpeciesComparisonDataController)
            ((SpeciesComparisonDataController) dataController).setSpecies();
		
        return dataController;
    }
    
    private DataController getDataController(AnalysisResult analysisResult) {
    		final AnalysisType analysisType = AnalysisType.getAnalysisType(analysisResult.getSummary().getType());
    		
    		if (analysisType == AnalysisType.Expression) {
    			return new ExpressionDataController(analysisResult);
    		} else if (analysisType == AnalysisType.SpeciesComparison) {
    			return new SpeciesComparisonDataController(analysisResult);
    		} else if (analysisType == AnalysisType.Overrepresentation) {
    			return new OverrepresentationDataController(analysisResult);
    		}
    		
    		return null;
    }
    
   public void setDataController(DataController dataController) {
    	overlayDataController = dataController;
    	if (overlayDataController == null)
    		return;
    	
    	DataPointChangeEventHandler dpChangeHandler = new DataPointChangeEventHandler() {

			@Override
			public void onDataPointChanged(DataPointChangeEvent e) {
				ExpressionCanvasModel expressionCanvasModel = expressionCanvas.getExpressionCanvasModel();
				
				Map<Long, String> color = e.getPathwayComponentIdToColor(); 
				Map<Long, Double> level = e.getPathwayComponentIdToExpressionLevel();
				Map<Long, List<String>> id = e.getPathwayComponentIdToExpressionId();
				
				expressionCanvasModel.setEntityExpressionInfoMap(id, level,	color);
				
				expressionCanvas.setPathway(getPathway()); // Updates the view after setting/re-setting the pathway
			}    		
    	};
    	
    	ExpressionOverlayStopEventHandler exprOverlayStopHandler = new ExpressionOverlayStopEventHandler() {

			@Override
			public void onExpressionOverlayStopped(ExpressionOverlayStopEvent e) {
				clearExpressionOverlay();
				fireEvent(e);
			}
    		
    	};
       	
    	overlayDataController.addDataPointChangeEventHandler(dpChangeHandler);
    	overlayDataController.addExpressionOverlayStopEventHandler(exprOverlayStopHandler);    	
    	overlayDataController.setNavigationPaneStyle(style.dataPointControl());
    	
    	if (overlayDataController instanceof ExpressionDataController)
    		((ExpressionDataController) overlayDataController).setColorPaneStyle(style.colorBar());
    	
    	overlayDataController.display(contentPane, 
    			expressionCanvas.getCoordinateSpaceWidth(), 
    			expressionCanvas.getCoordinateSpaceHeight());
    	
    	if (getPathway() != null)
    		overlayDataController.setPathway(getPathway());
    }
    
    private void clearInteractorOverlay() {
    	if (interactorCanvas != null)
    		interactorCanvas.removeAllProteins();
    }
    
    
    private void clearExpressionOverlay() {
    	clearDataController();
    	clearExpressionCanvas();
    }
    
    private void clearDataController() {
    	if (overlayDataController != null) {
    		overlayDataController.dispose();
    		overlayDataController = null;
    	}
    }
    
    private void clearExpressionCanvas() {
    	if (expressionCanvas != null )	{
    		expressionCanvas.setPathway(null);
    	}
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

        String greyOutCanvas();
        
        String controlPane();
        
        String overViewCanvas();
        
        String canvasPopup();
        
        String subMenu();

        String searchPopup();
        
        String tooltip();
        
        String dataPointControl();
        
        String colorBar();
        
        String expressionComplexPopup();
    }
    
    @Override
	public void onContextMenu(ContextMenuEvent event) {
		// Suppress browser context menu so canvas pop-up menu can be seen
		event.preventDefault();
		event.stopPropagation();
		
		int x = event.getNativeEvent().getClientX() - pathwayCanvas.getAbsoluteLeft();
		int y = event.getNativeEvent().getClientY() - pathwayCanvas.getAbsoluteTop();
		select(event, x, y);
		
		if (getSelectedObjects() != null && !getSelectedObjects().isEmpty()) {
			getPopupMenu().showPopupMenu(event);
		} else {
			OptionsMenu optionsMenu = new OptionsMenu(this); 
			optionsMenu.showPopup(new Point(event.getNativeEvent().getClientX(),
					event.getNativeEvent().getClientY()));
		}
	}

	public InteractorCanvas getInteractorCanvas() {
		return interactorCanvas;
	}
	
	public ExpressionCanvas getExpressionCanvas() {
		return expressionCanvas;
	}
	
	private class LocalResizeEvent extends ResizeEvent {
	    
	    public LocalResizeEvent(int width, int height) {
	        super(width, height);
	    }
	    
	}

	public void initInteractorCanvas() {
		if (interactorCanvas == null) {
			interactorCanvas = new InteractorCanvas(this, pathwayCanvas.getCanvasTransformation());
			interactorCanvasModel.setInteractorCanvas(interactorCanvas);
			
			int insertionIndex = canvasList.size(); // Above all other canvases
			contentPane.insert(interactorCanvas, 4, 4, insertionIndex);			
			canvasList.add(insertionIndex, interactorCanvas);
			
			interactorCanvas.resize(pathwayCanvas.getCoordinateSpaceWidth(),
									pathwayCanvas.getCoordinateSpaceHeight());
			
			//interactorCanvas.scale(pathwayCanvas.getScale());
			//interactorCanvas.translate(pathwayCanvas.getTranslateX(), pathwayCanvas.getTranslateY());
		}	
	}
	
	public void initExpressionCanvas() {
		if (expressionCanvas == null) {
			expressionCanvas = new ExpressionCanvas(this, pathwayCanvas.getCanvasTransformation());
			
			contentPane.insert(expressionCanvas, 4, 4, 1);
			canvasList.add(canvasList.indexOf(pathwayCanvas) + 1, expressionCanvas);
			
			//expressionCanvas.scale(pathwayCanvas.getScale());
			//expressionCanvas.translate(pathwayCanvas.getTranslateX(), pathwayCanvas.getTranslateY());
			
			expressionCanvas.resize(pathwayCanvas.getCoordinateSpaceWidth(),
									pathwayCanvas.getCoordinateSpaceHeight());
		}
	}
	
	public void showSearchPopup() {
		searchBar.setVisible(Boolean.TRUE);
		searchBar.focus();
	}
	
	private List<DiagramCanvas> getExistingCanvases() {
		List<DiagramCanvas> existingCanvases = new ArrayList<DiagramCanvas>();
		
		for (DiagramCanvas canvas : canvasList) {
			if (canvas != null)
				existingCanvases.add(canvas);
		}
		
		return existingCanvases;
	}
	
	private List<SelectionHandler> getExistingSelectionHandlers() {
		List<SelectionHandler> existingSelectionHandlers = new ArrayList<SelectionHandler>();
		
		for (DiagramCanvas canvas : getExistingCanvases()) {
			SelectionHandler canvasSelectionHandler = canvas.getSelectionHandler();
			
			if (canvasSelectionHandler != null)
				existingSelectionHandlers.add(canvasSelectionHandler);			
		}
		
		return existingSelectionHandlers;
	}
	
	private List<HoverHandler> getExistingHoverHandlers() {
		List<HoverHandler> existingHoverHandlers = new ArrayList<HoverHandler>();
		
		for (DiagramCanvas canvas : getExistingCanvases()) {
			HoverHandler canvasHoverHandler = canvas.getHoverHandler();
			
			if (canvasHoverHandler != null) 
				existingHoverHandlers.add(canvasHoverHandler);
		}
		
		return existingHoverHandlers;
	}

	public DataController getOverlayDataController() {
		return overlayDataController;
	}
	
}
