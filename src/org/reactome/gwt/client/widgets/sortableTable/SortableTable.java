/*
 * SortableTable Widget for GWT library of Google, Inc.
 * 
 * Copyright (c) 2006 Parvinder Thapar
 * http://psthapar.googlepages.com/
 * 
 * This library is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser 
 * General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or 
 * (at your option) any later version. This library is 
 * distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY  or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNULesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General 
 * PublicLicense along with this library; if not, write to the 
 * Free Software Foundation, Inc.,  
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA  
 */

package org.reactome.gwt.client.widgets.sortableTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

/*
 * SortableTable is a type of FlexTable which allows Sorting
 * on its column. Sorting is done totally on the client side.
 * No server side call is made in this table.
 * 
 * Current implementation of Sortable Table needs <code>Comparable</code>
 * Object in its column values to be able to sort them correctly
 * 
 * The objects being set in the column values must implement the interface
 * <code>Comparable</code> and implement methods: compareTo() and 
 * toString()
 *  
 * {@link com.google.gwt.user.client.ui.FlexTable}
 * {@link java.lang.Comparable}
 */ 
public class SortableTable extends Grid implements Sortable, TableListener {
	// Holds the current column being sorted
	private int sortColIndex		=	-1;
	
	// Holds the current direction of sort: Asc/ Desc
	private int sortDirection		=	-1;
	
	// All URLs are relative to ReactomeGWT.
	
	// The default image to show acending order arrow
	private String sortAscImage 	= "images/blue_up_arrow.gif";
	
	//The default image to show descending order arrow
	private String sortDescImage	= "images/blue_down_arrow.gif";
	
	// The default image to show the blank image
	// This is needed to paint the columns other than
	// the one which is being sorted.
	// Should be same length and width as the asc/ desc
	// images.
	private String blankImage		= "images/blue_arrows.gif";

	// Holds the data rows of the table
	// This is a list of RowData Object
	private List tableRows 			= new ArrayList();
	
	// Holds the data for the column headers
	private List tableHeader 		= new ArrayList();
	
	// Modes for columns - either sortable or not sortable.
	private List columnModes 		= new ArrayList();
	
	/*
	 * Default Constructor
	 * 
	 * Calls the super class constructor
	 * and adds a TableListener object
	 */
	public SortableTable(int rowCount, int ColCount){
		super();
		this.addTableListener(this);
		
		// Default sizing etc.
		setWidth("100%");
		setCellPadding(4);
		setCellSpacing(1);
		
		this.setStyleName("sortable_table"); // CSS
		
		// Set the size of the table, otherwise everything will break.
		this.resize(rowCount, ColCount);
	}
	
	/**
	 * This is a little piece of Voodoo.  With GWT 2.0, the lines around the
	 * cells do not get drawn by default in Firefox 3.0 when there is a progress
	 * bar on the page.  You have to click one of the cells at the top of the
	 * table, and I don't think users will like that.  So, call this method
	 * once the table cells have been filled, and the lines will get drawn.
	 * 
	 * I don't know why the problem arises, and I don't know why this works.
	 */
	public void touch() {
		this.drawTable();
	}

	/*
	 * addColumnHeader
	 * 
	 * Adds the Column Header to the table
	 * Uses the rowIndex 0 to add the header names.
	 * Renders the name and the asc/desc/blank gif 
	 * to the column
	 * 
	 * @param columnName (String)
	 * @param columnIndex (int)
	 */
	public void addColumnHeader(String name, int index){
		tableHeader.add(index, name);
		this.renderTableHeader(name, index);
	}
	
	/**
	 * You can optionally set modes for the columns in the
	 * table.  By default, all columns will be sortable,
	 * but if you set the mode for a given column to
	 * "Unsortable", then this column will not be available
	 * for sorting.
	 * 
	 * @param mode
	 * @param index
	 */
	public void addColumnMode(String mode, int index){
		// If the user has not yet added any mode data, fill out with nulls
		if (columnModes.size()<=index)
			for (int i=columnModes.size(); i<index; i++)
				columnModes.add(i, null);
		columnModes.add(index, mode);
	}
	
