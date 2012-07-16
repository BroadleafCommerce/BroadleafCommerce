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

package org.broadleafcommerce.openadmin.security;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.UnexpectedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;

/**
 * Inspired by GWTRPCSecuredServiceExporter by David Martin http://code.google.com/p/gwt-incubator-lib/
 * 
 * @author jfischer
 */
public class CompatibleGWTSecuredRPCServiceExporter extends GWTRPCServiceExporter {

	/** serialVersionUID */
	private static final long serialVersionUID = 2733022902422767233L;
	private static Log LOGGER = LogFactory.getLog(CompatibleGWTSecuredRPCServiceExporter.class);

	@SuppressWarnings("rawtypes")
	public void setServiceInterfaces(final Class[] serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getServiceInterfaces() {
		return this.serviceInterfaces;
	}

	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public void setCompressResponse(final int compressResponse) {
		this.compressResponse = compressResponse;
	}

	public int getCompressResponse() {
		return this.compressResponse;
	}

	public void setDisableResponseCaching(final boolean disableResponseCaching) {
		this.disableResponseCaching = disableResponseCaching;
	}

	public boolean getDisableResponseCaching() {
		return this.disableResponseCaching;
	}

	public void setMethodCache(final Map<Method, Method> methodCache) {
		this.methodCache = methodCache;
	}

	public Map<Method, Method> getMethodCache() {
		return this.methodCache;
	}

	/**
	 * Wrap the original method in order to detect a Spring Security specific exception and manage it the way we want.
	 * @param payload 
	 * @see org.gwtwidgets.server.spring.GWTRPCServiceExporter#processCall(java.lang.String)
	 */
	@Override
	public String processCall(final String payload) throws SerializationException {
		String response = null;
		// reported as not working with GWT.1.6.4 : Issue 2
//		final RPCRequest rpcRequest = RPC.decodeRequest(payload);
        final RPCRequest rpcRequest;
        try {
            rpcRequest = RPC.decodeRequest(payload, null, this);
        } catch (RuntimeException e) {
            LOGGER.error("Could not decode the request", e);
            throw e;
        }
		try {
			response = super.processCall(payload);
		} catch (final Throwable e) { // Security Exceptions (preciousException here) are wrapped into an UnexpectedException (cause1), which is wrapped into a RuntimeException (e)...
			LOGGER.error("Problem processing call", e);
			final Throwable cause1 = e.getCause();
			if (cause1 != null && cause1 instanceof UnexpectedException) {
				final Throwable preciousException = cause1.getCause();
				if (preciousException != null && (preciousException instanceof AccessDeniedException || preciousException instanceof AuthenticationException)) {
					return processException(preciousException, rpcRequest);
				} else if (preciousException != null && (preciousException instanceof TransactionSystemException)) {
					return processException(((TransactionSystemException) preciousException).getApplicationException(), rpcRequest);
				}
			}
			return processException(e, rpcRequest);
		}
		if(response!=null && response.startsWith("//EX")) {
			//BLC-604... For now lets display the raw message
			return   RPC.encodeResponseForFailure(
					rpcRequest.getMethod(),new com.gwtincubator.security.exception.AccessDeniedException(response));
		}
		return response;
	}
	
	protected String processException(final Throwable e, final RPCRequest rpcRequest) throws SerializationException {
		String failurePayload = null;
		try {
			failurePayload = RPC.encodeResponseForFailure(
				rpcRequest.getMethod(),
				SecurityExceptionFactory.get(e));
		} catch (final UnexpectedException ue) {
			LOGGER.error("You may have forgotten to add a 'throws ApplicationSecurityException' declaration to your service interface.");
			throw ue;
		}
		return failurePayload;
	}
	
}
