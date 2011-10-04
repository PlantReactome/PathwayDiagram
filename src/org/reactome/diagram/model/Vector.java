/**
 * 
 * @author Maulik Kamdar
 * Google Summer of Code Project 
 * Canvas Based Pathway Visualization Tool
 * 
 * Parts of Code have been derived from the tutorials and samples provided at Google Web Toolkit Website 
 * http://code.google.com/webtoolkit/doc/latest/tutorial/
 * 
 * Ideas for canvas based initiation functions derived and modified from Google Canvas API Demo
 * http://code.google.com/p/gwtcanvasdemo/source/browse/
 * 
 * Interactivity ideas taken from HTML5 canvas tutorials
 * http://www.html5canvastutorials.com/
 * 
 * 
 */

package org.reactome.diagram.model;

/**
 * Creates a vector from the specified X & Y coordinates
 */

public class Vector {
  public double x, y;
  
  public Vector() {
    this(0, 0);
  }
  
  /**
   * 
   * @param x The X coordinate
   * @param y The Y coordinate
   */
  
  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public Vector(Vector v) {
    this(v.x, v.y);
  }
  
  public void add(double x, double y) {
    this.x += x;
    this.y += y;
  }
  
  public void add(Vector v) {
    add(v.x, v.y);
  }
  
  public void sub(Vector v) {
    sub(v.x, v.y);
  }
  
  public void sub(double x, double y) {
    this.x -= x;
    this.y -= y;
  }
  
  public void mult(double x, double y) {
    this.x *= x;
    this.y *= y;
  }
  
  public void mult(Vector v) {
    mult(v.x, v.y);
  }
  
  public void mult(double c) {
    mult(c, c);
  }
  
  public double mag() {
    if (x == 0 && y == 0) {
      return 0;
    } else {
      return Math.sqrt(x * x + y * y);
    }
  }
  
  public double magSquared() {
    return x * x + y * y;
  }
  
  public void set(Vector v) {
    x = v.x;
    y = v.y;
  }
  
  public static Vector sub(Vector a, Vector b) {
    return new Vector(a.x - b.x, a.y - b.y);
  }
  
  public static Vector mult(Vector v, double c) {
    return new Vector(v.x * c, v.y * c);
  }
}
