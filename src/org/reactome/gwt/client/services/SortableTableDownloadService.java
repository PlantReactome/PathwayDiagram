/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("sortableTableDownload")
public interface SortableTableDownloadService extends RemoteService {
	public static String DOWNLOAD_FORMAT_UNKNOWN = "Unknown";
	public static String DOWNLOAD_FORMAT_CSV = "Comma-separated values";
	public static String DOWNLOAD_FORMAT_TSV = "Tab-separated values";
	public static String DOWNLOAD_FORMAT_XCEL = "Microsoft Xcel";
	
	void setStringArray(String[][] array);
	void setOutputFormat(String outputFormat);
}
