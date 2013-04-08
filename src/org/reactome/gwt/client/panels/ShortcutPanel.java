/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import java.util.HashMap;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.SurveyResult;
import org.reactome.gwt.client.pages.PathwayAnalysisDataUploadPage;
import org.reactome.gwt.client.services.ProjectPropertiesService;
import org.reactome.gwt.client.services.ProjectPropertiesServiceAsync;
import org.reactome.gwt.client.services.SurveyService;
import org.reactome.gwt.client.services.SurveyServiceAsync;
import org.reactome.gwt.client.widgets.ReactomePopup;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;
import org.reactome.gwt.client.widgets.buttons.InternalNewPageButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates a panel containing search plus shortcuts to various tools.
 *
 * @author David Croft
 */
public class ShortcutPanel extends VerticalPanel {
	private final SurveyServiceAsync surveyService = GWT.create(SurveyService.class);
	private final ProjectPropertiesServiceAsync projectPropertiesService = GWT.create(ProjectPropertiesService.class);
	private boolean surveyDone = false;
	private ReactomeGWT controller;
	private static String WIDTH = "200px";
	public static String BUTTON_WIDTH = "180px";
	private final HorizontalPanel hostpanel = new HorizontalPanel();
	private InternalNewPageButton pathwayAnalysisButton = null;
	private InternalNewPageButton sbmlRetrievalButton = null;
	private InternalNewPageButton speciesComparisonButton = null;
	private InternalNewPageButton expressionButton = null;

	public boolean isSurveyDone() {
		return surveyDone;
	}

	public void setSurveyDone(boolean surveyDone) {
		this.surveyDone = surveyDone;
	}

	public void setController(ReactomeGWT controller) {
		this.controller = controller;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		hostpanel.setVisible(false);
		
		VerticalPanel innerPanel = new VerticalPanel();
		VerticalPanel upperPanel = new VerticalPanel();
		VerticalPanel middlePanel = new VerticalPanel();
		final VerticalPanel lowerPanel = new VerticalPanel();
		
		// Getting the width of this panel right took a lot of patience
		// and trial-and-error, so mess with this at your peril.  I found
		// that it was best to set the absolute width at the level of
		// the upper, middle and lower panels.
		upperPanel.setWidth(WIDTH);
		middlePanel.setHeight("10px");
		lowerPanel.setWidth(WIDTH);
		
		upperPanel.setStyleName("pale_blue_textbox"); // CSS
		lowerPanel.setStyleName("pale_blue_textbox"); // CSS

		// Add contents to the panel.
		upperPanel.add(hostpanel);
		upperPanel.add(buildSearchFormPanel());
		upperPanel.add(buildSearchHelpPanel());
		upperPanel.add(buildButtonPanel());
		upperPanel.add(classicWebsitePanel());
		lowerPanel.add(buildDownloadsPanel());
		lowerPanel.add(buildTryThisPanel());
		lowerPanel.add(buildSocialNetworkIconPanel());
		innerPanel.add(upperPanel);
		innerPanel.add(middlePanel);
		innerPanel.add(lowerPanel);
		
		this.add(innerPanel);
	}
	
