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

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.List;

import javax.annotation.Resource;

public class OfferActivity extends BaseActivity<ProcessContext<Order>> {

    @Resource(name="blOfferService")
    protected OfferService offerService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();
        List<OfferCode> offerCodes = offerService.buildOfferCodeListForCustomer(order.getCustomer());
        if (offerCodes != null && !offerCodes.isEmpty()) {
            order = orderService.addOfferCodes(order, offerCodes, true);
        }

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);
        context.setSeedData(order);

        return context;
    }

}
