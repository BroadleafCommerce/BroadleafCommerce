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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.TaxService;
import org.broadleafcommerce.core.pricing.service.module.TaxModule;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

public class TaxActivity extends BaseActivity<ProcessContext<Order>> {

    protected TaxModule taxModule;

    protected TaxService taxService;

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();

        if (taxService != null) {
            order = taxService.calculateTaxForOrder(order);
        } else if (taxModule != null) {
            order = taxModule.calculateTaxForOrder(order);
        }

        context.setSeedData(order);
        return context;
    }

    public void setTaxModule(TaxModule taxModule) {
        this.taxModule = taxModule;
    }

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

}
