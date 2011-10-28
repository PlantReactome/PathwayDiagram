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
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.HyperEdge;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.view.Parameters;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
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
    private PlugInSupportCanvas overView;
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	private static final List<String> PATHWAYS = Arrays.asList("Pathway 1", "Pathway 2", "Pathway 3", "Pathway 4");
	// List Array Fetched from Database
	
	Context2d context;
	Context2d overViewContext;
	
	boolean draggable, dragged;
	boolean draggableNode;
	Point dragStart = null;
	float lastX, lastY;
	
	static final List<Double> trackScaleFactors = new ArrayList<Double>();
	static final List<Double> trackLeftTranslates = new ArrayList<Double>();
	static final List<Double> trackTopTranslates = new ArrayList<Double>();
	static final List<Double> setBoxCoordinates = new ArrayList<Double>();
	static final List<String> nodeSelected = new ArrayList<String>();
	
	double zoomFactor = Parameters.ZoomFactor;
	double upHeight = Parameters.UpHeight;
	
	public void onModuleLoad() {
		
		
	    //-------(Canvas Functions)
	
//		canvas = Canvas.createIfSupported();
//		backBuffer = Canvas.createIfSupported();
//		overView = Canvas.createIfSupported();
		
		overView = new PlugInSupportCanvas();
		
		if (overView == null) {
			Window.alert("Would not be able to load overview window");
		}
		
		diagramPane = new PathwayDiagramPanel();
		diagramPane.setSize(Window.getClientWidth(),
		                    Window.getClientHeight());
		RootPanel.get(holderId).add(diagramPane);
//		context = diagramPane.getContext2d();
//		context = canvas.getContext2d();
		
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
		
//		PushButton overViewButton = new PushButton(new Image("overview.png"), new ClickHandler() {
//		      public void onClick(ClickEvent event) {
//		    	  if(TabGroup.isVisible()) {
//		    		  TabGroup.setVisible(false);
//		    		  lb.setVisible(false);
//		    	  }
//		    	  if(!overView.isVisible()) {
//		    		  overView.setVisible(true);
//		    	  } else {
//		    		  overView.setVisible(false);
//		    	  }
//		      }
//		});
		
//		overViewButton.setSize("19px", "19px");
		listButton.setSize("19px", "19px");
		refresh.setSize("19px", "19px");
		zoomPlus.setSize("19px", "19px");
		zoomMinus.setSize("19px", "19px");
		scrollLeft.setSize("19px", "19px");
		scrollTop.setSize("19px", "19px");
		scrollBottom.setSize("19px", "19px");
		scrollRight.setSize("19px", "19px");
		
		listButton.setVisible(false); //To change to True once Pathway List is filled
//		RootPanel.get("listbutton").add(listButton);
//		RootPanel.get("overviewbutton").add(overViewButton);
//		RootPanel.get("savepng").add(savepng);
//		RootPanel.get("savejpeg").add(savejpeg);
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
//        diagramPane.update();
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
	
	/**Initializes the Canvas MouseDown, MouseMove, MouseUp and DoubleClick Handlers
	 * 
	 */
	public void initHandlers() {
		
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

