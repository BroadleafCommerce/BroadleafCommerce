/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.jaxrs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.web.api.WrapperOverrideTypeModifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Conditional;
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
 * Customized provider for a Jackson ObjectMapper that ensures singleelement objects are serialized in JSON array syntax
 * both on Serialization and Deserialization.
 * 
 * <p>
 * This also ensures that the {@link JaxbAnnotationModule} is registered with the {@link ObjectMapper}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @deprecated along with the other JAXRS components, this is deprecated in favor of using Spring MVC for REST services
 */
@Provider
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON })
@Component("blJaxrsObjectMapperProvider")
@Conditional(IsJaxrsAvailableCondition.class)
@Deprecated
public class JaxrsObjectMapperProvider implements ContextResolver<ObjectMapper>, InitializingBean {

    private static final Log LOG = LogFactory.getLog(JaxrsObjectMapperProvider.class);
    
    @Resource(name = "blWrapperOverrideTypeModifier")
    protected WrapperOverrideTypeModifier typeModifier;
    
    protected ObjectMapper mapper;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // serializing to singleelement arrays is enabled by default but just in case they change this in the future...
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
