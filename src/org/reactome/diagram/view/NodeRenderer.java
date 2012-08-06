/*
 * Created on Sep 22, 2011
 * Most of code here is copied from Maulik's original GSoC canvas prototype project.
 */
package org.reactome.diagram.view;

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
 * This customized Renderer is use to render Protein. 
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
        setColors(c2d, node.getFgColor(), node.getBgColor());
        Bounds bounds = node.getBounds();
        String color = node.getBgColor();
        if (color == null) {
            c2d.setFillStyle(Parameters.defaultbgColor);
        }
        else {
            c2d.setFillStyle(CssColor.make(color));
        }
        setStroke(c2d,
                  node);
        drawRectangle(bounds, 
                      c2d,
                      node);
        drawName(c2d, 
                 node);
        drawNodeAttachments(c2d,
                            node);
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
     * Divides the Annotation into separate arrays of Strings depending on the width of the enclosing node
     * @param context The Context2d object where the Annotation is to be rendered
     */
    protected void drawName(Context2d context,
                            Node node) {
        if (node.getDisplayName() == null || node.getDisplayName().isEmpty())
            return;
        String[] words = node.getDisplayName().split(" ");
        Point measures;
        int linebreak = 0;
        int printFlag = 0;
        String dashLastPhrase = "";
        String mainSeparator = " ";
        String separator = ":";
        Bounds bounds = node.getBounds();
        int x0 = bounds.getX() + bounds.getWidth() / 2;
        int y0 = bounds.getY() + 8;
        String fgColor = node.getFgColor();
        if (fgColor == null)
            fgColor = "rgba(0, 0, 0, 1)";
        CssColor strokeStyleColor = CssColor.make(fgColor);
        FillStrokeStyle oldStroke = context.getStrokeStyle();
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(fgColor);
        FillStrokeStyle oldFillStyle = context.getFillStyle();
        context.setFillStyle(fillStyleColor);
        String font = "12px Lucida Sans"; // This should be fixed
        context.setFont(font);
        context.setTextAlign(TextAlign.CENTER);
        context.setTextBaseline(TextBaseline.TOP);
        for (int i = 0; i < words.length ; i++) {
            String word = words[i];
            String[] colonWords = word.split(":");
            for (int j = 0; j < colonWords.length ; j++) {
                String colonword = colonWords[j];
                String[] dashWords = colonword.split("-");
                int flag = 0;
                for (int k = 0; k < dashWords.length ; k++) {
                    String dashword = dashWords[k];
                    measures = calculateMeasures(dashLastPhrase,dashword,context);
                    double testmeasure = measures.getY();
                    if ((testmeasure) <= bounds.getWidth()) {
                        if(dashLastPhrase == "") {
                            dashLastPhrase = dashword;
                        } else {
                            if (flag != 0) {
                                dashLastPhrase += "-" + dashword;   
                            } else {
                                dashLastPhrase += dashword;
                            }
                        }
                        flag = 1;
                        printFlag = 0;
                    } else {
                        drawLine(linebreak, context, dashLastPhrase, x0, y0);
                        if(flag != 0) {
                            dashLastPhrase = "-" + dashword;
                        } else {
                            dashLastPhrase = dashword;
                        }
                        printFlag = 1;
                        linebreak++;
                    }
                    if (k == (dashWords.length - 1)){
                        printFlag = 0;
                        break;
                    }
                }
                if (j < (colonWords.length - 1)){
                    dashLastPhrase += separator ;
                }
            }
            if (i < (words.length - 1)){
                dashLastPhrase += mainSeparator ;
            }
        }
        if(printFlag == 0){
            drawLine(linebreak, 
                     context, 
                     dashLastPhrase,
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
        double wordY = y0 + linebreak * 14;
        double measure = context.measureText(dashLastPhrase).getWidth(); 
        context.fillText(dashLastPhrase, wordX, wordY, measure);
    }
    
    /**
     * Calculates the width of a given phrase after adding a new word to it
     * @param lastPhrase The phrase after the line break
     * @param word The new word to be added to the phrase
     * @param context The Context2d object where the phrase is to be rendered 
     * @return Vector containing the measures of the phrase before and after the new word has been added
     */
    private Point calculateMeasures(String lastPhrase, String word, Context2d context) {
        double measure;
        double testmeasure;
        if (lastPhrase == "") {
            measure = 0;
            testmeasure = measure;
        }
        else {
            measure = context.measureText(lastPhrase).getWidth();
            testmeasure = context.measureText(lastPhrase + word).getWidth();
        }
        Point measures = new Point(measure,testmeasure);
        return measures;
    }
    
}
