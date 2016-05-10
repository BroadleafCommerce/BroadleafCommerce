/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.openadmin.web.rulebuilder.enums.RuleBuilderEnumOptionsExtensionListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * {@link ResourceResolver} for ruleBuilder-options.js. Delegates to all registered {@link RuleBuilderEnumOptionsExtensionListener}
 * to create the resource.
 * @author Reginald Cole
 * @since 4.0
 */
@Component("blRuleBuilderOptionResourceResolver")
public class RuleBuilderOptionsResourceResolver implements ResourceResolver {

    protected static final String RULE_BUILDER_OPTIONS_JS_PATH="admin/components/ruleBuilder-options.js";


    @javax.annotation.Resource(name = "blRuleBuilderEnumOptionsExtensionListeners")
    protected List<RuleBuilderEnumOptionsExtensionListener> listeners = new ArrayList<RuleBuilderEnumOptionsExtensionListener>();

    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {

        // Delegate to the chain if the request is not for ruildBuilder-options.js
        if(!requestPath.equalsIgnoreCase(RULE_BUILDER_OPTIONS_JS_PATH)){
            return chain.resolveResource(request,requestPath,locations);
        }
        // aggregate option values for all registered RuleBuilderEnumOptionsExtensionListener
        StringBuilder sb = new StringBuilder();
        for (RuleBuilderEnumOptionsExtensionListener listener : listeners) {
            sb.append(listener.getOptionValues()).append("\r\n");
        }
        return new GeneratedResource(sb.toString().getBytes(),requestPath) ;
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        if(resourcePath != RULE_BUILDER_OPTIONS_JS_PATH){
            return chain.resolveUrlPath(resourcePath,locations);
        }
        return RULE_BUILDER_OPTIONS_JS_PATH;
    }
}
