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
package org.broadleafcommerce.offer.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.offer.OfferDataProvider;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.order.service.OrderService;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.BaseTest;
import org.testng.annotations.Test;

public class OfferServiceTest extends BaseTest{

    @Resource
    OfferService offerService;

    @Resource
    OrderService orderService;

    @Resource
    CustomerService customerService;

    @Test(groups = {"applyOffersToOrder"}, dataProvider = "offerDataProvider",  dataProviderClass=OfferDataProvider.class, dependsOnGroups = {"findCurrentCartForCustomer","getItemsForOrder" })
    public void applyOffersToOrder(List<Offer> allOffers){
        //        String userName = "customer1";
        //        Customer customer = customerService.readCustomerByUsername(userName);
        //        Order order = orderService.findCartForCustomer(customer);
        //        order.setOrderItems(orderService.findItemsForOrder(order));
        //      offerService.applyOffersToOrder(allOffers, order);
        //offerService.applyOffersToOrderUsingEngine(allOffers, order);
    }
}
