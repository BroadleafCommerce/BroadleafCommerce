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
