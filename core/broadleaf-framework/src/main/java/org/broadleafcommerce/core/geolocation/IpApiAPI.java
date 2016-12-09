/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.geolocation;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component("blIpApiAPI")
public class IpApiAPI implements GeolocationAPI {

    public static final String JSON_API_URL = "http://ip-api.com/json/";
    public static final String FRIENDLY_NAME = "IP API";

    @Override
    public GeolocationDTO populate(String address) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = objectMapper.readValue(new URL(JSON_API_URL + address), HashMap.class);

        GeolocationDTO geolocationDTO = new GeolocationDTO();
        geolocationDTO.setSource(FRIENDLY_NAME);
        geolocationDTO.setIpAddress((String) response.get("query"));
        geolocationDTO.setCountryCode((String) response.get("countryCode"));

        return geolocationDTO;
    }
}
