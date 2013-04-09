package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

import org.broadleafcommerce.openadmin.web.resource.AbstractGeneratedResourceHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Generated resource handler for blc-rulebuilder-options.js.
 * 
 * Delegates to all registered {@link RuleBuilderEnumOptionsExtensionListener} to create the resource
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blRuleBuilderEnumOptionsResourceHandler")
public class RuleBuilderEnumOptionsResourceHandler extends AbstractGeneratedResourceHandler {
    
    @Resource(name = "blRuleBuilderEnumOptionsExtensionManager")
    protected RuleBuilderEnumOptionsExtensionListener ruleBuilderEnumOptions;
    
    @Override
    public String getHandledFileName() {
        return "/js/admin/blc-rulebuilder-options.js";
    }

    @Override
    public String getFileContents() {
        return ruleBuilderEnumOptions.getOptionValues();
    }

}
