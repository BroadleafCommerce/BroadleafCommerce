/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.web.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule.Priority;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * <p>
 * Customized provider for a Jackson ObjectMapper that ensures single-element objects are serialized in JSON array syntax
 * both on Serialization and Deserialization.
 * 
 * <p>
 * This also ensures that the {@link JaxbAnnotationModule} is registered with the {@link ObjectMapper}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
//This class MUST be a singleton Spring Bean
@Provider
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON })
@Component("blObjectMapperProvider")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ObjectMapperProvider implements ContextResolver<ObjectMapper>, InitializingBean {

    private static final Log LOG = LogFactory.getLog(ObjectMapperProvider.class);
    
    @Resource(name = "blWrapperOverrideTypeModifier")
    protected WrapperOverrideTypeModifier typeModifier;
    
    protected ObjectMapper mapper;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // serializing to single-element arrays is enabled by default but just in case they change this in the future...
        mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        
        // Register the JAXB annotation module 
        JaxbAnnotationModule jaxbModule = new JaxbAnnotationModule();
        // Make sure that JAXB is the primary serializer (technically the default behavior but let's be explicit)
        jaxbModule.setPriority(Priority.PRIMARY);
        mapper.registerModule(new JaxbAnnotationModule());
        
        mapper.setTypeFactory(TypeFactory.defaultInstance().withModifier(typeModifier));
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

}
