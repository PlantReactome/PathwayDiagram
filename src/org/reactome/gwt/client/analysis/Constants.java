/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.analysis;


/**
 * Stuff that is used by many classes, but never changes.
 * 
 * @author David Croft
 */
public class Constants {
	public static final String EXPRESSION_FORM_ACTION = "newExpressionAnalysis";
	public static final String PATHWAY_FORM_ACTION = "newPathwayAnalysis";
	public static final String ANALYSIS_FORM_ACTION = "analysis";
	public static final String ANALYSIS_ID_KEY = "analysisId";
	public static final String ANALYSIS_ACTION_URL_KEY = "analysisActionUrl";
	
	// These are duplicated of what is in org.reactome.analysis.Constants.
	// GWT makes it slightly tricky to share code between server and client.
	public final static String EXPRESSION_ANALYSIS_NAME = "expression_analysis";
	public static final String EXPRESSION_ANALYSIS_NAME_WITH_LEVELS = "expression_analysis_with_levels";
	public final static String PATHWAY_ANALYSIS_NAME = "pathway_analysis";
	public final static String COMPARE_SPECIES_ANALYSIS_NAME = "compare_species_analysis";
//	public static final String COMPARE_SPECIES_ANALYSIS_NAME_WITH_LEVELS = "compare_species_analysis_with_levels";
	public static final String EXPRESSION_ANALYSIS_SET_NAME = "expression_analysis_set";
	public static final String PATHWAY_ANALYSIS_SET_NAME = "pathway_analysis_set";
	public static final String COMBINED_ANALYSIS_SET_NAME = "combined_analysis_set";
	public static final String COMPARE_SPECIES_ANALYSIS_SET_NAME = "compare_species_analysis_set";
}
