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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Rollback handler to execute after an order has been marked as 'completed' and there is an exception.
 * 
 *  1. Change the status back to IN_PROCESS
 *  2. Change the order number back to null
 *  3. Change the submit date back to null
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCompleteOrderRollbackHandler")
public class CompleteOrderRollbackHandler implements RollbackHandler<CheckoutSeed> {

    @Override
    public void rollbackState(Activity<? extends ProcessContext<CheckoutSeed>> activity, ProcessContext<CheckoutSeed> processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        CheckoutSeed seed = processContext.getSeedData();
        seed.getOrder().setStatus(OrderStatus.IN_PROCESS);
        seed.getOrder().setOrderNumber(null);
        seed.getOrder().setSubmitDate(null);
    }

}
