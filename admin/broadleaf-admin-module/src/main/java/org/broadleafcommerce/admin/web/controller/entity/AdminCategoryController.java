/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import javax.annotation.Resource;

/**
 * Handles admin operations for the {@link Category} entity.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminCategoryController")
@RequestMapping("/" + AdminCategoryController.SECTION_KEY)
public class AdminCategoryController extends AdminBasicEntityController {
    
    public static final String SECTION_KEY = "category";
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }

    @Override
    protected void modifyEntityForm(EntityForm ef, Map<String, String> pathVars) {
        Field overrideGeneratedUrl = ef.findField("overrideGeneratedUrl");
        overrideGeneratedUrl.setFieldType(SupportedFieldType.HIDDEN.toString().toLowerCase());
    }

    @Override
    protected void modifyAddEntityForm(EntityForm ef, Map<String, String> pathVars) {
        Field overrideGeneratedUrl = ef.findField("overrideGeneratedUrl");
        overrideGeneratedUrl.setFieldType(SupportedFieldType.HIDDEN.toString().toLowerCase());
        boolean overriddenUrl = Boolean.parseBoolean(overrideGeneratedUrl.getValue());
        Field fullUrl = ef.findField("url");
        fullUrl.withAttribute("overriddenUrl", overriddenUrl)
            .withAttribute("sourceField", "name")
            .withAttribute("toggleField", "overrideGeneratedUrl")
            .withFieldType(SupportedFieldType.GENERATED_URL.toString().toLowerCase());
    }

    @Override
    public String[] getSectionCustomCriteria() {
        return new String[]{"categoryDirectEdit"};
    }
}
