package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * {@link ResourceResolver} for ruleBuilder-options.js. Delegates to all registered {@link RuleBuilderEnumOptionsExtensionListener}
 * to create the resource.
 * @author Reginald Cole
 * @since 4.0
 */
@Component("blBLCRuleBuilderOptionResourceResolver")
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
