/**
 * 
 * @author Maulik Kamdar
 * Google Summer of Code Project 
 * Canvas Based Pathway Visualization Tool
 * 
 * Parts of Code have been derived from the tutorials and samples provided at Google Web Toolkit Website 
 * http://code.google.com/webtoolkit/doc/latest/tutorial/
 * 
 * Ideas for canvas based initiation functions derived and modified from Google Canvas API Demo
 * http://code.google.com/p/gwtcanvasdemo/source/browse/
 * 
 * Interactivity ideas taken from HTML5 canvas tutorials
 * http://www.html5canvastutorials.com/
 * 
 * 
 */

package org.reactome.diagram.client;

// Required Imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.ModelHelper;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.Vector;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Implements the Module EntryPoint Interface.
 */

public class ReactomeBrowser implements EntryPoint {
	
	// Initialization
	static final String holderId = "maincanvas";
	
	private PathwayDiagramPanel diagramPane;
    private PlugInSupportCanvas canvas;
    private PlugInSupportCanvas backBuffer;
    private PlugInSupportCanvas overView;
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	private static final List<String> PATHWAYS = Arrays.asList("Pathway 1", "Pathway 2", "Pathway 3", "Pathway 4");
	// List Array Fetched from Database
	
	Context2d context;
	Context2d backBufferContext;
	Context2d overViewContext;
	
	boolean draggable, dragged;
	boolean draggableNode;
	Vector dragStart = null;
	float lastX, lastY;
	
	static final List<Double> trackScaleFactors = new ArrayList<Double>();
	static final List<Double> trackLeftTranslates = new ArrayList<Double>();
	static final List<Double> trackTopTranslates = new ArrayList<Double>();
	static final List<Double> setBoxCoordinates = new ArrayList<Double>();
	static final List<String> nodeSelected = new ArrayList<String>();
	
	double zoomFactor = Parameters.ZoomFactor;
	double upHeight = Parameters.UpHeight;
	
	public void onModuleLoad() {
		
		
		//------- Header (Search, Suggest Box, Annotate)
		final Button searchButton = new Button("Search Map");
		searchButton.addStyleName("searchButton");
		RootPanel.get("searchButton").add(searchButton);
		
		//---------------------
		final Button analyzeButton = new Button("Analyze, Annotate and Upload");
		RootPanel.get("analyzeButton").add(analyzeButton);
		
		//----------------------
		MultiWordSuggestOracle database = new MultiWordSuggestOracle();
		/* 
		 * Would be filled by an array from xml file
		 */
		   database.add("Signaling");
		   database.add("Protein transfer");
		   database.add("Gene Expression"); 

		final SuggestBox searchField = new SuggestBox(database);
		searchField.setText("Search Map");
		RootPanel.get("searchField").add(searchField);
		
		//-------------------------
				
		//Side Panel (Select Box, Tabs)
		final ListBox lb = new ListBox();
	    lb.addItem("Homo Sapiens");
	    lb.addItem("Plamodium");
	    lb.addItem("Escherichia Coli");

	    lb.setVisibleItemCount(1);
	    lb.setVisible(false);
		RootPanel.get("sidepanel").add(lb);

		//-------------------------------
		// TODO Change to CellTree
		
		
		TextCell textCell = new TextCell(); 
	    CellList<String> cellList = new CellList<String>(textCell);
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);  
	    final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        String selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          Window.alert("You selected: " + selected);
	        }
	      }
	    });

	    cellList.setRowCount(PATHWAYS.size(), true);

	    cellList.setRowData(0, PATHWAYS);
	    
	    //-------------------------------------
		final TabLayoutPanel TabGroup = new TabLayoutPanel(4, Unit.EM);
		TabGroup.add(new HTML(""), "Search Results");
		TabGroup.add(cellList, "Pathway");
		TabGroup.add(new HTML("<iframe src='help.html' scroll='true' height='375' width='280' frameborder='0'></iframe>"), "Help");
		TabGroup.setHeight("450px");
		TabGroup.setVisible(false);
	    RootPanel.get("sidepanel").add(TabGroup);
	   
	    //-------------------------------
	    
	   
	    
	    //-------(Canvas Functions)
	
