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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.util.ImmutableCopyMap;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.RPC;

/**
 * This component publishes an object (see {@link #setService(Object)}) as a
 * service to the GWT RPC protocol. Service targets can be:<p/>
 * <ul>
 * <li>POJOs which don't have to extend any class or implement any interface.
 * However you should provide a service interface (see
 * {@link #setServiceInterfaces(Class[])})</li>
 * <li>POJOs which implement {@link RemoteService}</li>
 * <li>You can extend the GWTRPCServiceExporter which assigns the target
 * service to itself.</li>
 * </ul>
 * <p/>
 * Exceptions directly thrown from the target service are propagated back to the
 * client. For special exception handling you can override the various <code>handle</code>*
 * methods which are invoked by the GWTRPCServiceExporter.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * @author Max Jonas Werner
 */
public class GWTRPCServiceExporter extends RemoteServiceServlet implements RPCServiceExporter, ServletContextAware, ServletConfigAware, BeanNameAware {

	/**
	 * Disable RPC response compression. Value is 0.
	 */
	public final static int COMPRESSION_DISABLED = 0;

	/**
	 * Leave default RPC response compression behavior. Value is 1.
	 */

	public final static int COMPRESSION_AUTO = 1;

	/**
	 * Force compression of all RPC responses. Value is 2.
	 */
	public final static int COMPRESSION_ENABLED = 2;

	protected Log logger = LogFactory.getLog(getClass());

	protected Class<?>[] serviceInterfaces;

	protected Object service = this;

	protected ServletContext servletContext;

	protected int compressResponse = COMPRESSION_AUTO;
	
	protected boolean disableResponseCaching = false;
	
	protected boolean throwUndeclaredExceptionToServletContainer = false;
	
	protected String beanName = "GWTRPCServiceExporter";
	
	@Override
	protected void doUnexpectedFailure(Throwable e) {
		super.doUnexpectedFailure(e);
		if (throwUndeclaredExceptionToServletContainer)
			throw new RuntimeException(e);
	}

	/*
	 * Concurrent put/get invocations are reasonably safe in this use case on an
	 * ImmutableCopyMap.
	 */
	protected Map<Method, Method> methodCache = new ImmutableCopyMap<Method, Method>();

	/**
	 * Disables HTTP response caching by modifying response headers for browsers.
	 * Can be overridden by extending classes to change behaviour.
	 * @param request
	 * @param response
	 */
	protected void preprocessHTTP(HttpServletRequest request, HttpServletResponse response){
		if (disableResponseCaching)
			ServletUtils.disableResponseCaching(response);
	}

	/**
	 * Implementation of {@link ServletContextAware}, is invoked by the Spring
	 * application context.
	 * 
	 * @param servletContext
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Returns the servlet context
	 * @return {@link ServletContext}
	 */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	protected void onAfterResponseSerialized(String serializedResponse) {
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC response: [" + serializedResponse + "]");
	}

	@Override
	protected void onBeforeRequestDeserialized(String serializedRequest) {
		if (logger.isTraceEnabled())
			logger.trace("Serialised RPC request: [" + serializedRequest + "]");
	}

	/**
	 * Handles method invocation on a service and is invoked by
	 * {@link #processCall(String)}.
	 * 
	 * @param service
	 *            Service to invoke method on
	 * @param targetMethod
	 *            Method to invoke.
	 * @param targetParameters
	 *            Parameters to pass to method. Can be null for no arguments.
	 * @param rpcRequest
	 *            RPCRequest instance for this request
	 * @return Return RPC encoded result.
	 * @throws Exception
	 */
	protected String invokeMethodOnService(Object service, Method targetMethod, Object[] targetParameters,
			RPCRequest rpcRequest) throws Exception {
		Object result = targetMethod.invoke(service, targetParameters);
		String encodedResult = RPC.encodeResponseForSuccess(rpcRequest.getMethod(), result, rpcRequest
				.getSerializationPolicy());
		return encodedResult;
	}

