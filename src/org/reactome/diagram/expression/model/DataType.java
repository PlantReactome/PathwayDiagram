/*
 * Created on Oct 1, 2013
 *
 */
package org.reactome.diagram.expression.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This enum lists data types: e.g. proteins, small compounds 
 * @author weiserj
 *
 */
public enum DataType { 
	Protein, 
	SmallCompound; 

	private static final Map<String, DataType> dataTypeMap;
	static {
		Map<String, DataType> dataTypeStringToEnum = new HashMap<String, DataType>();
		dataTypeStringToEnum.put("protein", Protein);
		dataTypeStringToEnum.put("small_compound", SmallCompound);
	
		dataTypeMap = Collections.unmodifiableMap(dataTypeStringToEnum);
	}
	
	
	public static boolean contains(String test) {
		for (DataType dataType : DataType.values()) {
			if (dataType.name().equals(test)) {
				return true;
			}
		}
	
		return false;
	}
	
	public static DataType getDataType(String dataType) {
		return dataTypeMap.get(dataType);
	}
}