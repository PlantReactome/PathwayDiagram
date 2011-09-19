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

package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;

/**Parses the given Annotation(Display Name of the Node, the Stoichiometry Number, etc.) into arrays of Strings according to the Maximum width of the enclosing Node and renders it on the Canvas.
 * 
 */
public class Annotations extends GraphNode{
	
	String DisplayName;
	static String bgColor = "0 0 0";
	int fontSize;
	
	/**Constructor to plot Annotations
	 * 
	 * @param idno The Id Number of the node
	 * @param Bounds The Bounds of the node
	 * @param Position The position of the node
	 * @param fontSize The Font Size of the Annotations
	 * @param ComplexName The Display Name of the Node (Annotation)
	 */
	public Annotations(String idno, String Bounds, String Position, int fontSize, String ComplexName) {
		super(idno, Bounds, Position, bgColor);
		this.DisplayName = ComplexName;	
		this.fontSize = fontSize;
	}
	
	/**Divides the Annotation into separate arrays of Strings depending on the width of the enclosing node
	 * 
	 * @param context The Context2d object where the Annotation is to be rendered
	 */
	public void writeText(Context2d context){
		String[] Words = DisplayName.split(" ");
        Vector measures;
		int linebreak = 0;
		int printFlag = 0;
		String dashLastPhrase = "";
		String mainSeparator = " ";
		String separator = ":";
		for (int i = 0; i < Words.length ; i++) {
			String word = Words[i];
			String[] colonWords = word.split(":");
			for (int j = 0; j < colonWords.length ; j++) {
				String colonword = colonWords[j];
				String[] dashWords = colonword.split("-");
				int flag = 0;
				for (int k = 0; k < dashWords.length ; k++) {
					String dashword = dashWords[k];
					measures = knowmeasures(dashLastPhrase,dashword,context);
					double testmeasure = measures.y;
					if ((testmeasure) <= nodeWidth) {
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
						printLine(linebreak,context,dashLastPhrase);
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
			if (i < (Words.length - 1)){
				dashLastPhrase += mainSeparator ;
			}
		}
		if(printFlag == 0){
			printLine(linebreak,context,dashLastPhrase);
		}	
	}

	/**Renders the new line formed on the canvas
	 * 
	 * @param linebreak The total number of lines plot before the given line
	 * @param context The Context2d object where the given line would be rendered
	 * @param dashLastPhrase The Line to be rendered on the canvas
	 */
	private void printLine(int linebreak, Context2d context, String dashLastPhrase) {
		double wordX = cenX;
		double wordY = coY + (linebreak*14/zoomFactor);
		double measure = context.measureText(dashLastPhrase).getWidth()/zoomFactor; 
		RenderableText text = new RenderableText(wordX, wordY, measure, strokeColor, bgColor, fontSize, dashLastPhrase);
		text.write(context);
	}

	/**Calculates the width of a given phrase after adding a new word to it
	 * 
	 * @param lastPhrase The phrase after the line break
	 * @param word The new word to be added to the phrase
	 * @param context The Context2d object where the phrase is to be rendered 
	 * @return Vector containing the measures of the phrase before and after the new word has been added
	 */
	
	private Vector knowmeasures(String lastPhrase, String word, Context2d context) {
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
