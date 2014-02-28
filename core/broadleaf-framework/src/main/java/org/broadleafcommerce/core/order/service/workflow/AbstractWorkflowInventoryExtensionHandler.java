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
package org.broadleafcommerce.core.order.service.workflow;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;

import java.util.Map;

/**
 * Abstract definition of {@link WorkflowInventoryExtensionHandler} that all implementors should extend from. Defaults to
 * {@link ExtensionResultStatusType#NOT_HANDLED} for all methods.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class AbstractWorkflowInventoryExtensionHandler extends AbstractExtensionHandler implements WorkflowInventoryExtensionHandler {

    @Override
    public ExtensionResultStatusType checkAvailability(Sku sku, Integer requestedQuantity, ProcessContext<CartOperationRequest> context) throws InventoryUnavailableException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType decrementInventory(Map<Sku, Integer> skuQuantities, ProcessContext<CheckoutSeed> context, Map<String, Object> rollbackState) throws InventoryUnavailableException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType rollbackInventoryOperation(ProcessContext<CheckoutSeed> context, Map<String, Object> rollbackState) throws RollbackFailureException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