//		canvas = Canvas.createIfSupported();
//		backBuffer = Canvas.createIfSupported();
//		overView = Canvas.createIfSupported();
		
		canvas = new PlugInSupportCanvas();
		backBuffer = new PlugInSupportCanvas();
		overView = new PlugInSupportCanvas();
		
		if (canvas == null) {
			RootPanel.get(holderId).add(new Label(upgradeMessage));
			return;
		}
		
		if (backBuffer == null) {
			Window.alert("Would not be able to render separate PNG images");
		}
		
		if (overView == null) {
			Window.alert("Would not be able to load overview window");
		}
		
		canvas.setWidth(Parameters.width + "px");
		canvas.setHeight(Parameters.height + "px");
		canvas.setFocus(true);
		canvas.setStyleName("mainCanvas");
		canvas.setCoordinateSpaceWidth((int) Parameters.width);
		canvas.setCoordinateSpaceHeight((int) Parameters.height);
		
//		RootPanel.get(holderId).add(canvas);
		diagramPane = new PathwayDiagramPanel();
		diagramPane.setSize((int)Parameters.width,
		                    (int)Parameters.height);
		RootPanel.get(holderId).add(diagramPane);
//		context = diagramPane.getContext2d();
//		context = canvas.getContext2d();
		
		backBufferContext = backBuffer.getContext2d();
		
		overView.setStyleName("overViewCanvas");
		RootPanel.get(holderId).add(overView);
		overViewContext = overView.getContext2d();
		overView.setVisible(false);
	
