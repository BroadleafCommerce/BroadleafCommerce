/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

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
        return createGeneratedResource(ruleBuilderEnumOptions.getOptionValues(), path, true);
    }

}
