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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

@Service("blGeolocationService")
public class GeolocationServiceImpl implements GeolocationService {

    private static final Log LOG = LogFactory.getLog(GeolocationServiceImpl.class);

    @Resource
    protected Environment env;

    @Autowired(required = false)
    protected Map<String, GeolocationAPI> geolocationMap = new HashMap<>();

    @Override
    public GeolocationDTO getLocationData(String ipAddress) {
        GeolocationAPI api = getGeolocationAPI();
        GeolocationDTO data = null;
        if (api != null) {
            data = api.getLocationData(ipAddress);
        }
        return data;
    }

    protected GeolocationAPI getGeolocationAPI() {
        String selectedAPI = env.getProperty("geolocation.api");
        GeolocationAPI api = geolocationMap.get(selectedAPI);

        if (api == null) {
            LOG.warn("A Geolocation API implementation could not be found for the provided resource name: '" + selectedAPI + "'. " +
                    "Please configure the 'geolocation.api' property to be the name of a registered GeolocationAPI implementation.");
        }

        return api;
    }
}
