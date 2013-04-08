/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import com.google.gwt.user.client.Window;

/**
 * A collection of methods for getting parameters from the referring URL.  The
 * base method is ReferrerUrlParams; other methods get parameters that are
 * Reactome specific.
 * 
 * @author David Croft
 */
public class ReferrerUrlParams {
	private static String requestURL = null;

	public static String getRequestURL() {
		if (requestURL == null)
			requestURL = Window.Location.getHref();
		return requestURL;
	}

	/**
	 * Gets the name of the database currently being accessed by the user.
	 */
	public static String getParam(String name) {
		String requestURL = getRequestURL();
		String param = null;
		
		// First try to get the information from the request URL
		if (requestURL != null) {
			String[] parts = requestURL.split("\\?");
			if (parts.length == 2) {
				String[] pairs = parts[1].split("&");
				for (String pair: pairs) {
					String[] keyVal = pair.split("=");
					if (keyVal[0].equals(name)) {
						param = keyVal[1];
						break;
					}
				}
			}
		}
		
		return param;
	}

	/**
	 * Gets the name of the database currently being accessed by the user.
	 */
	public static String getDb() {
		return getParam("DB");
	}

	/**
	 * Gets the name of the SkyPainter database currently being accessed by the user.
	 */
	public static String getDnDb() {
		String dnDb = getParam("DB");
		if (dnDb != null)
			dnDb += "_dn";
		return dnDb;
	}

	/**
	 * Gets the name of the entity database currently being accessed by the user.
	 */
	public static String getELVDb() {
		String db = getParam("DB");
//		if (!db.endsWith("_pathway_diagram"))
//			db += "_pathway_diagram";
		return db;
	}

	/**
	 * Gets the name of the species currently being used.
	 */
	public static String getSpecies() {
		return getParam("FOCUS_SPECIES");
	}
}
