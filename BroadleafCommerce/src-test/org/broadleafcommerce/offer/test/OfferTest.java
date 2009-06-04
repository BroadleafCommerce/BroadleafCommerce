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
package org.broadleafcommerce.offer.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.service.OfferService;
import org.broadleafcommerce.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.Test;

public class OfferTest extends BaseTest {

    @Resource
    private OfferService offerService;

    @Resource
    private CustomerService customerService;

    @Resource
    private CartService cartService;

    @Test(groups =  {"offerUsedForPricing"})
    public void testOfferUsedForPricing() throws Exception {
        Order order = cartService.createNewCartForCustomer(createCustomer());
        order.setFulfillmentGroups(createFulfillmentGroups("standard", 5D));

        order.addOrderItem(createDiscreteOrderItem(123456L, 10D, null, true, 2));
        order.addOrderItem(createDiscreteOrderItem(1234567L, 20D, null, true, 1));

        order.addAddedOfferCode(createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == 123456", true, true, 10));
        order.addAddedOfferCode(createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.id != 123456", true, true, 10));
        order.addAddedOfferCode(createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert (order.getAdjustmentPrice().equals(new Money(31.80D)));
    }

    @Test(groups =  {"offerNotStackableOffers"})
    public void testOfferNotStackableOffers() throws Exception {
        Order order = cartService.createNewCartForCustomer(createCustomer());
        order.setFulfillmentGroups(createFulfillmentGroups("standard", 5D));

        order.addOrderItem(createDiscreteOrderItem(123456L, 100D, null, true, 2));

        order.addAddedOfferCode(createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == 123456", true, true, 10));
        order.addAddedOfferCode(createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, "discreteOrderItem.sku.id == 123456", true, true, 1));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(112D)));
    }

    private Customer createCustomer() {
        /*IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("org.broadleafcommerce.profile.domain.Customer");
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(10L);
        em.persist(idGeneration);*/
        Customer customer = customerService.createCustomerFromId(null);
        return customer;
    }

    private List<FulfillmentGroup> createFulfillmentGroups(String method, Double shippingPrice) {
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setMethod(method);
        groups.add(group);
        group.setShippingPrice(new Money(shippingPrice));
        return groups;
    }

    private DiscreteOrderItem createDiscreteOrderItem(Long skuId, Double retailPrice, Double salePrice, boolean isDiscountable, int quantity) {
        DiscreteOrderItemImpl item = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setId(skuId);
        sku.setRetailPrice(new Money(retailPrice));
        if (salePrice != null) {
            sku.setSalePrice(new Money(salePrice));
        }
        sku.setDiscountable(isDiscountable);
        item.setSku(sku);
        item.setQuantity(quantity);
        return item;
    }

    private OfferCode createOfferCode(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule, boolean stackable, boolean combinable, int priority) {
        OfferCode offerCode = new OfferCodeImpl();
        Offer offer = createOffer(offerName, offerType, discountType, value, customerRule, orderRule, stackable, combinable, priority);
        offerCode.setOffer(offer);
        offerCode.setOfferCode("OPRAH");
        return offerCode;
    }

    private Offer createOffer(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule, boolean stackable, boolean combinable, int priority) {
        Offer offer = new OfferImpl();
        offer.setName(offerName);
        offer.setStartDate(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DATE, -1);
        offer.setStartDate(calendar.getTime());
        calendar.roll(Calendar.DATE, 2);
        offer.setEndDate(calendar.getTime());
        offer.setType(offerType);
        offer.setDiscountType(discountType);
        offer.setValue(new Money(value));
        offer.setDeliveryType(OfferDeliveryType.CODE);
        offer.setStackable(stackable);
        offer.setAppliesToOrderRules(orderRule);
        offer.setAppliesToCustomerRules(customerRule);
        offer.setCombinableWithOtherOffers(combinable);
        offer.setPriority(priority);
        return offer;
    }
}
