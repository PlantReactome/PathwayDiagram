/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.transport;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Transport class for passing pathway assignment data from server to client
 * 
 * @author David Croft
 *
 */

public class PathwayInfoPerIdentifier implements IsSerializable {
	public String pathwayAssignmentDataTableName;
	public String uniProtAccessUrl = null;
	public String firstNumericalIdentifier;
	public String defaultNumericalIdentifierType;
	public String[][] numericalIdentifierTypes;
	
	/**
	 *  The rows in the identifier table.
	 */
	public ArrayList<PathwayInfo> pathwayInfoPerIdentifier = new ArrayList<PathwayInfo>();

	public void add(String id, String referenceEntityDbId, String uniProtId, String[] speciesNames, String[] speciesIds, long[] pathwayDbIds, String[] pathwayNames) {
		if (id == null) {
			System.err.println("PathwayInfoPerIdentifier.add: WARNING - id == null, aborting");
			return;
		}
		
		try {
			PathwayInfo pathwayInfo = new PathwayInfo();
			
			pathwayInfo.id = id;
			if (referenceEntityDbId != null)
				pathwayInfo.referenceEntityDbId = referenceEntityDbId;
			if (uniProtId != null)
				pathwayInfo.uniProtId = uniProtId;
			if (speciesNames != null)
				pathwayInfo.speciesNames = speciesNames;
			if (speciesIds != null)
				pathwayInfo.speciesIds = speciesIds;
			if (pathwayDbIds != null)
				pathwayInfo.pathwayDbIds = pathwayDbIds;
			if (pathwayNames != null)
				pathwayInfo.pathwayNames = pathwayNames;
			
			pathwayInfoPerIdentifier.add(pathwayInfo);
		} catch (Exception e) {
			System.err.println("PathwayInfoPerIdentifier.add: WARNING - problem adding data for id=" + id);
			e.printStackTrace(System.err);
		}
	}
}
