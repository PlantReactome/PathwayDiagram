/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>DescriptionService</code>.
 * 
 * @author David Croft
 */
public interface NavigationBarServiceAsync {
	void getText(AsyncCallback<String> callback);
}
