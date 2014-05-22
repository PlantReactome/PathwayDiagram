/*
 * Created on July 3rd, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class performs customised styling on widgets.
 * @author weiserj
 *
 */
public class WidgetStyle {
	public static int getZIndex(Widget widget) {
		 return Integer.parseInt(getStyle(widget).getZIndex());
	}
	
	public static void setZIndex(Widget widget, int zIndex) {
    	getStyle(widget).setZIndex(zIndex);
    }
    
    public static void removeBorder(Widget widget) {
    	getStyle(widget).setBorderWidth(0, Unit.PX);
    }
    
    public static void setTransparentBackground(Widget widget) {
    	getStyle(widget).setBackgroundColor("transparent");
    }
    
    public static void setCursor(Widget widget, Cursor cursor) {
    	getStyle(widget).setCursor(cursor);
    }
    
    public static void setPosition(Widget widget, Position position) {
    	getStyle(widget).setPosition(position);
    }
    
    private static Style getStyle(Widget widget) {
    	return widget.getElement().getStyle();
    }
}    