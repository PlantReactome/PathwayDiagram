/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for handling the project properties retrieved from Config.pm.
 * 
 * @author David Croft
 */
public class ProjectPropertyUtils {
	/**
	 * Takes a string representing a 1-dimensional Perl array and
	 * parses it into a 1-dimensional Java String array.
	 * 
	 * @param perlArray
	 * @return
	 */
	public static String[] parsePerlArray(String perlArray) {
		String[] list = new String[0];
		
		if (perlArray == null || perlArray.isEmpty())
			return list;

		String[] openBracketSplit = perlArray.split("\\[");
		String openBracketElement = openBracketSplit[0];
		if (openBracketSplit.length > 1)
			openBracketElement = openBracketSplit[1];
		String[] closeBracketSplit = openBracketElement.split("\\]");
		String text = closeBracketSplit[0];
		if (closeBracketSplit.length > 1)
			text = closeBracketSplit[1];
		list = text.split(",");
		
		return list;
	}
	
	/**
	 * Takes a string representing a 2-dimensional Perl array and
	 * parses it into a 2-dimensional Java String array.
	 * 
	 * @param perlArray
	 * @return
	 */
	public static String[][] parsePerl2DArray(String perlArray) {
		String[][] array = new String[0][0];
		
		if (perlArray == null || perlArray.isEmpty())
			return array;

		String[] closeBracketSplit = perlArray.split("\\]");
		List<String[]> arrayList = new ArrayList<String[]>();
		int rowCount = 0;
		for (String closeBracketElement: closeBracketSplit) {
			if (closeBracketElement.isEmpty())
				continue;
			String[] openBracketSplit = closeBracketElement.split("\\[");
			String openBracketElement = openBracketSplit[0];
			if (openBracketSplit.length > 1)
				openBracketElement = openBracketSplit[1];
			String[] list = parsePerlArray(openBracketElement);
			arrayList.add(list);
			if (rowCount < list.length)
				rowCount = list.length;
		}
		
		array = new String[arrayList.size()][rowCount];
		for (int i=0; i<arrayList.size(); i++)
			array[i] = arrayList.get(i);
		
		return array;
	}
}
