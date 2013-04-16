/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.module.TaxModule;
import org.broadleafcommerce.core.workflow.BaseActivity;

public class TaxActivity extends BaseActivity<PricingContext> {

    private TaxModule taxModule;

    @Override
    public PricingContext execute(PricingContext context) throws Exception {
        Order order = context.getSeedData();
        order = taxModule.calculateTaxForOrder(order);

        context.setSeedData(order);
        return context;
    }

    public TaxModule getTaxModule() {
        return taxModule;
    }

    public void setTaxModule(TaxModule taxModule) {
        this.taxModule = taxModule;
    }

}