	private Panel buildSearchHelpPanel(){
		String link = "/cgi-bin/search2?OPERATOR=ALL&SPECIES=48887&QUERY=";
		
		HTML html = new HTML(
			"<ul>" +
				"<li><a href='" + link + "O95631'>" + "O95631</a></li>" +
				"<li><a href='" + link + "NTN1'>NTN1</a></li>" +
				"<li><a href='" + link + "netrin-1%20signaling'>netrin-1 signaling</a></li>" + 
				"<li><a href='" + link + "glucose'>glucose</a></li>" + 
			"</ul>"
		);
		html.setStylePrimaryName("search_help"); // CSS
		
		final Button more = new Button("Advanced search");
		DOM.setStyleAttribute(more.getElement(), "fontSize", "smaller");
		DOM.setStyleAttribute(more.getElement(), "backgroundColor", "#d9ebff");
		DOM.setStyleAttribute(more.getElement(), "color", "black");
		DOM.setStyleAttribute(more.getElement(), "cursor", "hand");
		
		more.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				Window.open("/cgi-bin/extendedsearch", null, null);
			}
		});
		
		VerticalPanel content = new VerticalPanel();
		content.setStylePrimaryName("search_help"); // CSS
		content.setWidth("100%");
		content.add(html);
		content.add(more);
		content.setCellHorizontalAlignment(more, HorizontalPanel.ALIGN_RIGHT);
				
		final DisclosurePanel dpanel = new DisclosurePanel("Search examples...");
		dpanel.setWidth("100%");
		dpanel.add(content);
		DOM.setStyleAttribute(dpanel.getElement(), "marginLeft", "6px");

		Panel panel = new VerticalPanel();
		panel.setWidth("180px");
		panel.add(dpanel);
		return panel;
	}
	
	private Panel buildSearchFormPanel() {	
		final HorizontalPanel panel = new HorizontalPanel();
		panel.setStylePrimaryName("textbox"); // CSS
		
		final TextBox queryBox = new TextBox();
		queryBox.setWidth("100%");
		queryBox.setHeight("27px");
		queryBox.setTitle("write your query here");
		DOM.setStyleAttribute(queryBox.getElement(), "verticalAlign", "middle");
		DOM.setStyleAttribute(queryBox.getElement(), "paddingLeft", "5px");
		DOM.setStyleAttribute(queryBox.getElement(), "paddingRight", "5px");
		
		final PushButton searchButton = new PushButton(new Image("images/search.gif"));
		searchButton.setTitle("Search");
		DOM.setStyleAttribute(searchButton.getElement(), "cursor", "hand");

	    FlexTable flextable = new FlexTable();
	    flextable.setWidget(0, 0, queryBox);
	    flextable.getCellFormatter().getElement(0, 0).setAttribute("width", "156px");
	    flextable.setWidget(0, 1, searchButton);
	    flextable.getCellFormatter().getElement(0, 1).setAttribute("width", "24px");
	    panel.add(flextable);
	    	    	    
		// These are the parameters that will be sent as "hidden" by the
		// POST request.
		final HashMap<String, String> params = new HashMap<String, String>();
		String db = getReactomeDb();
		if (db != null)
			params.put("DB", db);
		// Instruct the search mechanism to get all instance
		// types, rather than restricting them to, say pathways.
		params.put("OPERATOR", "ALL");
		// Sending an empty SPECIES value means "All species".
		// Sending no SPECIES parameter at all theoretically means "default
		// species", which is Homo sapiens for the main Reactome
		// website.
//		params.put("SPECIES", "");
		params.put("SPECIES", "48887"); // TODO: we need to get species from calling URL
				
		// Listen for keyboard events in the input box.
		queryBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					String value = queryBox.getValue();
					if (value.length() > 0 && !(value.matches("^ +$")) && !(value.matches("^[ \t\n]*$"))) {
						// Add the query to the form immediately before
						// submitting it, since we can't know when we
						// construct the form what query a user is going
						// to enter.
						params.put("QUERY", value);
						FormUtils.formCreator(hostpanel, "/cgi-bin/search2", params, null, null, FormPanel.METHOD_POST, true).submit();
					}
				}
			}
		});
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String value = queryBox.getValue();
				if (value.length() > 0 && !(value.matches("^ +$")) && !(value.matches("^[ \t\n]*$"))) {
					// Add the query to the form immediately before
					// submitting it, since we can't know when we
					// construct the form what query a user is going
					// to enter.
					params.put("QUERY", value);
					FormUtils.formCreator(hostpanel, "/cgi-bin/search2", params, null, null, FormPanel.METHOD_POST, true).submit();
				}
			}
		});

		return panel;
	}
	
	private VerticalPanel buildButtonPanel() {
		VerticalPanel panel = new VerticalPanel();
		
		// Class tag for stylesheet
		panel.setStyleName("textbox"); // CSS
		
		ExternalNewPageButton browsePathwaysButton = new ExternalNewPageButton("", "Browse Pathways") {
			// This button points at a URL that needs to have the currently active
			// Reactome database.  The init method is used to build the URL.
			protected void init() {
				setEnabled(false);
				setTitle("View interactive pathway diagrams");
				projectPropertiesService.getProperty("GKB.Config.GK_ENTITY_DB_NAME", 
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
							}


							public void onSuccess(String result) {
								String db = "";
								if (result != null && !result.equals(""))
									db = "#DB=" + result;
								setUrl("/entitylevelview/PathwayBrowser.html" + db);
								setEnabled(true);
							}
						});
			}
		};
		pathwayAnalysisButton = new InternalNewPageButton(controller, "PathwayAnalysisDataUploadPage", "Map IDs to Pathways", "See your data represented as Reactome pathways");
		sbmlRetrievalButton = new InternalNewPageButton(controller, "SBMLRetrievalPage", "Retrieve SBML", null);
		speciesComparisonButton = new InternalNewPageButton(controller, "SpeciesComparisonPage", "Compare Species", "See if a human pathway is represented in a model organism");
		expressionButton = new InternalNewPageButton(controller, "ExpressionDataUploadPage", "Analyze Expression Data", "Overlay your expression data onto Reactome pathways");
		ExternalNewPageButton toolsPageButton = new ExternalNewPageButton("/ReactomeGWT/site.html", "Choose analysis", "_blank");

		browsePathwaysButton.setWidth(BUTTON_WIDTH);
		pathwayAnalysisButton.setWidth(BUTTON_WIDTH);
		sbmlRetrievalButton.setWidth(BUTTON_WIDTH);
		speciesComparisonButton.setWidth(BUTTON_WIDTH);
		expressionButton.setWidth(BUTTON_WIDTH);
		toolsPageButton.setWidth(BUTTON_WIDTH);
		
		browsePathwaysButton.setStyleName("horizontal_inner_panel"); // CSS
		pathwayAnalysisButton.setStyleName("horizontal_inner_panel"); // CSS
		sbmlRetrievalButton.setStyleName("horizontal_inner_panel"); // CSS
		speciesComparisonButton.setStyleName("horizontal_inner_panel"); // CSS
		expressionButton.setStyleName("horizontal_inner_panel"); // CSS
		toolsPageButton.setStyleName("horizontal_inner_panel"); // CSS
		
		panel.add(browsePathwaysButton);
		panel.add(pathwayAnalysisButton);
		panel.add(sbmlRetrievalButton);
		panel.add(speciesComparisonButton);
		panel.add(expressionButton);
		panel.add(toolsPageButton);
		
		return panel;
	}
	
	private VerticalPanel classicWebsitePanel() {
		VerticalPanel panel = new VerticalPanel();
		
		// Class tag for stylesheet
		panel.setStyleName("textbox"); // CSS
		
		String linksHtml = "<DIV STYLE=\"text-align:center;padding-bottom:10px;color:red;\">If you would prefer to use our old website, click <a href=\"/cgi-bin/frontpage?CLASSIC=1\">here</a>.</DIV>\n";
		panel.add(new HTML(linksHtml));
		
		return panel;
	}
	
	/**
	 * Gets the name of the database currently being accessed by the user.
	 */
	public static String getReactomeDb() {
		String requestURL = Window.Location.getHref();
		String reactomeDb = null;
		
		// First try to get the information from the request URL
		if (requestURL != null) {
			String[] parts = requestURL.split("\\?");
			if (parts.length == 2) {
				String[] pairs = parts[1].split("&");
				for (String pair: pairs) {
					String[] keyVal = pair.split("=");
					if (keyVal[0].equals("DB")) {
						reactomeDb = keyVal[1];
						break;
					}
				}
			}
		}
		
		return reactomeDb;
	}

	private VerticalPanel buildDownloadsPanel() {
		final VerticalPanel panel = new VerticalPanel();
		
		panel.setStyleName("textbox"); // CSS
		
		HTML textLabel = new HTML("<b style=\"font-size:150%\">Download</b><br>The following links allow you to download Reactome data in various formats:");
		panel.add(textLabel);
		
		String linksHtml = "<ul>\n";
		linksHtml += "<li><a href=\"/download/current/biopax.zip\">BioPax</a></li>";
		linksHtml += "<li><a href=\"/download/current/homo_sapiens.sbml.gz\">SBML</a></li>";
		linksHtml += "<li><a href=\"/download/current/TheReactomeBook.pdf.zip\">Textbook</a></li>";
		linksHtml += "<li><a href=\"/download/\">Other formats</a></li>";
		linksHtml += "</ul>\n";
		panel.add(new HTML(linksHtml));

		return panel;
	}
	
	private VerticalPanel buildTryThisPanel() {
		final VerticalPanel panel = new VerticalPanel();
		
		panel.setStyleName("textbox"); // CSS
		
		HTML textLabel = new HTML("<b style=\"font-size:150%\">Try this</b><br>Have you got a set of genes or proteins, where you would like to understand the biological context better?  With Reactome, you can find out which of your genes or proteins are overrepresented in which pathways.");
		panel.add(textLabel);
		Button submitButton = new Button();
		submitButton.setHTML("<b style=\"font-size:70%\">Try it out!</b>");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HashMap state = new HashMap();
				state.put("analysis", (new Integer(PathwayAnalysisDataUploadPage.ANALYSIS_OVERREPRESENTATION)).toString());
				controller.createPage("PathwayAnalysisDataUploadPage", state);
			}
		});
		panel.add(submitButton);

		return panel;
	}
	
	private VerticalPanel buildUserFeedbackPanel() {
		final VerticalPanel panel = new VerticalPanel();
		
		panel.setStyleName("textbox"); // CSS
		
		HTML surveyLabel = new HTML("<b style=\"font-size:150%\">Your comments</b><br>Let us know how we could change our website to make it more useful for you:");
		panel.add(surveyLabel);
		final TextArea commentTextArea = new TextArea();
	    commentTextArea.setVisibleLines(3);
		panel.add(commentTextArea);
		
		Button submitButton = new Button();
		submitButton.setHTML("<b style=\"font-size:70%\">Submit</b>");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String comment = commentTextArea.getText();
				if (comment.isEmpty())
					return;
				
				SurveyResult surveyResult = new SurveyResult("unknown", "unknown", comment);
				ReactomePopup popup = new ReactomePopup();
				popup.setText("Submission successful - thank you for your suggestions!");

				surveyService.setSurveyResult(surveyResult, 
					new AsyncCallback<Void>() {
						public void onFailure(Throwable caught) {
						}
	
						public void onSuccess(Void nothing) {
						}
					});
			}
		});
		panel.add(submitButton);
		
		return panel;
	}
	
	private VerticalPanel buildSocialNetworkIconPanel() {
		final VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("textbox"); // CSS
		
		final HorizontalPanel strip1 = new HorizontalPanel();
		HTML facebookLabel = new HTML("<a href=\"http://www.facebook.com/group.php?gid=244908260192&v=wall\"><img src=\"images/logos/socialMedia/facebook.png\" /></a>");
		strip1.add(facebookLabel);
		HTML linkedInLabel = new HTML("<a href=\"http://www.linkedin.com/groups?mostPopular=&gid=2118372\"><img src=\"images/logos/socialMedia/linkedin.png\" /></a>");
		strip1.add(linkedInLabel);
		HTML rssLabel = new HTML("<a href=\"http://www.reactome.org/static_wordpress/feed/\"><img src=\"images/logos/socialMedia/wordpress.png\" /></a>");
		strip1.add(rssLabel);
		panel.add(strip1);
		
		final HorizontalPanel strip2 = new HorizontalPanel();
		HTML twitterLabel = new HTML("<a href=\"http://twitter.com/reactome\"><img src=\"images/logos/socialMedia/twitter.png\" /></a>");
		strip2.add(twitterLabel);
		HTML wordpressLabel = new HTML("<a href=\"http://news.reactome.org/\"><img src=\"images/logos/socialMedia/rss.png\" /></a>");
		strip2.add(wordpressLabel);
		HTML youTubeLabel = new HTML("<a href=\"http://www.youtube.com/user/Reactome\"><img src=\"images/logos/socialMedia/youtube.png\" /></a>");
		strip2.add(youTubeLabel);
		panel.add(strip2);
		
		return panel;
	}
}
