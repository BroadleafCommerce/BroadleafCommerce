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

package org.broadleafcommerce.common.web;


import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;

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
    private Site site;
    private Theme theme;
    public java.util.Locale javaLocale;

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

    public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
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
    
    /**
     * Returns the java.util.Locale constructed from the org.broadleafcommerce.common.locale.domain.Locale.
     * @return
     */
    public java.util.Locale getJavaLocale() {
    	if (this.javaLocale == null) {
    		this.javaLocale = convertLocaleToJavaLocale();
    	}
    	return this.javaLocale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.javaLocale = convertLocaleToJavaLocale();
    }

    public String getRequestURIWithoutContext() {
    	if (request.getContextPath() != null) {
    		return request.getRequestURI().substring(request.getContextPath().length());
    	} else {
    		return request.getRequestURI();
    	}
    }
    
    private java.util.Locale convertLocaleToJavaLocale() {    	
    	if (locale == null || locale.getLocaleCode() == null) {
    		return null;
        } else {
        	String localeString = locale.getLocaleCode();
	        String[] components = localeString.split("_");
        	if (components.length == 1) {
        		return new java.util.Locale(components[0]);
        	} else if (components.length == 2) {
        		return new java.util.Locale(components[0], components[1]);
        	} else if (components.length == 3) {
        		return new java.util.Locale(components[0], components[1], components[2]);
        	}
    		return null;	    	
    	}
    }
    
    public boolean isSecure() {
        boolean secure = false;
        if (request != null) {
             secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
        }
        return secure;
    }
    
    public boolean isProductionSandbox() {
        return (sandbox == null || SandBoxType.PRODUCTION.equals(sandbox.getSandBoxType()));
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
