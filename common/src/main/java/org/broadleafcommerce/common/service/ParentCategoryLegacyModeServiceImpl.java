/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.service;

import org.springframework.beans.BeansException;
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

    //TODO re-enable when feature is complete
    //@Value("${" + USE_LEGACY_DEFAULT_CATEGORY_MODE + ":true}")
    protected boolean useLegacyDefaultCategoryMode = true;

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
