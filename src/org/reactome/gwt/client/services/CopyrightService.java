/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author David Croft
 */
@RemoteServiceRelativePath("copyright")
public interface CopyrightService extends RemoteService {
	/**
	 * @uml.property  name="text"
	 */
	String getText();
}