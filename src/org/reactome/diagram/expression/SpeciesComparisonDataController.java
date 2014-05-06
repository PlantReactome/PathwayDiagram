/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.analysis.factory.AnalysisModelException;
import org.reactome.diagram.analysis.factory.AnalysisModelFactory;
import org.reactome.diagram.analysis.model.AnalysisResult;
import org.reactome.diagram.analysis.model.SpeciesSummary;
import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.PathwayDiagramController;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Label;

/**
 * This customized GUI is used to control species comparison overlay. 
 * @author 
 *
 */
public class SpeciesComparisonDataController extends DataController {
	private static Map<Long, String> speciesList = new HashMap<Long, String>();
	private String species;
	
    public SpeciesComparisonDataController(AnalysisResult analysisResult) {
    	super(analysisResult);
    	navigationPane = new SpeciesComparisonNavigationPane();
    }
    
    @Override
    public void setPathway(String token, CanvasPathway pathway) {
   		super.setPathway(token, pathway);
        
   		if (pathway != null) {
        	onDataPointChange(0);
        }
    }
    
    public Map<Long, String> convertValueToColor(Map<Long, Double> compIdToValue) {    	
    	final String YELLOW = "rgb(255, 255, 102)";
    	//final String BLUE = "rgb(0, 0, 255)";
    	
    	Map<Long, String> compIdToColor = new HashMap<Long, String>();
	
    	for (Long refId : compIdToValue.keySet()) {
    		//String color = (compIdToValue.get(Id).intValue() == 100) ? YELLOW	: BLUE;
    		
    		compIdToColor.put(refId, YELLOW);
    	}
    	
    	return compIdToColor;    	
    }
    
    public void setSpecies(String analysisResultText) {
    	final Long speciesDbId;
    	try {
    		SpeciesSummary speciesSummary = AnalysisModelFactory.getModelObject(SpeciesSummary.class, analysisResultText);
    		speciesDbId = speciesSummary.getDbId();
    	} catch (AnalysisModelException e) {
    		AlertPopup.alert("Unable to obtain species summary: " + e);
    		return;
    	}
    	
    	if (! getSpeciesList().isEmpty()) {
    		setSpecies(speciesDbId);
    	} else {
    		final PathwayDiagramController diagramController = PathwayDiagramController.getInstance();
    		diagramController.getSpeciesList(new RequestCallback() {

    			@Override
    			public void onResponseReceived(Request request, Response response) {
    				if (response.getStatusCode() != Response.SC_OK) {
    					diagramController.requestFailed("Unable to retrieve species list: " + response.getStatusText());
    					return;
    				}
    				setSpeciesList(response.getText());
    				setSpecies(speciesDbId);
    			}

    			@Override
    			public void onError(Request request, Throwable exception) {
    				diagramController.requestFailed(exception);
    			} 
    		});
    	}
    }
    	
    private void setSpecies(Long speciesDbId) {    
    	String species = getSpeciesList().get(speciesDbId);
    			
    	((SpeciesComparisonNavigationPane) navigationPane).getDataLabel().setText(species);
    	this.species = species;
    }
    
    public String getSpecies() {
		return species;    	
    }
    
    private void setSpeciesList(String speciesJSON) {
    	JSONArray speciesArray = JSONParser.parseStrict(speciesJSON).isArray();
    	
    	speciesList.clear();
    	for (int index = 0; index < speciesArray.size(); index++) {
    		JSONObject speciesObject = speciesArray.get(index).isObject();
    		
    		Long dbId = Long.parseLong(speciesObject.get("dbId").isString().stringValue()); 
    		String speciesName = speciesObject.get("displayName").isString().stringValue();
    		
    		speciesList.put(dbId, speciesName);
    	}
    }
    
    private Map<Long, String> getSpeciesList() {
		return speciesList;    	
    }
   
    protected class SpeciesComparisonNavigationPane extends NavigationPane {
                
        public SpeciesComparisonNavigationPane() {
            super();
            init();
        }
        
        protected void init() {	        	
        	dataLabel.setText("Species");
        	addDataLabel();
        	addCloseButton();
        	installHandlers();    
        } 
        
        protected Label getDataLabel() {
			return dataLabel;
        	
        }
    }
}
