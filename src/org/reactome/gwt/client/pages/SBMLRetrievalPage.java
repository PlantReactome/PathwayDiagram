/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.ReactomeGWT;
import org.reactome.gwt.client.services.SBMLDataExampleService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates page for uploading stuff to be turned into SBML.
 * 
 * The state hash for this page contains the following items:
 * 
 * rawData     	User-entered tab delimited rawData.
 * warningMessage			Printed out if something goes wrong.
 *
 * @author David Croft
 */
public class SBMLRetrievalPage extends DataUploadPage {
	private ListBox includedSpeciesSelector = new ListBox();
	private TextArea speciesTextArea = new TextArea();
	private Map<String,String> hiddenParameterMap = new HashMap<String,String>();

    public SBMLRetrievalPage(ReactomeGWT controller) {
    	super(controller);
    	
    	// This is needed to escape from GWT
    	String nullString = null;
    	form = new FormPanel(nullString);
    	
		setTitle("Generate SBML from a list of Identifiers");
		setDataExampleService(GWT.create(SBMLDataExampleService.class));
		analyseButton.setText("Generate SBML");
		UPLOAD_ACTION_URL += "sbmlRetrieval";
	    form.setAction(UPLOAD_ACTION_URL);
	}
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		setPackageName(getClass().getName());
		descriptionText = "Takes a list of Reactome reaction DB_IDs and generates the corresponding SBML.";
		moreDescriptionText = "Identifiers should be separated by commas or newlines.  You can use the filtering options to constrain the SBML generated.  E.g. If you use the compartment filter, you can constrain the SBML so that only reactions that take place in specific compartments are included";
		
		super.onModuleLoad();
		
		VerticalPanel emptySpacePanel = new VerticalPanel();
		emptySpacePanel.setHeight("10px");
		innerDataEntryPanel.add(emptySpacePanel);
		basePanel.add(emptySpacePanel);

		HTML parameterAndFilterLabel = new HTML("<h2 class=\"section_heading\">Optional parameter settings and filters</h2>\n");
		basePanel.add(parameterAndFilterLabel);

		HorizontalPanel extraPanel = new HorizontalPanel();
		extraPanel.setStyleName("pale_blue_textbox"); // CSS
		extraPanel.setWidth("100%");
		innerDataEntryPanel.add(extraPanel);
		basePanel.add(extraPanel);
		
		VerticalPanel extraPanelLeft = new VerticalPanel();
		extraPanelLeft.setWidth("50%");
		extraPanel.add(extraPanelLeft);
		
		VerticalPanel extraPanelRight = new VerticalPanel();
		extraPanelRight.setWidth("50%");
		extraPanel.add(extraPanelRight);
		
