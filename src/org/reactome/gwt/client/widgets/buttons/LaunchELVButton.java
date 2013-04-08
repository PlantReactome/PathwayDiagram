/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets.buttons;

import java.util.HashMap;

import org.reactome.gwt.client.FormUtils;
import org.reactome.gwt.client.services.ExpressionPerPathwayService;
import org.reactome.gwt.client.services.ExpressionPerPathwayServiceAsync;
import org.reactome.gwt.client.services.ProjectPropertiesService;
import org.reactome.gwt.client.services.ProjectPropertiesServiceAsync;
import org.reactome.gwt.client.widgets.sortableTable.CellClickCatcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;

/**
 * Wraps Button and makes it look like a Comparable, so
 * that the setValue method of SortableTable will accept it.
 * 
 * @author David Croft
 *
 */
public abstract class LaunchELVButton extends Button implements Comparable,CellClickCatcher {
	// Use ExpressionPerPathwayServiceAsync.setPathwayDbIdInSession for all ELV
	// buttons, even if they are not being used from an expression page.  The
	// assumption here is that this information will only be needed once and
	// that no synchronisation conflicts will arise as a result.  TODO: is this true?
	private ExpressionPerPathwayServiceAsync expressionPerPathwayService = GWT.create(ExpressionPerPathwayService.class);
	private ProjectPropertiesServiceAsync projectPropertiesService = GWT.create(ProjectPropertiesService.class);
	private String pathwayDbId = null;
	private String speciesDbId = "48887"; // default: human
	protected String dbName = null; // if this is not set, something is wrong with your code!
	private String dataTableName = "unknown"; // database table consumed by ELV
	private String dataType = null;
	private Panel basePanel = null;

	public LaunchELVButton(String pathwayDbId, String dataType, Panel basePanel) {
		super();
		this.pathwayDbId = pathwayDbId;
		this.dataType = dataType;
		this.basePanel = basePanel;
		setText("View");
	}
	
	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}

	public void setSpeciesDbId(String speciesDbId) {
		this.speciesDbId = speciesDbId;
	}

	// Always returns zero - there is no rational basis for
	// sorting a column of identical buttons.
	public int compareTo(java.lang.Object arg0) {
		return 0;
	}

	/**
	 * This method is called by SortableTable if a cell is clicked;
	 * SortableTable catches all clicks.
	 */
	@Override
	public void click() {
		super.click();

		// Get the database name from the server.  This is an asynchronous call,
		// hence the weird code structure.
		projectPropertiesService.getProperty("GKB.Config.GK_ENTITY_DB_NAME", 
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						String warningMessage = caught.getMessage();
						if (warningMessage == null || warningMessage.isEmpty())
							warningMessage = "Server side error - problem getting database name, cannot display ELV for pathway\n";

						dealWithWarning(warningMessage);
					}

					public void onSuccess(String result) {
						if (result != null && !result.equals(""))
							dbName = result;

						// Store pathway DB_ID in server session.  This is an asynchronous call,
						// hence the weird code structure.
						expressionPerPathwayService.setPathwayDbIdInSession(pathwayDbId,
								new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								String warningMessage = caught.getMessage();
								if (warningMessage == null || warningMessage.isEmpty())
									warningMessage = "Server side error - problem setting pathway DB_ID in session, cannot display ELV for pathway\n";

								dealWithWarning(warningMessage);
							}

							@Override
							public void onSuccess(Void result) {
								action();
							}
						});
					}
		});
	}
	
	/**
	 * The action that this button should perform.  You need to provide this class
	 * with a dataTableName, and overwriting this method would be the best way to
	 * do it.
	 */
	protected void action() {
		formLauncher();
	}

	/**
	 * Uses a hidden form to launch ELV.
	 */
	protected void formLauncher() {
		// Launch form in new browser window
		FormUtils.formCreator(basePanel, createUrl(), createParams(), null, "_blank").submit();
	}
	
	/**
	 * Creates the URL that will be used by the action.  This may contain some of the
	 * parameters that you would like to pass on, and may have some redundant overlap
	 * with the parameters that you add via createParams().
	 * 
	 * @return
	 */
	protected String createUrl() {
		String strippedDbName = dbName;
//		String url = "/cgi-bin/eventbrowser?DB=" + strippedDbName + "&FOCUS_SPECIES_ID=" + speciesDbId + "&FOCUS_PATHWAY_ID=" + pathwayDbId + "&ID=" + pathwayDbId + "&DATA_TYPE=" + dataType + "&DATA_TABLE_NAME=" + dataTableName + "&REACTOME_GWT=1";
		String url = "/entitylevelview/PathwayBrowser.html#DB=" + strippedDbName + "&FOCUS_SPECIES_ID=" + speciesDbId + "&FOCUS_PATHWAY_ID=" + pathwayDbId + "&ID=" + pathwayDbId + "&DATA_TYPE=" + dataType + "&DATA_TABLE_NAME=" + dataTableName + "&REACTOME_GWT=1";

		return url;
	}
	
	/**
	 * Creates a hash containing the parameters that will be passed on to the action
	 * to be used if it is a POST request.
	 * 
	 * @return
	 */
	protected HashMap createParams() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("REACTOME_GWT", "1");
		params.put("DATA_TYPE", dataType);
		params.put("DATA_TABLE_NAME", dataTableName);
		params.put("PATHWAY_DB_ID", pathwayDbId);
		params.put("FOCUS_PATHWAY_ID", pathwayDbId);
		params.put("ID", pathwayDbId);
		params.put("FOCUS_SPECIES_ID", speciesDbId);

		return params;
	}
	
	protected abstract void dealWithWarning(String warning);
}
