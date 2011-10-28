/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to monitor change of view.
 * @author gwu
 *
 */
public class ViewChangeEvent extends GwtEvent<ViewChangeEventHandler> {
    public static Type<ViewChangeEventHandler> TYPE = new Type<ViewChangeEventHandler>();
    private double scale;
    private double translateX;
    private double translateY;
    private double width;
    private double height;
    
    public ViewChangeEvent() {
    }
    
    public double getWidth() {
        return width;
    }



    public void setWidth(double width) {
        this.width = width;
    }



    public double getHeight() {
        return height;
    }



    public void setHeight(double height) {
        this.height = height;
    }



    public void setScale(double scale) {
        this.scale = scale;
    }


    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }


    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }


    public double getScale() {
        return scale;
    }



    public double getTranslateX() {
        return translateX;
    }



    public double getTranslateY() {
        return translateY;
    }



    @Override
    public Type<ViewChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(ViewChangeEventHandler handler) {
        handler.onViewChange(this);
    }
    
    
}