	/**
	 * Handles an exception which is raised when a method invocation with bad
	 * arguments is attempted. This implementation throws a
	 * {@link SecurityException}. For details on arguments please consult
	 * {@link #invokeMethodOnService(Object, Method, Object[], RPCRequest)}.
	 * 
	 * @param e
	 *            Exception thrown
	 * @param service
	 * @param targetMethod
	 * @return RPC encoded response (such as an RPC client exception)
	 */
	protected String handleIllegalArgumentException(IllegalArgumentException e, Object service, Method targetMethod,
			RPCRequest rpcRequest) {
		SecurityException securityException = new SecurityException("Blocked attempt to invoke method " + targetMethod);
		securityException.initCause(e);
		throw securityException;
	}

	/**
	 * Handles an exception which is raised when a method access is attempted to
	 * a method which is not part of the RPC interface. This method is invoked
	 * by {@link #processCall(String)}. This implementation throws a
	 * {@link SecurityException}. For details on arguments please consult
	 * {@link #invokeMethodOnService(Object, Method, Object[], RPCRequest)}.
	 * 
	 * @param e
	 *            Exception thrown
	 * @param service
	 * @param targetMethod
	 * @return RPC encoded response (such as an RPC client exception)
	 */
	protected String handleIllegalAccessException(IllegalAccessException e, Object service, Method targetMethod,
			RPCRequest rpcRequest) {
		SecurityException securityException = new SecurityException("Blocked attempt to access inaccessible method "
				+ targetMethod + (service != null ? " on service " + service : ""));
		securityException.initCause(e);
		throw securityException;
	}
	
	/**
	 * Wrapper around RPC utility invocation
	 * @param rpcRequest RPCRequest
	 * @param cause Exception to handle
	 * @return RPC payload
	 * @throws Exception
	 */
	protected String encodeResponseForFailure(RPCRequest rpcRequest, Throwable cause) throws SerializationException{
		return RPC.encodeResponseForFailure(rpcRequest.getMethod(), cause, rpcRequest
				.getSerializationPolicy());
	}

	/**
	 * Wrapper around RPC utility invocation
	 * @param cause Exception to handle
	 */
	protected String encodeResponseForFailure(Throwable cause) throws SerializationException{
		return RPC.encodeResponseForFailure(null, cause);
	}

	/**
	 * Handles exceptions thrown by the target service, which are wrapped in
	 * {@link InvocationTargetException}s due to invocation by reflection. This
	 * method is invoked by {@link #processCall(String)}. This implementation
	 * encodes exceptions as RPC errors and returns them. For details on
	 * arguments please consult
	 * {@link #invokeMethodOnService(Object, Method, Object[], RPCRequest)}.
	 * 
	 * @param e
	 *            Exception thrown
	 * @param service
	 * @param targetMethod
	 * @param rpcRequest
	 * @return RPC payload
	 * @throws Exception
	 */
	protected String handleInvocationTargetException(InvocationTargetException e, Object service, Method targetMethod,
			RPCRequest rpcRequest) throws Exception {
		Throwable cause = e.getCause();
		if (!(cause instanceof RuntimeException))
			logger.warn(cause);
		return encodeResponseForFailure(rpcRequest, cause);
	}

	/**
	 * Handles exceptions thrown during a service invocation that are not
	 * handled by other exception handlers. {@link #processCall(String)} on
	 * exceptions which have escaped the other exception handlers such as
	 * {@link #handleIllegalAccessException(IllegalAccessException, Object, Method, RPCRequest)}
	 * etc. This implementation re-casts 'e'. For details on arguments please
	 * consult
	 * {@link #invokeMethodOnService(Object, Method, Object[], RPCRequest)}.
	 * 
	 * @param e
	 *            Exception thrown
	 * @param service
	 * @param targetMethod
	 * @param rpcRequest
	 * @return RPC payload
	 * @throws Exception
	 */
	protected String handleServiceException(Exception e, Object service, Method targetMethod, RPCRequest rpcRequest)
			throws Exception {
		throw e;
	}

