/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;


/**
 * Extension handler for {@link BroadleafProductController} and {@link BroadleafCategoryController}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface BroadleafCatalogControllerExtensionHandler extends ExtensionHandler {
    
    /**
     * If this method returns something other than {@link ExtensionResultStatusType#NOT_HANDLED}, the result variable
     * in the {@link ExtensionResultHolder} will hold the associated path to the template that should be used
     * 
     * @param erh
     * @param product
     * @return the result of the extension handler call
     */
    public ExtensionResultStatusType getProductTemplate(ExtensionResultHolder<String> erh, Product product);

    /**
     * If this method returns something other than {@link ExtensionResultStatusType#NOT_HANDLED}, the result variable
     * in the {@link ExtensionResultHolder} will hold the associated path to the template that should be used
     * 
     * @param erh
     * @param category
     * @return the result of the extension handler call
     */
    public ExtensionResultStatusType getCategoryTemplate(ExtensionResultHolder<String> erh, Category category);

}
