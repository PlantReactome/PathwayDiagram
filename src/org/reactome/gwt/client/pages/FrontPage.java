/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ProjectPropertyUtils;
import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.panels.DescriptionPanel;
import org.reactome.gwt.client.panels.MilestonePanel;
import org.reactome.gwt.client.panels.NewsPanel;
import org.reactome.gwt.client.panels.PathwayOfTheMonthPanel;
import org.reactome.gwt.client.panels.ShortcutPanel;
import org.reactome.gwt.client.panels.TutorialPanel;
import org.reactome.gwt.client.services.ProjectPropertiesService;
import org.reactome.gwt.client.services.ProjectPropertiesServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates a Reactome front page.
 * 
 * The state hash for this page contains the following items:
 * 
 * surveyDone	Should have values either true or false.  Set to true if the user
 *              has already filled out the survey form.
 * 
 * @author David Croft
 */
public class FrontPage extends Page {
	private ShortcutPanel shortcutPanel = new ShortcutPanel();
	private ProjectPropertiesServiceAsync projectPropertiesService = GWT.create(ProjectPropertiesService.class);
	
	public FrontPage(ReactomeGWT controller) {
		this.controller = controller;
	}
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		super.onModuleLoad();
		
		FlexTable flextable = new FlexTable();
		basePanel.add(flextable);

		DescriptionPanel descriptionPanel = new DescriptionPanel();
		TutorialPanel tutorialPanel = new TutorialPanel();
//		MilestonePanel milestonePanel = new MilestonePanel();
		PathwayOfTheMonthPanel pathwayOfTheMonthPanel = new PathwayOfTheMonthPanel();
		NewsPanel newsPanel = new NewsPanel();

		VerticalAlignmentConstant align_top = VerticalPanel.ALIGN_TOP;
		
		shortcutPanel.setController(controller);
		shortcutPanel.setSurveyDone(state != null && state.get("surveyDone") != null && ((String)state.get("surveyDone")).equals("true"));
		shortcutPanel.onModuleLoad();
		
		// row,col
		
		// Column 0
		flextable.setWidget(0, 0, shortcutPanel);
		flextable.getFlexCellFormatter().setVerticalAlignment(0, 0, align_top);
		flextable.getFlexCellFormatter().setRowSpan(0, 0, 3); // span remaining depth

		// Column 1
		descriptionPanel.onModuleLoad();
		flextable.setWidget(0, 1, descriptionPanel);
		flextable.getFlexCellFormatter().setVerticalAlignment(0, 1, align_top);
		
		tutorialPanel.onModuleLoad();
		flextable.setWidget(1, 0, tutorialPanel); // subtract 1 from col for all rows > 0
		flextable.getFlexCellFormatter().setVerticalAlignment(1, 0, align_top);
		
		newsPanel.onModuleLoad();
		flextable.setWidget(2, 0, newsPanel); // subtract 1 from col for all rows > 0
		flextable.getFlexCellFormatter().setVerticalAlignment(2, 0, align_top);
		flextable.getFlexCellFormatter().setColSpan(2, 0, 2); // span remaining width
		
		// Column 2
		pathwayOfTheMonthPanel.onModuleLoad();
		flextable.setWidget(0, 2, pathwayOfTheMonthPanel); // subtract 1 from col for all rows > 0
		flextable.getFlexCellFormatter().setRowSpan(0, 2, 2);
		flextable.getFlexCellFormatter().setVerticalAlignment(0, 2, align_top);
		
		// Put in information about funders and funding at the bottom of the page
		// Get the information from the server.  This is an asynchronous call,
		// hence the weird code structure.
		projectPropertiesService.getProperty("GKB.Config.PROJECT_FUNDING", 
				new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
//				logPanel.add(new Label("Problem getting funding info, caught=" + caught.getMessage()));
//				footerPanel.add(new HTML("<i style=\"font-size:70%\">The development of Reactome is supported by " + DEFAULT_PROJECT_FUNDING +".</i>"));
			}

			public void onSuccess(String projectFunding) {
				if (projectFunding== null || projectFunding.isEmpty())
					return;
//					projectFunding = DEFAULT_PROJECT_FUNDING;
				footerPanel1.add(new HTML("<i style=\"font-size:70%\">The development of Reactome is supported by " + projectFunding +".</i>"));
				
				// Add a bunch of logos, if they can be pulled from the server.
				projectPropertiesService.getProperty("GKB.Config.PROJECT_LOGOS", 
						new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
//						logPanel.add(new Label("Problem getting logos, caught=" + caught.getMessage()));
					}

					public void onSuccess(String projectLogosPerlArray) {
						if (projectLogosPerlArray== null || projectLogosPerlArray.isEmpty())
							return;
						
						String html = "";
						String[][] projectLogos = ProjectPropertyUtils.parsePerl2DArray(projectLogosPerlArray);
						for (String[] projectLogo: projectLogos) {
							if (projectLogo == null || projectLogo.length < 1 || projectLogo[0] == null || projectLogo[0].isEmpty())
								continue;
							
							// TODO: it would be sensible to check the validity of the image URL here.
							
							// Incorporate the image into the logo
//							String logo = "<img src=\"" + projectLogo[0] + "\" height=\"50px\" alt=\" \" />";
							String logo = "<img src=\"" + projectLogo[0] + "\" alt=\" \" />";
							
							// If a URL has been supplied, wrap the image in an anchor
							// pointing to that URL.
							if (projectLogo[1] != null && !(projectLogo[1].isEmpty())) {
								logo = "<a href=\"" + projectLogo[1] + "\">" + logo + "</a>";
							}
							
							if (!html.isEmpty())
								html += "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
							
							// Add the logo to the other logos.
							html += logo;
						}
						
						footerPanel1.add(new HTML(html));
					}
				});
			}
		});
	}
	
	public HashMap getState() {
		if (shortcutPanel.isSurveyDone())
			state.put("surveyDone", "true");
		else
			state.put("surveyDone", "false");
		
		return state;
	}
}
