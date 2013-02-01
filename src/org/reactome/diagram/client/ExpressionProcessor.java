/**
 * 
 * @author weiserj
 * 
 * 
 */

package org.reactome.diagram.client;

import org.reactome.diagram.model.ReactomeExpressionValue;

public class ExpressionProcessor {
	private String url;
    private String jsonData;
    private ReactomeExpressionValue expressionData; 
    		
	/**
	 * Default constructor.
	 */
	public ExpressionProcessor() {		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public ReactomeExpressionValue getExpressionData() {
		return expressionData;
	}

	public void setExpressionData(ReactomeExpressionValue expressionData) {
		this.expressionData = expressionData;
	}
	
}
