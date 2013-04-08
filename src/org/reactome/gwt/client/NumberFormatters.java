/* Copyright (c) 2013 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * Various preset formats for numbers
 * 
 * @author David Croft
 *
 */
public abstract class NumberFormatters {
	private static final NumberFormat numberFormat = NumberFormat.getFormat("0.0"); // GWT doesn't implement DecimalFormat, so use NumberFormat instead
	private static final NumberFormat numberFormatHundred = NumberFormat.getFormat("00.0"); // GWT doesn't implement DecimalFormat, so use NumberFormat instead
	private static final NumberFormat numberFormatPower = NumberFormat.getFormat("0.00E00"); // GWT doesn't implement DecimalFormat, so use NumberFormat instead
	
	public static String compact(double value) {
		String formattedValueString = null;
		if (value >= 0.1 && value < 10)
			formattedValueString = numberFormat.format(value);
		else if (value >= 10 && value < 100)
			formattedValueString = numberFormatHundred.format(value);
		else
			formattedValueString = numberFormatPower.format(value);
		
		return formattedValueString;
	}
	
	public static String power(double value) {
		return numberFormatPower.format(value);
	}
}
