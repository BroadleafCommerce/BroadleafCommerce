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
package org.broadleafcommerce.core.web.api;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * JAXB has trouble mapping interfaces, therefore this allows us to find the correct implementation
 * for interfaces and set up the appropriate JAXBContext.
 * 
 * For example, a ProductSkuImpl is related to SkuImpl. If SkuImpl is subclassed with a custom implementation
 * (e.g. MyCompanySkuImpl) then MyCompanySkuImpl will be added to the JAXBContext for serialization instead
 * of SkuImpl.
 * 
 * @author phillipverheyden
 *
 */
@Component
@Provider
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class BroadleafContextResolver implements ContextResolver<JAXBContext>, InitializingBean {
	
	@Resource(name="blEntityConfiguration")
	protected EntityConfiguration ec;
	
	protected static JAXBContext context;
	
	/**
	 * getContext can pass in an implementation. Store the implementation details from the EntityConfiguration
	 */
	protected HashSet<String> entityImplementationNames = new HashSet<String>();
	
	@Override
	public JAXBContext getContext(Class<?> type) {
		if (entityImplementationNames.contains(type.getName())) {
			return context;
		}
		
		try {
			if (ec.lookupEntityClass(type.getName()) != null){
				return context;
			}
			
			return null;
			
		} catch (BeansException e) {
			//bean not found in the context
			return null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String[] entityBeans = ec.getEntityBeanNames();
		Class<?>[] classes = new Class<?>[entityBeans.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = ec.lookupEntityClass(entityBeans[i]);
		}
		
		//Go through the declared beans and only add the ones declared via JAXB annotations to the JAXB context
		ArrayList<Class<?>> xmlClasses = new ArrayList<Class<?>>();
		for (Class<?> clazz : classes) {
			if (clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class)) {
				xmlClasses.add(clazz);
				entityImplementationNames.add(clazz.getName());
			}
		}
		
		try {
			context = JAXBContext.newInstance(xmlClasses.toArray(new Class<?>[xmlClasses.size()]));
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}		
	}

}
