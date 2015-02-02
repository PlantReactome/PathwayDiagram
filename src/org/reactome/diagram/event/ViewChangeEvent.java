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
    private ZoomEvent zoomEvent;
    private TranslationEvent translationEvent;
    private ResizeEvent resizeEvent;

    public ViewChangeEvent(ZoomEvent zoom, TranslationEvent translation, ResizeEvent resize) {
    	zoomEvent = zoom;
    	translationEvent = translation;
    	resizeEvent = resize;
    }
    
    public ZoomEvent getZoomEvent() {
		return zoomEvent;
	}

	public TranslationEvent getTranslationEvent() {
		return translationEvent;
	}

	public ResizeEvent getResizeEvent() {
		return resizeEvent;
	}

	@Override
    public Type<ViewChangeEventHandler> getAssociatedType() {
        return TYPE;
    }
    
    @Override
    protected void dispatch(ViewChangeEventHandler handler) {
        handler.onViewChange(this);
    }
    
    public static class ZoomEvent {
    	private static double minScale = 0;
    	private double scale;
    	
		public ZoomEvent(double scale) {
			this.scale = scale;
		}

		public static void setMinScale(double minScale) {
			ZoomEvent.minScale = minScale;
		}
		
		public static double getMinScale() {
			return minScale;
		}

		public double getScale() {
			return scale;
		}
		
		public boolean scaleLessThanMinimum() {
			return getScale() < getMinScale();
		}
    }
    
    public static class TranslationEvent {
    	private double translateX;
    	private double translateY;
    	
		public TranslationEvent(double translateX, double translateY) {
			this.translateX = translateX;
			this.translateY = translateY;
		}

		public double getTranslateX() {
			return translateX;
		}

		public double getTranslateY() {
			return translateY;
		}
    }
    
    public static class ResizeEvent {
    	private double width;
    	private double height;
		
    	public ResizeEvent(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}
    }
}
