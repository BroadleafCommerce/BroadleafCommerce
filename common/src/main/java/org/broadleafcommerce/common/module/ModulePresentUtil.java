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
package org.broadleafcommerce.common.module;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.condition.ConditionalOnBroadleafModule;
import org.broadleafcommerce.common.condition.OnBroadleafModuleCondition;
import org.broadleafcommerce.common.logging.ModuleLifecycleLoggingBean;
import org.broadleafcommerce.common.module.BroadleafModuleRegistration.BroadleafModuleEnum;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * <p>
 * Utility class that checks for the presence of registered Broadleaf modules.
 *
 * @see {@link ConditionalOnBroadleafModule}
 * @see {@link OnBroadleafModuleCondition}
 * @author Nathan Moore (nathanmoore).
 * @author Phillip Verheyden (phillipuniverse)
 * @author Philip Baggett (pbaggett)
 */
public class ModulePresentUtil {

    public static final List<BroadleafModuleRegistration> MODULE_REGISTRATIONS = SpringFactoriesLoader.loadFactories(BroadleafModuleRegistration.class, null);
    
    /**
     * Checks if the given module is registered
     *
     * @param moduleInQuestion the module that should be checked
     * @return whether the module in question has registered itself at runtime
     * @see {@link #isPresent(String)}
     */
    public static boolean isPresent(@Nonnull final BroadleafModuleEnum moduleInQuestion) {
        return isPresent(moduleInQuestion.getName());
    }

    /**
     * Checks that every module in the list is registered.
     *
     * @param modulesInQuestion list of modules that should be checked
     * @return true if all modules in the list are present, false otherwise
     * @see #isPresent(String)
     */
    public static boolean allPresent(@Nonnull final List<String> modulesInQuestion) {
        for (String module : modulesInQuestion) {
            if (StringUtils.isEmpty(module) || !isPresent(module)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This version takes a String instead of a {@link BroadleafModuleEnum} but operates in the same way by checking to see if
     * a particular Broadleaf module has registered itself
     *
     * @param moduleInQuestion a String that maps to {@link ModuleLifecycleLoggingBean#getModuleName()}
     */
    public static boolean isPresent(@Nonnull final String moduleInQuestion) {
        for (BroadleafModuleRegistration registration : MODULE_REGISTRATIONS) {
            String moduleName = registration.getModuleName();
            if (moduleInQuestion.equals(moduleName)) {
                return true;
            }
        }

        return false;
    }
}
