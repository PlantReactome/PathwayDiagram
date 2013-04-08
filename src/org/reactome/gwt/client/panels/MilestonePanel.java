/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import com.google.gwt.user.client.ui.HTML;

/**
 * Creates the panel for a Reactome tutorial.
 *
 * @author David Croft
 */
public class MilestonePanel extends HTML {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		this.setStyleName("textbox"); // CSS

		String heading = "<h2 class=\"section_heading\">Reactome Milestone</h2>\n";
		String newsUrlString = "http://reactome.oicr.on.ca/static_wordpress#post-548";
		String html = "Reactome has achieved its milestone of curating reactions and pathways involving at least 5000 distinct human proteins... <a href=\"" + newsUrlString  + "\">[more]</a>\n";
		String text = heading + html;
		
		setHTML(text);
	}
}
