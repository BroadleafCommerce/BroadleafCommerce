/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.openadmin.web.rulebuilder.enums.RuleBuilderEnumOptionsExtensionListener;
import org.springframework.core.io.Resource;
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
