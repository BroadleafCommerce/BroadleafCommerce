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
package org.gwtwidgets.server.spring.gilead;

import java.lang.reflect.Method;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.hibernate.HibernateUtil;
import net.sf.gilead.gwt.GileadRPCHelper;
import net.sf.gilead.gwt.GwtConfigurationHelper;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import org.gwtwidgets.server.spring.GWTRPCServiceExporter;
import org.hibernate.SessionFactory;

import com.google.gwt.user.server.rpc.RPCRequest;

/**
 * Incorporates <a
 * href="http://hibernate4gwt.sourceforge.net/">Hibernate4GWT's</a>
 * <code>HibernateBeanManager</code>. The concept is similar to
 * <code>HibernateRemoteService</code>: RPC objects are merged into the current
 * Hibernate session and are detached on their way out. Instances must be
 * provided with a <code>HibernateBeanManager</code>.
 * 
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class GileadRPCServiceExporter extends GWTRPCServiceExporter {

	// Used to store the old classloader for the duration of an invocation
	private ThreadLocal<ClassLoader> tmpClassloader;

	private boolean createSessionIfNotExists = true;

	private boolean usingProxyClassLoader = false;

	private SessionFactory sessionFactory;

	private PersistentBeanManager beanManager;

	public boolean isUsingProxyClassLoader() {
		return usingProxyClassLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (sessionFactory == null && beanManager == null)
			throw new IllegalArgumentException(
					"You must provide either a SessionFactory or a PersistentBeanManager");
		if (usingProxyClassLoader)
			tmpClassloader = new ThreadLocal<ClassLoader>();
		if (beanManager == null)
			beanManager = GwtConfigurationHelper
					.initGwtStatelessBeanManager(new HibernateUtil(
							sessionFactory));
		super.afterPropertiesSet();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PersistentBeanManager getBeanManager() {
		return beanManager;
	}

	public void setBeanManager(PersistentBeanManager beanManager) {
		this.beanManager = beanManager;
	}

	/**
	 * Specify whether to use Hibernate4GWT's ProxyClassloader (
	 * <code>true</code>) or the default class loader (<code>false</code>,
	 * default).
	 * 
	 * @param usingProxyClassLoader
	 */
	public void setUsingProxyClassLoader(boolean usingProxyClassLoader) {
		this.usingProxyClassLoader = usingProxyClassLoader;
	}

	/**
	 * Specify if a servlet session should be created if one does not already
	 * exists.
	 * 
	 * @param createSessionIfNotExists
	 *            Defaults to <code>true</code>
	 */
	public void setCreateSessionIfNotExists(boolean createSessionIfNotExists) {
		this.createSessionIfNotExists = createSessionIfNotExists;
	}

	@Override
	protected void onBeforeRequestDeserialized(String serializedRequest) {
		super.onBeforeRequestDeserialized(serializedRequest);
		if (isUsingProxyClassLoader()) {
			Thread currentThread = Thread.currentThread();
			// We are going to swap the class loader
			ClassLoader oldContextClassLoader = currentThread
					.getContextClassLoader();
			logger.info("Classloader before invocation: "
					+ oldContextClassLoader);
			logger.info("Replacing with classloader   : "
					+ getClass().getClassLoader());
			tmpClassloader.set(oldContextClassLoader);
			currentThread.setContextClassLoader(getClass().getClassLoader());

			GileadRPCHelper.initClassLoader();
			logger.info("New classloader              : "
					+ getClass().getClassLoader());
		}
	}

	@Override
	protected void onAfterResponseSerialized(String serializedResponse) {
		super.onAfterResponseSerialized(serializedResponse);
		if (isUsingProxyClassLoader()) {
			Thread currentThread = Thread.currentThread();
			ClassLoader oldContextClassLoader = tmpClassloader.get();
			currentThread.setContextClassLoader(oldContextClassLoader);
			logger.info("reinstating old classloader");
		}
	}

	@Override
	public String invokeMethodOnService(Object service, Method targetMethod,
			Object[] targetParameters, RPCRequest rpcRequest) throws Exception {

		GileadRPCHelper.parseInputParameters(rpcRequest, beanManager,
				getThreadLocalRequest().getSession(createSessionIfNotExists));
		Object result = targetMethod.invoke(service, targetParameters);
		result = GileadRPCHelper.parseReturnValue(result, beanManager);
		String encodedResult = RPC.encodeResponseForSuccess(rpcRequest
				.getMethod(), result, rpcRequest.getSerializationPolicy());
		return encodedResult;
	}

	@Override
	protected String encodeResponseForFailure(Throwable cause)
			throws SerializationException {
		Throwable throwable = (Throwable) GileadRPCHelper.parseReturnValue(
				cause, beanManager);
		return super.encodeResponseForFailure(throwable);
	}

	@Override
	protected String encodeResponseForFailure(RPCRequest rpcRequest,
			Throwable cause) throws SerializationException {
		Throwable throwable = (Throwable)GileadRPCHelper.parseReturnValue(
				cause, beanManager);
		return super.encodeResponseForFailure(rpcRequest, throwable);
	}

}
