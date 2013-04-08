/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.transport;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transport class for passing expression data from server to client
 * 
 * @author David Croft
 *
 */
public class PathwayInfo implements IsSerializable {
	public String id;
	public String uniProtId = "";
	public String referenceEntityDbId = "";
	public String[] speciesNames = new String[0];
	public String[] speciesIds = new String[0];
	public String[] pathwayNames = new String[0];
	public long[] pathwayDbIds = new long[0];
}
