/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.annotation.Resource;

@Component("blCommitTaxRollbackHandler")
public class CommitTaxRollbackHandler implements RollbackHandler {

    @Resource(name = "blTaxService")
    protected TaxService taxService;

    @Override
    public void rollbackState(Activity<? extends ProcessContext> activity, ProcessContext processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        CheckoutContext ctx = (CheckoutContext) processContext;
        Order order = ctx.getSeedData().getOrder();
        try {
            taxService.cancelTax(order);
        } catch (TaxException e) {
            throw new RollbackFailureException("An exception occured cancelling taxes for order id: " + order.getId(), e);
        }

    }

}