	/*
	 * setValue
	 * 
	 * Sets the values in specifed row/column
	 * Expects a Comparable Object for sorting
	 *  
	 * @param rowIndex (int)
	 * @param columnIndex (int)
	 * @param Value (Comparable)
	 */
	public void setValue(int rowIndex, int colIndex, Comparable value){
		// The rowIndex should begin with 1 as rowIndex 0 is for the Header
		// Any row with index == 0 will not be displayed.
		if(rowIndex == 0){
			return;
		}
		
		if((rowIndex-1) >= this.tableRows.size() || null == tableRows.get(rowIndex-1)){
			tableRows.add(rowIndex-1, new RowData());
		}
		
		RowData rowData = (RowData)this.tableRows.get(rowIndex-1); 
		rowData.addColumnValue(colIndex, value);
		String stringValue = null;
		if (value != null)
			stringValue = value.toString();
		this.setHTML(rowIndex, colIndex, stringValue);
	}
	
//	/*
//	 * setValue
//	 * 
//	 * Put a widget in specifed row/column
//	 * Expects a Comparable Object for sorting
//	 *  
//	 * @param rowIndex (int)
//	 * @param columnIndex (int)
//	 * @param Widget (Widget, Comparable)
//	 */
//	public void setWidget(int rowIndex, int colIndex, Widget widget){
//		// The rowIndex should begin with 1 as rowIndex 0 is for the Header
//		// Any row with index == 0 will not be displayed.
//		if(rowIndex == 0){
//			return;
//		}
//		
//		if((rowIndex-1) >= this.tableRows.size() || null == tableRows.get(rowIndex-1)){
//			tableRows.add(rowIndex-1, new RowData());
//		}
//		
//		RowData rowData = (RowData)this.tableRows.get(rowIndex-1); 
//		rowData.addColumnValue(colIndex, (Comparable)widget);
//		super.setWidget(rowIndex, colIndex, widget);
//	}
//	
	@Override
	public void setHTML(int row, int column, String html) {
		try {
			super.setHTML(row, column, html);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String style = "sortable_table_cell";
		if (row == 0)
			style += " sortable_table_header";
		if (column == 0)
			style += " sortable_table_first_col";
		
		getCellFormatter().setStyleName(row, column, style);
	}

	/*
	 * sort
	 * 
	 * Implementation of Sortable Interface, this
	 * method describes how to sort the specified
	 * column. It checks the current sort direction
	 * and flips it
	 *  
	 * @param columnIndex (int)
	 */
	public void sort(int columnIndex){
		// The only valid mode is "Unsortable", so don't
		// bother with complicated checks.
		if (getColumnMode(columnIndex) != null)
			return;
		
		Collections.sort(this.tableRows);
		if(this.sortColIndex != columnIndex){
			// New Column Header clicked
			// Reset the sortDirection to ASC
			this.sortDirection = SORT_ASC;
		}else{
			// Same Column Header clicked
			// Reverse the sortDirection
			this.sortDirection = (this.sortDirection == SORT_ASC)? SORT_DESC:SORT_ASC; 
		}
		this.sortColIndex = columnIndex;
	}
	
	/*
	 * onCellClicked
	 * 
	 * Implementation of Table Listener Interface, this
	 * method decribes what to do when a cell is clicked
	 * It checks for the header row and calls the sort 
	 * method to sort the table
	 *  
	 * @param sender (SourcesTableEvents)
	 * @param rowIndex (int)
	 * @param colIndex (int)
	 */
	public void onCellClicked(SourcesTableEvents sender, int row, int col) {
		if(row != 0){
			RowData rowData = (RowData)this.tableRows.get(row-1);
			Object cell = rowData.getColumnValue(col);
			try {
				// Pass click onto cell, if it contains a CellClickCatcher.
				((CellClickCatcher)cell).click();
			} catch (Exception e) {
				// This is a rather dirty way to get around the absence
				// of reflection in Javascript - simply catch an exception if
				// the cell doesn't implement a click() method.
			}

			return;
		}
		// For some reason, the drawTable method executes very fast, but the page on
		// the browser takes ages to load.  So the wait cursor never displays for more than
		// a fraction of a second.  I don't know how to fix this.
		waitCursor(true);
		int rowCount = this.getRowCount() - 1;
		this.setSortColIndex(col);
		this.sort(col);
		this.drawTable();
		waitCursor(false);
	}	
	
	private void waitCursor(boolean isWaiting) {
		String cursorType = "default";
		if (isWaiting)
			cursorType = "wait";
		DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", cursorType); 
//		this.addStyleName("waitCursor");
//		this.getElement().getStyle().setCursor(Cursor.WAIT);
//      this.removeStyleName("waitCursor");
//		this.getElement().getStyle().setCursor(Cursor.HELP);
	}


	/*
	 * getSortAscImage
	 * 
	 * Getter for Sort Ascending Image
	 * 
	 * @return String
	 */
	public String getSortAscImage() {
		return sortAscImage;
	}

	/*
	 * setSortAscImage
	 * 
	 * Setter for Sort Ascending Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/asc.gif
	 */
	public void setSortAscImage(String sortAscImage) {
		this.sortAscImage = sortAscImage;
	}

	/*
	 * getSortDescImage
	 * 
	 * Getter for Sort Descending Image
	 * 
	 * @return String
	 */
	public String getSortDescImage() {
		return sortDescImage;
	}

	/*
	 * setSortDescImgage
	 * 
	 * Setter for Sort Descending Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/desc.gif
	 */
	public void setSortDescImgage(String sortDescImgage) {
		this.sortDescImage = sortDescImgage;
	}
	
	/*
	 * getBlankImage
	 * 
	 * Getter for blank Image
	 * 
	 * @return String
	 */
	public String getBlankImage() {
		return blankImage;
	}

	/*
	 * setBlankImage
	 * 
	 * Setter for the blank Image
	 * 
	 * @param relative path + image name (String)
	 * e.g. images/blank.gif
	 */
	public void setBlankImage(String blankImage) {
		this.blankImage = blankImage;
	}
	
	/*
	 * drawTable
	 * 
	 * Renders the header as well as the body 
	 * of the table
	 */
	protected void drawTable(){
		this.displayTableHeader();
		this.displayTableBody();
	}
	
	/*
	 * displayTableHeader
	 * 
	 * Renders only the table header
	 */
	private void displayTableHeader(){
		int colIndex=0;
		for(Iterator colHeaderIter = this.tableHeader.iterator(); colHeaderIter.hasNext();){
			String colHeader = (String)colHeaderIter.next();
			this.renderTableHeader(colHeader, colIndex++);
		}
	}
	
	/*
	 * displayTableBody
	 * 
	 * Renders the body or the remaining rows of the table
	 * except the header.
	 * It checks the sort direction and displays the rows 
	 * accordingly
	 */
	private void displayTableBody(){
		if (tableRows == null)
			return;
		
		// First remove existing rows.  We do this because row heights
		// can't be readjusted when sorting is done, which can result
		// in text overflowing across row boundaries after a sort operation.
		int rowCount = this.getRowCount();
		for(int rowIndex=rowCount-1; rowIndex>0; rowIndex--)
			this.removeRow(rowIndex);
		resize(rowCount, tableHeader.size());
		
		int tableRowsSize = tableRows.size();
		if(this.sortDirection == SORT_ASC || this.sortDirection == -1){
			// Ascending order and Default Display
			for(int rowIndex=0; rowIndex<tableRowsSize; rowIndex++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(int colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
					if(null != value){
						this.setHTML(rowIndex+1, colIndex, value.toString());
					}
				}
			}
		}else{
			// Descending Order Display
			for(int rowIndex=tableRowsSize-1, rowNum = 1; rowIndex>=0; rowIndex--, rowNum++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(int colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
					if(null != value){
						this.setHTML(rowNum, colIndex, value.toString());
					}
				}
			}
		}
	}
	
	/**
	 * Returns a 2D array representing the current state of the
	 * table.  Any sorting that the user has performed will be reflected
	 * in the ordering of the columns in this array.  The first row will
	 * contain the table header.
	 * 
	 * @return
	 */
	public String[][] toStringArray() {
		int tableRowsSize = tableRows.size();
		int columnCount = tableHeader.size();
		String[][] array = new String[tableRowsSize + 1][columnCount];
		int colIndex=0;
		for(Iterator colHeaderIter = this.tableHeader.iterator(); colHeaderIter.hasNext();){
			array[0][colIndex++] = colHeaderIter.next().toString();
		}
		
		if (this.sortDirection == SORT_ASC || this.sortDirection == -1){
			// Ascending order and Default Display
			for (int rowIndex=0; rowIndex<tableRowsSize; rowIndex++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
					if (value == null)
						array[rowIndex + 1][colIndex] = null;
					else
						array[rowIndex + 1][colIndex] = value.toString();
				}
			}
		} else {
			// Descending Order Display
			for (int rowIndex=tableRowsSize-1, rowNum = 1; rowIndex>=0; rowIndex--, rowNum++){
				RowData columns = (RowData)tableRows.get(rowIndex);
				for(colIndex=0; colIndex<columns.getColumnValues().size(); colIndex++){
					Object value = columns.getColumnValue(colIndex);
					if (value == null)
						array[rowNum][colIndex] = null;
					else
						array[rowNum][colIndex] = value.toString();
				}
			}
		}
		
		return array;
	}
	
