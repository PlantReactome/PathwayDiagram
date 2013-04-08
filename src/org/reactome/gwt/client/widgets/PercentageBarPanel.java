/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Graphical bar for displaying percentage values.
 * 
 * Implements Comparable, so that it can be incorporated into SortableTable.
 * 
 * The ProgressBar is wrapped in a VerticalPanel, because a naked ProgressBar
 * does not display properly in a table cell.
 *
 * @author David Croft
 */
public class PercentageBarPanel extends VerticalPanel implements Comparable { 
	ProgressBar progressBar;
	private double percent = 0.0;

	public PercentageBarPanel(double percent) {
		super();
		if (percent < 0.0)
			percent = 0.0;
		if (percent > 100.0)
			percent = 100.0;
		this.percent = percent;
		progressBar = new ProgressBar(0.0, 100.0, percent);
		progressBar.setStyleName("comparison-ProgressBar-shell");
		progressBar.setWidth("100px");
		add(progressBar);
	}

	@Override
	public int compareTo(Object arg0) {
		PercentageBarPanel otherPercentageBar = (PercentageBarPanel)arg0;
		double otherPercent = otherPercentageBar.getPercent();
		if (otherPercent < percent)
			return (-1);
		else if (otherPercent > percent)
			return 1;
		else
			return 0;
	}
	
	public double getPercent() {
		return percent;
	}

//	@Override
//	public void onFocus(Widget sender) {
//        Window.alert("PercentageBarPanel got focus!");
//	}
//
//	@Override
//	public void onLostFocus(Widget sender) {
//        Window.alert("PercentageBarPanel lost focus!");
//	}
//
//	@Override
//	public void onFocus(FocusEvent event) {
//        Window.alert("PercentageBarPanel.onFocus: got focus!");
//	}
//
//	@Override
//	public void onClick(ClickEvent event) {
//        Window.alert("PercentageBarPanel.onClick: got click!");
//        ReactomePopup popup = new ReactomePopup();
//        popup.setText("Ho ho ho!");
//        popup.setWidth("400px");
//        popup.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
//	}
	
	public String getHTML() {
		return getElement().getInnerHTML();
	}
}
