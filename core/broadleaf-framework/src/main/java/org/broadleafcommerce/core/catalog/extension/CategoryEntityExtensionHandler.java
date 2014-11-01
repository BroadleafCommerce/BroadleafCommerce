/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Category;

/**
 * For internal usage. Allows extending API calls without subclassing the entity.
 *
 * @author Jeff Fischer
 */
public interface CategoryEntityExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType getChildCategoryXrefs(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllChildCategoryXrefs(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllChildCategories(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType hasAllChildCategories(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getChildCategories(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType hasChildCategories(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getChildCategoryIds(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllParentCategoryXrefs(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllParentCategories(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getActiveProductXrefs(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllProductXrefs(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getActiveProducts(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getAllProducts(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getFeaturedProducts(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getCrossSaleProducts(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getUpSaleProducts(Category delegate, ExtensionResultHolder resultHolder);

    ExtensionResultStatusType getChildCategoryURLMap(Category delegate, ExtensionResultHolder resultHolder);
}