	/*
	 * setSortColIndex
	 * 
	 * Sets the current column index being sorted
	 * 
	 * @param column index being sorted (int)
	 */
	private void setSortColIndex(int sortIndex){
		for(int rowIndex=0; rowIndex<tableRows.size(); rowIndex++){
			RowData row = (RowData)tableRows.get(rowIndex);
			row.setSortColIndex(sortIndex);
		}
	}
	
	/*
	 * renderTableHeader
	 * Renders a particular column in the Table Header
	 * 
	 * @param Column Name (String)
	 * @param Column Index (int) 
	 */
	private void renderTableHeader(String name, int index){
		StringBuffer headerText = new StringBuffer();
		headerText.append(name);
		if (getColumnMode(index) == null) {
			headerText.append("&nbsp;<img border='0' src=");
			if(this.sortColIndex == index){
				if(this.sortDirection == SORT_ASC){
					headerText.append("'" + this.sortAscImage + "' alt='Ascending' ");	
				}else{
					headerText.append("'" + this.sortDescImage + "' alt='Descending' ");
				}
			}else{
				headerText.append("'" + this.blankImage + "'");
			}
			headerText.append("/>");
		}

		this.setHTML(0, index, headerText.toString());
	}
	
	private String getColumnMode(int index) {
		if (columnModes == null)
			return null;
		if (columnModes.size()<=index)
			return null;
		return (String)columnModes.get(index);
	}
}
