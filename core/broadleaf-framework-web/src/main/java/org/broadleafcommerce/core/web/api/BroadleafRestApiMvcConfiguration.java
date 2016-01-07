/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

/**
 * <p>Default Broadleaf-recommended configuration for REST APIs. Recommended use is to extend this class and annotate
 * your extension with {@code @Configuration} and {@link @EnableWebMvc}
 *
 * <p>The child class must also be added as the {@code contextConfigLocation} for a new {@link org.springframework.web.servlet.DispatcherServlet}
 * servlet with {@code contextClass} {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext} in web.xml.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafRestApiMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(getJsonConverter());
        converters.add(getXmlConverter());
    }
    
    /**
     * Setup a simple strategy: use all the defaults and return JSON by default when not sure. 
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
        
    protected HttpMessageConverter<?> getJsonConverter() { 
        return new MappingJackson2HttpMessageConverter(getObjectMapper(false));
    }
    
    /**
     * Subclasses might override this method to use JAXB natively for XML serialization by
     * {@code return new Jaxb2RootElementHttpMessageConverter()}
     * @see {@link #getObjectMapper(boolean)}
     */
    protected HttpMessageConverter<?> getXmlConverter() {
        return new MappingJackson2XmlHttpMessageConverter(getObjectMapper(true));
    }
    
    protected ObjectMapper getObjectMapper(boolean useXml) {
        Jackson2ObjectMapperBuilder builder = getObjectMapperBuilder();
        TypeFactory factory = TypeFactory.defaultInstance().withModifier(blWrapperOverrideTypeModifier());
        if (useXml) {
            return builder.createXmlMapper(true).build().setTypeFactory(factory);
        } else {
            return builder.build().setTypeFactory(factory);
        }
    }
    
    protected Jackson2ObjectMapperBuilder getObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
            // Ensure JAXB annotations get picked up
            .findModulesViaServiceLoader(true)
            // Enable/disable some features
            .featuresToEnable(new Object[]{DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY})
            .featuresToDisable(new Object[]{SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED});
    }

    @Bean
    protected WrapperOverrideTypeModifier blWrapperOverrideTypeModifier() {
        return new WrapperOverrideTypeModifier();
    }

}
