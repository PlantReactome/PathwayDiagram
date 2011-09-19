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

package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**Initializes the Color and the Font Settings on a Context
 * 
 */
public class ContextSettings {
	
	String bgColor;
	String strokeColor;
	int fontSize;
	int zoomFactor;
	
	/**Constructor to set the Color Settings on a Context
	 * 
	 * @param bgColor The Fill Color on the Context
	 * @param strokeColor The Stroke Color on the Context
	 */
	public ContextSettings(String bgColor, String strokeColor) {
		// TODO Auto-generated constructor stub
		this.bgColor = bgColor;
		this.strokeColor = strokeColor;
		this.zoomFactor = Parameters.ZoomFactor;
	}
	
	/**Constructor to set the Font Settings on a Context
	 * 
	 * @param fontSize The Font Size of the text
	 */
	public ContextSettings(int fontSize) {
		// TODO Auto-generated constructor stub
		this.zoomFactor = Parameters.ZoomFactor;
		this.fontSize = fontSize/zoomFactor;
	}

	/**Converts the Stroke and Fill Colors to Standard CssColor Objects and sets it on the Canvas
	 * 
	 * @param context Context2d object for which the Color Settings are initialized
	 */
	public void makecolor(Context2d context) {
		// TODO Auto-generated method stub
		CssColor strokeStyleColor = CssColor.make(strokeColor);
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(bgColor);
        context.setFillStyle(fillStyleColor);
	}
	
	/**Sets the Font Size and Alignment on the Canvas
	 * 
	 * @param context Context2d object for which the Font Settings are initialized
	 */
	public void setFont(Context2d context) {
		String font =  fontSize + "px Lucida Sans";
        context.setFont(font);
        context.setTextAlign("center");
        context.setTextBaseline("top");
	}
	
}
