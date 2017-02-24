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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.module.BroadleafModuleRegistration;
import org.broadleafcommerce.common.module.BroadleafModuleRegistration.BroadleafModuleEnum;
import org.broadleafcommerce.common.module.ModulePresentUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * Detects whether or not a Broadleaf module has been registered via am {@link spring.factories} entry for {@link BroadleafModuleRegistration}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ConditionalOnBroadleafModule}
 * @since 5.2
 */
public class OnBroadleafModuleCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnBroadleafModule.class.getName());
        BroadleafModuleEnum module = (BroadleafModuleEnum) attributes.get("value");
        String moduleName = (BroadleafModuleEnum.IGNORED != module) ? module.getName() : (String) attributes.get("moduleName");
        
        return StringUtils.isNotEmpty(moduleName) && ModulePresentUtil.isPresent(moduleName);
    }
    
}