		extraPanelLeft.add(new LevelVersionSelectorPanel());
		extraPanelRight.add(new LayoutPanel());
		extraPanelLeft.add(new InstanceClassFilterPanel("INCLUSION", "Pathway", "FrontPage.frontPageItem", "Pathway", "Apoptosis"));
		extraPanelRight.add(new InstanceClassFilterPanel("EXCLUSION", "Pathway", "FrontPage.frontPageItem", "Pathway", "Apoptosis"));
		extraPanelLeft.add(new InstanceClassFilterPanel("INCLUSION", "ReactionlikeEvent", "Compartment", "Compartment", "cytosol"));
		extraPanelRight.add(new InstanceClassFilterPanel("EXCLUSION", "ReactionlikeEvent", "Compartment", "Compartment", "cytosol"));
		extraPanelLeft.add(new InstanceClassFilterPanel("INCLUSION", "ReactionlikeEvent", "Species", "Organism", "Homo sapiens"));
		extraPanelRight.add(new InstanceClassFilterPanel("EXCLUSION", "ReactionlikeEvent", "Species", "Organism", "Homo sapiens"));
	}
	
	private void addHiddenParameterToHash(String key, String value) {
		addHiddenParameterToHash(key, value, "");
	}
	
	private void addHiddenParameterToHash(String key, String value, String seperator) {
		String existingValue = hiddenParameterMap.get(key);
		if (existingValue == null)
			hiddenParameterMap.put(key, value);
		else
			hiddenParameterMap.put(key, existingValue + seperator + value);
	}
	
	private void addHiddenParametersToForm() {
		Set<String> keys = hiddenParameterMap.keySet();
		for (String key: keys)
			innerDataEntryPanel.add(new Hidden(key, hiddenParameterMap.get(key)));

	}
	
	protected void analyseButtonAddClickHandler() {
		analyseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addHiddenParametersToForm();
				form.submit();
			}
		});
	}

	/**
	 * Don't do anything while form is being submitted.
	 */
	protected void formAddSubmitHandler() {
	}
	
	/**
	 * Don't do anything after form has been submitted.
	 */
	protected void formAddSubmitCompleteHandler() {
	}
	
	public HashMap getState() {
		super.getState();
		
		return state;
	}
	
	private abstract class ParameterPanel extends VerticalPanel {
		private String title;
		protected HorizontalPanel widgetPanel = new HorizontalPanel();

		public ParameterPanel(String title) {
			super();
			this.title = title;
		}
		
		public abstract void createWidgetPanel();
		
		protected void init() {
			HorizontalPanel titlePanel = new HorizontalPanel();
			titlePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			titlePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
			titlePanel.setStyleName("horizontal_inner_panel"); // CSS
			add(titlePanel);
			
			widgetPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			widgetPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
			widgetPanel.setStyleName("horizontal_inner_panel"); // CSS
			add(widgetPanel);
			
			HTML titleLabel = new HTML("<b>" + title + "</b>");
			titlePanel.add(titleLabel);
			
			createWidgetPanel();
		}
	}
	
	private class LevelVersionSelectorPanel extends ParameterPanel {
		private ListBox levelSelector = new ListBox();
		private ListBox versionSelector = new ListBox();

		public LevelVersionSelectorPanel() {
			super("Choose SBML level and version numbers");
			init();
		}

		@Override
		public void createWidgetPanel() {
			HTML levelSelectLabel = new HTML("Level: ");
			widgetPanel.add(levelSelectLabel);
			
			levelSelector.setName("LEVEL");
			levelSelector.addItem("1");
			levelSelector.addItem("2");
			levelSelector.addItem("3");
			levelSelector.setSelectedIndex(1);
			levelSelector.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					addHiddenParameterToHash("LEVEL", Integer.toString(levelSelector.getSelectedIndex() + 1));
				}
			});
			widgetPanel.add(levelSelector);
			
			HTML versionSelectLabel = new HTML("Version: ");
			widgetPanel.add(versionSelectLabel);
			
			versionSelector.setName("VERSION");
			versionSelector.addItem("1");
			versionSelector.addItem("2");
			versionSelector.addItem("3");
			versionSelector.addItem("4");
			versionSelector.setSelectedIndex(2);
			versionSelector.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					addHiddenParameterToHash("VERSION", Integer.toString(versionSelector.getSelectedIndex() + 1));
				}
			});
			widgetPanel.add(versionSelector);
		}
	}
	
	private class LayoutPanel extends ParameterPanel {
		private CheckBox layoutExtensionCheckbox = new CheckBox("LayoutExtension");
		private CheckBox sbgnCheckbox = new CheckBox("SBGN");
		private CheckBox cellDesignerCheckbox = new CheckBox("CellDesigner");

		public LayoutPanel() {
			super("Choose layout");
			init();
		}

		@Override
		public void createWidgetPanel() {
			layoutExtensionCheckbox.setName("LAYOUT_EXTENSION");
			layoutExtensionCheckbox.addValueChangeHandler(new ValueChangeHandler() {
				@Override
				public void onValueChange(ValueChangeEvent event) {
					boolean isChecked = layoutExtensionCheckbox.getValue();
					if (isChecked)
						addHiddenParameterToHash("LAYOUT", "EXTENSION", ",");
				}
			});
			widgetPanel.add(layoutExtensionCheckbox);

			sbgnCheckbox.setName("LAYOUT_SBGN");
			sbgnCheckbox.addValueChangeHandler(new ValueChangeHandler() {
				@Override
				public void onValueChange(ValueChangeEvent event) {
					boolean isChecked = sbgnCheckbox.getValue();
					if (isChecked)
						addHiddenParameterToHash("LAYOUT", "SBGN", ",");
				}
			});
			widgetPanel.add(sbgnCheckbox);

			cellDesignerCheckbox.setName("LAYOUT_CELL_DESIGNER");
			cellDesignerCheckbox.addValueChangeHandler(new ValueChangeHandler() {
				@Override
				public void onValueChange(ValueChangeEvent event) {
					boolean isChecked = cellDesignerCheckbox.getValue();
					if (isChecked)
						addHiddenParameterToHash("LAYOUT", "CELL_DESIGNER", ",");
				}
			});
			widgetPanel.add(cellDesignerCheckbox);
		}
	}
	
	private class InstanceClassFilterPanel extends ParameterPanel {
		private String filterType;
		private String filteredInstanceClass;
		private String filteringInstanceClass;
		private String selectedInstanceName;
		private ListBox instanceNameSelector = new ListBox();
		private TextArea instanceNameTextArea = new TextArea();
		private String[] instanceNames;
		private int MAX_INSTANCE_NAME_LENGTH = 20;
		
		/**
		 * Construct a panel for displaying filters and allowing the user to manipulate them.
		 * 
		 * e.g.
		 * 
		 * InstanceClassFilterPanel("EXCLUSION", "ReactionlikeEvent", "Species", "Organism", "Homo sapiens")
		 * InstanceClassFilterPanel("INCLUSION", "Pathway", "FrontPage.frontPageItem", "Pathway", "Apoptosis")
		 * 
		 * @param filterType				Either "INCLUSION" or "EXCLUSION".
		 * @param filteredInstanceClass		Reactome instance class being filtered.
		 * @param filteringInstanceClass	This can be either filteredInstanceClass itself, or the instance class of one of its attributes.
		 * @param displayInstanceClass		User-friendly name of filtered instance class, used in GUI.
		 * @param selectedInstanceName		Instance class name that will be selected by default in the GUI.
		 */
		public InstanceClassFilterPanel(String filterType, String filteredInstanceClass, String filteringInstanceClass, String displayInstanceClass, String selectedInstanceName) {
			super(displayInstanceClass + " " + filterType.toLowerCase() + " filter");
			this.filterType = filterType;
			this.filteredInstanceClass = filteredInstanceClass;
			this.filteringInstanceClass = filteringInstanceClass;
			this.selectedInstanceName = selectedInstanceName;
			init();
		}

		@Override
		public void createWidgetPanel() {
			// Get a list of acceptable term values for this filter, and pop
			// them into a selector in the GUI.
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("LIST_" + filteringInstanceClass.toUpperCase().replaceAll("\\.", "_") + "_NAMES", "1");
			FormPanel sbmlForm = FormUtils.formCreator(basePanel, UPLOAD_ACTION_URL, params, null, "invisible");
			sbmlForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					if (event == null) {
						showWarning("onSubmitComplete: event is null!!");
						return;
					}
					
					String results = event.getResults();
					instanceNames = results.split("\n");
					List<String> instanceNameList = new ArrayList<String>();
					for (int i=0; i<instanceNames.length; i++) {
						String instanceName = instanceNames[i];
						instanceName = instanceName.replaceAll("<pre>", "");
						instanceName = instanceName.replaceAll("</pre>", "");
						if (instanceName.isEmpty())
							continue;
						instanceNameList.add(instanceName);
						instanceNameSelector.addItem(shortenInstanceName(instanceName));
						if (instanceName.equals(selectedInstanceName))
							instanceNameSelector.setSelectedIndex(i);
					}
					instanceNames = new String[instanceNameList.size()];
					for (int i=0; i<instanceNameList.size(); i++)
						instanceNames[i] = instanceNameList.get(i);
				}
			});
			sbmlForm.submit();
			widgetPanel.add(instanceNameSelector);
			
			// The button that adds the user's selected filter term value to the
			// list that will actually be used in filtering.
			Button addButton = new Button("add");
			addButton.addClickHandler(new ClickHandler() {
				/**
				 * Display the user-selected term value in the text area,
				 * and add it to the list of filter terms to be sent to
				 * the server.
				 */
				@Override
				public void onClick(ClickEvent event) {
					int selectedIndex = instanceNameSelector.getSelectedIndex();
					String instanceName = instanceNames[selectedIndex];
					
					// Update text area
					String instanceNameText = instanceNameTextArea.getText();
					instanceNameTextArea.setText(instanceNameText + "\n" + instanceName);
					
					// Create hidden parameter
					if (filterType.equals("INCLUSION"))
						addHiddenParameterToHash("FILTER", "inc", ",");
					else
						addHiddenParameterToHash("FILTER", "exc", ",");
					addHiddenParameterToHash("FILTER", filteredInstanceClass, ",");
					addHiddenParameterToHash("FILTER", filteringInstanceClass, ",");
					addHiddenParameterToHash("FILTER", instanceName, ",");
				}
			});
			widgetPanel.add(addButton);
			widgetPanel.add(instanceNameTextArea);
		}
		
		private String shortenInstanceName(String instanceName) {
			String shortInstanceName = instanceName;
			if (shortInstanceName.length() > MAX_INSTANCE_NAME_LENGTH) {
				String[] instanceNameElements = shortInstanceName.split(" ");
				String zerothElement = instanceNameElements[0].substring(0, 1) + ".";
				shortInstanceName = zerothElement;
				for (int i=1; i<instanceNameElements.length; i++)
					shortInstanceName += " " + instanceNameElements[i];
			}
			if (shortInstanceName.length() > MAX_INSTANCE_NAME_LENGTH)
				shortInstanceName = shortInstanceName.substring(0, MAX_INSTANCE_NAME_LENGTH);
			
			return shortInstanceName;
		}
	}
}
