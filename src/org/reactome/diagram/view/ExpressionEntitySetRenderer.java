/*
  * Created on May 17, 2013
 *
 */
package org.reactome.diagram.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.ComplexNode;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.touch.client.Point;

/**
 * @author weiserj
 *
 */
public class ExpressionEntitySetRenderer extends EntitySetRenderer {
	private Double currentX;
    private Double currentY;
	private Double innerRectStartX;
	private Double innerRectEndX;
	private Double segmentHeight;
	private QuadraticBezierCurve curve;
	private List<String> componentColors;
	
    public ExpressionEntitySetRenderer() {
        super();
    }

    @Override
    protected void drawRectangle(Context2d context, Node node) {
    	drawRectangle(getOuterBounds(node), context, node);
    	drawRectangle(getOuterBounds(node), context, false, true);
    	drawRectangle(getInnerBounds(node), context, false, true);
    }
    
    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
    	final ComplexNode currentComplex = (ComplexNode) node;
    	
    	componentColors = currentComplex.getComponentColors();
    	
    	if (getUniqueComponentColors().size() == 1) {
    		final String bgColor = getComponentColors().get(0); 
    		
    		currentComplex.setBgColor(bgColor);
    		currentComplex.setFgColor(currentComplex.getVisibleFgColor(bgColor));
    		
    		context.setFillStyle(bgColor);
    		super.drawRectangle(bounds, context, node);
    		return;
    	}
    	
    	setStroke(context, node);
        currentX = (double) bounds.getX();
        currentY = (double) bounds.getY() + getRadius();
        innerRectStartX = currentX + getRadius();
        innerRectEndX = currentX + bounds.getWidth() - getRadius();
        
        Double segmentWidth = ((double) bounds.getWidth() / getComponentColors().size());
        segmentHeight = (double) (bounds.getHeight() - (2 * getRadius()));
        
        System.out.println(node.getDisplayName() + ": " + segmentWidth);

