/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import org.reactome.gwt.client.widgets.sortableTable.CellClickCatcher;

import com.google.gwt.user.client.ui.Anchor;

/**
 * An abstract class that allows you to put an anchor into a SortableTable cell.  This
 * forces the user to define the "click" method, allowing the anchor to react to click
 * events.  This is a workaround for the fact that GWT tables don't seem to pass click
 * events on to widgets inside cells.
 *
 * @author David Croft
 */
public abstract class SortableTableAnchor extends Anchor implements Comparable,CellClickCatcher {
	public SortableTableAnchor(String text) {
		super(text);
	}

	public abstract void click();
	
	@Override
	public int compareTo(Object arg0) {
		return this.getText().compareTo(((SortableTableAnchor)arg0).getText());
	}
}
