/*
 * Created on Sep 22, 2011
 * Most of code here is copied from Maulik's original GSoC canvas prototype project.
 */
package org.reactome.diagram.view;

import org.reactome.diagram.client.Vector;
import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;

/**
 * This customized Renderer is use to render Protein. 
 * @author Maulik & Guanming
 *
 */
public class NodeRenderer extends AbstractRenderer<Node> {
    // TODO: to be changed
    private int zoomFactor = 1;
    
    /* (non-Javadoc)
     * @see org.reactome.diagram.view.GraphObjectRenderer#render(com.google.gwt.canvas.dom.client.Context2d)
     */
    @Override
    public void render(Context2d c2d,
                       Node node) {
        setColors(c2d, node.getFgColor(), node.getBgColor());
        Bounds bounds = node.getBounds();
        String color = node.getBgColor();
        if (color == null)
            color = "rgba(204, 255, 204, 1)";
        c2d.setFillStyle(CssColor.make(color));
        color = node.getFgColor();
        if (color == null)
            color = "rgba(0, 0, 0, 1)";
        c2d.setStrokeStyle(CssColor.make(color));
        drawRectangle(bounds, c2d);
        drawName(c2d, node);
    }
    
    private void drawRectangle(Bounds bounds,
                               Context2d context) {
        int coX = bounds.getX();
        int coY = bounds.getY();
        int radius = Parameters.radius;
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
        context.fill();
        context.stroke();
        context.closePath();
    }
    
    /**
     * Divides the Annotation into separate arrays of Strings depending on the width of the enclosing node
     * @param context The Context2d object where the Annotation is to be rendered
     */
    private void drawName(Context2d context,
                          Node node) {
        if (node.getDisplayName() == null || node.getDisplayName().isEmpty())
            return;
        String[] words = node.getDisplayName().split(" ");
        Vector measures;
        int linebreak = 0;
        int printFlag = 0;
        String dashLastPhrase = "";
        String mainSeparator = " ";
        String separator = ":";
        Bounds bounds = node.getBounds();
        int x0 = bounds.getX() + bounds.getWidth() / 2;
        int y0 = bounds.getY() + 4;
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
        context.setTextAlign("center");
        context.setTextBaseline("top");
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
                    double testmeasure = measures.y;
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
        double wordY = y0 + (linebreak * 14 / zoomFactor);
        double measure = context.measureText(dashLastPhrase).getWidth() / zoomFactor; 
        context.fillText(dashLastPhrase, wordX, wordY, measure);
    }
    
    /**
     * Calculates the width of a given phrase after adding a new word to it
     * @param lastPhrase The phrase after the line break
     * @param word The new word to be added to the phrase
     * @param context The Context2d object where the phrase is to be rendered 
     * @return Vector containing the measures of the phrase before and after the new word has been added
     */
    private Vector calculateMeasures(String lastPhrase, String word, Context2d context) {
        double measure;
        double testmeasure;
        if (lastPhrase == "") {
            measure = 0;
            testmeasure = measure;
        }
        else {
            measure = context.measureText(lastPhrase).getWidth()/zoomFactor;
            testmeasure = context.measureText(lastPhrase + word).getWidth()/zoomFactor;
        }
        Vector measures = new Vector(measure,testmeasure);
        return measures;
    }
    
}
