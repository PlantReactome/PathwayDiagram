/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.HashMap;

import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.services.CopyrightService;
import org.reactome.gwt.client.services.CopyrightServiceAsync;
import org.reactome.gwt.client.widgets.ReactomePopup;
import org.reactome.gwt.client.widgets.buttons.ExternalNewPageButton;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *  Reactome base page class.
 *  
 *  The main point of this is to allow a separation of view and model for pages.  The
 *  state variable carries the page model.  The model will, in general, be much more
 *  lightweight than the page object itself, so that a record of the states of old
 *  pages can be kept without a great memory overload.  This makes it possible to
 *  have a sensible back and forward button implementation, using the history mechanism.
 *  
 *  Also provides a "logPanel" for showing diagnostics.  You can use it like this:
 *  
 *  logPanel.add(new Label("The value of variable x is: " + x));
 * 
 * @author David Croft
 */
public abstract class Page implements EntryPoint {
	public static int BASE_PANEL_WIDTH = 800;
	// Hash may be very different for different pages, so we can't specify the type
	// more explicitly.
	protected HashMap<String,Object> state = new HashMap<String,Object>();
	protected ReactomeGWT controller;
	private String packageName;
	protected VerticalPanel basePanel = new VerticalPanel();
	protected VerticalPanel footerPanel1 = new VerticalPanel();
	private VerticalPanel footerPanel2 = new VerticalPanel();
	protected String descriptionText = null;
	protected ExternalNewPageButton videoTutorialButton = null;
	protected String moreDescriptionText = null;
	protected String warningMessage = "";
	protected HTML warningMessageLabel = new HTML();
	protected String title = null;
	protected static int WIDGET_SPACING = 100;
	protected HorizontalAlignmentConstant horizontalAlignment = VerticalPanel.ALIGN_LEFT;
	private String name = null;

	public Page() {
		super();
	}

	public VerticalPanel getBasePanel() {
		return basePanel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This is the generic entry point method.  You would normally want to do a:
	 * 
	 * super.onModuleLoad()
	 * 
	 * near the top of the onModuleLoad() method of any class inheriting from Page.
	 * 
	 */
	public void onModuleLoad() {
		controller.initialize(); // Start with a clean slate
		
		controller.createBanner(BASE_PANEL_WIDTH, isFrontPage(getClasseName()));
		controller.alignNavigationBar(BASE_PANEL_WIDTH);

		basePanel.setWidth(BASE_PANEL_WIDTH + "px");
//		basePanel.setHorizontalAlignment(horizontalAlignment);
		controller.getPageFillerPanel().add(basePanel);
		
		// Get title from state, if available.  This will override any default title.
		String stateTitle = (String) state.get("title");
		if (stateTitle != null && !stateTitle.isEmpty())
			title = stateTitle;
		// Display title, if available.
		if (title != null) {
			HTML titleLabel = new HTML("<H1 CLASS=\"frontpage\">" + title + "</H1>");
			basePanel.add(titleLabel);
		}
		
		// Print a warning message, if it is in the state
		showWarning();
		
		// Get more description text from state, if available.  This will override any default title.
		String stateMoreDescriptionText = (String) state.get("moreDescriptionText");
		if (stateMoreDescriptionText != null && !stateMoreDescriptionText.isEmpty())
			moreDescriptionText = stateMoreDescriptionText;

		// Get description text from state, if available.  This will override any default title.
		String stateDescriptionText = (String) state.get("descriptionText");
		if (stateDescriptionText != null && !stateDescriptionText.isEmpty())
			descriptionText = stateDescriptionText;
		// Display description text, if available.
		if (descriptionText != null) {
			// Add more description text, if available.
			if (moreDescriptionText != null) {
				initJS(this);
				descriptionText += "  <a onclick=\"launchMoreDescriptionPopup()\">More....</a>";
			}
			
			HTML descriptionLabel = new HTML(descriptionText);
			descriptionLabel.setHorizontalAlignment(Label.ALIGN_LEFT);
			descriptionLabel.setStyleName("textbox"); // CSS
			basePanel.add(descriptionLabel);
		}
		
		if (videoTutorialButton != null)
			basePanel.add(videoTutorialButton);
				
		RootPanel rootPanel = controller.getRootPanel();

		footerPanel1.setWidth(BASE_PANEL_WIDTH + "px");
		footerPanel1.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		rootPanel.add(footerPanel1);
		
		footerPanel2.setWidth(BASE_PANEL_WIDTH + "px");
		footerPanel2.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		// Instead of using the Perl Mod thing, try inserting the HTML/Javascript
		// directly into the element.
		CopyrightServiceAsync copyrightService = GWT.create(CopyrightService.class);
		copyrightService.getText(
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
					}

					public void onSuccess(String result) {
						if (result == null || result.isEmpty())
							return;
						
						footerPanel2.add(new HTML(result));
					}
				});
		rootPanel.add(footerPanel2);
	}
	
	/**
	 * Launches a popup containing extra description information, for users who
	 * are not satisfied with the one-sentence description that gets put at the
	 * head of most pages.
	 */
	protected void launchMoreDescriptionPopup() {
		ReactomePopup popup = new ReactomePopup();
		popup.setText(moreDescriptionText);
		popup.setWidth("400px");
		popup.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	}
	
	/**
	 * Inserts some native JavaScript that can be used elsewhere to launch a popup
	 * for extra description information.
	 * 
	 * @param expressionDataUploadPage
	 */
	protected native void initJS(Page page) /*-{
		$wnd.launchMoreDescriptionPopup = function () {
			page.@org.reactome.gwt.client.pages.ExpressionDataUploadPage::launchMoreDescriptionPopup()();
		};
	}-*/;	
	
	/**
	 * If the current page is the front page, return true, otherwise return
	 * false.
	 * 
	 * @return
	 */
	public static boolean isFrontPage(String pageName) {
		if (pageName == null)
			return true;
		if (pageName.isEmpty())
			return true;
		if (pageName.equals("FrontPage"))
			return true;
		return false;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String thisClass) {
		this.packageName = thisClass;
	}

	/**
	 * Gets the class associated with the package of the class at the end of the
	 * class hierarchy.
	 * 
	 * @return
	 */
	public String getClasseName() {
		String[] packageParts = packageName.split("\\.");
		String className = packageName;
		if (packageParts.length>0)
			className = packageParts[packageParts.length-1];
		String[] classNameParts = className.split("\\$");
		if (classNameParts.length>0)
			className = classNameParts[0];
		
		return className;
	}

	public HashMap<String,Object> getState() {
		state.put("warningMessage", warningMessage);
		return state;
	}

	/**
	 * This is made abstract, because in its implementation, some set up of the
	 * actual page is likely to be necessary.  I.e. it will and should have
	 * sideffects beyond merely setting the variable "state".
	 * 
	 * @param state
	 */
	public void setState(HashMap<String,Object> state) {
		this.state = state;
	}
	
	protected void showWarning() {
		showWarning(warningMessage);
	}

	protected void showWarning(String warningMessage) {
		if (warningMessage != null && !warningMessage.isEmpty()) {
			String html = warningMessageLabel.getHTML();
			if (html == null)
				html = "";
			warningMessageLabel.setHTML(html + "<DIV STYLE=\"color:red;\">" + warningMessage + "</DIV>");
			basePanel.add(warningMessageLabel);
		}
	}
	
	protected void waitCursor(boolean isWaiting) {
		String cursorType = "default";
		if (isWaiting)
			cursorType = "wait";
		DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", cursorType); 
	}
}
