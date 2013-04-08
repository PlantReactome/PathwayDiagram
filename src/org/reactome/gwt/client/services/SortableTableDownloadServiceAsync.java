/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ExpressionPerPathwayService</code>.
 * 
 * @author David Croft
 */
public interface SortableTableDownloadServiceAsync {
	void setStringArray(String[][] array, AsyncCallback<Void> callback);
	void setOutputFormat(String outputFormat, AsyncCallback<Void> callback);
}
