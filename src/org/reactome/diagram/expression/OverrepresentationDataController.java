/*
 * Created on Sept 23, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.analysis.model.AnalysisResult;
import org.reactome.diagram.model.CanvasPathway;

import com.google.gwt.user.client.ui.Label;

/**
 * This customized GUI is used to control the display of the presence/absence 
 * of proteins or small molecules from a user submitted id list. 
 * @author weiserj
 *
 */
public class OverrepresentationDataController extends DataController {
	
    public OverrepresentationDataController(AnalysisResult analysisResult) {
    	super(analysisResult);
    	navigationPane = new OverrepresentationNavigationPane();
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
    	
    	Map<Long, String> compIdToColor = new HashMap<Long, String>();
	
    	for (Long dbId : compIdToValue.keySet()) {
    		//String color = (compIdToValue.get(dbId).intValue() == 100) ? YELLOW	: Parameters.defaultExpressionColor.value();
    		
    		compIdToColor.put(dbId, YELLOW);
    	}
    	
    	return compIdToColor;
    }
    
    protected class OverrepresentationNavigationPane extends NavigationPane {
                
        public OverrepresentationNavigationPane() {
            super();
            init();
        }
        
        protected void init() {	        	
        	dataLabel.setText("Molecule Overlay");
        	addDataLabel();
        	addCloseButton();
        	installHandlers();    
        } 
        
        protected Label getDataLabel() {
			return dataLabel;
        }
    }
}
