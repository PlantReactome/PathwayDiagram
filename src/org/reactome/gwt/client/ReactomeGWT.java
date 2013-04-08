/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import java.util.HashMap;

import org.reactome.gwt.client.analysis.PathwayFormPage;
import org.reactome.gwt.client.pages.ExpressionDataUploadPage;
import org.reactome.gwt.client.pages.ExpressionPerPathwayPage;
import org.reactome.gwt.client.pages.FrontPage;
import org.reactome.gwt.client.pages.Page;
import org.reactome.gwt.client.pages.PathwayAnalysisDataUploadPage;
import org.reactome.gwt.client.pages.PathwayAssignmentPage;
import org.reactome.gwt.client.pages.SBMLRetrievalPage;
import org.reactome.gwt.client.pages.SpeciesComparisonPage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the entry point into Reactome's GWT web pages.  It also
 * acts as a controller, deciding how to swap between the different
 * web pages, according to the buttons clicked by the user.  It
 * maintains a history, so that the user can navigate forwards or
 * backwards through the pages already created.
 * 
 * @author David Croft
 */
public class ReactomeGWT implements EntryPoint, ValueChangeHandler<String> {
	private Page currentPage = null;
	private HashMap<String, HashMap<String,Object>> states = new HashMap<String, HashMap<String,Object>>();
	public static boolean HISTORY_ACTIVE = true;
	private boolean pageChangeInitiatedByUserAction = false;
	protected RootPanel rootPanel = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	    // Add history listener.  This gets fired whenever a user
		// hits the back or forward button on the browser, or does
		// a page refresh.
	    History.addValueChangeHandler(this);
	    
	    String initToken = null;
	    if (HISTORY_ACTIVE)
	    	initToken = History.getToken();
	    
	    createPage(initToken);
	}

	/**
	 * This method gets called whenever the browser's history changes,
	 * e.g. when a user hits the back or forward button on the browser,
	 * or does a page refresh.  If the history mechanism is not active,
	 * the front page will be displayed.
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		if (pageChangeInitiatedByUserAction) {
			// If a History event has been triggered by a user action,
			// such as button clicking, rather than by using the
			// browser's back button or restoring a bookmark, then
			// don't create a new page, because that will be done
			// elsewhere.
			pageChangeInitiatedByUserAction = false;
			return;
		}
		
		if (HISTORY_ACTIVE)
			createPage(event.getValue());
		else
			createPage(null);
	}
	
	/**
	 * If a page with this name exists, redisplay it with the last content only
	 * if it has been shown before in this session. If page name is null or
	 * empty the front page is created
	 * 
	 * @param pageName
	 */
	public void createPage(String pageName) {
		if (pageName != null && !pageName.isEmpty())
			if (states.containsKey(pageName))
				createPage(pageName, states.get(pageName));
			else
				createPage(pageName, null);
		else
			createPage("FrontPage", null);
	}
	
	/**
	 * Core page creation method.  Creates and displays a page, based on
	 * the text part of the page key.  The supplied state will be used
	 * to pre-populate the page with information, if it is not null.
	 * The state of the previously displayed page will be saved, so that
	 * it can be retrieved by the history mechanism.
	 * 
	 * @param pageName
	 * @param state
	 */
	public void createPage(String pageName, HashMap<String,Object> state) {
		if(pageName!=null && !pageName.isEmpty()){
			// If the page the user is trying to create is the same as the
			// current page, abort.
			if (currentPage != null && currentPage.getName().equals(pageName))
				return;

			Page page = createPageObject(pageName);
	
			// State-related manipulations, to keep the history mechanism
			// happy, if it is active.  If it is not active, these lines
			// will not do any harm.
			if (HISTORY_ACTIVE)
				saveCurrentPageState(pageName, page, state);
	
			this.currentPage = page;
			page.onModuleLoad();
		}
	}
	
	/**
	 * Saves the state of the current page.  This is keyed by the pageKey,
	 * so that it can be restored later if the user clicks the back button.
	 */
	private void saveCurrentPageState(String pageKey, Page page, HashMap<String,Object> state) {	    
	    if (state == null)
			state = new HashMap<String,Object>();
		state.put("pageKey", pageKey);
		
		page.setState(state);
		
		states.put(pageKey, state);
		
		if (currentPage != null) {
			HashMap<String,Object> oldState = currentPage.getState();
			String oldPageKey = (String)oldState.get("pageKey");
			states.put(oldPageKey, oldState);
		}
	}
	
	/**
	 * Creates a new page object of the given type.  This is really a
	 * workaround for the fact that Javascript can't create new objects
	 * based on class names.  It only recognises a limited number of
	 * page types.  Returns null if the supplied page type is not
	 * recognised.
	 * @param pageName
	 * @return
	 */
	private Page createPageObject(String pageName) {
		//Make sense solve the problem of creating history items of the front
		//page (what was causing a non 'REACTOME exit' problem) and other states
		//where is not a good idea to arrive with the back and forward history
		//buttons
		boolean MAKE_SENSE_HISTORY = true;
		
		Page page = null;
		//  FRONTPAGE
		if (pageName.equals("FrontPage")){
			MAKE_SENSE_HISTORY = false;
			page = new FrontPage(this);
		}
		
		// EXPRESSION DATA
		if (pageName.equals("ExpressionDataUploadPage"))
			page = new ExpressionDataUploadPage(this);
		if (pageName.equals("ExpressionPerPathwayPage"))
			page = new ExpressionPerPathwayPage(this);
		
		// SPECIES COMPARISON
		if (pageName.equals("SpeciesComparisonPage"))
			page = new SpeciesComparisonPage(this);
		
		// PATHWAY ANALYSIS DATA
		if (pageName.equals("PathwayAnalysisDataUploadPage"))
			page = new PathwayAnalysisDataUploadPage(this);
		if (pageName.equals("PathwayAssignmentPage"))
			page = new PathwayAssignmentPage(this);
		if (pageName.equals("PathwayFormPage"))
			page = new PathwayFormPage(this);
		
		// SBML builder
		if (pageName.equals("SBMLRetrievalPage"))
			page = new SBMLRetrievalPage(this);
		
		page.setName(pageName);
		
		// Add a new item to the history stack
		if(page!=null && HISTORY_ACTIVE && MAKE_SENSE_HISTORY){
			pageChangeInitiatedByUserAction = true;
			History.newItem(pageName);
		}
		
		return page;
	}
	
	protected VerticalPanel logPanel = new VerticalPanel(); // Use this for adding diagnostics
