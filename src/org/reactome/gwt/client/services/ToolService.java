/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("tool")
public interface ToolService extends RemoteService { 
	ProgressInfo getProgressInfo();
	String getStatus();
}