	/**
	 * Handles {@link UndeclaredThrowableException}s which are thrown by the
	 * target service. This method is invoked by
	 * {@link #processCall(String)}. This implementation re-casts 'e'. For
	 * details on arguments please consult
	 * {@link #invokeMethodOnService(Object, Method, Object[], RPCRequest)}.
	 * 
	 * @param e
	 *            Exception thrown
	 * @param service
	 * @param targetMethod
	 * @param rpcRequest
	 * @return RPC payload
	 * @throws Exception
	 */
	protected String handleUndeclaredThrowableException(Exception e, Object service, Method targetMethod,
			RPCRequest rpcRequest) throws Exception {
		throw e;
	}

	/**
	 * Returns method to invoke on service. This implementation calls
	 * {@link ReflectionUtils#getRPCMethod(Object, Class[], Method)}
	 * 
	 * @param decodedMethod
	 *            Method as determined by RPC
	 * @return Method to invoke.
	 * @throws NoSuchMethodException
	 */
	protected Method getMethodToInvoke(Method decodedMethod) throws NoSuchMethodException {
		// Synchronization is unnecessary here, the worst thing that can happen
		// is that a method key is put multiple times in a map, which still
		// would leave only the latest addition. After a short time of operation
		// the method cache should get no writes at all, which makes the
		// immutable copy pattern a good choice.
		Method method = methodCache.get(decodedMethod);
		if (method != null)
			return method;
		method = ReflectionUtils.getRPCMethod(service, serviceInterfaces, decodedMethod);
		return methodCache.put(decodedMethod, method);
	}
	

	/**
	 * Overridden from {@link RemoteServiceServlet} and invoked by the servlet
	 * code.
	 */
	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			// Copy & pasted & edited from the GWT 1.4.3 RPC documentation
			RPCRequest rpcRequest = RPC.decodeRequest(payload, null, this);
			onAfterRequestDeserialized(rpcRequest);
			Method targetMethod = getMethodToInvoke(rpcRequest.getMethod());
			Object[] targetParameters = rpcRequest.getParameters();

