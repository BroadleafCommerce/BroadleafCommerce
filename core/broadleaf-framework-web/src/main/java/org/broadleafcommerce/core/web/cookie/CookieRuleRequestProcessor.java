/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.cookie;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.security.util.CookieUtils;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.rule.RuleDTOConfig;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Populate configured cookie values on the http request thread for use by MVEL request-based rules
 * </p>
 * Configuration is generally as easy as enabling the feature
 * via a property and then configuring one or more cookie configurations.
 * </p>
 * Add {@code cookie.content.targeting.enabled=true} to a property file visible to both admin and site (i.e. common-shared.properties)
 * </p>
 * Add a cookie configuration to your Spring xml or Java configuration. Sample below demonstrated Java-based config:
 * {@code
 *    @Merge("blCookieRuleConfigs")
 *    public RuleDTOConfig myCookieRuleDTOConfig() {
 *        RuleDTOConfig config = new RuleDTOConfig("myFieldName", "myLabel");
 *        config.setAlternateName("cookieName");
 *        return config;
 *    }
 * }
 * @author Jeff Fischer
 */
public class CookieRuleRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String FORWARD_HEADER = "X-FORWARDED-FOR";
    public static final String COOKIE_ATTRIBUTE_NAME = "_blCookieAttribute";
    protected static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    protected CookieUtils cookieUtils;
    protected List<RuleDTOConfig> configs;

    public CookieRuleRequestProcessor(List<RuleDTOConfig> configs, CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
        this.configs = configs;
    }

    @Override
    public void process(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            Map proxy = (Map) BLCRequestUtils.getSessionAttributeIfOk(request, COOKIE_ATTRIBUTE_NAME);
            if (proxy == null) {
                proxy = getVals(servletWebRequest);
                BLCRequestUtils.setSessionAttributeIfOk(request, COOKIE_ATTRIBUTE_NAME, proxy);
            }
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(COOKIE_ATTRIBUTE_NAME, proxy);

            Map<String, Object> ruleMap = getRuleMapFromRequest(request);
            ruleMap.put(COOKIE_ATTRIBUTE_NAME, proxy);
            request.setAttribute(BLC_RULE_MAP_PARAM, ruleMap, WebRequest.SCOPE_REQUEST);
        }
    }

    protected Map<String,Object> getRuleMapFromRequest(WebRequest request) {
        Map<String,Object> ruleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM, WebRequest.SCOPE_REQUEST);
        if (ruleMap == null) {
            ruleMap = new HashMap<>();
        }
        return ruleMap;
    }

    protected Map<String, String> getVals(ServletWebRequest request) {
        Map<String, String> vals = new HashMap<String, String>();
        for (RuleDTOConfig config : configs) {
            if (config.getAlternateName() != null) {
                String val = cookieUtils.getCookieValue(request.getRequest(), config.getAlternateName());
                if (!StringUtils.isEmpty(val)) {
                    vals.put(config.getFieldName(), val);
                }
            }
        }
        return vals;
    }
}
