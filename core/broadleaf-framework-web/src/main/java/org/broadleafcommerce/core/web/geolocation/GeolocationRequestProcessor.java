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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

@Component("blGeolocationRequestProcessor")
public class GeolocationRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String FORWARD_HEADER = "X-FORWARDED-FOR";
    public static final String GEOLOCATON_ATTRIBUTE_NAME = "_blGeolocationAttribute";
    protected static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    @Resource(name="blGeolocationService")
    protected GeolocationService geolocationService;

    @Resource
    protected Environment env;

    @Override
    public void process(WebRequest request) {
        if (isGeolocationEnabled()) {
            if (request instanceof ServletWebRequest) {
                ServletWebRequest servletWebRequest = (ServletWebRequest) request;
                GeolocationDTO location = (GeolocationDTO) BLCRequestUtils.getSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME);
                if (location == null) {
                    String ipAddress = getIPAddress(servletWebRequest);
                    location = geolocationService.getLocationData(ipAddress);
                    BLCRequestUtils.setSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME, location);
                }
                BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(GEOLOCATON_ATTRIBUTE_NAME, location);

                Map<String, Object> ruleMap = getRuleMapFromRequest(request);
                ruleMap.put(GEOLOCATON_ATTRIBUTE_NAME, location);
                request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap, WebRequest.SCOPE_REQUEST);
            }
        }
    }

    protected boolean isGeolocationEnabled() {
        return env.getProperty("geolocation.api.enabled", Boolean.class, false);
    }

    protected String getIPAddress(ServletWebRequest request) {
        String ipAddress = request.getHeader(FORWARD_HEADER);
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = request.getRequest().getRemoteAddr();
        }
        return ipAddress;
    }

    protected Map<String,Object> getRuleMapFromRequest(WebRequest request) {
        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM, WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            ruleMap = new HashMap<>();
        }
        return ruleMap;
    }
}