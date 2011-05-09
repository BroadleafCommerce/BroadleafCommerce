/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The GWTHandler implements a Spring {@link HandlerMapping} which maps RPC from
 * URLs to {@link RemoteService} implementations. It does so by wrapping service
 * beans with a {@link GWTRPCServiceExporter} dynamically proxying all
 * {@link RemoteService} interfaces implemented by the service and delegating
 * RPC to these interfaces to the service. It is possible to use custom
 * implementations of the {@link GWTRPCServiceExporter}, see
 * {@link #setServiceExporterFactory(RPCServiceExporterFactory)}. Will also pick
 * up any beans with an {@link GWTRequestHandlerMapping} annotation and publish
 * it under the specified URL.
 * 
 * 
 * @author John Chilton
 * @author George Georgovassilis, g.georgovassilis[at]gmail.com
 * 
 */
public class GWTHandler extends AbstractUrlHandlerMapping implements
		HandlerMapping, InitializingBean, ServletContextAware,
		ServletConfigAware {

	// temporary mapping, void after bean initialisation
	private Map<String, Object> _mapping = new HashMap<String, Object>();

	protected RPCServiceExporterFactory factory;
	protected boolean disableResponseCaching = false;
	protected boolean throwUndeclaredExceptionToServletContainer = false;
	protected boolean scanParentApplicationContext = false;
	protected ServletConfig servletConfig;

	/**
	 * Scans the application context and its parents for service beans that
	 * implement the {@link GWTRequestMapping}
	 * 
	 * @param appContext
	 *            Application context
	 */
	private void scanForAnnotatedBeans(final ApplicationContext appContext) {
		if (appContext == null) {
			return;
		}
		for (String beanName : appContext
				.getBeanNamesForType(RemoteService.class)) {
			Object service = appContext.getBean(beanName);
			if (service == null)
				continue;
			final Class<?> beanClass = service.getClass();

			final GWTRequestMapping requestMapping = ReflectionUtils
					.findAnnotation(beanClass, GWTRequestMapping.class);
			if (requestMapping == null) {
				continue;
			}

			// Create serviceExporter to bind to
			String mapping = requestMapping.value();
			if (getMappings().containsKey(mapping))
				logger.warn("Bean '" + mapping
						+ "' already in mapping, skipping.");
			else
				getMappings().put(mapping, service);
		}
		if (scanParentApplicationContext)
			scanForAnnotatedBeans(appContext.getParent());
	}

	/**
	 * Recursively scan the parent application contexts for annotated beans to
	 * publish. Beans from applications contexts that are lower in the hierarchy
	 * overwrite beans found in parent application contexts.
	 * 
	 * @param scanParentApplicationContext
	 *            Defaults to <code>false</code>
	 */
	public void setScanParentApplicationContext(
			boolean scanParentApplicationContext) {
		this.scanParentApplicationContext = scanParentApplicationContext;
	}

	private RPCServiceExporter initServiceInstance(RPCServiceExporter exporter,
			Object service, Class<RemoteService>[] serviceInterfaces) {
		try {
			exporter.setResponseCachingDisabled(disableResponseCaching);
			exporter.setServletContext(getServletContext());
			exporter.setServletConfig(servletConfig);
			exporter.setService(service);
			exporter.setServiceInterfaces(serviceInterfaces);
			exporter
					.setThrowUndeclaredExceptionToServletContainer(throwUndeclaredExceptionToServletContainer);
			exporter.afterPropertiesSet();
			return exporter;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> getMappings() {
		return _mapping;
	}

	/**
	 * Set a mapping between URLs and services
	 * 
	 * @param mapping
	 */
	public void setMappings(Map<String, Object> mapping) {
		this._mapping = mapping;
	}

	/**
	 * Invoked automatically by Spring after initialisation.
	 */
	public void afterPropertiesSet() throws Exception {
		if (factory == null)
			factory = new DefaultRPCServiceExporterFactory();
		scanForAnnotatedBeans(getApplicationContext());
		for (Map.Entry<String, Object> entry : _mapping.entrySet()) {
			RPCServiceExporter exporter = factory.create();
			registerHandler(entry.getKey(), initServiceInstance(exporter, entry
					.getValue(), ReflectionUtils.getExposedInterfaces(entry
					.getValue().getClass())));
		}
		this._mapping = null;
	}

	/**
	 * Optionally, a {@link RPCServiceExporterFactory} can be injected if a
	 * different implementation or setup is required. Note that after
	 * initialization, the following sequence of invocations will be performed
	 * on the {@code serviceExporter} :<br>
	 * <br>
	 * <code>
	 * 		exporter.setServletContext();<br>
	 * 		exporter.setService();<br>
	 * 		exporter.setServiceInterfaces();<br>
	 * 		exporter.afterPropertiesSet();<br>
	 *</code>
	 * 
	 * @param factory
	 */
	public void setServiceExporterFactory(RPCServiceExporterFactory factory) {
		this.factory = factory;
	}

	/**
	 * Can be used to explicitly disable caching of RPC responses in the client
	 * by modifying the HTTP headers of the response.
	 * 
	 * @param disableResponseCaching
	 */
	public void setDisableResponseCaching(boolean disableResponseCaching) {
		this.disableResponseCaching = disableResponseCaching;
	}

	/**
	 * @see {@link RPCServiceExporter#setThrowUndeclaredExceptionToServletContainer(boolean)}
	 * @param throwUndeclaredExceptionToServletContainer
	 */
	public void setThrowUndeclaredExceptionToServletContainer(
			boolean throwUndeclaredExceptionToServletContainer) {
		this.throwUndeclaredExceptionToServletContainer = throwUndeclaredExceptionToServletContainer;
	}

	/**
	 * Setter for servlet configuration
	 */
	public void setServletConfig(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

}
