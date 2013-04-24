/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.client.AlertPopup;
import org.reactome.diagram.client.ExpressionCanvas;
import org.reactome.diagram.client.PathwayDiagramPanel;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.PathwayComponentExpressionValue;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class ExpressionProcessor {
	private String analysisName = "expression_analysis_with_levels";
    private String analysisId;
	private ReactomeExpressionValue expressionData; 
    
    public ExpressionProcessor(String analysisString) {
    	String[] analysisParams = analysisString.split("\\.");    	
    	//this.analysisName = analysisParams[0];
    	this.analysisId = analysisParams[1];
    }	
    	
	
	public String getAnalysisName() {
		return analysisName;
	}


	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}


	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public ReactomeExpressionValue getExpressionData() {
		return expressionData;
	}

	public void setExpressionData(ReactomeExpressionValue expressionData) {
		this.expressionData = expressionData;
	}

	public void createDataController(final PathwayDiagramPanel diagramPane,
	                                 AbsolutePanel contentPane, 
	                                 final ExpressionCanvas expressionCanvas) {
		if (analysisId == null)
			return;
		
		// Create a simple URL call
		String url = "ReactomeGWT/service/analysis/results/" + analysisId + "/" + analysisName;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    AlertPopup.alert("Error in retrieving expression results: " + exception);
                }
                
                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {                	
                        JSONValue jsonObj = JSONParser.parseStrict(response.getText());
                        expressionData = parseExpressionData((JSONObject)jsonObj);
                        
                        DataController dataController;
                        String analysisType = expressionData.getAnalysisType();
                        
                        if (analysisType.equals("expression")) {
                            expressionCanvas.setAnalysisType(AnalysisType.Expression);
                            dataController = new ExpressionDataController();
                        } else if (analysisType.equals("species_comparison")) {
                            expressionCanvas.setAnalysisType(AnalysisType.SpeciesComparison);
                            dataController = new SpeciesComparisonDataController();
                        } else {
                            AlertPopup.alert(analysisType + " is an unknown analysis type");
                            return;
                        }
                        
                        dataController.setDataModel(expressionData);
                        diagramPane.setDataController(dataController);
                    }
                }
            });
        } 
        catch (RequestException ex) {
            AlertPopup.alert("Error in retrieving expression results: " + ex);
        } 
	}
	
	private ReactomeExpressionValue parseExpressionData(JSONObject jsonData) {
		ReactomeExpressionValue expressionData = new ReactomeExpressionValue();
		
		try {
			JSONObject expressionJson = getJsonObject("springModel", jsonData);
			
			expressionData.setAnalysisId(getStringFromJson("analysisId", expressionJson));
			JSONObject experimentData = getJsonObject("table", expressionJson);

			expressionData.setMinExpression(getDoubleFromJson("minExpression", experimentData));
			expressionData.setMaxExpression(getDoubleFromJson("maxExpression", experimentData));
			expressionData.setAnalysisType(getStringFromJson("analysisType", experimentData));
		
			JSONArray columnNamesArray = getJsonArray("expressionColumnNames", experimentData);
			List<String> expressionColumnNames = new ArrayList<String>();
		
			for (int i = 0; i < columnNamesArray.size(); i++) {
				String columnName = getStringFromJson(i, columnNamesArray);
				expressionColumnNames.add(columnName);
			}
			expressionData.setExpressionColumnNames(expressionColumnNames);
		
			JSONArray pathways = getJsonArray("rows", experimentData);		
			for (int i = 0; i < pathways.size(); i++) {
				PathwayExpressionValue pev = new PathwayExpressionValue();
				JSONArray pathway = getJsonArray(i, pathways);
			
				for (int j = 0; j < pathway.size(); j++) {
					JSONArray componentArray = getJsonArray(j, pathway);
					
					JSONObject componentObject = getJsonObject(0, componentArray);
					String componentType = getStringFromJson("type", componentObject);
				
					if (componentType.equals("pathway.expression")) {
						JSONObject valueObject = getJsonObject("value", componentObject);
					
						String pathwayName = getStringFromJson("name", valueObject);
						Long pathwayId = getLongFromJson("DB_ID", valueObject);
				
						pev.setPathway(pathwayName);
						pev.setPathwayId(pathwayId);					
					} else if (componentType.equals("instance")) {
						JSONObject valueObject = getJsonObject("value", componentObject);
					
						String speciesName = getStringFromJson("name", valueObject);
						Long speciesId = getLongFromJson("DB_ID", valueObject);
					
						pev.setSpecies(speciesName);
						pev.setSpeciesId(speciesId);
					} else if (componentType.equals("pathway.expressionlevels")) {
						for (int k = 0; k < componentArray.size(); k++) {
							PathwayComponentExpressionValue pcev = new PathwayComponentExpressionValue();
						
							//pcev.setDataType(componentType);
						
							JSONObject pathwayComponentValue = getJsonObject("value", getJsonObject(k, componentArray));
						
							Long dbId = getLongFromJson("DB_ID", pathwayComponentValue);
							String expressionId = getStringFromJson("ID", pathwayComponentValue);
					
							JSONArray expressionLevelsArray = getJsonArray("levels", pathwayComponentValue);
							List<Double> expressionLevels = new ArrayList<Double>();
						
							for (int l = 0; l < expressionLevelsArray.size(); l++) {
								Double value = Double.parseDouble(getStringFromJson(l, expressionLevelsArray));
								expressionLevels.add(value);						
							}
						
							pcev.setDbId(dbId);
							pcev.setExpressionId(expressionId);
							pcev.setValues(expressionLevels);
							pev.getExpressionValues().put(dbId, pcev);
						}
					} else {
						AlertPopup.alert("Unknown type -- " + componentType);
					}					
				}
				expressionData.getPathwayExpressionValues().put(pev.getPathwayId(), pev);
			}
		} catch (JSONException e) {
			AlertPopup.alert(e.getMessage());
		}
		
		// Make sure the parsed data is correct
		if (!expressionData.validateExpressionData() && expressionData.getAnalysisType().equals("expression")) {
		    AlertPopup.alert("Some pathway object has not enough expression values!");
		    return null;
		}
		return expressionData;		
	}
	
	private JSONValue getJsonValue(Object key, JSONValue json) throws JSONException {
		JSONValue jsonValue = null;
		if (json instanceof JSONObject)
			jsonValue = ((JSONObject) json).get((String) key);
		else if (json instanceof JSONArray)
			jsonValue = ((JSONArray) json).get((Integer) key);
			
			
		if (jsonValue == null) {
			throw new JSONException(key + " does not exist in the JSONObject provided");
		}
		
		return jsonValue;
	}
	
	private JSONObject getJsonObject(Object key, JSONValue json) throws JSONException {
		JSONObject jsonObject = getJsonValue(key, json).isObject();
		if (jsonObject == null) {
			throw new JSONException(key + " is not a JSONObject");
		}
		
		return jsonObject;				
	}
	
	private JSONArray getJsonArray(Object key, JSONValue json) throws JSONException {
		JSONArray jsonArray = getJsonValue(key, json).isArray();
		if (jsonArray == null) {
			throw new JSONException(key + " is not a JSONArray");
		}
		
		return jsonArray;		
	}
	
	private String getStringFromJson(Object key, JSONValue json) throws JSONException {
		JSONString jsonString = getJsonValue(key, json).isString();
		if (jsonString == null) {
			throw new JSONException(key + " is not a string");
		}		
		
		return jsonString.stringValue();
	}
	
	private Double getDoubleFromJson(Object key, JSONValue json) throws JSONException {
		JSONNumber jsonNumber = getJsonValue(key, json).isNumber();
		if (jsonNumber == null) {
			throw new JSONException(key + " is not a number");
		}
		
		return jsonNumber.doubleValue();		
	}
	
	private Long getLongFromJson(Object key, JSONValue json) throws JSONException {
		String doubleValue = getStringFromJson(key, json);
		
		try {
			return Long.parseLong(doubleValue);
		} catch (NumberFormatException e) {
			throw new JSONException(key + " is not a long value", e);
		}
	}
	
	public void showJsonKeys(JSONObject o) {
		for (String key : o.keySet())
				System.out.println(key);	
	}
}
