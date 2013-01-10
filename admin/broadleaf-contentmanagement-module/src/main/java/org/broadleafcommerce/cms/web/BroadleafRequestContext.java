/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.cms.web;


import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafRequestContext {
    
    private static final ThreadLocal<BroadleafRequestContext> BROADLEAF_REQUEST_CONTEXT = new ThreadLocal<BroadleafRequestContext>();
    
    public static BroadleafRequestContext getBroadleafRequestContext() {
        return BROADLEAF_REQUEST_CONTEXT.get();
    }
    
    public static void setBroadleafRequestContext(BroadleafRequestContext broadleafRequestContext) {
        BROADLEAF_REQUEST_CONTEXT.set(broadleafRequestContext);
    }
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private SandBox sandbox;
    private Locale locale;
    private String requestURIWithoutContext;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public SandBox getSandbox() {
        return sandbox;
    }

    public void setSandbox(SandBox sandbox) {
        this.sandbox = sandbox;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getRequestURIWithoutContext() {
        return requestURIWithoutContext;
    }

    public void setRequestURIWithoutContext(String requestURIWithoutContext) {
        this.requestURIWithoutContext = requestURIWithoutContext;
    }
}
