/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interfaces extending this force the programmer to implement
 * methods that allow the progress of a server-side process to be
 * monitored by the client.
 * 
 * @author David Croft
 */
public interface ProgressMonitorAsync {
	void getProgressInfo(AsyncCallback<ProgressInfo> callback);
}