//		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "Mitotic G1-G1_S phases.xml");
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "EGFR_Simple_37.xml");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					requestFailed(exception);
				}
				public void onResponseReceived(Request request, Response response) {
//					System.out.println("Acquired Document");
					renderXML(response.getText());
				}
			});
		} catch (RequestException ex) {
			requestFailed(ex);
		} 

		initHandlers();
		
		PushButton savepng = new PushButton("Save as PNG", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  String url = "";
		    	  if(backBuffer != null)
		    		  url = backBuffer.toDataUrl("image/png");
		    	  Window.open(url, "Image/PNG", "status=1");
		      }
		});
		
		PushButton savejpeg = new PushButton("Save as JPEG", new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  String url = "";
		    	  if(backBuffer != null)
		    		  url = backBuffer.toDataUrl("image/jpeg");
		    	  Window.open(url, "Image/JPEG", "status=1");
		      }
		});
		
		PushButton refresh = new PushButton(new Image("refresh.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		          diagramPane.reset();
		          diagramPane.update();
//		    	  double factor = 1.0 , leftTranslate = 0.0, topTranslate = 0.0;
//		    	  double scaleFactor = (1 / factor);
//
//		    	  double scaleFactorX = setBoxCoordinates.get(4);
//		    	  double scaleFactorY = setBoxCoordinates.get(5);
//		    	  int totalTracks = trackScaleFactors.size();
//		    	  
//		    	  for(int i = totalTracks-1 ; i >= 0; i--) {
//		    		  leftTranslate = trackLeftTranslates.get(i);
//		    		  topTranslate = trackTopTranslates.get(i);
//		    		  factor = trackScaleFactors.get(i);
//		    		  scaleFactor = (1 / factor);
//			    	  context.translate(-leftTranslate, -topTranslate);
//			    	  context.scale(scaleFactor, scaleFactor);
//		    	  }
//		    
//		    	  trackTopTranslates.clear();
//		    	  trackLeftTranslates.clear();
//		    	  trackScaleFactors.clear();
//		    	  doUpdate(context);
//		    	  trackTransforms(1.0, 0.0, 0.0);
//		    	  doUpdate(overViewContext);
//		    	  setBox();
//		    	  setBoxCoordinates.add(4, scaleFactorX);
//		    	  setBoxCoordinates.add(5, scaleFactorY);
		      }
		});
		
		PushButton zoomPlus = new PushButton(new Image("zoomplus.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		          diagramPane.scale(1.25d);
		          diagramPane.update();
//		    	  double i = 1;
//		    	  double scaleFactor = 1.1;
//		    	  double scaleFactorX = setBoxCoordinates.get(4);
//		    	  double scaleFactorY = setBoxCoordinates.get(5);
//		    	  double factor = Math.pow(scaleFactor, i);
//		    	  context.scale(factor, factor);
//		    	  doUpdate(context);
//		    	  trackTransforms(factor, 0.0, 0.0);
//		    	  doUpdate(overViewContext);
//		    	  setBox();
//		    	  setBoxCoordinates.add(4, scaleFactorX);
//		    	  setBoxCoordinates.add(5, scaleFactorY);
		      }
		});
		PushButton zoomMinus = new PushButton(new Image("zoomminus.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		          diagramPane.scale(0.8d);
		          diagramPane.update();
//		    	  double i = -1;
//		    	  double scaleFactor = 1.1;
//		    	  double scaleFactorX = setBoxCoordinates.get(4);
//		    	  double scaleFactorY = setBoxCoordinates.get(5);
//		    	  double factor = Math.pow(scaleFactor, i);
//		    	  context.scale(factor, factor);
//		    	  doUpdate(context);
//		    	  trackTransforms(factor, 0.0, 0.0);
//		    	  doUpdate(overViewContext);
//		    	  setBox();
//		    	  setBoxCoordinates.add(4, scaleFactorX);
//		    	  setBoxCoordinates.add(5, scaleFactorY);
		      }
		});
		PushButton scrollLeft = new PushButton(new Image("left.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		        diagramPane.translate(100, 0);
		        doUpdateFromTranslate();
		      }
		});
		PushButton scrollTop = new PushButton(new Image("top.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		          diagramPane.translate(0, 100);
		    	  doUpdateFromTranslate();
		      }
		});
		PushButton scrollBottom = new PushButton(new Image("bottom.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  diagramPane.translate(0, -100);
		    	  doUpdateFromTranslate();
		      }
		});
		PushButton scrollRight = new PushButton(new Image("right.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  diagramPane.translate(-100, 0);
		    	  doUpdateFromTranslate();
		      }
		});
		PushButton listButton = new PushButton(new Image("list.jpg"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
//		    	  if(overView.isVisible()) {
//		    		  overView.setVisible(false);
//		    	  }
//		    	  if(!TabGroup.isVisible()) {
//		    		  TabGroup.setVisible(true);
//		    		  lb.setVisible(true);
//		    		  TabGroup.setWidth("300px");
//		    		  canvas.setWidth((Parameters.width - 300) + "px");
//		    		  canvas.setCoordinateSpaceWidth((int) (Parameters.width - 300));
//		    		  doUpdate(context);
//		    	  } else {
//		    		  lb.setVisible(false);
//		    		  TabGroup.setVisible(false);
//		    		  canvas.setWidth((Parameters.width) + "px");
//		    		  canvas.setCoordinateSpaceWidth((int) Parameters.width);
//		    		  doUpdate(context);
//		    	  }
		      }
		});
		
		PushButton overViewButton = new PushButton(new Image("overview.png"), new ClickHandler() {
		      public void onClick(ClickEvent event) {
		    	  if(TabGroup.isVisible()) {
		    		  TabGroup.setVisible(false);
		    		  lb.setVisible(false);
		    	  }
		    	  if(!overView.isVisible()) {
		    		  overView.setVisible(true);
		    	  } else {
		    		  overView.setVisible(false);
		    	  }
		      }
		});
		
		overViewButton.setSize("19px", "19px");
		listButton.setSize("19px", "19px");
		refresh.setSize("19px", "19px");
		zoomPlus.setSize("19px", "19px");
		zoomMinus.setSize("19px", "19px");
		scrollLeft.setSize("19px", "19px");
		scrollTop.setSize("19px", "19px");
		scrollBottom.setSize("19px", "19px");
		scrollRight.setSize("19px", "19px");
		
		listButton.setVisible(false); //To change to True once Pathway List is filled
		RootPanel.get("listbutton").add(listButton);
		RootPanel.get("overviewbutton").add(overViewButton);
		RootPanel.get("savepng").add(savepng);
		RootPanel.get("savejpeg").add(savejpeg);
		RootPanel.get("refresh").add(refresh);
		RootPanel.get("zoomplus").add(zoomPlus);
		RootPanel.get("zoomminus").add(zoomMinus);
		RootPanel.get("scroll-left").add(scrollLeft);
		RootPanel.get("scroll-top").add(scrollTop);
		RootPanel.get("scroll-bottom").add(scrollBottom);
		RootPanel.get("scroll-right").add(scrollRight);
	}
	
	private void doUpdateFromTranslate() {
        diagramPane.update();
//      doUpdate(context);
//      trackTransforms(1.0, 0.0, -100.0);
//      doUpdate(overViewContext);
//      setBox();
//      double scaleFactorX = setBoxCoordinates.get(4);
//      double scaleFactorY = setBoxCoordinates.get(5);
//      setBoxCoordinates.add(4, scaleFactorX);
//      setBoxCoordinates.add(5, scaleFactorY);
	}
	
	/** Parses the XML Text and Builds a HashMap of the nodes and the edges. Renders the Canvas Visualization.
	 * 
	 * @param xmlText The XML Text to be parsed
	 */
	private void renderXML(String xmlText) {
//	       testPathwayBuild(xmlText);

		Document pathwayDom = XMLParser.parse(xmlText);
		Element pathwayElement = pathwayDom.getDocumentElement();
		XMLParser.removeWhitespace(pathwayElement);
        CanvasPathway pathway = new CanvasPathway();
        pathway.buildPathway(pathwayElement);
        diagramPane.setPathway(pathway);
        // Check size of diagramPane
//        System.out.println("Size of diagram pane: " + diagramPane.getOffsetWidth() + ", " + 
//                           diagramPane.getOffsetHeight());
//		
//		CanvasElements elements = new CanvasElements(pathwayElement);
//		elements.process();
//		
//		doUpdate(context);
//		trackTransforms(1.0, 0.0, 0.0);
//
//		Vector maxDim = elements.getMaxDim();
//		
//  	  	if(backBuffer != null) {
//  	  		setBackBuffer((int) maxDim.x, (int) maxDim.y);
//  	  		doUpdate(backBufferContext);
//  	  	}
//  	  	
//  	  	if(overView != null) {
//  	  		setOverView(300,180);
//  	  		double scaleFactorX = (300.0)/maxDim.x;
//  	  		double scaleFactorY = (180.0)/maxDim.y;
//  	  		overViewContext.scale(scaleFactorX, scaleFactorY);
//  	  		doUpdate(overViewContext);
//
//  	  		setBox();
//  	  		setBoxCoordinates.add(4, scaleFactorX);
//	  		setBoxCoordinates.add(5, scaleFactorY);
//  	  	}
	}

	void testPathwayBuild(String xmlText) {
	    try {
	        Document document = XMLParser.parse(xmlText);
	        Element element = document.getDocumentElement();
	        CanvasPathway pathway = new CanvasPathway();
	        pathway.buildPathway(element);
	        List<Node> nodes = pathway.getChildren();
	        List<HyperEdge> edges = pathway.getEdges();
	        // Do some simple test
	        System.out.println("ReactomeId: " + pathway.getReactomeId() + 
	                           "\nName: " + pathway.getDisplayName());
	        System.out.println("A List of node: " + nodes.size());
	        for (Node node : nodes) {
	            System.out.println("Node: " + node.getDisplayName() + " (" + node.getReactomeId() + ")");
	        }
	        System.out.println("A list of edges: " + edges.size());
	        for (HyperEdge edge : edges) {
	            System.out.println("Edge: " + edge.getDisplayName() + " (" + edge.getReactomeId() + ")");
	        }
	        // Check one edges
	        HyperEdge edge = edges.get(0);
	        System.out.println(edge.getInputBranches());
	        System.out.println(edge.getOutputBranches());
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 
	 * @param exception Exception whenever the XML file is not load
	 */
	private void requestFailed(Throwable exception) {
		Window.alert("Failed to send the message: " + exception.getMessage());
	}
	
	/**Tracks the transforms of the Context
	 * 
	 * @param scaleFactor The percentage of scale applied on the canvas
	 * @param leftTranslate The number of pixels translated on the horizontal axis
	 * @param topTranslate The number of pixels translated on the vertical axis
	 */
	
	public void trackTransforms(double scaleFactor, double leftTranslate, double topTranslate) {
		trackScaleFactors.add(scaleFactor);
		trackTopTranslates.add(topTranslate);
		trackLeftTranslates.add(leftTranslate);
	}
	
	/**Initializes the BackBuffer Canvas
	 * 
	 * @param width The Maximum Width of the Network
	 * @param height The Maximum Height of the Network 
	 */
	
	private void setBackBuffer(int width, int height) {
		if(backBuffer != null) {
			backBuffer.setWidth( width + "px");
			backBuffer.setHeight( height + "px");
			backBuffer.setCoordinateSpaceWidth((int) (width));
			backBuffer.setCoordinateSpaceHeight((int) (height));
			backBuffer.setVisible(false);
		}
	}
	
	/**Initializes the Overview Canvas
	 * 
	 * @param width The width of the Overview Canvas
	 * @param height The height of the Overview Canvas
	 */
	private void setOverView (int width, int height) {
		if(overView != null) {
			overView.setWidth( width + "px");
			overView.setHeight( height + "px");
			overView.setCoordinateSpaceWidth((int) (width));
			overView.setCoordinateSpaceHeight((int) (height));
			overView.setVisible(false);
		}
	}
	
	/**
	 * Draws a box on the overview window indicating the current position of the user on the Main Canvas
	 */
	private void setBox () {
//		double factor = 1.0;
//		double leftTranslate = 0.0, topTranslate = 0.0;
//		double scaleFactor = (1 / factor);
//		int totalTracks = trackScaleFactors.size();
//  	  
//		for(int i = totalTracks-1 ; i >= 0; i--) {
//			leftTranslate = leftTranslate + trackLeftTranslates.get(i);
//			topTranslate = topTranslate + trackTopTranslates.get(i);
//			factor = trackScaleFactors.get(i);
//  		  	scaleFactor = scaleFactor * (1 / factor); 		  	
//		}
//		
//		double coX = - leftTranslate;
//		double coY = - topTranslate;
//		double width = canvas.getCoordinateSpaceWidth() * scaleFactor;
//		double height = canvas.getCoordinateSpaceHeight() * scaleFactor;
//		
//		String nodeColor = "rgba(255,255,255,1)";
//		String strokeColor = "rgba(255,0,0,1)";
//		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
//		colors.makecolor(overViewContext);
//		
//		overViewContext.setLineWidth(20);
//		overViewContext.beginPath();
//		overViewContext.rect(coX, coY, width, height);
//		overViewContext.strokeRect(coX, coY, width, height); 
//		overViewContext.closePath();
//		overViewContext.setLineWidth(2);
//		
//		setBoxCoordinates.add(0, coX);
//		setBoxCoordinates.add(1, coY);
//		setBoxCoordinates.add(2, width);
//		setBoxCoordinates.add(3, height);
	}
	
	/**Checks against the Node HashMaps, whether the point where the user has clicked is a Node, and builds up the node attributes, if a node is selected
	 * 
	 * @param lastEvent The point where the user has clicked
	 * @return isSelected Boolean value indicating a Node has been selected.
	 */
	private boolean isNodeSelected (Vector lastEvent) {
		
////		boolean isSelected = false;
////		double factor = 1.0;
////		double leftTranslate = 0.0, topTranslate = 0.0;
////		double scaleFactor = (1 / factor);
////		int totalTracks = trackScaleFactors.size();
////  	  
////		for(int i = totalTracks-1 ; i >= 0; i--) {
////			leftTranslate = leftTranslate + trackLeftTranslates.get(i);
////			topTranslate = topTranslate + trackTopTranslates.get(i);
////			factor = trackScaleFactors.get(i);
////  		  	scaleFactor = scaleFactor * (1 / factor); 		  	
////		}
////		
////		double selectX = (lastEvent.x * scaleFactor) - leftTranslate;
////		double selectY = (lastEvent.y * scaleFactor) - topTranslate;
////		Iterator<Double> iterator = NodeGroup.BoundsHashmap.keySet().iterator();
////		while(iterator.hasNext()) {
////			Double key = (Double) iterator.next();
////			String bounds = NodeGroup.BoundsHashmap.get(key);
////			ModelHelper parser = new ModelHelper();
////			
////			String[] boundCo = parser.splitbySpace(bounds);
////			double coX = (Double.parseDouble(boundCo[0]))/zoomFactor;
////			double coY = ((Double.parseDouble(boundCo[1]))/zoomFactor)+upHeight;
////			double nodeWidth = (Double.parseDouble(boundCo[2]))/zoomFactor;
////			double nodeHeight = (Double.parseDouble(boundCo[3]))/zoomFactor;
////			
////			if(coX < selectX && selectX < (coX + nodeWidth)) {
////				if(coY < selectY && selectY < (coY + nodeHeight)) {
////					if(ProteinGroup.proteinValuesHashmap.containsKey(key)) {
////						List<String> attributes = ProteinGroup.proteinValuesHashmap.get(key);
////						nodeSelected.add(0, attributes.get(0));
////						nodeSelected.add(1, attributes.get(1));
////						nodeSelected.add(2, attributes.get(2));
////						nodeSelected.add(3, attributes.get(3));
////						nodeSelected.add(4, attributes.get(4));
////						nodeSelected.add(5, attributes.get(5));
////						nodeSelected.add(6, "1");
////						isSelected = true;
////						break;
////					} else if (ComplexGroup.complexValuesHashmap.containsKey(key)) {
////						List<String> attributes = ComplexGroup.complexValuesHashmap.get(key);
////						nodeSelected.add(0, attributes.get(0));
////						nodeSelected.add(1, attributes.get(1));
////						nodeSelected.add(2, attributes.get(2));
////						nodeSelected.add(3, attributes.get(3));
////						nodeSelected.add(4, attributes.get(4));
////						nodeSelected.add(5, attributes.get(5));
////						nodeSelected.add(6, "2");
////						isSelected = true;
////						break;
////					} else if (EntityGroup.entityValuesHashmap.containsKey(key)) {
////						List<String> attributes = EntityGroup.entityValuesHashmap.get(key);
////						nodeSelected.add(0, attributes.get(0));
////						nodeSelected.add(1, attributes.get(1));
////						nodeSelected.add(2, attributes.get(2));
////						nodeSelected.add(3, attributes.get(3));
////						nodeSelected.add(4, attributes.get(4));
////						nodeSelected.add(5, attributes.get(5));
////						nodeSelected.add(6, "3");
////						isSelected = true;
////						break;
////					} else if (ChemicalGroup.chemicalValuesHashmap.containsKey(key)) {
////						List<String> attributes = ChemicalGroup.chemicalValuesHashmap.get(key);
////						nodeSelected.add(0, attributes.get(0));
////						nodeSelected.add(1, attributes.get(1));
////						nodeSelected.add(2, attributes.get(2));
////						nodeSelected.add(3, attributes.get(3));
////						nodeSelected.add(4, attributes.get(4));
////						nodeSelected.add(5, attributes.get(5));
////						nodeSelected.add(6, "4");
////						isSelected = true;
////						break;
////					}
////				}
////			}
////		}
//		
//		return isSelected;
	    return false;
	}
	
	/**Shows which node is selected by enclosing it with a green rectangle
	 * 
	 */
	private void selectNode() {
//		String bounds = nodeSelected.get(2);
//		ModelHelper parser = new ModelHelper();
//		String[] boundCo = parser.splitbySpace(bounds);
//		double coX = ((Double.parseDouble(boundCo[0]) - 5))/zoomFactor;
//		double coY = (((Double.parseDouble(boundCo[1])) - 5)/zoomFactor)+upHeight;
//		double nodeWidth = ((Double.parseDouble(boundCo[2])) + 10)/zoomFactor;
//		double nodeHeight = ((Double.parseDouble(boundCo[3])) + 10)/zoomFactor;
//		
//		String nodeColor = "rgba(255,255,255,1)";
//		String strokeColor = "rgba(0,255,0,1)";
//		ContextSettings colors = new ContextSettings(nodeColor,strokeColor);
//		colors.makecolor(context);
//		
//		context.setLineWidth(5);
//		context.beginPath();
//		context.rect(coX, coY, nodeWidth, nodeHeight);
//		context.strokeRect(coX, coY, nodeWidth, nodeHeight); 
//		context.closePath();
//		context.setLineWidth(1);
	}
	
	/** Updates the Node HashMaps whenever a node is dragged to new bounds and new position
	 * 
	 * @param newPosition The position where the Node is dragged
	 */
	private void updateHashmap(Vector newPosition) {
//		String idno = nodeSelected.get(0);
//		String reactomeId = nodeSelected.get(1);
//		String bounds = nodeSelected.get(2);
//		String oldposition = nodeSelected.get(3);
//		String bgColor = nodeSelected.get(4);
//		String displayName = nodeSelected.get(5);
//		String type = nodeSelected.get(6);
//		
//		int typeNo = (int)(Double.parseDouble(type));
//		double id = Double.parseDouble(idno);
//		ModelHelper parser = new ModelHelper();		
//		String[] boundCo = parser.splitbySpace(bounds);
//		double nodeWidth = Double.parseDouble(boundCo[2]);
//		double nodeHeight = Double.parseDouble(boundCo[3]);
//		double coX = newPosition.x - (nodeWidth)/2;
//		double coY = newPosition.y - (nodeHeight)/2;
//		
//		String newbounds = coX + " " + coY + " " + nodeWidth + " " + nodeHeight;
//		String newposition = newPosition.x + " " + newPosition.y;
//		
//		nodeSelected.add(0, idno);
//		nodeSelected.add(1, reactomeId);
//		nodeSelected.add(2, newbounds);
//		nodeSelected.add(3, newposition);
//		nodeSelected.add(4, bgColor);
//		nodeSelected.add(5, displayName);
//		nodeSelected.add(6, type);
//		
//		NodeGroup.BoundsHashmap.remove(id);
//		NodeGroup.BoundsHashmap.put(id, newbounds);
//		
//		switch(typeNo) {
//			case 1: ProteinGroup.proteinValuesHashmap.remove(id);
//					ProteinGroup.proteinValuesHashmap.put(id, nodeSelected);
//					break;
//			case 2: ComplexGroup.complexValuesHashmap.remove(id);
//					ComplexGroup.complexValuesHashmap.put(id, nodeSelected);
//					break;
//			case 3: EntityGroup.entityValuesHashmap.remove(id);
//					EntityGroup.entityValuesHashmap.put(id, nodeSelected);
//					break;
//			case 4: ChemicalGroup.chemicalValuesHashmap.remove(id);
//					ChemicalGroup.chemicalValuesHashmap.put(id, nodeSelected);
//					break; 
//			default: break;
//		}				
	}
	
	/**Initializes the Canvas MouseDown, MouseMove, MouseUp and DoubleClick Handlers
	 * 
	 */
	public void initHandlers() {
		
		lastX = canvas.getOffsetWidth()/2;
		lastY = canvas.getOffsetHeight()/2;
		
		canvas.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
//				lastX = event.getX();
//				lastY = event.getY();
//				dragged = false;
//				dragStart = new Vector(lastX,lastY);
//				if(isNodeSelected(dragStart)) {
//					doUpdate(context);
//					selectNode();
//					nodeSelected.clear();
//				} else {
//					doUpdate(context);
//				}
			}
		});

		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				lastX = event.getX();
				lastY = event.getY();
				dragged = true;
				Vector point = new Vector(lastX,lastY);	
				if(dragStart != null) {
//						double moveX = point.x - dragStart.x;
//						double moveY = point.y - dragStart.y;
//						double scaleFactorX = setBoxCoordinates.get(4);
//						double scaleFactorY = setBoxCoordinates.get(5);
//						context.translate(moveX, moveY);
//						doUpdate(context);
//						trackTransforms(1.0, moveX, moveY);
//						dragStart = point;
//						doUpdate(overViewContext);
//						setBox();
//						setBoxCoordinates.add(4, scaleFactorX);
//						setBoxCoordinates.add(5, scaleFactorY);
				}
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				dragStart = null;
			}
		});

