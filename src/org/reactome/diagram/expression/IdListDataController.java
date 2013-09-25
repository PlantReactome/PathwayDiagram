/*
 * Created on Sept 23, 2013
 *
 */
package org.reactome.diagram.expression;

import java.util.HashMap;
import java.util.Map;

import org.reactome.diagram.view.Parameters;

import com.google.gwt.user.client.ui.Label;

/**
 * This customized GUI is used to control the display of the presence/absence 
 * of proteins or small molecules from a user submitted id list. 
 * @author weiserj
 *
 */
public class IdListDataController extends DataController {
	
    public IdListDataController() {
        init();
    }
       
    protected void init() {
    	navigationPane = new IdListNavigationPane();
    }
     
    public Map<Long, String> convertValueToColor(Map<Long, Double> compIdToValue) {    	
    	final String YELLOW = "rgb(255, 255, 102)";
    	
    	Map<Long, String> compIdToColor = new HashMap<Long, String>();
	
    	for (Long dbId : compIdToValue.keySet()) {
    		String color = (compIdToValue.get(dbId).intValue() == 100) ? YELLOW	: Parameters.defaultExpressionColor.value();
    		
    		compIdToColor.put(dbId, color);
    	}
    	
    	return compIdToColor;
    }
    
    protected class IdListNavigationPane extends NavigationPane {
                
        public IdListNavigationPane() {
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
