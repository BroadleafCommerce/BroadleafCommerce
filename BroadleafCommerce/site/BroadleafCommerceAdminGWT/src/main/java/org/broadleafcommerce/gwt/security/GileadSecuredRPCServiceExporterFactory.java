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
package org.broadleafcommerce.gwt.security;

import net.sf.gilead.core.PersistentBeanManager;

import org.gwtwidgets.server.spring.RPCServiceExporter;
import org.gwtwidgets.server.spring.RPCServiceExporterFactory;
import org.hibernate.SessionFactory;

/**
 * 
 * @author jfischer
 */
public class GileadSecuredRPCServiceExporterFactory implements RPCServiceExporterFactory {
	
	private boolean createSessionIfNotExists = true;

	private boolean usingProxyClassLoader = false;

	private SessionFactory sessionFactory;

	private PersistentBeanManager beanManager;

	public void setCreateSessionIfNotExists(boolean createSessionIfNotExists) {
		this.createSessionIfNotExists = createSessionIfNotExists;
	}

	public boolean isUsingProxyClassLoader() {
		return usingProxyClassLoader;
	}

	public void setUsingProxyClassLoader(boolean usingProxyClassLoader) {
		this.usingProxyClassLoader = usingProxyClassLoader;
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

	public RPCServiceExporter create() {
		GileadSecuredRPCServiceExporter exporter = new GileadSecuredRPCServiceExporter();
		exporter.setBeanManager(beanManager);
		exporter.setSessionFactory(sessionFactory);
		exporter.setCreateSessionIfNotExists(createSessionIfNotExists);
		exporter.setUsingProxyClassLoader(usingProxyClassLoader);
		return exporter;
	}
}
