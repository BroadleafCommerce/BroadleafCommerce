/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Generic interface that should be used for processing requests from Servlet Filters, Spring interceptors or Portlet
 * filters. Note that the actual type of the request passed in should be something that extends {@link NativeWebRequest}.
 * 
 * Example usage by a Servlet Filter:
 * 
 * <pre>
 *   public class SomeServletFilter extends GenericFilterBean {
 *      &#064;Resource(name="blCustomerStateRequestProcessor")
 *      protected BroadleafWebRequestProcessor processor;
 *      
 *      public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
 *          processor.process(new ServletWebRequest(request, response));
 *      }
 *   }
 * </pre>
 * 
 * <p>Also note that you should always instantiate the {@link WebRequest} with as much information available. In the above
 * example, this means using both the {@link HttpServletRequest} and {@link HttpServletResponse} when instantiating the
 * {@link ServletWebRequest}</p>
 * 
 * @author Phillip Verheyden
 * @see {@link NativeWebRequest}
 * @see {@link ServletWebRequest}
 * @see {@link org.springframework.web.portlet.context.PortletWebRequest}
 * @see {@link BroadleafRequestFilter}
 */
public interface BroadleafWebRequestProcessor {

    /**
     * Process the current request. Examples would be setting the currently logged in customer on the request or handling
     * anonymous customers in session
     * 
     * @param request
     */
    public void process(WebRequest request);
    
}
