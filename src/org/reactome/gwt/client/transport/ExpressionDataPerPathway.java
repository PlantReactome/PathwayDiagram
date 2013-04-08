/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.transport;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Transport class for passing expression data from server to client
 * 
 * @author David Croft
 *
 */

public class ExpressionDataPerPathway implements IsSerializable { 
	public String dataTableName;
	public String firstNumericalIdentifier;
	public String defaultNumericalIdentifierType;
	public String[][] numericalIdentifierTypes;
	
	/**
	 *  The rows in the pathway table.
	 */
	public ArrayList<PathwayExpressionData> expressionDataPerPathway = new ArrayList<PathwayExpressionData>();

	public void add(long pathwayDbId, String pathwayName, String speciesName, String speciesDbId, int referenceEntityCount, int matchingIdentifierCount) {
		try {
			PathwayExpressionData pathwayExpressionData = new PathwayExpressionData();
			
			pathwayExpressionData.pathwayDbId = pathwayDbId;
			pathwayExpressionData.pathwayName = pathwayName;
			pathwayExpressionData.speciesName = speciesName;
			pathwayExpressionData.speciesDbId = speciesDbId;
			pathwayExpressionData.referenceEntityCount = referenceEntityCount;
			pathwayExpressionData.matchingIdentifierCount = matchingIdentifierCount;
			pathwayExpressionData.meanExpressionLevel = 0;
			
			expressionDataPerPathway.add(pathwayExpressionData);
		} catch (Exception e) {
			System.err.println("ExpressionDataPerPathway.add: WARNING - problem adding data for pathway DB_ID=" + pathwayDbId);
			e.printStackTrace(System.err);
		}
	}
	
	public void add(ExpressionDataPerPathway expressionDataPerPathway) {
		for (PathwayExpressionData pathwayExpressionData: expressionDataPerPathway.expressionDataPerPathway)
			this.expressionDataPerPathway.add(pathwayExpressionData);
	}
	
	public String toString() {
		String string = "";
		string += "expressionDataTableName:" + dataTableName + "\n";
		for (PathwayExpressionData pathwayExpressionData: expressionDataPerPathway)
			string += pathwayExpressionData.toString() + "\n";
		
		return string;
	}
}
