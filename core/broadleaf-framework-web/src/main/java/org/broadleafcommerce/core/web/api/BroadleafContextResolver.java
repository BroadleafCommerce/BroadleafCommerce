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
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * JAXB is used as a mapping and marshalling framework for XML and JSON associated with RESTful services.
 * The classes that we care about marshalling and unmarshalling should be configured in Broadleaf's
 * EntityConfiguration.  They should also be annotated with @XMLRootElement.
 * 
 * This class is a Spring-managed Bean.  It is also discoverable by the JAXRS framework as it is annotated
 * with @Provider.  This class will provide a JAXBContext for classes configured in the EntityConfiguration
 * that have @XMLRootElement annotations.  It will return a null context for classes that to not match those
 * requirements.
 * 
 * @author phillipverheyden
 *
 */
@Component
@Provider
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class BroadleafContextResolver implements ContextResolver<JAXBContext>, InitializingBean, ApplicationContextAware {
	
    protected ApplicationContext applicationContext;
    
	protected static JAXBContext context;
	
	/**
	 * getContext can pass in an implementation. Store the implementation details from the EntityConfiguration
	 */
	protected Map<String, Object> apiWrappers;
	
	@Override
	public JAXBContext getContext(Class<?> type) {
		if (apiWrappers.containsKey(type.getName())) {
			return context;
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		apiWrappers = applicationContext.getBeansWithAnnotation(XmlRootElement.class);
        Set<String> keySet = apiWrappers.keySet();
        
		Class<?>[] classes = new Class<?>[keySet.size()];
        int count = 0;
		for (String key : keySet) {
			classes[count] = apiWrappers.get(key).getClass();
            count++;
		}

        context = JAXBContext.newInstance(classes);
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
