/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gwtwidgets.server.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet Filter that applies any HTTP header to a matched URL. Init parameters
 * are HTTP header-value pairs. Will silently fail when the ServletResponse does
 * not cast to a {@link HttpServletResponse}. A special init-parameter is
 * 'ResponseHeaderFilter.UrlPattern': it is not set as an HTTP header but is
 * rather a regular expression which can be used to further refine URLs on which
 * the filter should match. Headers containing an empty value are removed from
 * the response, even if they already exist there.
 * 
 * Inspired by a very <a
 * href="http://www.onjava.com/pub/a/onjava/2004/03/03/filters.html">worthreading
 * article</a> from Jayson Falkner.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class ResponseHeaderFilter implements Filter {

	public final static String URL_PATTERN = "ResponseHeaderFilter.UrlPattern";

	private String[] headers = new String[0];

	private String[] values = new String[0];

	private Log logger = LogFactory.getLog(getClass());

	private Pattern urlPattern = Pattern.compile(".*?");

	private void setupMatcher(String pattern) {
		urlPattern = Pattern.compile(pattern);
		logger.debug("Matching " + pattern);
	}


	private void addHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.setBufferSize(0);
		String query = request.getRequestURI();
		Matcher urlMatcher = urlPattern.matcher(query);
		if (!urlMatcher.matches())
			return;
		for (int i = 0; i < headers.length; i++)
			response.setHeader(headers[i], values[i]);
	}

	@SuppressWarnings("unchecked")
	private int getSize(Enumeration e) {
		int size = 0;
		for (; e.hasMoreElements(); e.nextElement(), size++)
			;
		return size;
	}

	public void destroy() {
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (response instanceof HttpServletResponse)
			addHeaders((HttpServletRequest) request, (HttpServletResponse) response);
		chain.doFilter(request, response);
	}

	@SuppressWarnings("unchecked")
	public void init(FilterConfig conf) throws ServletException {
		int size = getSize(conf.getInitParameterNames());
		if (conf.getInitParameter(URL_PATTERN) != null) {
			size--;
			setupMatcher(conf.getInitParameter(URL_PATTERN));
		}
		headers = new String[size];
		values = new String[size];
		if (size == 0) {
			logger.warn("Instantiated ResponseHeaderFilter without header mappings");
			return;
		}
		logger.debug("ResponseHeaderFilter header mappings:");
		Enumeration e = conf.getInitParameterNames();
		for (int i = 0; i < size;) {
			String header = e.nextElement().toString();
			if (URL_PATTERN.equals(header)) continue;
			headers[i] = header;
			values[i] = conf.getInitParameter(headers[i]);
			if ("".equals(values[i])) values[i]=null;
			logger.debug(headers[i] + " = " + values[i]);
			i++;
		}
	}
}
