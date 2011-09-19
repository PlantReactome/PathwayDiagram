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
 */

package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Renders the text on the Context2d object at the specified coordinates within the specified width.
 */

public class RenderableText {

	double wordX;
	double wordY;
	double wordmeasure;
	
	String strokeColor;
	String bgColor;
	String text;
	int fontSize;
	
	/**Specifies how and what text is actually rendered on the Context
	 * 
	 * @param wordX The X coordinate of the point to begin render of the text
	 * @param wordY The Y coordinate of the point to begin render of the text
	 * @param wordmeasure The maximum set width to fill the text
	 * @param strokeColor Stroke Color of the text
	 * @param bgColor Fill Color of the text
	 * @param fontSize Font Size of the text
	 * @param displayName text to be rendered
	 */
	
	public RenderableText(double wordX, double wordY, double wordmeasure, String strokeColor, String bgColor, int fontSize, String displayName) {
		// TODO Auto-generated constructor stub
		this.wordX = wordX;
		this.wordY = wordY;
		this.wordmeasure = wordmeasure;
		
		this.strokeColor = "rgba(0,0,0,1)";
		this.bgColor = "rgba(0,0,0,1)";
		this.text = displayName;
		this.fontSize = fontSize;
	}
	
	/**Renders the text squeezed into the set maximum width beginning from the X & Y coordinates of the specified points.
	 * 
	 * @param context The Context2d object to render the text.
	 */
	
	public void write(Context2d context){
		ContextSettings colors = new ContextSettings(bgColor,strokeColor);
		colors.makecolor(context);
		
		ContextSettings font = new ContextSettings(fontSize);
		font.setFont(context);
		context.beginPath();
		context.fillText(text, wordX, wordY, wordmeasure);
        context.fill();
		context.stroke();
		context.closePath();
        
	}

}
