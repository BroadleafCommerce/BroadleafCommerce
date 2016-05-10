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
package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.web.resource.AbstractGeneratedResourceHandler;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Generated resource handler for blc-rulebuilder-options.js.
 * 
 * Delegates to all registered {@link RuleBuilderEnumOptionsExtensionListener} to create the resource
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blRuleBuilderEnumOptionsResourceHandler")
public class RuleBuilderEnumOptionsResourceHandler extends AbstractGeneratedResourceHandler {
    
    @javax.annotation.Resource(name = "blRuleBuilderEnumOptionsExtensionManager")
    protected RuleBuilderEnumOptionsExtensionManager ruleBuilderEnumOptions;
    
    @Override
    public boolean canHandle(String path) {
        return "admin/components/ruleBuilder-options.js".equals(path);
    }

    @Override
    public Resource getFileContents(String path, List<Resource> locations) {
        return new GeneratedResource(ruleBuilderEnumOptions.getOptionValues().getBytes(), path);
    }

    @Override
    public boolean isCachedResourceExpired(GeneratedResource cachedResource, String path, List<Resource> locations) {
        return false;
    }

}
