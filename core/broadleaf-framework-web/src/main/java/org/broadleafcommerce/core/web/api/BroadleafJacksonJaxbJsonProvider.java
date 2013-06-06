/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.util.StdDateFormat;
import org.springframework.context.annotation.Scope;

import java.text.DateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

/**
 * By default, the JacksonJaxbJsonProvider does not serialize dates as a string, rather as a 
 * long value (millis from Epoch).  This overrides the constructor of that class and forces 
 * date serialization as an ISO 8601 date format.
 * 
 * @author Kelly Tisdell
 *
 */
@Scope("singleton")
@Provider
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON })
public class BroadleafJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {

    public BroadleafJacksonJaxbJsonProvider() {
        super(null, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
        //Thread safety of this DateFormat is handled within Jackson, as it clones this 
        //DateFormat before using it.
        DateFormat fmt = (DateFormat) StdDateFormat.getBlueprintISO8601Format().clone();
        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().withDateFormat(fmt);
        mapper.getDeserializationConfig().withDateFormat(fmt);
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setVisibility(JsonMethod.ALL, Visibility.ANY);
        super.setMapper(mapper);
    }

}
