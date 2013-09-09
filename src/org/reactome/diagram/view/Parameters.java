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
    // Font used to draw text
    public static final String DEFAULT_FONT = "12px Lucida Sans";
    
	public static final int MOVEX = 100;
	public static final int MOVEY = 100;
	public static final double ZOOMMAX = 5d;
	public static final double ZOOMMIN = 0.20d;
	public static final double ZOOMFACTOR = 1.20d;

	public static final int TOTAL_INTERACTOR_NUM = 10;
	public static final int INTERACTOR_EDGE_LENGTH = 170;
	public static final int INTERACTOR_CHAR_WIDTH = 9;
	public static final int IMAGE_HEIGHT = 80;
	public static final int IMAGE_WIDTH = 80;

	public static final CssColor defaultExpressionColor = CssColor.make(255, 255, 255);
	
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
	
	// Using green for default selection as in the pathway hierarchy
	public static final CssColor defaultSelectionColor = CssColor.make(0, 0, 255);
	public static final CssColor defaultHighlightColor = CssColor.make(0, 127, 200);
	public static final int defaultNodeSelectionLineWidth = 3;
	public static final int defaultEdgeSelectionLineWidth = 2;
	// For drawing cross
	public static final CssColor defaultCrossColor = CssColor.make(255, 0, 0);
	public static final int defaultCrossWidth = 3;
	
	public static final double[] dashedLinePattern = new double[]{5.0d, 5.0d};
	public static final double[] setToSetLinePattern = new double[]{10.0d, 20.0d};
	public static final int dashedLineWidth = 2;
	
	public static final int OVERVIEW_SIZE = 100;	
}
