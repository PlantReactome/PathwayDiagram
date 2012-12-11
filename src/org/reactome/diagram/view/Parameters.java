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


package org.reactome.diagram.view;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * Sets static final parameters to be used across the entire module
 *
 */
public class Parameters {
	public static final int MOVEX = 100;
	public static final int MOVEY = 100;
	public static final double ZOOMIN = 1.25d;
	public static final double ZOOMOUT = 0.8d;

	public static final int TOTAL_INTERACTOR_NUM = 10;
	public static final int INTERACTOR_EDGE_LENGTH = 170;
	public static final int INTERACTOR_WIDTH = 80;
	public static final int INTERACTOR_HEIGHT = 55;
	
	public static final int ZoomFactor = 1;
	public static final int UpHeight = 0;
	public static final int refreshRate = 1500;
	public static final int radius = 5;
	public static final double reqReactomeId = 177929;
	public static final int Reactwidth = 12;
    public static final int Reactheight = 12;
    public static final int catRadius = 5;
	public static final CssColor defaultbgColor = CssColor.make(204, 255, 204);
	public static final CssColor defaultstrokeColor = CssColor.make(0, 0, 0);
	// Used to set the height of each line in name drawing
	public static final int LINE_HEIGHT = 14;
	// A grey shade used in disease pathway drawing
	public static final CssColor defaultShadeColor = CssColor.make("rgba(204, 204, 204, 0.65)"); // Alpha value has to be double
	// Using blue for default selection as in the curator tool
	public static final CssColor defaultSelectionColor = CssColor.make(0, 0, 255);
	public static final CssColor defaultHighlightColor = CssColor.make(0, 255, 0);
	public static final int defaultNodeSelectionLineWidth = 3;
	public static final int defaultEdgeSelectionLineWidth = 2;
	// For drawing cross
	public static final CssColor defaultCrossColor = CssColor.make(255, 0, 0);
	public static final int defaultCrossWidth = 3;
	
	public static final double[] dashedLinePattern = new double[]{5.0d, 5.0d};
	public static final int dashedLineWidth = 2;
}
