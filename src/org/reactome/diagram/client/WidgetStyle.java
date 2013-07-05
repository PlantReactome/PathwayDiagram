/*
 * Created on July 3rd, 2013
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * This class performs customised styling on widgets.
 * @author weiserj
 *
 */
public class WidgetStyle {
    
    public static void bringToFront(Widget widget) {
    	widget.getElement().getStyle().setZIndex(2);
    }
}    