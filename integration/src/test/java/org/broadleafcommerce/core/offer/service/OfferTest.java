/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.SkuDaoDataProvider;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CustomerOffer;
import org.broadleafcommerce.core.offer.domain.CustomerOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferInfo;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * This integration test class is kept to guarantee backwards
 * compatibility for Broadleaf offers. The code demonstrated
 * here employs some deprecated APIs and should therefore not be
 * used as an example of programatically creating offers.
 */
public class OfferTest extends CommonSetupBaseTest {

    @Resource
    private OfferService offerService;

    @Resource
    private CustomerService customerService;

    @Resource(name = "blOrderService")
    private OrderService orderService;

    @Resource
    private OfferDao offerDao;

    @Resource
    private CustomerOfferDao customerOfferDao;

    @Resource
    private CatalogService catalogService;

    @Resource
    private OfferCodeDao offerCodeDao;
    
    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;
    
    @Resource
    private CountryService countryService;
    
    @Resource
    private StateService stateService;

    private long sku1;
    private long sku2;
    private CreateOfferUtility createOfferUtility;

    @Test(groups = { "offerCreateSku1" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class)
    @Rollback(false)
    public void createSku1(Sku sku) {
        createOfferUtility = new CreateOfferUtility(offerDao, offerCodeDao, offerService);
        sku.setSalePrice(new Money(BigDecimal.valueOf(10.0)));
        sku.setRetailPrice(new Money(BigDecimal.valueOf(15.0)));
        sku.setName("test1");
        assert sku.getId() == null;
        sku = catalogService.saveSku(sku);
        assert sku.getId() != null;
        sku1 = sku.getId();
    }

    @Test(groups = { "offerCreateSku2" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class)
    @Rollback(false)
    public void createSku2(Sku sku) {
        sku.setSalePrice(new Money(BigDecimal.valueOf(10.0)));
        sku.setRetailPrice(new Money(BigDecimal.valueOf(15.0)));
        sku.setName("test2");
        assert sku.getId() == null;
        sku = catalogService.saveSku(sku);
        assert sku.getId() != null;
        sku2 = sku.getId();
    }
    
    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testPercentOffOfferWithScaleGreaterThanTwo() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        order.setFulfillmentGroups(createFulfillmentGroups(option, 5D, order));
        orderService.save(order, false);

        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(createOfferUtility.createOfferCode("20.5 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20.5, null, null, true, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        // 20% results in $240.  20.5% off results in $238.50
        assert ( order.getSubTotal().equals(new Money(238.50D) ));
    }

    @Test(groups =  {"offerUsedForPricing"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testOfferUsedForPricing() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(createOfferUtility.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        order.addOfferCode(createOfferUtility.createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testOfferNotCombinableItemOffers"}, dependsOnGroups = { "offerUsedForPricing"})
    @Transactional
    public void testOfferNotCombinableItemOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == " + sku1, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, "discreteOrderItem.sku.id == " + sku1, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id != " + sku1, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, "discreteOrderItem.sku.id != " + sku1, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(300D)));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOffer"}, dependsOnGroups = { "testOfferNotCombinableItemOffers"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOffer() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
       
        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, null, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, null, true, false, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(240D)));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOfferAndInformation"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOfferAndInformation() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        OfferCode offerCode1 = createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, null, true, true, 1);
        OfferCode offerCode2 = createOfferUtility.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, null, true, false, 1);

        order.addOfferCode(offerCode1);
        order.addOfferCode(offerCode2);

        OfferInfo info1 = offerDao.createOfferInfo();
        info1.getFieldValues().put("key1", "value1");
        order.getAdditionalOfferInformation().put(offerCode1.getOffer(), info1);
        OfferInfo info2 = offerDao.createOfferInfo();
        info2.getFieldValues().put("key2", "value2");
        order.getAdditionalOfferInformation().put(offerCode2.getOffer(), info2);
        
        order = orderService.save(order, true);

        assert (order.getSubTotal().equals(new Money(240D)));

        order = orderService.findOrderById(order.getId());
        assert(order.getAdditionalOfferInformation().get(offerCode1.getOffer()).equals(info1));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOffer2"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOffer2() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, 50D, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("25 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 25, null, null, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("35 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 35, null, null, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("45 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 45, null, null, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, null, null, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(170D));
    }

    @Test(groups =  {"testOfferNotStackableOrderOffers"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer2"})
    @Transactional
    public void testOfferNotStackableOrderOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Order Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF, 20, null, "order.subTotal.getAmount() >= 400", true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, null, "order.subTotal.getAmount() >= 400", false, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("100 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 100, null, "order.subTotal.getAmount() >= 400", false, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, null, "order.subTotal.getAmount() < 400", false, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        //     assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(240D));
    }

    @Test(groups =  {"testOfferNotCombinableOrderOffers"}, dependsOnGroups = { "testOfferNotStackableOrderOffers"})
    @Transactional
    public void testOfferNotCombinableOrderOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Order Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF, 20, null, null, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, null, null, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, null, null, true, false, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(290D));
    }

    @Test(groups =  {"testOfferNotCombinableOrderOffersWithItemOffer"}, dependsOnGroups = { "testOfferNotCombinableOrderOffers"})
    @Transactional
    public void testOfferNotCombinableOrderOffersWithItemOffer() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, null, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("10 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 10, null, null, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("15 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 15, null, null, true, true, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("90 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 90, null, null, true, false, 1));
        order.addOfferCode(createOfferUtility.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, null, null, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        // Item offers (10+15 are combinable resulting in $100 off for the item.   Plus $90 as the best offer for the order).
        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(210D));
    }

    @Test(groups =  {"testGlobalOffers"}, dependsOnGroups = { "testOfferNotCombinableOrderOffersWithItemOffer"})
    @Transactional
    public void testGlobalOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(createOfferUtility.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        
        Offer offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offer.setDeliveryType(OfferDeliveryType.AUTOMATIC);
        offer.setAutomaticallyAdded(true);
        offer = offerService.save(offer);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testCustomerAssociatedOffers"}, dependsOnGroups = { "testGlobalOffers"})
    @Transactional
    public void testCustomerAssociatedOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(createOfferUtility.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, null, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        
        Offer offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offer.setDeliveryType(OfferDeliveryType.MANUAL);
        offer = offerService.save(offer);
        CustomerOffer customerOffer = new CustomerOfferImpl();
        customerOffer.setCustomer(order.getCustomer());
        customerOffer.setOffer(offer);
        customerOffer = customerOfferDao.save(customerOffer);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testCustomerAssociatedOffers2"}, dependsOnGroups = { "testCustomerAssociatedOffers"})
    @Transactional
    public void testCustomerAssociatedOffers2() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(createDiscreteOrderItem(sku1, 20D, null, true, 1, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(createOfferUtility.createOfferCode("15%OFF", "15 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 15, null, null, false, true, 0));

        Offer offer1 = createOfferUtility.createOffer("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, "discreteOrderItem.sku.id == " + sku1, false, true, 0);
        offer1.setDeliveryType(OfferDeliveryType.MANUAL);
        offerDao.save(offer1);
        CustomerOffer customerOffer1 = new CustomerOfferImpl();
        customerOffer1.setCustomer(order.getCustomer());
        customerOffer1.setOffer(offer1);
        customerOfferDao.save(customerOffer1);

        Offer offer2 = createOfferUtility.createOffer("10 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 10, null, "discreteOrderItem.sku.id == " + sku2, false, true, 0);
        offer2.setDeliveryType(OfferDeliveryType.MANUAL);
        offerDao.save(offer2);
        CustomerOffer customerOffer2 = new CustomerOfferImpl();
        customerOffer2.setCustomer(order.getCustomer());
        customerOffer2.setOffer(offer2);
        customerOfferDao.save(customerOffer2);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(33D)));
    }

    @Test(groups =  {"testFulfillmentGroupOffers"}, dependsOnGroups = { "testCustomerAssociatedOffers2"})
    @Transactional
    public void testFulfillmentGroupOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        order.setFulfillmentGroups(createFulfillmentGroups(option, 5D, order));
        orderService.save(order, false);
        
        order.addOrderItem(createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(createOfferUtility.createOfferCode("20 Percent Off Item Offer", OfferType.FULFILLMENT_GROUP, OfferDiscountType.PERCENT_OFF, 20, null, null, true, true, 10));
        order.addOfferCode(createOfferUtility.createOfferCode("3 Dollars Off Item Offer", OfferType.FULFILLMENT_GROUP, OfferDiscountType.AMOUNT_OFF, 3, null, null, true, true, 10));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyOffersToOrder(offers, order);
        offerService.applyFulfillmentGroupOffersToOrder(offers, order);

        assert (order.getFulfillmentGroups().get(0).getShippingPrice().equals(new Money(1.6D)));
    }

    @Test(groups =  {"testOfferDelete"}, dependsOnGroups = { "testFulfillmentGroupOffers"})
    @Transactional
    public void testOfferDelete() throws Exception {
        CustomerOffer customerOffer = customerOfferDao.create();
        Customer customer = createCustomer();
        Long customerId = customer.getId();
        customerOffer.setCustomer(customerService.saveCustomer(customer));

        Offer offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offer = offerService.save(offer);
        Long offerId = offer.getId();
        offerDao.delete(offer);
        Offer deletedOffer = offerDao.readOfferById(offerId);
        assert ((OfferImpl) deletedOffer).getArchived() == 'Y';

        offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offer = offerService.save(offer);

        customerOffer.setOffer(offer);
        customerOffer = customerOfferDao.save(customerOffer);
        Long customerOfferId = customerOffer.getId();
        customerOffer = customerOfferDao.readCustomerOfferById(customerOfferId);
        assert(customerOffer != null);

        Customer customer2 = createCustomer();
        customerOffer.setCustomer(customerService.saveCustomer(customer2));
        customerOffer = customerOfferDao.save(customerOffer);

        assert !customerOffer.getCustomer().getId().equals(customerId);

        customerOfferDao.delete(customerOffer);
        customerOffer = customerOfferDao.readCustomerOfferById(customerOfferId);
        
        assert customerOffer == null || ((OfferImpl) customerOffer).getArchived() == 'Y';
    }

    @Test(groups =  {"testReadAllOffers"}, dependsOnGroups = { "testOfferDelete"})
    @Transactional
    public void testReadAllOffers() throws Exception {
        Offer offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offer.setDeliveryType(OfferDeliveryType.MANUAL);
        offer = offerService.save(offer);
        List<Offer> allOffers = offerService.findAllOffers();
        assert allOffers != null && allOffers.isEmpty() == false;
    }

    @Test(groups =  {"testOfferCodeDao"}, dependsOnGroups = { "testReadAllOffers"})
    @Transactional
    public void testOfferCodeDao() throws Exception {
        String offerCodeString = "AJ's Code";
        OfferCode offerCode = createOfferUtility.createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        offerCode.setOfferCode(offerCodeString);
        offerCode = offerService.saveOfferCode(offerCode);
        Long offerCodeId = offerCode.getId();
        assert offerCode.getOfferCode().equals(offerCodeString);

        Offer offer = offerCode.getOffer();
        Offer storedOffer = offerService.lookupOfferByCode(offerCodeString);
        assert offer.getId().equals(storedOffer.getId());

        OfferCode newOfferCode = offerCodeDao.readOfferCodeById(offerCodeId);
        assert newOfferCode.getOfferCode().equals(offerCode.getOfferCode());

        newOfferCode = offerCodeDao.readOfferCodeByCode(offerCodeString);
        assert newOfferCode.getOfferCode().equals(offerCode.getOfferCode());
        offerCodeId = newOfferCode.getId();
        offerCodeDao.delete(newOfferCode);

        OfferCode deletedOfferCode = offerCodeDao.readOfferCodeById(offerCodeId);
        assert deletedOfferCode == null;
    }

    @Test(groups =  {"testCustomerOffers"}, dependsOnGroups = { "testOfferCodeDao"})
    @Transactional
    public void testCustomerOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        Offer offer = createOfferUtility.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, null, true, true, 10);
        CustomerOffer customerOffer = new CustomerOfferImpl();
        customerOffer.setCustomer(order.getCustomer());
        customerOffer.setOffer(offer);
        customerOffer = customerOfferDao.save(customerOffer);
        CustomerOffer customerOfferTest = customerOfferDao.readCustomerOfferById(customerOffer.getId());

        assert (customerOffer.getId().equals(customerOfferTest.getId()));
    }

    private List<FulfillmentGroup> createFulfillmentGroups(FulfillmentOption option, Double shippingPrice, Order order) {
        List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
        FulfillmentGroup group = new FulfillmentGroupImpl();
        group.setFulfillmentOption(option);
        groups.add(group);
        group.setRetailShippingPrice(new Money(shippingPrice));
        group.setOrder(order);
        
        Address address = new AddressImpl();
        address.setAddressLine1("123 Test Rd");
        address.setCity("Dallas");
        address.setFirstName("Jeff");
        address.setLastName("Fischer");
        address.setPostalCode("75240");
        address.setPrimaryPhone("972-978-9067");
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        
        countryService.save(country);

        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2("US");
        isoCountry.setName("UNITED STATES");

        isoService.save(isoCountry);
        
        State state = new StateImpl();
        state.setAbbreviation("TX");
        state.setName("Texas");
        state.setCountry(country);
        
        stateService.save(state);
        
        address.setState(state);
        address.setCountry(country);
        address.setIsoCountrySubdivision("US-TX");
        address.setIsoCountryAlpha2(isoCountry);
        
        for (OrderItem orderItem : order.getOrderItems()) {
            FulfillmentGroupItem fgItem = new FulfillmentGroupItemImpl();
            fgItem.setFulfillmentGroup(group);
            fgItem.setOrderItem(orderItem);
            fgItem.setQuantity(orderItem.getQuantity());
            group.addFulfillmentGroupItem(fgItem);
        }
        
        group.setAddress(address);

        return groups;
    }

    private DiscreteOrderItem createDiscreteOrderItem(Long skuId, Double retailPrice, Double salePrice, boolean isDiscountable, int quantity, Order order) {
        DiscreteOrderItem item = new DiscreteOrderItemImpl();
        Sku sku = catalogService.findSkuById(skuId);
        sku.setRetailPrice(new Money(retailPrice));
        if (salePrice != null) {
            sku.setSalePrice(new Money(salePrice));
        } else {
            sku.setSalePrice(null);
        }
        sku.setDiscountable(isDiscountable);
        sku.setName("test");
        sku = catalogService.saveSku(sku);

        item.setSku(sku);
        item.setQuantity(quantity);
        Product product = new ProductImpl();
        product.setDefaultSku(sku);

        product = catalogService.saveProduct(product);

        item.setProduct(product);
        
        item.setOrder(order);
        
        item = (DiscreteOrderItem) orderItemService.saveOrderItem(item);

        return item;
    }
}
