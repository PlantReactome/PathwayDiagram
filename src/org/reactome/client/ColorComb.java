package org.reactome.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class ColorComb {
	
	String bgColor;
	String StrokeColor;
	
	public ColorComb(String bgColor, String StrokeColor) {
		// TODO Auto-generated constructor stub
		this.bgColor = bgColor;
		this.StrokeColor = StrokeColor;
	}

	public void makecolor(Context2d context) {
		// TODO Auto-generated method stub
		CssColor strokeStyleColor = CssColor.make(StrokeColor);
        context.setStrokeStyle(strokeStyleColor);
        CssColor fillStyleColor = CssColor.make(bgColor);
        context.setFillStyle(fillStyleColor);
	}
	
}
