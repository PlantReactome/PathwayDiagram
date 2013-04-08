/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.pages.Page;
import org.reactome.gwt.client.services.SortableTableDownloadService;
import org.reactome.gwt.client.services.SortableTableDownloadServiceAsync;
import org.reactome.gwt.client.widgets.sortableTable.SortableTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates a panel containing a SortablePanel plus facilities for downloading
 * the contents of the table..
 *
 * @author David Croft
 */
public class SortableTablePanel extends VerticalPanel { 
	private SortableTable sortableTable;
	private ListBox downloadFormatSelector = null;
	private static String DOWNLOAD_FORMAT_CSV = "Comma-separated values";
	private static String DOWNLOAD_FORMAT_TSV = "Tab-separated values";
	private static String DOWNLOAD_FORMAT_XCEL = "Microsoft Xcel";
	private SortableTableDownloadServiceAsync sortableTableDownloadService = GWT.create(SortableTableDownloadService.class);

	public SortableTablePanel() {
		super();
	}
	
	public SortableTablePanel(SortableTable sortableTable) {
		super();
		this.sortableTable = sortableTable;
	}
	
	public void setSortableTable(SortableTable sortableTable) {
		this.sortableTable = sortableTable;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		this.setStyleName("textbox"); // CSS
		this.setWidth(Page.BASE_PANEL_WIDTH + "px");
		
		int rowCount = sortableTable.getRowCount();
		Label infoLabel;
		if (rowCount>1) {
			Panel downloadFormatSelectorStripPanel = createDownloadSelector();
			add(downloadFormatSelectorStripPanel);
		
			if (rowCount > 5000) {
				infoLabel = new Label("Your data contains too many identifiers (" + rowCount + ") to be displayed in a webpage.  Please use the download facility to save the results to a file and view using a text editor or spreadsheet.");
				add(infoLabel);
			} else {
				add(sortableTable);
			
				Panel rowCountPanel = createRowCount(rowCount);
				add(rowCountPanel);
			}
//		} else {
//			infoLabel = new Label("Sorry, no results were returned.");
//			add(infoLabel);
		}
	}
	
	/**
	 * Creates a selector for downloading the table data.
	 */
	public Panel createDownloadSelector() {
		HorizontalPanel downloadFormatSelectorStripPanel = new HorizontalPanel();
		downloadFormatSelectorStripPanel.setStyleName("pale_blue_textbox"); // CSS
		downloadFormatSelectorStripPanel.setWidth("100%");
		downloadFormatSelectorStripPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		
		final HorizontalPanel downloadFormatSelectorPanel = new HorizontalPanel();
		downloadFormatSelectorPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		downloadFormatSelectorPanel.setStyleName("horizontal_inner_panel"); // CSS
		downloadFormatSelectorStripPanel.add(downloadFormatSelectorPanel);
		
		Label downloadFormatSelectLabel = new Label("Select format to download this table:");
		downloadFormatSelectorPanel.add(downloadFormatSelectLabel);
		
		downloadFormatSelector = new ListBox();
		downloadFormatSelector.addItem(DOWNLOAD_FORMAT_XCEL); // selected index 0
		downloadFormatSelector.addItem(DOWNLOAD_FORMAT_TSV); // selected index 1
		downloadFormatSelector.addItem(DOWNLOAD_FORMAT_CSV); // selected index 2
		downloadFormatSelectorPanel.add(downloadFormatSelector);
		
		VerticalPanel miscPanel = new VerticalPanel();
		final Button downloadButton = new Button("Download");
		downloadButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) {
				downloadButton.setEnabled(false); // don't let user click twice
				
				// Switch on the "waiting" cursor
				DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "wait"); 

				int selectedIndex = downloadFormatSelector.getSelectedIndex();
				if (selectedIndex >= 0) {
					final String outputFormat;
					if (selectedIndex == 0)
						outputFormat = SortableTableDownloadService.DOWNLOAD_FORMAT_XCEL;
					else if (selectedIndex == 1)
						outputFormat = SortableTableDownloadService.DOWNLOAD_FORMAT_TSV;
					else if (selectedIndex == 2)
						outputFormat = SortableTableDownloadService.DOWNLOAD_FORMAT_CSV;
					else
						outputFormat = SortableTableDownloadService.DOWNLOAD_FORMAT_UNKNOWN;
					sortableTableDownloadService.setStringArray(sortableTable.toStringArray(),
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Void result) {
									sortableTableDownloadService.setOutputFormat(outputFormat,
											new AsyncCallback<Void>() {
												@Override
												public void onFailure(Throwable caught) {
													downloadButton.setEnabled(true);
												}

												@Override
												public void onSuccess(Void result) {
													String hostPageBaseURL = GWT.getHostPageBaseURL();
													String url = hostPageBaseURL + "entrypoint/sortableTableDownload";
													FormUtils.formCreator(downloadFormatSelectorPanel, url, null, null, "_top", FormPanel.METHOD_GET).submit();
													downloadButton.setEnabled(true);
												}
											});
								}
							});
				}
				
				// Switch back to the regular cursor
				DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "default"); 
				downloadButton.setEnabled(true);
			}
		});
		miscPanel.add(downloadButton);
		downloadFormatSelectorPanel.add(miscPanel);

		return downloadFormatSelectorStripPanel;
	}
	
	/**
	 * Returns a panel enumerating the number of rows in the table.
	 */
	public Panel createRowCount(int rowCount) {
		HorizontalPanel rowCountPanel = new HorizontalPanel();
		rowCountPanel.setStyleName("horizontal_inner_panel"); // CSS
		rowCountPanel.setWidth("100%");
		rowCountPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		
		Label downloadFormatSelectLabel = new Label(rowCount + " rows");
		rowCountPanel.add(downloadFormatSelectLabel);

		return rowCountPanel;
	}
}
