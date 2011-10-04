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

import com.google.gwt.user.client.Window;

/**
 * Sets static final parameters to be used across the entire module
 *
 */
public class Parameters {
	public static final int ZoomFactor = 1;
	public static final int UpHeight = 0;
	public static final int refreshRate = 1500;
	public static final int radius = 5;
	public static final double reqReactomeId = 177929;
	public static final int Reactwidth = 12;
    public static final int Reactheight = 12;
    public static final int catRadius = 5;
	public static final String defaultbgColor = "rgba(0,0,0,1)";
	public static final String defaultstrokeColor = "rgba(0,0,0,1)";
	public static final double height = (int) (0.75 * Window.getClientHeight());
	public static final double width = (int) (0.95 * Window.getClientWidth());
}
