/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blParentCategoryLegacyModeService")
public class ParentCategoryLegacyModeServiceImpl implements ApplicationContextAware, ParentCategoryLegacyModeService {

    public static final String USE_LEGACY_DEFAULT_CATEGORY_MODE = "use.legacy.default.category.mode";
    private static ApplicationContext applicationContext;
    private static ParentCategoryLegacyModeService service;

    @Value("${" + USE_LEGACY_DEFAULT_CATEGORY_MODE + ":false}")
    protected boolean useLegacyDefaultCategoryMode = false;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean isLegacyMode() {
        return useLegacyDefaultCategoryMode;
    }

    public static ParentCategoryLegacyModeService getLegacyModeService() {
        if (applicationContext == null) {
            return null;
        }
        if (service == null) {
            service = (ParentCategoryLegacyModeService) applicationContext.getBean("blParentCategoryLegacyModeService");
        }
        return service;
    }
}
