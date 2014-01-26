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
import org.reactome.diagram.client.WidgetStyle;
import org.reactome.diagram.expression.model.AnalysisType;
import org.reactome.diagram.expression.model.PathwayComponentExpressionValue;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.dom.client.Style.Cursor;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;

public class ExpressionProcessor {
	private static final String BASEURL = "/Analysis/service/analysis/";
	
	private String analysisName = "expression_analysis_with_levels";
    private String analysisId;
    private ResultStatus resultStatus;
    private String resultErrorMessage;
	private ReactomeExpressionValue expressionData; 
	private String species;
		
	public ExpressionProcessor(String analysisString) {
		initAnalysisId(analysisString);
		initResultStatus(analysisString);
	}	
    	
	private void initAnalysisId(String analysisString) {		
		final String analysisId = isLocalData(analysisString) ? analysisString : analysisString.split("\\.")[1];
		setAnalysisId(analysisId);
		if (analysisId == null)
			AlertPopup.alert("No analysis id could be obtained from the analysis string \"" + analysisString + "\"");
	}
	
	private void initResultStatus(String analysisString) {
		final ResultStatus resultStatus = isLocalData(analysisString) ? ResultStatus.FINISHED : ResultStatus.PENDING;
		setResultStatus(resultStatus);
	}

