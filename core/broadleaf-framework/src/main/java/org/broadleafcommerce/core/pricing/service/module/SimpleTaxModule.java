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

package org.broadleafcommerce.core.pricing.service.module;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.TaxDetail;
import org.broadleafcommerce.core.order.domain.TaxDetailImpl;
import org.broadleafcommerce.core.order.domain.TaxType;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;

/**
 * Simple factor-based tax module. Not really useful for anything
 * other than demonstration.
 * 
 * @author jfischer
 */
public class SimpleTaxModule implements TaxModule {

    public static final String MODULENAME = "simpleTaxModule";

    protected String name = MODULENAME;
    protected Double factor;

    public Order calculateTaxForOrder(Order order) throws TaxException {
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            
            // Set taxes on the fulfillment group items
            for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                List<TaxDetail> taxes = new ArrayList<TaxDetail>();
                
                TaxDetail tax = new TaxDetailImpl();
                tax.setRate(new BigDecimal(factor));
                tax.setType(TaxType.STATE);
                tax.setAmount(fgItem.getPrice().multiply(fgItem.getQuantity()).multiply(factor));
                taxes.add(tax);
                
                fgItem.setTaxes(taxes);
            }
    
            // Do not tax fulfillment group fees
            // Do not tax shipping
        }

        return order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the factor
     */
    public Double getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(Double factor) {
        this.factor = factor;
    }

}
