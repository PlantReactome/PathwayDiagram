/*
 * Created on Mar 7, 2013
 *
 */
package org.reactome.diagram.expression;

import org.junit.Test;

/**
 * This simple helper class is used to convert expression values into colors. The implementation
 * of this class is adapted from Java source code java.awt.MultipleGradientPaintContext by considering
 * three colors only: blue (min) - green (mean) - red (max). Two arrays of colors are pre-generated, 
 * each of which has 255 colors in int.
 * 
 * As of June 28, 2013, two colors scheme (YELLOW for highest and BLUE for lowest) have been used.
 * @author gwu
 *
 */
public class ExpressionColorHelper {
    private final int GRADIENT_SIZE = 255;
    private int[] lowColors;
    private int[] upColors;
    //For two colors system
    private int[] colors;
    // These anchor colors used
    private Integer minColor;
    private Integer maxColor;
    private Integer meanColor;
    
    public ExpressionColorHelper() {
        this(0x0000FF, // blue
             null,
             0xFFFF00); // yellow
    }
    
    public ExpressionColorHelper(Integer minColor,
                                 Integer meanColor,
                                 Integer maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.meanColor = meanColor;
        initColors();
    }
    
    /**
     * Pre-generate two arrays of colors as implemented in the original 
     * java.awt.MultipleGradientPaintContext.
     */
    private void initColors() {
        if (meanColor == null) {
            colors = new int[GRADIENT_SIZE * 2];
            initColors(minColor, maxColor, colors);
        }
        else {
            lowColors = new int[GRADIENT_SIZE];
            initColors(minColor, meanColor, lowColors);
            upColors = new int[GRADIENT_SIZE];
            initColors(meanColor, maxColor, upColors);
        }
    }
    
    private void initColors(int color1, int color2, int[] colors) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = (color1) & 0xFF;
        int dr = ((color2 >> 16) & 0xFF) - r1;
        int dg = ((color2 >> 8) & 0xFF) - g1;
        int db = ((color2) & 0xFF) - b1;
        
        double stepSize = 1.0f / colors.length;
        
        for (int i = 0; i < colors.length; i++) {
            colors[i] = (((int) ((r1 + i * dr * stepSize) + 0.5) << 16)) |
                        (((int) ((g1 + i * dg * stepSize) + 0.5) << 8)) |
                        ((int) ((b1 + i * db * stepSize) + 0.5));           
        }
    }
    
    private int searchColor(double value, 
                            double minValue, 
                            double maxValue, 
                            int[] colors) {
        double ratio = (value - minValue) / (maxValue - minValue);
        // Use half-round up.
        int index = (int) (ratio * (colors.length - 1) + 0.5d); 
        return colors[index];
    }
    
    /**
     * Get a color from a two-color system.
     * @param value
     * @param min
     * @param max
     * @return
     */
    public String convertValueToColor(double value,
                                      double min,
                                      double max) {
        // A special case
        if ((max - min) < 1.0e-4) // minimum 0.0001 // This is rather arbitrary
            return Integer.toHexString(0x00FF00); // Use green
        // Check if value is in the lower or upper half
        int color = searchColor(value, min, max, colors);
        String rtn = Integer.toHexString(color);
        // Make sure it has six digits
        if (rtn.length() < 6) {
            for (int i = rtn.length(); i < 6; i++) {
                rtn = "0" + rtn;
            }
        }
        return "#" + rtn.toUpperCase(); // This should be a valid CSS color
    }
    
    /**
     * Get a color from a three-color system.
     * @param value
     * @param min
     * @param middle
     * @param max
     * @return
     */
    public String convertValueToColor(double value, 
                                      double min,
                                      double middle,
                                      double max) {
        // A special case
        if ((max - min) < 1.0e-4) // minimum 0.0001 // This is rather arbitrary
            return Integer.toHexString(0x00FF00); // Use green
        // Check if value is in the lower or upper half
        int color;
        if (value < middle) {
            // Color should be placed between blue (lowest) and green (middle)
            color = searchColor(value, min, middle, lowColors);
        }
        else {
            // Color should be placed between green (middle) and red (highest)
            color = searchColor(value, middle, max, upColors);
        }
//        System.out.println("Color: " + color);
        String rtn = Integer.toHexString(color);
        // Make sure it has six digits
        if (rtn.length() < 6) {
            for (int i = rtn.length(); i < 6; i++) {
                rtn = "0" + rtn;
            }
        }
        return "#" + rtn.toUpperCase(); // This should be a valid CSS color
    }
    
    @Test
    public void testConvertValueToColor() {
        System.out.println("Yellow: " + 0xFFFF00);
        maxColor = 0xFFFF00;
//        System.out.println("Red: " + 0xFF0000);
//        maxColor = 0xFF0000;
//        System.out.println("Red: " + Integer.toHexString(0xFF0000));
//        System.out.println("Green: " + 0x00FF00);
//        meanColor = 0x00FF00;
//        System.out.println("Green: " + Integer.toHexString(0x00FF00));
        System.out.println("Blue: " + 0x0000FF);
        minColor = 0x0000FF;
        System.out.println("Blue: " + Integer.toHexString(0x0000FF));
        System.out.println();
        initColors();
        double min = 3.50;
        double max = 10.00;
        double middle = (min + max) / 2.0d;
        System.out.println("Mean: " + middle);
        double[] values = new double[] {
                3.50d,
                5.17d,
                6.75d,
                8.38d,
                10.00d
        };
        for (double value : values) {
//            String color = convertValueToColor(value, min, middle, max);
            String color = convertValueToColor(value, min, max);
            System.out.println(value + ": " + color);
        }
    }
    
    public static void main(String[] args) {
        new ExpressionColorHelper().testConvertValueToColor();
    }
    
}
