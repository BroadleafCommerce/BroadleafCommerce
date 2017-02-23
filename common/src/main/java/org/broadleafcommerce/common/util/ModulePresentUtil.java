/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.condition.BroadleafModuleCondition;
import org.broadleafcommerce.common.condition.ConditionalOnBroadleafModule;
import org.broadleafcommerce.common.condition.ConditionalOnBroadleafModule.BroadleafModuleEnum;
import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * <p>
 * Component class that checks for the presence of a specified module, by verifying their registration via {@link ModuleLifecycleLoggingBean}.
 *
 * @see {@link ConditionalOnBroadleafModule}
 * @see {@link BroadleafModuleCondition}
 * @author Nathan Moore (nathanmoore).
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blModulePresentUtil")
public class ModulePresentUtil {

    @Autowired
    protected ApplicationContext applicationContext;
    
    /**
     * Tre
     *
     * @param moduleInQuestion the module that should be checked
     * @return whether the module in question has registered itself at runtime
     * @see {@link #isPresent(String)}
     */
    public boolean isPresent(@Nonnull final BroadleafModuleEnum moduleInQuestion) {
        return isPresent(moduleInQuestion.getName());
    }

    /**
     * This version takes a String instead of a {@link BroadleafModuleEnum} but operates in the same way by checking to see if
     * a particular Broadleaf module has registered itself
     *
     * @param moduleInQuestion a String that maps to {@link ModuleLifecycleLoggingBean#getModuleName()}
     * @return
     */
    public boolean isPresent(@Nonnull final String moduleInQuestion) {
        Map<String, ModuleLifecycleLoggingBean> beanMap = applicationContext.getBeansOfType(ModuleLifecycleLoggingBean.class);
        for (ModuleLifecycleLoggingBean module : beanMap.values()) {
            String moduleName = module.getModuleName();

            if (moduleInQuestion.equals(moduleName)) {
                return true;
            }
        }

        return false;
    }
}