//	protected NavigationBarPanel navigationBarPanel = new NavigationBarPanel(BASE_PANEL_WIDTH, logPanel);
	protected VerticalPanel pageFillerPanel = new VerticalPanel();

	public VerticalPanel getLogPanel() {
		return logPanel;
	}

	public VerticalPanel getPageFillerPanel() {
		return pageFillerPanel;
	}

	public void initialize() {
		getRootPanel();
		rootPanel.clear(); // Start with a clean slate
		
		logPanel.setStyleName("log_panel"); // CSS
		rootPanel.add(logPanel);
		
//		navigationBarPanel.onModuleLoad();
//		basePanel.add(navigationBarPanel);
		
		createPageFillerPanel();
		rootPanel.add(pageFillerPanel);
	}

	public void createBanner(int panelWidth, boolean isFull) {
		// Put an appropriate image into the empty div element where the banner
		// ought to be.
		Element bannerElement = Document.get().getElementById("banner");
		if (bannerElement != null) {
			String innerHTMLString = "";
			
			if (isFull)
				innerHTMLString = "\n<img alt=\"Banner\" src=\"images/banner1.png\" />\n";
//				innerHTMLString = "\n<img alt=\"Banner\" width=\"" + panelWidth + "\" src=\"images/banner1.png\" />\n";
			else {
				innerHTMLString  = "\n<TABLE  WIDTH=\"" + panelWidth + "\" BORDER=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
				innerHTMLString += "  <TR>\n";
				innerHTMLString += "    <TD><IMG alt=\"Banner\" WIDTH=" + (panelWidth - 200) + " SRC=\"images/half_height_banner1.png\" /></TD>\n";
				innerHTMLString += "    <TD style=\"width:100%;\"><DIV style=\"background:#4443A8;height:37px;\"></DIV></TD>\n";
				innerHTMLString += "  </TR>\n";
				innerHTMLString += "</TABLE>\n";
			}
			bannerElement.setInnerHTML(innerHTMLString);
		}
	}
	
	public void alignNavigationBar(int panelWidth) {
		// Mess with the navigation bar's style, to gain control of its width
		// and to align it to the page's center.
		Element navigationBarElement = Document.get().getElementById("navigation_bar");
		if (navigationBarElement != null) {
			changeElementWidthAndCenter(navigationBarElement, panelWidth);
		}
	}
	
	/**
	 * Use native Javascript to change the width of an element
	 * 
	 * @param element
	 * @param width
	 */
	private native void changeElementWidthAndCenter(Element element, int width) /*-{
		element.style.width=width;
	}-*/;
	
	/**
	 *  Create an auto-resizing page filler panel.  This exists purely with the
	 *  intention of providing a surface to show a "waiting" cursor where
	 *  needed.  The only widget that is added to this panel is the basePanel.
	 */
	private void createPageFillerPanel() {
		pageFillerPanel = new VerticalPanel();
		pageFillerPanel.setWidth("100%");
		pageFillerPanel.setHeight(Window.getClientHeight() + "px");
		pageFillerPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				int height = event.getHeight();
				pageFillerPanel.setHeight(height + "px");
			}
		});
	}

	public RootPanel getRootPanel() {
		if (rootPanel == null)
			rootPanel = RootPanel.get("entrypointTag");
		
		return rootPanel;
	}
}
