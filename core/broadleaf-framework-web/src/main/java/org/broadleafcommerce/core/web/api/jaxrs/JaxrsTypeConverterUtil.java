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
