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
package org.broadleafcommerce.core.web.geolocation;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.geolocation.GeolocationDTO;
import org.broadleafcommerce.core.geolocation.GeolocationService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;

@Component("blGeolocationRequestProcessor")
public class GeolocationRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String FORWARD_HEADER = "X-FORWARDED-FOR";
    public static final String GEOLOCATON_ATTRIBUTE_NAME = "_blGeolocationAttribute";

    @Resource(name="blGeolocationService")
    protected GeolocationService geolocationService;

    @Override
    public void process(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;

            GeolocationDTO location = (GeolocationDTO) BLCRequestUtils.getSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME);
            if (location == null) {
                location = geolocationService.getLocationData(getIPAddress(servletWebRequest));
                BLCRequestUtils.setSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME, location);
            }
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(GEOLOCATON_ATTRIBUTE_NAME, location);
        }
    }

    protected String getIPAddress(ServletWebRequest request) {
        String ipAddress = request.getHeader(FORWARD_HEADER);
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = request.getRequest().getRemoteAddr();
        }
        return ipAddress;
    }
}