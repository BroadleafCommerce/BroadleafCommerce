/**
 * 
 */
package com.gwtincubator.security.client;

import com.google.gwt.core.client.GWT;

/**
 * Utility class.
 * @author David MARTIN
 *
 */
public final class GWTUtil {

	private static final String CONTEXT_URL_REGEXP = "[http|https]+:\\/\\/[0-9a-zA-Z.]*[:]*[0-9]*";

	/**
	 * Constructor.
	 * Private, as needed for an utility class.
	 */
	private GWTUtil() {
		
	}

	/**
	 * Provide the real context URL as it seems GWT.getHostPageBaseURL() does not return what I expect...
	 * @return the web application root context URL.
	 */
	public static String getContextUrl () {
	    if (GWT.getHostPageBaseURL().equals(GWT.getModuleBaseURL())) {
	        final String ret = GWT.getHostPageBaseURL();
	        int indexLast = ret.lastIndexOf(GWT.getModuleName());
	        return ret.substring(0, indexLast).replaceAll(CONTEXT_URL_REGEXP, "");
	    } else {
	        return GWT.getHostPageBaseURL();
	    }
	}

}
