/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.transport;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transport class for passing expression data from server to client
 * 
 * @author David Croft
 *
 */

public class PathwayExpressionData implements IsSerializable { 
	public String pathwayName;
	public long pathwayDbId;
	public String speciesDbId;
	public String speciesName;
	public int referenceEntityCount;
	public int matchingIdentifierCount;
	public float meanExpressionLevel;
	
	public String toString() {
		return pathwayName + "," + pathwayDbId + "," + speciesDbId + "," + speciesName + "," + referenceEntityCount + "," + matchingIdentifierCount + "," + meanExpressionLevel;
	}
}
