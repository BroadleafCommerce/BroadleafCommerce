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
 */
package org.gwtwidgets.server.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Simple spring controller that merges GWT's {@link RemoteServiceServlet}, the
 * {@link Controller} and also implements the {@link RemoteService} interface so
 * as to be able to directly delegate RPC calls to extending classes.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 */
public class GWTSpringController extends RemoteServiceServlet implements ServletContextAware, ServletConfigAware, Controller, RemoteService {
	private static final long serialVersionUID = 5399966488983189122L;

	private ServletContext servletContext;

	/**
	 * Disables HTTP response caching by modifying response headers for browsers.
	 * Can be overridden by extending classes to change behaviour.
	 * @param request
	 * @param response
	 */
	protected void preprocessHTTP(HttpServletRequest request, HttpServletResponse response){
		ServletUtils.disableResponseCaching(response);
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * Return the request which invokes the service. Valid only if used in the
	 * dispatching thread.
	 * 
	 * @return the servlet request associated with the thread servicing the current RPC 
	 * @deprecated Use {@link ServletUtils#getRequest()}
	 */
	@Deprecated
	public static HttpServletRequest getRequest() {
		return ServletUtils.getRequest();
	}

	/**
	 * Return the response which accompanies the request. Valid only if used in
	 * the dispatching thread.
	 * 
	 * @return the servlet response associated with the thread servicing the current RPC 
	 * @deprecated Use {@link ServletUtils#getResponset()}
	 */
	@Deprecated
	public static HttpServletResponse getResponse() {
		return ServletUtils.getResponse();
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			preprocessHTTP(request, response);
			ServletUtils.setRequest(request);
			ServletUtils.setResponse(response);
			doPost(request, response);
		} finally {
			ServletUtils.setRequest(null);
			ServletUtils.setResponse(null);
		}
		return null;
	}

	public void setServletConfig(ServletConfig configuration) {
		try {
			init(configuration);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
}
