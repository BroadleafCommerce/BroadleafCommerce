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
package org.broadleafcommerce.core.web.api.jaxrs;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.UriInfo;

/**
 * Utility to convert from JAXRS types into something Spring-MVC compatible
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @deprecated along with the other JAXRS components, this is deprecated in favor of using Spring MVC for REST services
 */
@Deprecated
public class JaxrsTypeConverterUtil {

    /**
     * Converts a the given JAXRS {@link UriInfo} into something that works for Spring MVC
     * 
     * @param uriInfo
     * @return
     */
    public static MultiValueMap<String, String> convertJaxRSUriInfoToParameterMap(UriInfo uriInfo) {
        LinkedMultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
        for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue());
        }
        return paramMap;
    }

}