        if (segmentWidth < 10) {
        		currentY -= getRadius();
        		for (Integer i = 0; i < getComponentColors().size(); i++) {
        			context.setFillStyle(getComponentColors().get(i));
        			drawRectangleSegment(segmentWidth, (double) bounds.getHeight(), context);
        		}
        } else {
        	for (Integer i = 0; i < getComponentColors().size(); i++) {
        		drawSegment(segmentWidth, segmentHeight, bounds.getHeight(), getComponentColors().get(i), context);
        	}
        }
        //createPath(bounds, context);
        //context.stroke();
    }
    
    protected void drawLine(int lineBreak, Context2d context2d, String dashLastPhrase, int x0 , int y0) {
    	// When complex has white background, draw line as a generic node does
    	if (getUniqueComponentColors().size() == 1 && getComponentColors().get(0).equals("rgb(255,255,255)")) {
    		super.drawLine(lineBreak, context2d, dashLastPhrase, x0, y0);
    		return;
    	}
    	
    	double wordX = x0;
    	double wordY = y0 + lineBreak * Parameters.LINE_HEIGHT;
    	double measure = context2d.measureText(dashLastPhrase).getWidth();
    	
    	final FillStrokeStyle oldFillStyle = context2d.getFillStyle();
    	final FillStrokeStyle oldStrokeStyle = context2d.getStrokeStyle();
    	final double lineWidth = context2d.getLineWidth();
    	
    	context2d.setFillStyle("rgb(255,255,255)"); // White fill 
    	context2d.setStrokeStyle("rgb(0, 0, 0)"); // Black stroke
    	context2d.setLineWidth(2.5);
    	
    	context2d.strokeText(dashLastPhrase, wordX, wordY, measure);
    	context2d.fillText(dashLastPhrase, wordX, wordY, measure);
     	
    	context2d.setFillStyle(oldFillStyle);
    	context2d.setStrokeStyle(oldStrokeStyle);
    	context2d.setLineWidth(lineWidth);
    }
    
    private void drawSegment(Double width, Double height, Integer maxSegmentHeight, String color, Context2d context) {
    	context.setFillStyle(color);
    	
    	Double segmentStart = currentX;
    	Double segmentEnd = currentX + width;
    	Double leftHeight = height;
    	
    	if (segmentStart < innerRectStartX) {
    		if (curve == null || !curve.isBeingDrawn()) {
    			Point startPoint = new Point(segmentStart, currentY + leftHeight);
    			Point endPoint = new Point(innerRectStartX, currentY + leftHeight + getRadius());
    			Point controlPoint = new Point(startPoint.getX(), endPoint.getY());
    			curve = new QuadraticBezierCurve(startPoint, endPoint, controlPoint);
    		}
    		
    		
    		Double pastArc = (double) 0;
    		if (segmentEnd > innerRectStartX) {
    			pastArc = segmentEnd - innerRectStartX;
    			curve.setBeingDrawn(false);
    		}
    		
    		drawSegmentInArc(width, leftHeight, pastArc, true, context);
    	} else if (segmentEnd > innerRectEndX) {    		
    		if (curve == null || !curve.isBeingDrawn()) {
    			Point startPoint = new Point(innerRectEndX, currentY + leftHeight);
    			Point endPoint = new Point(innerRectEndX + getRadius(), currentY + leftHeight - getRadius());
    			Point controlPoint = new Point(endPoint.getX(), startPoint.getY());
    			
    			curve = new QuadraticBezierCurve(startPoint, endPoint, controlPoint);
    		}
    		
    		
    		Double pastArc = (double) 0;
    		if (segmentStart < innerRectEndX) {
    			pastArc = innerRectEndX - segmentStart;
    			curve.setBeingDrawn(false);
    		}
    		
    		drawSegmentInArc(width, leftHeight, pastArc, false, context);
    	} else {
    		drawRectangleSegment(width, height, context);
    	}
    }
    
    private void drawSegmentInArc(Double width, Double leftHeight, Double pastArc, Boolean leftArc, Context2d context) {
    	context.beginPath();
    	
    	Double x = currentX;
    	Double y = currentY;
    	
    	context.moveTo(x, y);
    	
    	y = y + leftHeight; 
    	context.lineTo(x, y);
    	
    	Double arcSize = width - pastArc;
    	
    	if (!leftArc) {
    		x += pastArc;
    		context.lineTo(x, y);
    	}
    		
    	double tValue = (curve.getSubCurveStart().getX() - curve.getStartPoint().getX() + arcSize) / getRadius();
    	curve.setSubCurveEnd(curve.getPointOnCurve(tValue));
    	
    	x = curve.getSubCurveEnd().getX();
    	y = curve.getSubCurveEnd().getY();
    	Point control;
    	if (leftArc)
    		control = new Point(curve.getSubCurveStart().getX(), y);
    	else
    		control = new Point(x, curve.getSubCurveStart().getY());
    	
    	context.quadraticCurveTo(control.getX(), 
    							 control.getY(),
    							 x,
    							 y);

    	if (leftArc) {	
    		x += pastArc;
    		context.lineTo(x, y);	
    	}
    		
    	Double rightHeight = (2 * (curve.getSubCurveEnd().getY() - curve.getSubCurveStart().getY()) + leftHeight); 
    	curve.setSubCurveStart(curve.getSubCurveEnd());
    	
    	y -= rightHeight;
    	context.lineTo(x, y);
    	
    	if (leftArc) {
    		context.lineTo(x - pastArc, y);
    	}
    	
    	if (leftArc)
    		context.quadraticCurveTo(currentX, y, currentX, currentY);
    	else
    		context.quadraticCurveTo(x, currentY, currentX + pastArc, currentY);
    	
    	if (!leftArc) {
    		context.lineTo(currentX, currentY);
    	}
    	
    	context.closePath();
    	
    	context.fill();  
    	
    	currentX = x;
    	currentY = y;
    	segmentHeight = rightHeight;
    }
    
    private void drawRectangleSegment(Double width, Double height, Context2d context) {
    	context.fillRect(currentX, currentY, width, height);
    	currentX += width;
    }
    
    private List<String> getComponentColors() {
    	return componentColors;
    }
    
    private Set<String> getUniqueComponentColors() {
    	return new HashSet<String>(getComponentColors());
    }
    
    private class QuadraticBezierCurve {
    	private Point start;
    	private Point end;
    	private Point control;
    	private Point subCurveStart;
    	private Point subCurveEnd;
    	private boolean beingDrawn;
    	
    	public QuadraticBezierCurve(Point start, Point end, Point control) {
    		this.start = start;
    		this.end = end;
    		this.control = control;
    		this.subCurveStart = new Point(start);
    		this.beingDrawn = true;
    	}
    	
		public Point getStartPoint() {
    		return start;
    	}
    	
    	public Point getEndPoint() {
    		return end;
    	}
    	
    	public Point getControlPoint() {
    		return control;
    	}
    	
    	public Point getSubCurveStart() {
			return subCurveStart;
		}

		public void setSubCurveStart(Point subCurveStart) {
			this.subCurveStart = subCurveStart;
		}

		public Point getSubCurveEnd() {
			return subCurveEnd;
		}

		public void setSubCurveEnd(Point subCurveEnd) {
			this.subCurveEnd = subCurveEnd;
		}

		public boolean isBeingDrawn() {
    		return beingDrawn;
    	}
    	
    	public void setBeingDrawn(boolean beingDrawn) {
    		this.beingDrawn = beingDrawn;
    	}
    	
    	public Point getPointOnCurve(double tValue) {
    		return new Point(getCoordinateOnCurve(tValue, getStartPoint().getX(), 
    													  getControlPoint().getX(),
    													  getEndPoint().getX()
    											 ),
    						 getCoordinateOnCurve(tValue, getStartPoint().getY(),
    								 					  getControlPoint().getY(),
    								 					  getEndPoint().getY()
    								 			 )
    		);
    	}

    	// Based on http://stackoverflow.com/questions/5634460/quadratic-bezier-curve-calculate-point
		private double getCoordinateOnCurve(double tValue, double startCoord, double controlCoord,
				double endCoord) {
			
			double startTerm = (1 - tValue) * (1 - tValue) * startCoord;
			double controlTerm = 2 * (1 - tValue) * tValue * controlCoord;
			double endTerm = tValue * tValue * endCoord;
			
			return startTerm + controlTerm + endTerm;
		}
    }
}
