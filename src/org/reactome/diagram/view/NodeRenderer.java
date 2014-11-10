/*
 * Created on Sep 22, 2011
 * Most of code here is copied from Maulik's original GSoC canvas prototype project.
 */
package org.reactome.diagram.view;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;
import org.reactome.diagram.model.NodeAttachment;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.touch.client.Point;

/**
 * This customized Renderer is used to render Protein. 
 * @author Maulik & Guanming
 *
 */
//TODO: Make sure text names are correctly encapsulated as in the curator tool. Work on display name for compartments.
//display pathway icons.
public class NodeRenderer extends AbstractRenderer<Node> {
	
    public NodeRenderer() {
        defaultLineColor = Parameters.defaultstrokeColor;
        defaultLineWidth = 1.0d;
    }
    
    /* (non-Javadoc)
     * @see org.reactome.diagram.view.GraphObjectRenderer#render(com.google.gwt.canvas.dom.client.Context2d)
     */
    @Override
    public void render(Context2d c2d,
                       Node node) {
        setFillStyle(c2d, node);
        
        setStroke(c2d,
                  node);
        drawNode(c2d, node);
    }

    protected void setFillStyle(Context2d c2d, Node node) {
        String color = node.getBgColor();
        if (color == null) {
            c2d.setFillStyle(Parameters.defaultbgColor);
        }
        else {
            c2d.setFillStyle(CssColor.make(color));
        }
    }
    
    protected void drawNode(Context2d c2d, Node node) {
        Bounds bounds = node.getBounds();
    	
    	drawRectangle(bounds, 
                      c2d,
                      node);
        drawName(c2d, 
                 node);
        drawNodeAttachments(c2d,
                            node);
        
    }

    protected void drawNodeAttachments(Context2d context, Node node) {
    	drawNodeAttachments(node.getBounds(), context, node);
    }
    
    protected void drawNodeAttachments(Bounds bounds, Context2d context, Node node) {
        if (node.getNodeAttachments() == null || node.getNodeAttachments().size() == 0)
            return;
        double x, y, w;
        // Alway use white background for node attachment
        CssColor white = CssColor.make(255, 255, 255);
        // Set for text drawing
        context.setFont(WIDGET_FONT);
        context.setTextAlign(TextAlign.CENTER);
        context.setTextBaseline(TextBaseline.MIDDLE);
        for (NodeAttachment attachment : node.getNodeAttachments()) {
            String label = attachment.getLabel();
            w = 0.0d;
            if (label != null && label.length() > 0) {
                TextMetrics size = context.measureText(label);
                w = size.getWidth();
            }
            if (w + 4 < EDGE_TYPE_WIDGET_WIDTH)
                w = EDGE_TYPE_WIDGET_WIDTH;
            else
                w += 4;
            // Position for attachments
            x = bounds.getX() + bounds.getWidth() * attachment.getRelativeX();
            y = bounds.getY() + bounds.getHeight() * attachment.getRelativeY();
            context.beginPath();
            context.rect(x - w / 2.0d, 
                         y - EDGE_TYPE_WIDGET_WIDTH / 2.0d,
                         w, 
                         EDGE_TYPE_WIDGET_WIDTH);
            context.closePath();
            context.setFillStyle(white);
            context.fill();
            context.stroke();
            if (label != null && label.length() > 0) {
                context.setFillStyle(context.getStrokeStyle());
                context.fillText(label, x, y);
            }
        }
    }
    
    protected int getRadius() {
        return ROUND_RECT_ARC_WIDTH;
    }
    
