/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service.manipulation;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DynamicPriceDiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

public class OrderItemVisitorAdapter implements OrderItemVisitor {

    public void visit(BundleOrderItem bundleOrderItem) throws PricingException {
        //do nothing
    }

    public void visit(DiscreteOrderItem discreteOrderItem) throws PricingException {
        //do nothing
    }

    public void visit(DynamicPriceDiscreteOrderItem dynamicPriceDiscreteOrderItem) throws PricingException {
        //do nothing
    }

    public void visit(GiftWrapOrderItem giftWrapOrderItem) throws PricingException {
        //do nothing
    }

    public void visit(OrderItem orderItem) throws PricingException {
        //do nothing
    }

}
