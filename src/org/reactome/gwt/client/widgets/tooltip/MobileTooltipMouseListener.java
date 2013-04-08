/**
 * Listener class for  MobileTooltip
 * Copyright (C) http://thecodecentral.com | webmaster@thecodecentral.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.reactome.gwt.client.widgets.tooltip;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class MobileTooltipMouseListener extends MouseListenerAdapter {
    private MobileTooltip mobileTooltip;
    
    public MobileTooltipMouseListener(MobileTooltip mobileTooltip){
        this.mobileTooltip = mobileTooltip;
    }
    
    public void onMouseEnter(Widget sender) {
        mobileTooltip.show();
    }
    
    public void onMouseMove(Widget sender, int x, int y) {
        //calculate the posistion of the mouse pointer
        //relative to the client window
        mobileTooltip.setPopupPosition(
                this.getDisplayLocationX(sender, x),
                this.getDisplayLocationY(sender, y));
    }
    
    private int getDisplayLocationX(Widget sender, int x){
        return sender.getAbsoluteLeft() + x +
                getPageScrollLeft() + mobileTooltip.getOffsetX();
    }
    
    private int getDisplayLocationY(Widget sender, int y){
        return sender.getAbsoluteTop() + y +
                getPageScrollTop() + mobileTooltip.getOffsetY();
    }
    
    public void onMouseDown(Widget sender, int x, int y) {
        mobileTooltip.hide();
    }

    public void onMouseLeave(Widget sender) {
        mobileTooltip.hide();
    }
    
    /**
     * Get the offset for the horizontal scroll
     * Thanks Eric for this useful function
     * http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/220a035f47b5ac66/dcfc19a3534f7715?lnk=gst&q=tooltip+listener&rnum=1#dcfc19a3534f7715
     */
    private int getPageScrollTop() {
        return DOM.getAbsoluteTop(
                DOM.getParent( RootPanel.getBodyElement()));
    }
    
    /**
     * Get the offset for the vertical scroll
     * Again.
     * http://groups.google.com/group/Google-Web-Toolkit/browse_thread/thread/220a035f47b5ac66/dcfc19a3534f7715?lnk=gst&q=tooltip+listener&rnum=1#dcfc19a3534f7715
     */
    private int getPageScrollLeft() {
        return DOM.getAbsoluteLeft(
                DOM.getParent( RootPanel.getBodyElement()));
    }
}