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
package org.broadleafcommerce.pricing.service.module;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.exception.TaxException;
import org.broadleafcommerce.util.money.Money;

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
        Money totalTax = getFulfillmentGroupFeeTotal(order).multiply(factor);

        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotalTax = fulfillmentGroup.getShippingPrice().multiply(factor);
            fulfillmentGroup.setTotalTax(fgTotalTax);
            fulfillmentGroup.setCityTax(new Money(0D));
            fulfillmentGroup.setStateTax(new Money(0D));
            fulfillmentGroup.setDistrictTax(new Money(0D));
            fulfillmentGroup.setCountyTax(new Money(0D));
            fulfillmentGroup.setCountryTax(new Money(0D));

            totalTax = totalTax.add(fgTotalTax);
        }

        order.setCityTax(new Money(0D));
        order.setStateTax(new Money(0D));
        order.setDistrictTax(new Money(0D));
        order.setCountyTax(new Money(0D));
        order.setCountryTax(new Money(0D));
        order.setTotalTax(totalTax);

        return order;
    }
    
    protected Money getFulfillmentGroupFeeTotal(Order order) {
        Money feeSubtotal = order.getSubTotal();
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupFee fulfillmentGroupFee : fulfillmentGroup.getFulfillmentGroupFees()) {
                if (fulfillmentGroupFee.isTaxable()) {
                    feeSubtotal = feeSubtotal.add(fulfillmentGroupFee.getAmount());
                }
            }
        }
        
        return feeSubtotal;
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
