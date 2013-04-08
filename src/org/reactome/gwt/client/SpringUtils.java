/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


/**
 * Utility methods used in conjunction with Spring..
 *
 * @author David Croft
 */
public class SpringUtils {
	public static JSONObject unpackFromSpring(JSONObject jsonObject) {
		JSONObject unpackedJsonObject = jsonObject;
		try {
			if (jsonObject != null && jsonObject.isNull() == null && jsonObject.containsKey("springModel")) {
				JSONValue unpackedJsonValue = jsonObject.get("springModel");
				// Test if unpackedJsonValue is not a JSON null.  Note
				// the unintuitive syntax.
				if (unpackedJsonValue.isNull() == null)
					unpackedJsonObject = (JSONObject) jsonObject.get("springModel");
			}
		} catch (Exception e) {
			System.err.println("SpringUtils.unpackFromSpring: WARNING - problem extracting from JSON");
			e.printStackTrace(System.err);
		}

		return unpackedJsonObject;
	}
	
	public static JSONValue unpackFromSpringToValue(JSONValue jsonValue) {
		JSONValue unpackedJsonValue = jsonValue;
		try {
			if (jsonValue != null && jsonValue.isObject() != null && jsonValue.isObject().containsKey("springModel"))
				unpackedJsonValue = jsonValue.isObject().get("springModel");
		} catch (Exception e) {
			System.err.println("SpringUtils.unpackFromSpring: WARNING - problem extracting from JSON");
			e.printStackTrace(System.err);
		}

		return unpackedJsonValue;
	}
	
	public static JSONObject unpackFromSpringToObject(String string) {
		JSONObject unpackedJsonObject = null;
		try {
			JSONObject jsonObject = (JSONObject)JSONParser.parseStrict(string);
			unpackedJsonObject = unpackFromSpring(jsonObject);
		} catch (Exception e) {
		}
		
		return unpackedJsonObject;
	}
	
	public static JSONValue unpackFromSpringToValue(String string) {
		JSONValue unpackedJsonObject = null;
		try {
			JSONValue jsonValue = JSONParser.parseStrict(string);
			unpackedJsonObject = unpackFromSpringToValue(jsonValue);
		} catch (Exception e) {
		}
		
		return unpackedJsonObject;
	}
	
	public static String unpackFromSpringToString(String string) {
		String unpackedString = string;
		JSONValue unpackedJsonValue = unpackFromSpringToValue(string);
		if (unpackedJsonValue != null)
			unpackedString = unpackedJsonValue.toString();
		
		return unpackedString;
	}
}
