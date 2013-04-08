/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import java.io.Serializable;

/**
 * Simple data transfer class for the "20 second survey".
 * 
 * @author David Croft
 */
public class SurveyResult implements Serializable {
	/**
	 * @uml.property  name="vocation"
	 */
	String vocation;
	/**
	 * @uml.property  name="experience"
	 */
	String experience;
	/**
	 * @uml.property  name="comment"
	 */
	String comment;
	
	public SurveyResult() {
	}

	public SurveyResult(String vocation, String experience, String comment) {
		this.vocation = vocation;
		this.experience = experience;
		this.setComment(comment);
	}

	/**
	 * @return
	 * @uml.property  name="comment"
	 */
	public String getComment() {
		String comment = this.comment;
		// try to break stuff that looks like SQL or code
		// This stuff is in getComment so that it is only
		// run on the server side, to stop people from
		// playing tricks with fake clients.
		comment = comment.replaceAll("[sS][eE][lL][eE]", "s ele");
		comment = comment.replaceAll("[dD][rR][oO][pP]", "d rop");
		comment = comment.replaceAll("[cC][rR][eE][aA]", "c rea");
		comment = comment.replaceAll("[uU][pP][dD][aA]", "u pda");
		comment = comment.replaceAll("[dD][eE][lL][eE]", "d ele");
		comment = comment.replaceAll("[eE][xX][iI][tT]", "e xit");
		return comment;
	}

	/**
	 * @param comment
	 * @uml.property  name="comment"
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Converts to an array of strings, of the form:
	 * 
	 * [vocation, experience, comment]
	 * 
	 * @return
	 */
	public String toString() {
		String string = "";
		
		if (vocation != null && !vocation.isEmpty())
			string += "Vocation: " + vocation + "\n\n";
		if (experience != null && !experience.isEmpty())
			string += "Experience: " + experience + "\n\n";
		String comment = getComment();
		if (comment != null && !comment.isEmpty())
			string += "Comment:\n\n" + comment + "\n";
		
		return string;
	}

	/**
	 * Converts to an array of strings, of the form:
	 * 
	 * [vocation, experience, comment]
	 * 
	 * @return
	 */
	public String[] toStringArray() {
		String[] stringArray = new String[3];
		
		stringArray[0] = vocation;
		stringArray[1] = experience;
		stringArray[2] = getComment();
		
		return stringArray;
	}
}
