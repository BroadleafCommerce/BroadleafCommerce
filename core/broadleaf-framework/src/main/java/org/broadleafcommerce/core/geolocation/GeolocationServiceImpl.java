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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service("blGeolocationService")
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${geolocation.api}")
    protected String selectedAPI;

    @Autowired
    protected Map<String, GeolocationAPI> geolocationMap;

    @Override
    public GeolocationDTO getLocationData(String ipAddress) {
        GeolocationAPI api = geolocationMap.get(selectedAPI);
        GeolocationDTO data = null;
        if (api != null) {
            try {
                data = api.populate(ipAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
