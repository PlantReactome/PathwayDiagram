/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import org.reactome.gwt.client.Color;
import org.reactome.gwt.client.ExpressionColorPicker;
import org.reactome.gwt.client.NumberFormatters;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Graphical bar for displaying expression values.
 * 
 * Implements Comparable, so that it can be incorporated into SortableTable.
 * 
 * @author David Croft
 */
public class ExpressionBarPanel extends VerticalPanel implements Comparable { 
	private double level = 0.0;

	public ExpressionBarPanel(double level, ExpressionColorPicker expressionColorPicker) {
		super();
		this.level = level;
		
		Color foregroundColor = expressionColorPicker.pickForegroundColor(level);
		Color backgroundColor = expressionColorPicker.pickBackgroundColor(level);
		String foregroundColorString = "yellow";
		if (foregroundColor != null)
			foregroundColorString = foregroundColor.getHexValue();
		String backgroundColorString = "black";
		if (backgroundColor != null)
			backgroundColorString = backgroundColor.getHexValue();
		
		HorizontalPanel bar = new HorizontalPanel();
		bar.setHeight("17px");
		bar.setWidth("100px");
		DOM.setStyleAttribute(bar.getElement(), "background", foregroundColorString);
		
		Label label = new Label(NumberFormatters.compact(level));
		DOM.setStyleAttribute(label.getElement(), "color", backgroundColorString);
		
		bar.add(label);
		
		add(bar);
	}
	
	@Override
	public int compareTo(Object arg0) {
		ExpressionBarPanel otherPercentageBar = (ExpressionBarPanel)arg0;
		double otherLevel = otherPercentageBar.getLevel();
		if (otherLevel < level)
			return (-1);
		else if (otherLevel > level)
			return 1;
		else
			return 0;
	}
	
	public double getLevel() {
		return level;
	}
}
