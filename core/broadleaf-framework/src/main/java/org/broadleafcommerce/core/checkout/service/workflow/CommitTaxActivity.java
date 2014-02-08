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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import javax.annotation.Resource;

/**
 * This is an optional activity to allow a committal of taxes to a tax sub system. Many tax 
 * providers store tax details for reference, debugging, reporting, and reconciliation.
 * 
 * @author Kelly Tisdell
 *
 */
public class CommitTaxActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    @Resource(name = "blTaxService")
    protected TaxService taxService;

    public CommitTaxActivity() {
        super();
        //We can automatically register a rollback handler because the state will be in the process context.
        super.setAutomaticallyRegisterRollbackHandler(true);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();

        if (!order.getTaxOverride()) {
            order = taxService.commitTaxForOrder(order);
            context.getSeedData().setOrder(order);
        }

        return context;
    }

}
