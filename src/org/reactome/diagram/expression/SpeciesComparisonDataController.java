/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.Map;


import org.reactome.diagram.analysis.model.AnalysisResult;
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
 * @author weiserj 
 *
 */
public class SpeciesComparisonDataController extends DataController {
	private static Map<Long, String> speciesList = new HashMap<Long, String>();
	private String speciesName;
	private Long speciesDbId;
	
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
    
    public void setSpecies() {
    	if (! getSpeciesList().isEmpty()) {
    		setSpeciesDbId(getAnalysisResult().getSummary().getSpecies());
    		setSpeciesName();
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
    				setSpeciesDbId(getAnalysisResult().getSummary().getSpecies());
    				setSpeciesName();
    			}

    			@Override
    			public void onError(Request request, Throwable exception) {
    				diagramController.requestFailed(exception);
    			} 
    		});
    	}
    }
    
    private void setSpeciesDbId(Long speciesDbId) {
    	this.speciesDbId = speciesDbId;
    }
    
    private Long getSpeciesDbId() {
    	return speciesDbId;
    }
    
    private void setSpeciesName() {    
    	String species = getSpeciesList().get(getSpeciesDbId());
    	
    	((SpeciesComparisonNavigationPane) navigationPane).getDataLabel().setText(species);
    	this.speciesName = species;
    }
    
    public String getSpeciesName() {
		return speciesName;
    }
    
    private void setSpeciesList(String speciesJSON) {
    	JSONArray speciesArray = JSONParser.parseStrict(speciesJSON).isArray();
    	
    	speciesList.clear();
    	for (int index = 0; index < speciesArray.size(); index++) {
    		JSONObject speciesObject = speciesArray.get(index).isObject();
    		
    		Long dbId = Long.parseLong(speciesObject.get("dbId").isNumber().toString()); 
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
