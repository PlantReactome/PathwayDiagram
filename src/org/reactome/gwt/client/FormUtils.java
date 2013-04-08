/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Utility methods for constructing HTML forms under GWT.
 * 
 * @author David Croft
 *
 */

public class FormUtils {
	/**
	 * Generic form-launching method.  The host panel is the panel to which
	 * this form will be attached, and is mandatory.You need to supply an "action" URL,
	 * which will carry out the work of the form.  Additionally, if you
	 * want to send data, this will be packed into hidden parameters.
	 * You should put these into a hash map.  If the hash map is null,
	 * then it will be assumed that there are no parameters to be sent.
	 * 
	 * @param hostPanel this panel should have a direct connection to root
	 * @param actionUrl
	 * @param params
	 */
	public static FormPanel formCreator(Panel hostPanel, String actionUrl, HashMap<String,String> params) {
		return formCreator(hostPanel, actionUrl, params, null, "_top");
	}
	
	/**
	 * Generic form-launching method.  The host panel is the panel to which
	 * this form will be attached, and is mandatory.You need to supply an "action" URL,
	 * which will carry out the work of the form.  Additionally, if you
	 * want to send data, this will be packed into hidden parameters.
	 * You should put these into a hash map.  If the hash map is null,
	 * then it will be assumed that there are no parameters to be sent.
	 * You can use the run method of the runnable object to execute
	 * additional code before the form is submitted.  The runnable may
	 * be null, in which case nothing will be done.
	 * The "target" argument allows you to specify how a new page will be opened.
	 * Possible values are:
	 * 
	 * null				Open in current window
	 * "_top"			Open outside current IFRAME but in current window
	 * "_blank"			Open in a new page
	 * "invisible"		Used to feed AJAX, not seen by the user
	 * 
	 * 
	 * @param hostPanel this panel should have a direct connection to root
	 * @param actionUrl
	 * @param params
	 * @param runnable
	 * @param target
	 */
	public static FormPanel formCreator(Panel hostPanel, String actionUrl, Map<String,String> params, Runnable runnable, String target) {
		return formCreator(hostPanel, actionUrl, params, runnable, target, FormPanel.METHOD_POST);
	}
	
	/**
	 * Generic form-launching method.  The host panel is the panel to which
	 * this form will be attached, and is mandatory.You need to supply an "action" URL,
	 * which will carry out the work of the form.  Additionally, if you
	 * want to send data, this will be packed into hidden parameters.
	 * You should put these into a hash map.  If the hash map is null,
	 * then it will be assumed that there are no parameters to be sent.
	 * You can use the run method of the runnable object to execute
	 * additional code before the form is submitted.
	 * The "target" argument allows you to specify how a new page will be opened.
	 * Possible values are:
	 * 
	 * null				Open in current window
	 * "_top"			Open outside current IFRAME but in current window
	 * "_blank"			Open in a new page
	 * 
	 * The method allows you to deliver the form's content by either get or post:
	 * 
	 * FormPanel.METHOD_GET
	 * FormPanel.METHOD_POST
	 * 
	 * @param hostPanel this panel should have a direct connection to root
	 * @param actionUrl
	 * @param params
	 * @param runnable
	 * @param target
	 * @param method
	 */
	public static FormPanel formCreator(Panel hostPanel, String actionUrl, Map<String,String> params, Runnable runnable, String target, String method) {
		return formCreator(hostPanel, actionUrl, params, runnable, target, method, false);
	}	
	
	/**
	 * Generic form-launching method.  The host panel is the panel to which
	 * this form will be attached, and is mandatory.You need to supply an "action" URL,
	 * which will carry out the work of the form.  Additionally, if you
	 * want to send data, this will be packed into hidden parameters.
	 * You should put these into a hash map.  If the hash map is null,
	 * then it will be assumed that there are no parameters to be sent.
	 * You can use the run method of the runnable object to execute
	 * additional code before the form is submitted.
	 * The "target" argument allows you to specify how a new page will be opened.
	 * Possible values are:
	 * 
	 * null				Open in current window
	 * "_top"			Open outside current IFRAME but in current window
	 * "_blank"			Open in a new page
	 * 
	 * The method allows you to deliver the form's content by either get or post:
	 * 
	 * FormPanel.METHOD_GET
	 * FormPanel.METHOD_POST
	 * 
	 * paramsInUrl is a flag; if false, any parameters supplied are sent as hidden parameters.  If true,
	 * supplied parameters are added to the URL.
	 * 
	 * @param hostPanel this panel should have a direct connection to root
	 * @param actionUrl
	 * @param params
	 * @param runnable
	 * @param target
	 * @param method
	 * @param paramsInUrl
	 */
	public static FormPanel formCreator(Panel hostPanel, String actionUrl, Map<String,String> params, Runnable runnable, String target, String method, boolean paramsInUrl) {
		FormPanel genericForm ;
		if (target!=null && target.equals("invisible"))
			genericForm = new FormPanel();
		else
			genericForm = new FormPanel(target);
		genericForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		genericForm.setMethod(method);
		if (method.equals(FormPanel.METHOD_GET))
			paramsInUrl = true;
			
		// Set parameters in a method-dependent way
		if (params != null && params.size()>0) {
			HorizontalPanel hiddenPanel = new HorizontalPanel();
			String value;
			if (paramsInUrl) {
				String paramSeparator = "?";
				if (actionUrl.indexOf("?") >= 0)
					paramSeparator = "&";
				for (String key: params.keySet()) {
					value = params.get(key);
					value = value.replaceAll("!", "%21");
					actionUrl += paramSeparator + key + "=" + value;
					if (paramSeparator.equals("?"))
						paramSeparator = "&";
				}
			}
			for (String key: params.keySet()) {
				value = params.get(key);
				hiddenPanel.add(new Hidden(key, value));
			}
			genericForm.add(hiddenPanel);
		}
		genericForm.setAction(actionUrl);
		
		if (runnable != null)
			runnable.run();
		
		hostPanel.add(genericForm);
		genericForm.submit();
		
		return genericForm;
	}
}
