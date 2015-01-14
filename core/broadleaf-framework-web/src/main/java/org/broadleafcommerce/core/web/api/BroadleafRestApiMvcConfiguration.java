/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.List;

import javax.annotation.Resource;

/**
 * Default Broadleaf-recommended configuration for REST APIs. Recommended use is to extend this class and annotate
 * your extension with {@code @Configuration} and {@link @EnableWebMvc}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafRestApiMvcConfiguration extends WebMvcConfigurerAdapter {

    @Resource(name = "blWrapperOverrideTypeModifier")
    protected WrapperOverrideTypeModifier typeModifier;
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(getObjectMapper(false)));
        converters.add(new MappingJackson2XmlHttpMessageConverter(getObjectMapper(true)));
    }
    
    protected ObjectMapper getObjectMapper(boolean useXml) {
        Jackson2ObjectMapperBuilder builder = getBuilder();
        TypeFactory factory = TypeFactory.defaultInstance().withModifier(typeModifier);
        if (useXml) {
            return builder.createXmlMapper(true).build().setTypeFactory(factory);
        } else {
            return builder.build().setTypeFactory(factory);
        }
    }
    
    protected Jackson2ObjectMapperBuilder getBuilder() {
        return new Jackson2ObjectMapperBuilder()
            // Ensure JAXB annotations get picked up
            .findModulesViaServiceLoader(true)
            // Enable/disable some features
            .featuresToEnable(new Object[]{DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY})
            .featuresToDisable(new Object[]{SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED});
    }

}