//		canvas.addDoubleClickHandler(new DoubleClickHandler() {
//			public void onDoubleClick(DoubleClickEvent event) {
//				dragStart = null;
//				if (!dragged) {
//					zoom(event.isShiftKeyDown() ? -1 : 1);
//				}
//			}
//			private double scaleFactor = 1.1;
//			private void zoom(int i) {
////				double factor = Math.pow(scaleFactor, i);
////				double scaleFactorX = setBoxCoordinates.get(4);
////				double scaleFactorY = setBoxCoordinates.get(5);
////				context.scale(factor, factor);
////				doUpdate(context);
////				trackTransforms(factor, 0.0, 0.0);
////				doUpdate(overViewContext);
////				setBox();
////				setBoxCoordinates.add(4, scaleFactorX);
////				setBoxCoordinates.add(5, scaleFactorY);
//			}
//		});

		if(overView != null) {
			overView.addMouseDownHandler(new MouseDownHandler() {
				public void onMouseDown(MouseDownEvent event) {
//					int overViewX = event.getX();
//					int overViewY = event.getY();
//					double scaleFactorX = setBoxCoordinates.get(4);
//					double scaleFactorY = setBoxCoordinates.get(5);
//					double newCoX = (overViewX / setBoxCoordinates.get(4)) - (setBoxCoordinates.get(2)/2);
//					double newCoY = (overViewY / setBoxCoordinates.get(5)) - (setBoxCoordinates.get(3)/2);
//					double moveX = - newCoX + setBoxCoordinates.get(0);
//					double moveY = - newCoY + setBoxCoordinates.get(1);
//					context.translate(moveX, moveY);
//					doUpdate(context);
//					trackTransforms(1.0, moveX, moveY);
//					doUpdate(overViewContext);
//					setBox();
//					setBoxCoordinates.add(4, scaleFactorX);
//					setBoxCoordinates.add(5, scaleFactorY);
				}
			});
		}
	}

}

