/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.pricing.service.workflow;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.module.TaxModule;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class TaxActivity extends BaseActivity {

    private TaxModule taxModule;

    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext)context).getSeedData();
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
