/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>PathwayOfTheMonthService</code>.
 * 
 * @author David Croft
 */
public interface CopyrightServiceAsync {
	void getText(AsyncCallback<String> callback);
}
