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
package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.Collection;
import java.util.Map;


public abstract class AbstractInventoryServiceExtensionHandler extends AbstractExtensionHandler implements InventoryServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType retrieveQuantitiesAvailable(Collection<Sku> skus, Map<String, Object> context, ExtensionResultHolder<Map<Sku, Integer>> result) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType decrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) throws InventoryUnavailableException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType incrementInventory(Map<Sku, Integer> skuQuantities, Map<String, Object> context) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
}
