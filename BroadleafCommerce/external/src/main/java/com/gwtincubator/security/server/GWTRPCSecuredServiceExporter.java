package com.gwtincubator.security.server;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.UnexpectedException;

/**
 * Security oriented version of George Georgovassilis GWTRPCServiceExporter.
 * @author David MARTIN
 * 
 * BroadleafCommerce
 * Changed to make compatible with Spring 3. Changed to a normal decorator pattern.
 * @author jfischer
 */
public class GWTRPCSecuredServiceExporter extends GWTRPCServiceExporter {
	
	private static final long serialVersionUID = 1L;

	/** logger. */
	private final static Log LOGGER = LogFactory.getLog(GWTRPCSecuredServiceExporter.class);
	
	private GWTRPCServiceExporter delegate;

	public GWTRPCSecuredServiceExporter(GWTRPCServiceExporter delegate) {
		this.delegate = delegate;
	}

	public void afterPropertiesSet() throws Exception {
		delegate.afterPropertiesSet();
	}

	public void destroy() {
		delegate.destroy();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public String getInitParameter(String name) {
		return delegate.getInitParameter(name);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getInitParameterNames() {
		return delegate.getInitParameterNames();
	}

	public Object getService() {
		return delegate.getService();
	}

	public ServletConfig getServletConfig() {
		return delegate.getServletConfig();
	}

	public ServletContext getServletContext() {
		return delegate.getServletContext();
	}

	public String getServletInfo() {
		return delegate.getServletInfo();
	}

	public String getServletName() {
		return delegate.getServletName();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		delegate.handleRequest(request, response);
	}

	public void init() throws ServletException {
		delegate.init();
	}

	public void init(ServletConfig config) throws ServletException {
		delegate.init(config);
	}

	public void log(String message, Throwable t) {
		delegate.log(message, t);
	}

	public void log(String msg) {
		delegate.log(msg);
	}

	public void setServletContext(ServletContext servletContext) {
		delegate.setServletContext(servletContext);
	}

	public String processCall(String payload) throws SerializationException {
		String response = null;
		final RPCRequest rpcRequest = RPC.decodeRequest(payload, null, this);
		try {
			response = delegate.processCall(payload);
		} catch (final Throwable e) { // Security Exceptions (preciousException here) are wrapped into an UnexpectedException (cause1), which is wrapped into a RuntimeException (e)...
			final Throwable cause1 = e.getCause();
			if (cause1 != null && cause1 instanceof UnexpectedException) {
				final Throwable preciousException = cause1.getCause();
				if (preciousException != null && (preciousException instanceof AccessDeniedException || preciousException instanceof AuthenticationException)) {
					String failurePayload = null;
					try {
						failurePayload = RPC.encodeResponseForFailure(
							rpcRequest.getMethod(),
							SecurityExceptionFactory.get(preciousException));
					} catch (final UnexpectedException ue) {
						LOGGER.error("You may have forgotten to add a 'throws ApplicationSecurityException' declaration to your service interface.");
						throw ue;
					}
					return failurePayload;
				}
			}
			handleOtherException(e);
		}
		return response;
	}
	
	protected void handleOtherException(final Throwable e) throws SerializationException {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else if (e instanceof SerializationException) {
			throw (SerializationException) e;
		} else {
			throw new SerializationException(e);
		}
	}

	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		delegate.service(arg0, arg1);
	}

	public void setService(Object service) {
		delegate.setService(service);
	}

	public void setServiceInterfaces(Class<RemoteService>[] serviceInterfaces) {
		delegate.setServiceInterfaces(serviceInterfaces);
	}

	public void setResponseCachingDisabled(boolean disableResponseCaching) {
		delegate.setResponseCachingDisabled(disableResponseCaching);
	}

	public void setThrowUndeclaredExceptionToServletContainer(
			boolean throwUndeclaredExceptionToServletContainer) {
		delegate.setThrowUndeclaredExceptionToServletContainer(throwUndeclaredExceptionToServletContainer);
	}

	public void setServletConfig(ServletConfig servletConfig) {
		delegate.setServletConfig(servletConfig);
	}

	public void setBeanName(String beanName) {
		delegate.setBeanName(beanName);
	}

	public String toString() {
		return delegate.toString();
	}
	
	
}
