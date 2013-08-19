/*
 * Created on July 3rd, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class performs customised styling on widgets.
 * @author weiserj
 *
 */
public class WidgetStyle {
    
    public static void bringToFront(Widget widget) {
    	getStyle(widget).setZIndex(2);
    }
    
    public static void removeBorder(Widget widget) {
    	getStyle(widget).setBorderWidth(0, Unit.PX);
    }
    
    private static Style getStyle(Widget widget) {
    	return widget.getElement().getStyle();
    }
}    