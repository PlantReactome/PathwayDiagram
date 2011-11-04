/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * This customized FlexTable is used to set up controls for PathwayCanvas: e.g. 
 * zooming, translation. The set up basically is copied from Maulik's original code.
 * @author gwu
 *
 */
public class PathwayCanvasControls extends FlexTable {
    private PathwayCanvas canvas;
    
    public PathwayCanvasControls(PathwayCanvas canvas) {
        this.canvas = canvas;
        init();
    }
    
    private void init() {
        Image refresh = new Image("Reset.png");
        refresh.setAltText("reset");
        refresh.setTitle("reset");
        refresh.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.reset();
                canvas.update();
            }
        });
        
        Image zoomPlus = new Image("Plus.png");
        zoomPlus.setAltText("zoom in");
        zoomPlus.setTitle("zoom in");
        zoomPlus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.scale(1.25d);
                canvas.update();
            }
        });
        
        Image zoomMinus = new Image("Minus.png");
        zoomMinus.setAltText("zoom out");
        zoomMinus.setTitle("zoom out");
        zoomMinus.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.scale(0.8d);
                canvas.update();
            }
        });
        
        Image scrollLeft = new Image("Left.png");
        scrollLeft.setAltText("move left");
        scrollLeft.setTitle("move left");
        scrollLeft.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(100, 0);
                canvas.update();
            }
        });
        
        Image scrollTop = new Image("Up.png");
        scrollTop.setAltText("move up");
        scrollTop.setTitle("move up");
        scrollTop.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(0, 100);
                canvas.update();
            }
        });
        
        Image scrollBottom = new Image("Down.png");
        scrollBottom.setAltText("move down");
        scrollBottom.setTitle("move down");
        scrollBottom.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(0, -100);
                canvas.update();
            }
        });
        
        Image scrollRight = new Image("Right.png");
        scrollRight.setAltText("move right");
        scrollRight.setTitle("move right");
        scrollRight.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(-100, 0);
                canvas.update();
            }
        });
        
//        setButtonSize(refresh);
//        setButtonSize(zoomPlus);
//        setButtonSize(zoomMinus);
//        setButtonSize(scrollLeft);
//        setButtonSize(scrollRight);
//        setButtonSize(scrollBottom);
//        setButtonSize(scrollTop);
        
        FlexCellFormatter cellFormatter = getFlexCellFormatter();
        setWidget(0, 0, refresh);
        cellFormatter.setRowSpan(0, 0, 2);
        setWidget(0, 1, zoomPlus);
        cellFormatter.setRowSpan(0, 1, 2);
        setWidget(0, 2, zoomMinus);
        cellFormatter.setRowSpan(0, 2, 2);
        setWidget(0, 3, scrollLeft);
        cellFormatter.setRowSpan(0, 3, 2);
        setWidget(0, 4, scrollTop);
        setWidget(1, 0, scrollBottom);
        setWidget(0, 5, scrollRight);
        cellFormatter.setRowSpan(0, 5, 2);
    }
    
//    private void setButtonSize(PushButton btn) {
//        btn.setSize("10px", "10px");
//    }
    
}
