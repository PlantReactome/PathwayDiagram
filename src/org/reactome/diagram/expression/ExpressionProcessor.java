/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.expression;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.client.ExpressionCanvas;

import org.reactome.diagram.expression.model.PathwayComponentExpressionValue;
import org.reactome.diagram.expression.model.PathwayExpressionValue;
import org.reactome.diagram.expression.model.ReactomeExpressionValue;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class ExpressionProcessor {
	private String url;
    private JSONObject jsonData;
    private ReactomeExpressionValue expressionData; 
    
	/**
	 * Default constructor.
	 */
	public ExpressionProcessor() {		

	}

	public ExpressionProcessor(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JSONObject getJsonData() {
		return jsonData;
	}

	public ReactomeExpressionValue getExpressionData() {
		return expressionData;
	}

	public void setExpressionData(ReactomeExpressionValue expressionData) {
		this.expressionData = expressionData;
	}

	public void displayExpressionData(final AbsolutePanel contentPane, final DataController dataController, final ExpressionCanvas expressionCanvas) {
		if (url == null)
			return;
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
	
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,	Response response) {
					if (response.getStatusCode() == 200) {
						jsonData = (JSONObject) JSONParser.parseStrict(response.getText());
						expressionData = parseExpressionData();
						//expressionCanvas.setAnalysisType(expressionData.getAnalysisType());
						dataController.setDataModel(expressionData);
						dataController.display(contentPane,
								    expressionCanvas.getCoordinateSpaceWidth(),
								    expressionCanvas.getCoordinateSpaceHeight());
					} else {
						Window.alert("Unable to retrieve expression data - " + response.getStatusText());
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					Window.alert("No response from server");
				}
			
			});
		} catch (RequestException e) {
			Window.alert("Error sending request for expression data");		
		}
		
	}
	
	private ReactomeExpressionValue parseExpressionData() {
		ReactomeExpressionValue expressionData = new ReactomeExpressionValue();
		
		expressionData.setAnalysisId(getStringFromJson("analysisId", jsonData));
		expressionData.setAnalysisType(getStringFromJson("analysisType", jsonData));
		
		JSONObject experimentData = jsonData.get("table").isObject();

		expressionData.setMinExpression(experimentData.get("minExpression").isNumber().doubleValue());
		expressionData.setMaxExpression(experimentData.get("maxExpression").isNumber().doubleValue());
		
		
		JSONArray columnNamesArray = experimentData.get("expressionColumnNames").isArray();
		List<String> expressionColumnNames = new ArrayList<String>();
		
		for (int i = 0; i < columnNamesArray.size(); i++) {
			String columnName = columnNamesArray.get(i).isString().stringValue();
			expressionColumnNames.add(columnName);
		}
		expressionData.setExpressionColumnNames(expressionColumnNames);
		
		JSONArray pathways = experimentData.get("rows").isArray();		
		for (int i = 0; i < pathways.size(); i++) {
			PathwayExpressionValue pev = new PathwayExpressionValue();
			JSONArray pathway = pathways.get(i).isArray();
			
			for (int j = 0; j < pathway.size(); j++) {
				JSONArray componentArray = pathway.get(j).isArray();
				
				JSONObject componentObject = componentArray.get(0).isObject();
				String componentType = getStringFromJson("type", componentObject);
				
				if (componentType.equals("pathway.expression")) {
					JSONObject valueObject = componentObject.get("value").isObject();
					
					String pathwayName = getStringFromJson("name", valueObject);
					Long pathwayId = Long.parseLong(getStringFromJson("DB_ID", valueObject));
				
					pev.setPathway(pathwayName);
					pev.setPathwayId(pathwayId);					
				} else if (componentType.equals("instance")) {
					JSONObject valueObject = componentObject.get("value").isObject();
					
					String speciesName = getStringFromJson("name", valueObject);
					Long speciesId = Long.parseLong(getStringFromJson("DB_ID", valueObject));
					
					pev.setSpecies(speciesName);
					pev.setSpeciesId(speciesId);
				} else if (componentType.equals("pathway.expressionlevels")) {
					for (int k = 0; k < componentArray.size(); k++) {
						PathwayComponentExpressionValue pcev = new PathwayComponentExpressionValue();
						
						//pcev.setDataType(componentType);
						
						JSONObject pathwayComponentValue = componentArray.get(k).isObject().get("value").isObject();
						
						Long dbId = Long.parseLong(getStringFromJson("DB_ID", pathwayComponentValue));
						String expressionId = getStringFromJson("ID", pathwayComponentValue);
					
						JSONArray expressionLevelsArray = pathwayComponentValue.get("levels").isArray();
						List<Double> expressionLevels = new ArrayList<Double>();
						
						for (int l = 0; l < expressionLevelsArray.size(); l++) {
							Double value = new Double(100.0); 
									
							//		Double.parseDouble(expressionLevelsArray.get(l).isString().stringValue());
							expressionLevels.add(value);
						}
						
						pcev.setDbId(dbId);
						pcev.setExpressionId(expressionId);
						pcev.setValues(expressionLevels);
						pev.getExpressionValues().put(dbId, pcev);
					}
				} else {
					Window.alert("Unknown type -- " + componentType);
				}				
			}
			expressionData.getPathwayExpressionValues().put(pev.getPathwayId(), pev);
		}
		// Make sure the parsed data is correct
		if (!expressionData.validateExpressionData() && expressionData.getAnalysisType().equals("expression")) {
		    Window.alert("Some pathway object has not enough expression values!");
		    //return null;
		}
		return expressionData;		
	}
	
	private String getStringFromJson(String key, JSONObject json) {
		return json.get(key).isString().stringValue();
	}
	
	public void showJsonKeys(JSONObject o) {
		for (String key : o.keySet())
				System.out.println(key);	
	}
}
