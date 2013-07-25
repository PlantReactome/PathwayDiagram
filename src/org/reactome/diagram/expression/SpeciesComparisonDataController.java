/*
 * Created on Feb 7, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.user.client.ui.Label;

/**
 * This customized GUI is used to present the user a way to select a data point, and
 * show a color bars for expression values. 
 * @author gwu
 *
 */
public class SpeciesComparisonDataController extends DataController {

    public SpeciesComparisonDataController() {
        init();
    }
       
    protected void init() {
    	navigationPane = new SpeciesComparisonNavigationPane();
    }
    
    public void setDataModel(ReactomeExpressionValue dataModel) {
    	super.setDataModel(dataModel);
    	((SpeciesComparisonNavigationPane) navigationPane).setDataModel(dataModel);
    }
    
    public void setPathwayId(Long pathwayId) {
        if (this.pathwayId == null) {
    		this.pathwayId = pathwayId;
        } else {
        	this.pathwayId = pathwayId;
        	setSpecies();
        }
    }
    
    public Map<Long, String> convertValueToColor(Map<Long, Double> compIdToValue) {
    	Map<Long, String> compIdToColor = new HashMap<Long, String>();
	
    	for (Long dbId : compIdToValue.keySet()) {
    		Double value = compIdToValue.get(dbId);
    		
    		String color;
    		if (value.intValue() == 100) {
    			color = "rgb(255, 255, 0)"; // Yellow for inference
    		} else {
    			color = "rgb(0, 0, 255)"; // Blue for no inference
    		}
    		
    		compIdToColor.put(dbId, color);
    	}
    	
    	return compIdToColor;    	
    }
    
    protected void setSpecies() {
    	((SpeciesComparisonNavigationPane) navigationPane).getDataLabel().setText(dataModel.getPathwayExpressionValue(pathwayId).getSpecies());
    	onDataPointChange(0);
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
        
        Label getDataLabel() {
			return dataLabel;
        	
        }
        
        public void setDataModel(ReactomeExpressionValue dataModel) {
        	SpeciesComparisonDataController.this.setSpecies();
        }
    }
}
