/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package org.gwtwidgets.server.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class that allows access to the invoking servlet request and
 * response, which are stored in a thread local variable of the invoking thread.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class ServletUtils {
	private static ThreadLocal<HttpServletRequest> servletRequest = new ThreadLocal<HttpServletRequest>();

	private static ThreadLocal<HttpServletResponse> servletResponse = new ThreadLocal<HttpServletResponse>();

	/**
	 * Adjusts HTTP headers so that browsers won't cache response.
	 * @param response
	 * For more background see <a href="http://www.onjava.com/pub/a/onjava/excerpt/jebp_3/index2.html">this</a>.
	 */
	public static void disableResponseCaching(HttpServletResponse response) {
		response.setHeader("Expires", "Sat, 1 January 2000 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
	}

	/**
	 * Return the request which invokes the service. Valid only if used in the
	 * dispatching thread.
	 * 
	 * @return the servlet request
	 */
	public static HttpServletRequest getRequest() {
		return servletRequest.get();
	}

	/**
	 * Return the response which accompanies the request. Valid only if used in
	 * the dispatching thread.
	 * 
	 * @return the servlet response
	 */
	public static HttpServletResponse getResponse() {
		return servletResponse.get();
	}

	/**
	 * Assign the current servlet request to a thread local variable. Valid only
	 * if used inside the invoking thread scope.
	 * 
	 * @param request
	 */
	public static void setRequest(HttpServletRequest request) {
		servletRequest.set(request);
	}

	/**
	 * Assign the current servlet response to a thread local variable. Valid
	 * only if used inside the invoking thread scope.
	 * 
	 * @param response
	 */
	public static void setResponse(HttpServletResponse response) {
		servletResponse.set(response);
	}

}
