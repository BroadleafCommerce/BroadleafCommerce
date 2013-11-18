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
package org.broadleafcommerce.admin.web.controller;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.web.controller.AdminTranslationControllerExtensionListener;
import org.broadleafcommerce.openadmin.web.form.TranslationForm;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Component("blAdminProductTranslationExtensionListener")
public class AdminProductTranslationExtensionListener implements AdminTranslationControllerExtensionListener {
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    /**
     * If we are trying to translate a field on Product that starts with "defaultSku.", we really want to associate the
     * translation with Sku, its associated id, and the property name without "defaultSku."
     */
    @Override
    public boolean applyTransformation(TranslationForm form) {
        if (form.getCeilingEntity().equals(Product.class.getName()) && form.getPropertyName().startsWith("defaultSku.")) {
            Product p = catalogService.findProductById(Long.parseLong(form.getEntityId()));
            form.setCeilingEntity(Sku.class.getName());
            form.setEntityId(String.valueOf(p.getDefaultSku().getId()));
            form.setPropertyName(form.getPropertyName().substring("defaultSku.".length()));
            return true;
        }
        
        return false;
    }
    
}
