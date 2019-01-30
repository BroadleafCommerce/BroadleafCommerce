/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.condition;

import org.broadleafcommerce.common.module.BroadleafModuleRegistration.BroadleafModuleEnum;
import org.broadleafcommerce.common.module.ModulePresentUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects whether or not a Broadleaf module has been registered via am {@link spring.factories} entry for {@link BroadleafModuleRegistration}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Philip Baggett (pbaggett)
 * @see {@link ConditionalOnBroadleafModule}
 * @since 5.2
 */
public class OnBroadleafModuleCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnBroadleafModule.class.getName());
        List<Object> modules = attributes.get("value");
        List<Object> moduleNames = attributes.get("moduleName");
        List<String> moduleNameStrings = new ArrayList<>();
        for (int i = 0; i < modules.size(); ++i) {
            BroadleafModuleEnum module = (BroadleafModuleEnum) modules.get(i);
            String moduleName = (BroadleafModuleEnum.IGNORED != module) ? module.getName() : (String) moduleNames.get(i);
            moduleNameStrings.add(moduleName);
        }
        return ModulePresentUtil.allPresent(moduleNameStrings);
    }
}
