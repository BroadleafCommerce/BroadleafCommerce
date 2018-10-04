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
package org.broadleafcommerce.pricing.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.domain.OfferCodeImpl;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.pricing.dao.ShippingRateDao;
import org.broadleafcommerce.pricing.domain.ShippingRate;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.IdGeneration;
import org.broadleafcommerce.profile.domain.IdGenerationImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.ShippingRateDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.time.SystemTime;
import org.broadleafcommerce.util.money.Money;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class PricingTest extends BaseTest {

    @Resource
    private PricingService pricingService;

    @Resource
    private CustomerService customerService;

    @Resource
    private OrderDao orderDao;

    @Resource
    private ShippingRateDao shippingRateDao;

    @Test(groups =  {"testShippingInsert"}, dataProvider = "basicShippingRates", dataProviderClass = ShippingRateDataProvider.class)
    @Rollback(false)
    public void testShippingInsert(ShippingRate shippingRate, ShippingRate sr2) throws Exception {
        shippingRateDao.save(shippingRate);
        shippingRateDao.save(sr2);
    }

    @Test(dependsOnGroups = { "testShippingInsert" })
    public void testPricing() throws Exception {
        Order order = orderDao.create();
        order.setCustomer(createCustomer());
        FulfillmentGroup group = new FulfillmentGroupImpl();
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        group.setMethod("standard");
        groups.add(group);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group.setShippingPrice(total);

        DiscreteOrderItemImpl item = new DiscreteOrderItemImpl();
        Sku sku = new SkuImpl();
        sku.setId(123456L);
        sku.setRetailPrice(new Money(10D));
        sku.setDiscountable(true);
        item.setSku(sku);
        item.setQuantity(2);
        order.addOrderItem(item);

        item = new DiscreteOrderItemImpl();
        sku = new SkuImpl();
        sku.setId(1234567L);
        sku.setRetailPrice(new Money(20D));
        sku.setDiscountable(true);
        item.setSku(sku);
        item.setQuantity(1);
        order.addOrderItem(item);

        order.addAddedOfferCode(createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == 123456"));
        order.addAddedOfferCode(createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.id != 123456"));
        order.addAddedOfferCode(createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null));
        order.setTotalShipping(new Money(0D));

        order = pricingService.executePricing(order);

        assert (order.getAdjustmentPrice().equals(new Money(31.80D)));
        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D).add(group.getShippingPrice().multiply(0.05D))));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax()).add(order.getTotalShipping())));
    }

    @Test(groups = { "testShipping" }, dependsOnGroups = { "testShippingInsert" })
    public void testShipping() throws Exception {
        Order order = orderDao.create();
        order.setCustomer(createCustomer());
        FulfillmentGroup group1 = new FulfillmentGroupImpl();
        FulfillmentGroup group2 = new FulfillmentGroupImpl();

        // setup group1 - standard
        group1.setMethod("standard");
        Address address = new AddressImpl();
        State state = new StateImpl();
        state.setAbbreviation("hi");
        address.setState(state);
        group1.setAddress(address);

        // setup group2 - truck
        group2.setMethod("truck");

        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        groups.add(group1);
        //groups.add(group2);
        order.setFulfillmentGroups(groups);
        Money total = new Money(5D);
        group1.setShippingPrice(total);
        group2.setShippingPrice(total);
        //group1.setTotalTax(new Money(1D));
        //group2.setTotalTax(new Money(1D));
        order.setSubTotal(total);
        order.setTotal(total);

        DiscreteOrderItemImpl item = new DiscreteOrderItemImpl();
        item.setPrice(new Money(10D));
        item.setRetailPrice(new Money(15D));
        Sku sku = new SkuImpl();
        sku.setId(1234567L);
        sku.setRetailPrice(new Money(15D));
        sku.setDiscountable(true);
        item.setSku(sku);
        item.setQuantity(1);
        List<OrderItem> items = new ArrayList<OrderItem>();
        items.add(item);
        order.setOrderItems(items);
        for (OrderItem orderItem : items) {
            FulfillmentGroupItem fgi = new FulfillmentGroupItemImpl();
            fgi.setOrderItem(orderItem);
            fgi.setRetailPrice(new Money(15D));
            group1.addFulfillmentGroupItem(fgi);
        }

        order.setTotalShipping(new Money(0D));

        order = pricingService.executePricing(order);

        assert (order.getTotal().greaterThan(order.getSubTotal()));
        assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D).add(group1.getShippingPrice().multiply(0.05D))));
        assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax().add(order.getTotalShipping()))));
    }

    private Customer createCustomer() {
        IdGeneration idGeneration = new IdGenerationImpl();
        idGeneration.setType("org.broadleafcommerce.profile.domain.Customer");
        idGeneration.setBatchStart(1L);
        idGeneration.setBatchSize(10L);
        em.persist(idGeneration);
        Customer customer = customerService.createCustomerFromId(null);
        return customer;
    }


    private OfferCode createOfferCode(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule) {
        OfferCode offerCode = new OfferCodeImpl();
        Offer offer = createOffer(offerName, offerType, discountType, value, customerRule, orderRule);
        offerCode.setOffer(offer);
        offerCode.setOfferCode("OPRAH");
        return offerCode;
    }



    private Offer createOffer(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule) {
        Offer offer = new OfferImpl();
        offer.setName(offerName);
        offer.setStartDate(SystemTime.asDate());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        offer.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 2);
        offer.setEndDate(calendar.getTime());
        offer.setType(offerType);
        offer.setDiscountType(discountType);
        offer.setValue(new Money(value));
        offer.setDeliveryType(OfferDeliveryType.CODE);
        offer.setStackable(true);
        offer.setAppliesToOrderRules(orderRule);
        offer.setAppliesToCustomerRules(customerRule);
        offer.setCombinableWithOtherOffers(true);
        return offer;
    }
}
