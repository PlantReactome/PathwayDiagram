/*
 * Created on Oct 28, 2011
 *
 */
package org.reactome.diagram.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * This customized FlexTable is used to set up controls for PathwayCanvas: e.g. 
 * zooming, translation. The set up basiclly is copied from Maulik's original code.
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
        PushButton refresh = new PushButton(new Image("refresh.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.reset();
                canvas.update();
            }
        });
        
        PushButton zoomPlus = new PushButton(new Image("zoomplus.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.scale(1.25d);
                canvas.update();
            }
        });
        PushButton zoomMinus = new PushButton(new Image("zoomminus.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.scale(0.8d);
                canvas.update();
            }
        });
        PushButton scrollLeft = new PushButton(new Image("left.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(100, 0);
                canvas.update();
            }
        });
        PushButton scrollTop = new PushButton(new Image("top.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(0, 100);
                canvas.update();
            }
        });
        PushButton scrollBottom = new PushButton(new Image("bottom.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(0, -100);
                canvas.update();
            }
        });
        PushButton scrollRight = new PushButton(new Image("right.png"), new ClickHandler() {
            public void onClick(ClickEvent event) {
                canvas.translate(-100, 0);
                canvas.update();
            }
        });
        refresh.setSize("17px", "17px");
        zoomPlus.setSize("17px", "17px");
        zoomMinus.setSize("17px", "17px");
        scrollLeft.setSize("17px", "17px");
        scrollTop.setSize("17px", "17px");
        scrollBottom.setSize("17px", "17px");
        scrollRight.setSize("17px", "17px");
        
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
    
}
