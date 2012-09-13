/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.util.sql;

import javax.persistence.spi.PersistenceUnitInfo;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.broadleafcommerce.common.extensibility.context.MergeFileSystemAndClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.jpa.MergePersistenceUnitManager;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.ant.ConfigurationTask;
import org.hibernate.util.ReflectHelper;
import org.xml.sax.EntityResolver;

/**
 * This is a re-worked version from Hibernate tools
 * 
 * @author jfischer
 *
 */
public class JPAConfigurationTask extends ConfigurationTask {

	private String persistenceUnit;
	private String dialect;
	
	public JPAConfigurationTask() {
		setDescription("JPA Configuration");
	}
	
	@SuppressWarnings("unchecked")
	protected Configuration createConfiguration(MergeFileSystemAndClassPathXMLApplicationContext mergeContext) {
		try {
			PersistenceUnitInfo unitInfo = ((MergePersistenceUnitManager) mergeContext.getBean("blPersistenceUnitManager")).obtainPersistenceUnitInfo(persistenceUnit);
			
			Map overrides = new HashMap();
			Properties p = getProperties();
			
			if(p!=null) {
				overrides.putAll( p );
			}
			
			overrides.put("hibernate.dialect",dialect);
			
			Class clazz = ReflectHelper.classForName("org.hibernate.ejb.Ejb3Configuration", JPAConfigurationTask.class);
			Object ejb3cfg = clazz.newInstance();
			
			if(entityResolver!=null) {
				Class resolver = ReflectHelper.classForName(entityResolver, this.getClass());
				Object object = resolver.newInstance();
				Method method = clazz.getMethod("setEntityResolver", new Class[] { EntityResolver.class });
				method.invoke(ejb3cfg, new Object[] { object } );
			}
			
			Method method = clazz.getMethod("configure", new Class[] { PersistenceUnitInfo.class, Map.class });
			if ( method.invoke(ejb3cfg, new Object[] { unitInfo, overrides } ) == null ) {
				throw new BuildException("Persistence unit not found: '" + persistenceUnit + "'.");
			}
			
			method = clazz.getMethod("getHibernateConfiguration", new Class[0]);
			return (Configuration) method.invoke(ejb3cfg, (Object[])null);
		} 
		catch(HibernateException he) {
			throw new BuildException(he);
		}
		catch(BuildException be) {
			throw be;
		}
		catch(Exception t) {
			throw new BuildException("Problems in creating a configuration for JPA. Have you remembered to add hibernate EntityManager jars to the classpath ?",t);			
		}
	}
	
	protected void doConfiguration(Configuration configuration) {
	}
	
	protected void validateParameters() throws BuildException {
	}

	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	
}