			try {
				return invokeMethodOnService(service, targetMethod, targetParameters, rpcRequest);
			} catch (IllegalArgumentException e) {
				return handleIllegalArgumentException(e, service, targetMethod, rpcRequest);
			} catch (IllegalAccessException e) {
				return handleIllegalAccessException(e, service, targetMethod, rpcRequest);
			} catch (InvocationTargetException e) {
				return handleInvocationTargetException(e, service, targetMethod, rpcRequest);
			} catch (UndeclaredThrowableException e) {
				return handleUndeclaredThrowableException(e, service, targetMethod, rpcRequest);
			} catch (Exception e) {
				return handleServiceException(e, service, targetMethod, rpcRequest);
			}
		} catch (IncompatibleRemoteServiceException e) {
			return handleIncompatibleRemoteServiceException(e);
		} catch (Exception e) {
			return handleExporterProcessingException(e);
		}
	}

	/**
	 * Invoked by {@link #processCall(String)} when RPC throws an
	 * {@link IncompatibleRemoteServiceException}. This implementation
	 * propagates the exception back to the client via RPC.
	 * 
	 * @param e
	 *            Exception thrown
	 * @return RPC encoded failure response
	 * @throws SerializationException
	 */
	protected String handleIncompatibleRemoteServiceException(IncompatibleRemoteServiceException e)
			throws SerializationException {
		logger.warn(e.getMessage());
		return encodeResponseForFailure(null, e);
	}

	/**
	 * Invoked by {@link #processCall(String)} for an exception if no suitable
	 * exception handler was found. This is the outermost exception handler,
	 * catching any exceptions not caught by other exception handlers or even
	 * thrown by those handlers. This implementation wraps 'e' in a
	 * {@link RuntimeException} which is then thrown.
	 * 
	 * @param e
	 * @return RPC encoded failure response
	 */
	protected String handleExporterProcessingException(Exception e) {
		throw new RuntimeException(e);
	}

	/**
	 * Set the wrapped service bean. RPC requests are decoded and the corresponding
	 * method of the service object is invoked.
	 * 
	 * @param service Service to which the decoded requests are forwarded
	 */
	public void setService(Object service) {
		this.service = service;
	}

	/**
	 * Implementation of inherited interface
	 * @see {@link HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)}
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			preprocessHTTP(request, response);
			ServletUtils.setRequest(request);
			ServletUtils.setResponse(response);
			doPost(request, response);
		} finally {
			ServletUtils.setRequest(null);
			ServletUtils.setResponse(null);
		}
	}

	/**
	 * Specifies the interfaces which must be implemented by the service bean.
	 * If not specified then any interface extending {@link RemoteService} which
	 * is implemented by the service bean is assumed. Implementation note:
	 * as methods are only lazily bound to the service implementation you may get
	 * away with mismatches between the specified interfaces and the actual implementation
	 * as long as no method is invoked which has a different/missing signature in the interface
	 * and the service implementation.
	 * 
	 * @param serviceInterfaces
	 */
	public void setServiceInterfaces(Class<RemoteService>[] serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}

	/**
	 * Should be invoked after all properties have been set. Normally invoked
	 * by the Spring application context setup.
	 * @see InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (service == null)
			throw new Exception("You must specify a service object.");
		if (serviceInterfaces == null) {
			logger.debug("Discovering service interfaces");
			serviceInterfaces = ReflectionUtils.getExposedInterfaces(service.getClass());
			if (serviceInterfaces.length == 0)
				logger.warn("The specified service does neither implement RemoteService "
						+ "nor were any service interfaces specified. RPC access to *all* object methods is allowed.");
		}
		if (servletContext == null){
			logger.warn("No servlet context found. You should declare a GWTRPCServiceExporter, GileadRPCServiceExporter or GWTHandler in a servlet context and not the application context.");
		}
	}

	/**
	 * Return target service. Each {@link GWTRPCServiceExporter} has a single
	 * target service which it redirects RPC to.
	 * 
	 * @return Object
	 */
	public Object getService() {
		return service;
	}

	@Override
	protected boolean shouldCompressResponse(HttpServletRequest request, HttpServletResponse response,
			String responsePayload) {
		switch (compressResponse) {
		case COMPRESSION_DISABLED:
			return false;
		case COMPRESSION_ENABLED:
			return true;
		}
		return super.shouldCompressResponse(request, response, responsePayload);
	}

	/**
	 * Enables or disables compression of RPC output. Defaults to
	 * {@link #COMPRESSION_AUTO}. Allowed values are
	 * {@link #COMPRESSION_ENABLED}, {@link #COMPRESSION_DISABLED} and
	 * {@link #COMPRESSION_AUTO}.
	 * 
	 * @param compressResponse
	 */
	protected void setCompressResponse(int compressResponse) {
		if (compressResponse != COMPRESSION_ENABLED && compressResponse != COMPRESSION_DISABLED && compressResponse != COMPRESSION_AUTO)
			throw new IllegalArgumentException("Invalid compressResponse argumnet "+compressResponse);
		this.compressResponse = compressResponse;
	}

	/**
	 * Can be used to set HTTP response headers that explicitly disable caching on the browser side.
	 * Note that due to the additional headers the response size increases.
	 * @param responseCaching
	 */
	public void setResponseCachingDisabled(boolean disableResponseCaching) {
		this.disableResponseCaching = disableResponseCaching;
	}

	/**
	 * When enabled will throw exceptions which originate from the service and have not been
	 * declared in the RPC interface back to the servlet container.
	 * @param throwUndeclaredExceptionToServletContainer Defaults to <code>false</code> 
	 */
	public void setThrowUndeclaredExceptionToServletContainer(boolean throwUndeclaredExceptionToServletContainer) {
		this.throwUndeclaredExceptionToServletContainer = throwUndeclaredExceptionToServletContainer;
	}

	/**
	 * Setter for servlet configuration
	 */
	public void setServletConfig(ServletConfig servletConfig) {
		try {
			init(servletConfig);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	@Override
	public String toString() {
		return beanName;
	}

}