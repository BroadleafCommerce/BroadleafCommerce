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
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;

/**
 * TODO investigate how taxware is used and how tax rate information is populated into the
 * database. We would like to have a taxware tax module.
 * 
 * @author jfischer
 *
 */
public class SimpleTaxModule implements TaxModule {

    public static final String MODULENAME = "simpleTaxModule";

    protected String name = MODULENAME;
    protected Double factor;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#calculateTaxForOrder(org.broadleafcommerce.order.domain.Order)
     */
    @Override
    public Order calculateTaxForOrder(Order order) {
        Money totalTax = order.getSubTotal().multiply(factor);
        order.setTotalTax(totalTax);

        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotalTax = fulfillmentGroup.getShippingPrice().multiply(factor);
            fulfillmentGroup.setTotalTax(fgTotalTax);
            fulfillmentGroup.setCityTax(new Money(0D));
            fulfillmentGroup.setCountyTax(new Money(0D));
            fulfillmentGroup.setCountryTax(new Money(0D));
        }

        return order;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#setName(java.lang.String)
     */
    @Override
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
