/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>PathwayAssignmentService</code>.
 * 
 * @author David Croft
 */
public interface ToolServiceAsync { 
	void getProgressInfo(AsyncCallback<ProgressInfo> callback);
	void getStatus(AsyncCallback<String> callback);
}