	private boolean isLocalData(String analysisString) {
		return analysisString.startsWith("http:");
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

	public ResultStatus getResultStatus() {
		return resultStatus;
	}
	
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	public ReactomeExpressionValue getExpressionData() {
		return expressionData;
	}

	public void setExpressionData(ReactomeExpressionValue expressionData) {
		this.expressionData = expressionData;
	}

	public String getSpecies() {
		return species;
	}
	
	public void setSpecies(String species) {
		this.species = species;
	}
	
	public void createDataController(final PathwayDiagramPanel diagramPane,
	                                 final AbsolutePanel contentPane, 
	                                 final ExpressionCanvas expressionCanvas) {
		if (analysisId == null)
			return;
					
		final Integer INTERVAL = 200;
		Timer timer = new Timer() {
			private Integer elapsedTime = 0;
			private DialogBox dataControllerIsLoadingAlertBox;
			
			@Override
			public void run() {
				//System.out.println(resultStatus);
				WidgetStyle.setCursor(expressionCanvas, Cursor.WAIT);
				
				elapsedTime += INTERVAL;
				if (resultStatus != ResultStatus.PENDING) {
					WidgetStyle.setCursor(expressionCanvas, Cursor.DEFAULT);
					if (dataControllerIsLoadingAlertBox != null && dataControllerIsLoadingAlertBox.isShowing())
						dataControllerIsLoadingAlertBox.hide();
					
					cancel(); // Timer repeat is cancelled
					
					if (resultStatus == ResultStatus.FINISHED)
						makeDataController(diagramPane, contentPane, expressionCanvas);
					else if (resultStatus == ResultStatus.ABORTED)
						AlertPopup.alert("Unable to show expression/inference analysis: " + resultErrorMessage);
					
					return;
				}

				if (elapsedTime >= INTERVAL * 15 && dataControllerIsLoadingAlertBox == null)
					dataControllerIsLoadingAlertBox = AlertPopup.alert("The analysis information to be overlaid on this pathway is being processed.  This may take a few minutes.");
				
				checkIfResultsReady();
			}			
		};
		
		timer.scheduleRepeating(INTERVAL);
	}
	
	private void checkIfResultsReady() {
		String url = BASEURL + "status/" + analysisId + "/" + analysisName;
		
		//System.out.println(url);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					//System.out.println("Response code is " + response.getStatusCode());
					//System.out.println("Response is " + response.getText());//
					if (response.getStatusCode() != 200) {
						resultStatus = ResultStatus.ABORTED;
						resultErrorMessage = response.getStatusCode() + "- " + response.getStatusText();
						return;
					}
					
					String statusMessage = response.getText();
					
					if (statusMessage.contains("Finished")) {
						resultStatus = ResultStatus.FINISHED;
					} else if (statusMessage.contains("Warning") || statusMessage.contains("Error")) {
						resultStatus = ResultStatus.ABORTED;
						resultErrorMessage = "Error in obtaining expression results";
					}					
				}

				@Override
				public void onError(Request request, Throwable exception) {
					resultStatus = ResultStatus.ABORTED;
					resultErrorMessage = "Error in retrieving expression result status " + exception;
				}
				
			});
		} catch (RequestException ex) {
			resultStatus = ResultStatus.ABORTED;
			resultErrorMessage = "Error in sending request for expression result status " + ex;
		}
	}
	
	private void makeDataController(final PathwayDiagramPanel diagramPane, AbsolutePanel contentPane, final ExpressionCanvas expressionCanvas) {
	    final String url = isLocalData(getAnalysisId()) ? getAnalysisId() : getResultsUrl(); 
	    
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    AlertPopup.alert("Error in retrieving expression results: " + exception);
                }
                
                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        setExpressionData(parseExpressionData((JSONObject) JSONParser.parseStrict(response.getText())));
                        
                        DataController dataController = getDataController(getExpressionData().getAnalysisType());
                        
                        if (dataController == null) {
                            AlertPopup.alert(expressionData + " is an unknown analysis type");
                            return;
                        }
                        
                        dataController.setDataModel(getExpressionData());
                        expressionCanvas.setAnalysisType(getExpressionData().getAnalysisType());
                        expressionCanvas.setDataController(dataController);
                        
                        if (dataController instanceof SpeciesComparisonDataController)
                        	((SpeciesComparisonDataController) dataController).setSpecies(getSpecies());
                        
                        diagramPane.setDataController(dataController);
                    }
                }
            });
        } 
        catch (RequestException ex) {
            AlertPopup.alert("Error in retrieving expression results: " + ex);
        } 
	}
	
	private String getResultsUrl() {
		return BASEURL + "results/" + analysisId + "/" + analysisName;
	}
		
	private ReactomeExpressionValue parseExpressionData(JSONObject jsonData) {
		ReactomeExpressionValue expressionData = new ReactomeExpressionValue();
		
		try {
			JSONObject expressionJson = getJsonObject("springModel", jsonData);
			
			expressionData.setAnalysisId(getStringFromJson("analysisId", expressionJson));
			//JSONObject experimentData = getJsonObject("table", expressionJson);

			expressionData.setMinExpression(getDoubleFromJson("minExpression", expressionJson));
			expressionData.setMaxExpression(getDoubleFromJson("maxExpression", expressionJson));
			expressionData.setDataType(getStringFromJson("dataType", expressionJson));
			expressionData.setAnalysisType(getStringFromJson("analysisType", expressionJson));
		
			JSONArray columnNamesArray = getJsonArray("expressionColumnNames", expressionJson);
			List<String> expressionColumnNames = new ArrayList<String>();
		
			for (int i = 0; i < columnNamesArray.size(); i++) {
				String columnName = getStringFromJson(i, columnNamesArray);
				expressionColumnNames.add(columnName);
			}
			expressionData.setExpressionColumnNames(expressionColumnNames);
		
			JSONArray pathways = getJsonArray("rows", expressionJson);		
			for (int i = 0; i < pathways.size(); i++) {
				PathwayExpressionValue pev = new PathwayExpressionValue();
				JSONArray pathway = getJsonArray(i, pathways);
			
				for (int j = 0; j < pathway.size(); j++) {
					JSONArray componentArray;
					
					try {
						componentArray = getJsonArray(j, pathway);						
					} catch (JSONException e) {
						continue;
					}
					
					if (componentArray.size() == 0)
						continue;
						
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
						
						if (species == null)
							species = speciesName;
						
					} else if (componentType.equals("pathway.expressionlevels")) {
						for (int k = 0; k < componentArray.size(); k++) {
							//PathwayComponentExpressionValue pcev = new PathwayComponentExpressionValue();
						
							//pcev.setDataType(componentType);
						
							JSONObject pathwayComponentValue = getJsonObject("value", getJsonObject(k, componentArray));
						
							Long dbId = getLongFromJson("DB_ID", pathwayComponentValue);
							String expressionId = getStringFromJson("ID", pathwayComponentValue);
					
							JSONArray expressionLevelsArray = getJsonArray("levels", pathwayComponentValue);
							List<Double> expressionLevels = new ArrayList<Double>();
						
							for (int l = 0; l < expressionLevelsArray.size(); l++) {
								String level = null;
								try {
									level = getStringFromJson(l, expressionLevelsArray);
									expressionLevels.add(Double.parseDouble(level));
								} catch (NumberFormatException e) {
									AlertPopup.alert(level + " can't be parsed as a numeric expression value");
								}
							}
						
							
							PathwayComponentExpressionValue pcev = pev.getExpressionValues().get(dbId);
							
							if (pcev == null) {
								pcev = new PathwayComponentExpressionValue();
								pcev.setDbId(dbId);
							}	
							
							pcev.addExpressionValues(expressionId, expressionLevels);
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
	

	private DataController getDataController(String analysisTypeString) {
		final AnalysisType analysisType = AnalysisType.getAnalysisType(analysisTypeString);
		
		if (analysisType == AnalysisType.Expression) {
			return new ExpressionDataController();
		} else if (analysisType == AnalysisType.SpeciesComparison) {
			return new SpeciesComparisonDataController();
		} else if (analysisType == AnalysisType.IdList) {
			return new IdListDataController();
		}
		
		return null;
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
	
	public enum ResultStatus {
		FINISHED,
		PENDING,
		ABORTED
	}
}