    /**
     * This is a template that should be implemented by a sub-class.
     * @param bounds
     * @param context
     * @param node
     */
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
        if (node.isNeedDashedBorder())
            drawDashedRectangle(bounds, context, true);
        else
            drawRectangle(bounds, 
                          context,
                          true);
    }
    
    protected void drawDashedRectangle(Bounds bounds, 
    								   Context2d context, 
    								   boolean needFill) {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
    	
    	context.setLineWidth(Parameters.dashedLineWidth);
        // Draw four dashed lines
        Point p1 = new Point(x, y);
        Point p2 = new Point(x + w, y);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x + w, y);
        p2 = new Point(x + w, y + h);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x + w, y + h);
        p2 = new Point(x, y + h);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x, y + h);
        p2 = new Point(x, y);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
    	
    	if (needFill)
    		context.fill();
	}

	protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 boolean needFill) {
            drawRectangle(bounds, 
                          context, 
                          needFill, 
                          true);
    }
    
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 boolean needFill,
                                 boolean needStroke) {
        drawRectangle(context, bounds);
    	
        if (needFill)
            context.fill();
        if (needStroke)
            context.stroke();
    }
    
    /**
     * Use this method to split a display name into multiple lines.
     * @param name
     * @return
     */
    public List<String> splitName(String name,
                                   Context2d c2d,
                                   double width) {
        List<String> rtn = new ArrayList<String>();
        String[] tokens = name.split(" "); // Use these delimits
        StringBuilder line = new StringBuilder();
        int lineStart = 0;
        for (String token : tokens) {
            lineStart = line.length();
            if (lineStart > 0)
                line.append(" ");
            line.append(token);
            double lineWidth = c2d.measureText(line.toString()).getWidth();
            if (lineWidth == width) {
                rtn.add(line.toString());
                line.setLength(0);
            }
            else if (lineWidth > width) {
                splitWord(line, lineStart, width, c2d, rtn);
            }
        }
        if (line.length() > 0)
            rtn.add(line.toString());
        return rtn;
    }
    
    private void splitWord(StringBuilder line,
                           int start,
                           double width,
                           Context2d c2d,
                           List<String> lines) {
        boolean isSplit = false;
        for (int i = line.length() - 1; i >= start; i--) {
            char letter = line.charAt(i);
            // Check if this letter can be used to break this work
            if (letter == ':' || letter == '.' || letter == '-' || letter == ',' || letter == '(') {
                double currentWidth = c2d.measureText(line.substring(0, i)).getWidth();
                if (currentWidth <= width) {
                    // Split this word into two parts
                    if (letter == '(') // Make sure '(' should be in the next line
                        _splitWord(line, i, lines, width, c2d);
                    else
                        _splitWord(line, i + 1, lines, width, c2d);
                    isSplit = true;
                    break;
                }
            }
        }
        if (!isSplit) {
            // Have to remove the whole word
            _splitWord(line, start, lines, width, c2d);
        }
    }

    private void _splitWord(StringBuilder line, 
                            int start, 
                            List<String> lines,
                            double width,
                            Context2d c2d) {
        if (start == 0)
            return; // This should be listed as a whole word, cannot split any more (e.g. a very long word)
        String word = line.substring(start);
        line.delete(start, line.length());
        lines.add(line.toString());
        line.setLength(0);
        line.append(word);
        if (c2d.measureText(line.toString()).getWidth() > width) {
            // Sometimes a single word may be too long. Need to run this recursively
            splitWord(line, 0, width, c2d, lines);
        }
    }
    
    protected void drawName(Context2d context,
    						Node node) {
    	drawName(node.getBounds(), context, node);
    }
    
    /**
     * Divides the Annotation into separate arrays of Strings depending on the width of the enclosing node
     * @param context The Context2d object where the Annotation is to be rendered
     */
    protected void drawName(Bounds bounds,
                            Context2d context,
                            Node node) {
        if (node.getDisplayName() == null || node.getDisplayName().isEmpty())
            return;
       
        String fgColor = node.getFgColor();
        if (fgColor == null)
            fgColor = "rgba(0, 0, 0, 1)";
        CssColor strokeStyleColor = CssColor.make(fgColor);
        FillStrokeStyle oldStroke = context.getStrokeStyle();
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(fgColor);
        FillStrokeStyle oldFillStyle = context.getFillStyle();
        context.setFillStyle(fillStyleColor);
        
        String font = node.getFont();
        if (font == null)		
        	font = Parameters.DEFAULT_FONT; // This should be pre-set as this font used in the curator tool
       	context.setFont(font);
        context.setTextAlign(TextAlign.CENTER);
        context.setTextBaseline(TextBaseline.TOP);
        double width = bounds.getWidth() - 2 * node.getBounsBuffer();
        List<String> lines = splitName(node.getDisplayName(), 
                                       context, 
                                       width);
        int totalHeight = lines.size() * Parameters.LINE_HEIGHT;
        double x0 = bounds.getX() + bounds.getWidth() / 2;
        double y0 = getTextPositionY(bounds, totalHeight);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            drawLine(i, 
                     context, 
                     line,
                     x0, 
                     y0);
        }
        context.setStrokeStyle(oldStroke);
        context.setFillStyle(oldFillStyle);
    }

    /**
     * A refactored method for getting the text position's y coordinate.
     * @param bounds
     * @param totalHeight
     * @return
     */
    protected double getTextPositionY(Bounds bounds, int totalHeight) {
        double y0 = (bounds.getHeight() - totalHeight) / 2 + bounds.getY();
        return y0;
    }
    
    /**
     * Renders the new line formed on the canvas
     * @param linebreak The total number of lines plot before the given line
     * @param context The Context2d object where the given line would be rendered
     * @param dashLastPhrase The Line to be rendered on the canvas
     */
    protected void drawLine(int linebreak, 
                          Context2d context,
                          String dashLastPhrase,
                          double x0,
                          double y0) {
        double wordX = x0;
        double wordY = y0 + linebreak * Parameters.LINE_HEIGHT;
        double measure = context.measureText(dashLastPhrase).getWidth(); 
        context.fillText(dashLastPhrase, wordX, wordY, measure);
    }

    /**
     * A method to draw a rectangle without rounding angles.
     * @param context
     * @param bounds
     */
    protected void drawRectangle(Context2d context, Bounds bounds) {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
    	
    	context.beginPath();
        context.moveTo(x, y);
        context.lineTo(x + w, y);
        context.lineTo(x + w, y + h);
        context.lineTo(x, y + h);
        context.lineTo(x, y);
        context.closePath();
    }
    
}
