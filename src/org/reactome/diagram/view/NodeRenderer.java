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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This customized Renderer is use to render Protein. 
 * @author Maulik & Guanming
 *
 */
//TODO: Make sure text names are correctly encapsulated as in the curator tool. Work on display name for compartments.
//display pathway icons.
public class NodeRenderer extends AbstractRenderer<Node> {
    //private static Resources resources;
	
    public NodeRenderer() {
        defaultLineColor = Parameters.defaultstrokeColor;
        defaultLineWidth = 1.0d;
    }
    
    //interface Resources extends ClientBundle {
    //	@Source("Down.png")
    //	ImageResource down();    	
    //}
    
    //private static Resources getResource() {
    //	if (resources == null)
    //		resources = GWT.create(Resources.class);
	//	
    //	return resources;    	
    //}
    
    /* (non-Javadoc)
     * @see org.reactome.diagram.view.GraphObjectRenderer#render(com.google.gwt.canvas.dom.client.Context2d)
     */
    @Override
    public void render(Context2d c2d,
                       Node node) {
        setColors(c2d, node.getFgColor(), node.getBgColor());
        
        String color = node.getBgColor();
        if (color == null) {
            c2d.setFillStyle(Parameters.defaultbgColor);
        }
        else {
            c2d.setFillStyle(CssColor.make(color));
        }
        
        setStroke(c2d,
                  node);
        drawNode(c2d, node);
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
        
      //  Image down = new Image(getResource().down());
        
       // ImageElement downElement = ImageElement.as(down.getElement());
        
        //Integer x = bounds.getX();
        //Integer w = bounds.getWidth();

        //downElement.setWidth(20);
        //downElement.setHeight(20);
        
       // Integer imageX = x + w - downElement.getWidth();
        
        //c2d.drawImage(downElement, imageX, bounds.getY());
    }

    private void drawNodeAttachments(Context2d context,
                                     Node node) {
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
            Bounds bounds = node.getBounds();
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
    
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 boolean needFill) {
        int coX = bounds.getX();
        int coY = bounds.getY();
        int radius = getRadius();
        int nodeWidth = bounds.getWidth();
        int nodeHeight = bounds.getHeight();
        context.beginPath();
        context.moveTo(coX+radius, coY);
        context.lineTo(coX+nodeWidth-radius, coY);
        context.quadraticCurveTo(coX+nodeWidth, coY, coX+nodeWidth, coY+radius);
        context.lineTo(coX+nodeWidth, coY+nodeHeight-radius);
        context.quadraticCurveTo(coX+nodeWidth, coY+nodeHeight, coX+nodeWidth-radius, coY+nodeHeight);
        context.lineTo(coX+radius, coY+nodeHeight);
        context.quadraticCurveTo(coX, coY+nodeHeight, coX, coY+nodeHeight-radius);
        context.lineTo(coX, coY+radius);
        context.quadraticCurveTo(coX, coY, coX+radius, coY);
        context.closePath();
        if (needFill)
            context.fill();
        context.stroke();
    }
    
    private void drawDashedRectangle(Bounds bounds,
                                     Context2d context,
                                     boolean needFill) {
        int x0 = bounds.getX();
        int y0 = bounds.getY();
        int r = getRadius();
        int w = bounds.getWidth();
        int h = bounds.getHeight();
        context.setLineWidth(Parameters.dashedLineWidth);
        // Draw four dashed lines
        Point p1 = new Point(x0 + r, y0);
        Point p2 = new Point(x0 + w - r, y0);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0 + w, y0 + r);
        p2 = new Point(x0 + w, y0 + h - r);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0 + w - r, y0 + h);
        p2 = new Point(x0 + r, y0 + h);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        p1 = new Point(x0, y0 + h - r);
        p2 = new Point(x0, y0 + r);
        drawDashedLine(context, p1, p2, Parameters.dashedLinePattern);
        // Need to draw rounded corners
        context.beginPath();
        context.moveTo(x0, y0 + r);
        context.quadraticCurveTo(x0, y0, x0 + r, y0);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + w - r, y0);
        context.quadraticCurveTo(x0 + w, y0, x0 + w, y0 + r);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + w, y0 + h - r);
        context.quadraticCurveTo(x0 + w, y0 + h, x0 + w - r, y0 + h);
        context.closePath();
        context.stroke();
        context.beginPath();
        context.moveTo(x0 + r, y0 + h);
        context.quadraticCurveTo(x0, y0 + h, x0, y0 + h - r);
        context.closePath();
        context.stroke();
        if (needFill) {
            context.beginPath();
            context.moveTo(x0+r, y0);
            context.lineTo(x0+w-r, y0);
            context.quadraticCurveTo(x0+w, y0, x0+w, y0+r);
            context.lineTo(x0+w, y0+h-r);
            context.quadraticCurveTo(x0+w, y0+h, x0+w-r, y0+h);
            context.lineTo(x0+r, y0+h);
            context.quadraticCurveTo(x0, y0+h, x0, y0+h-r);
            context.lineTo(x0, y0+r);
            context.quadraticCurveTo(x0, y0, x0+r, y0);
            context.closePath();
            context.fill();
        }
    }
    
    /**
     * Use this method to split a display name into multiple lines.
     * @param name
     * @return
     */
    public List<String> splitName(String name,
                                   Context2d c2d,
                                   int width) {
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
                           int width,
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
                            int width,
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
    
    /**
     * Divides the Annotation into separate arrays of Strings depending on the width of the enclosing node
     * @param context The Context2d object where the Annotation is to be rendered
     */
    protected void drawName(Context2d context,
                            Node node) {
        if (node.getDisplayName() == null || node.getDisplayName().isEmpty())
            return;
        Bounds bounds = node.getBounds();
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
        	font = "12px Lucida Sans"; // This should be pre-set as this font used in the curator tool
       	context.setFont(font);
        context.setTextAlign(TextAlign.CENTER);
        context.setTextBaseline(TextBaseline.TOP);
        int width = bounds.getWidth() - 2 * node.getBounsBuffer();
        List<String> lines = splitName(node.getDisplayName(), 
                                       context, 
                                       width);
        int totalHeight = lines.size() * Parameters.LINE_HEIGHT;
        int x0 = bounds.getX() + bounds.getWidth() / 2;
        int y0 = (bounds.getHeight() - totalHeight) / 2 + bounds.getY();
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
     * Renders the new line formed on the canvas
     * @param linebreak The total number of lines plot before the given line
     * @param context The Context2d object where the given line would be rendered
     * @param dashLastPhrase The Line to be rendered on the canvas
     */
    private void drawLine(int linebreak, 
                          Context2d context,
                          String dashLastPhrase,
                          int x0,
                          int y0) {
        double wordX = x0;
        double wordY = y0 + linebreak * Parameters.LINE_HEIGHT;
        double measure = context.measureText(dashLastPhrase).getWidth(); 
        context.fillText(dashLastPhrase, wordX, wordY, measure);
    }
    
}
