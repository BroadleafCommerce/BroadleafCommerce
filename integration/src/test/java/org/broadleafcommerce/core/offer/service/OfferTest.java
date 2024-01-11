/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.SkuDaoDataProvider;
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
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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
    private CreateOfferUtility offerUtil;
    private CreateOrderEntityUtility orderUtil;

    @Test(groups = { "offerCreateSku1" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class)
    @Rollback(false)
    public void createSku1(Sku sku) {
        offerUtil = new CreateOfferUtility(offerDao, offerCodeDao, offerService);
        orderUtil = new CreateOrderEntityUtility(catalogService, orderItemService, isoService, stateService, countryService);
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
    public void testThreePercentOffOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 4.99D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 9.99D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("60 Percent Off Item Sku1 Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 60, "discreteOrderItem.sku.id == " + sku1, false, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("60 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 60, "discreteOrderItem.sku.id == " + sku2, false, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("40 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 40, null, false, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(6D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testTwoPercentOffOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 10, null, false, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("15 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 15, "discreteOrderItem.sku.id == " + sku2, false, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(985D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testBOGOCombination() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 10, null, false, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("NONAME", "30 Percent Off Second Item Sku2 Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 30, "discreteOrderItem.sku.id == " + sku2, false, true, 10, "discreteOrderItem.sku.id == " + sku2));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1070D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testBOGOAmountOffCombination() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 10, null, false, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("NONAME", "30 Amount Off Second Item Sku2 Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, "discreteOrderItem.sku.id == " + sku2, false, true, 10, "discreteOrderItem.sku.id == " + sku2));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1070D) ));
    }
    
    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testPercentOffOfferWithScaleGreaterThanTwo() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        order.setFulfillmentGroups(orderUtil.createFulfillmentGroups(option, 5D, order));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("20.5 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20.5, null, true, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        // 20% results in $240.  20.5% off results in $238.50
        assert ( order.getSubTotal().equals(new Money(238.50D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1" })
    @Transactional
    public void testPercentOffOfferWithItemMinPriceSecondEvaluation() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 10D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("75 Percent Off Items Over $5.00", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 75, "orderItem.?price.getAmount()>5.00", true, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        // Evaluate the subtotal again to ensure that the second calculation
        //  is based on the pre-offer price, not the reduced price
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(2.50D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1" })
    @Transactional
    public void testPercentOffOfferWithItemMinPriceQuantityReduction() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 3D, null, true, 2, order));
        order.addOfferCode(offerUtil.createOfferCode("75 Percent Off Items Over $5.00", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 75, "orderItem.?price.getAmount()>5.00", true, true, 10));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        // Evaluate the subtotal again with a reduced quantity to ensure that the
        //  offer is NOT applied due to the subtotal being greater than $5.00
        order.getOrderItems().get(0).setQuantity(1);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(3.00D) ));
    }

    @Test(groups =  {"offerUsedForPricing"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testOfferUsedForPricing() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testOfferNotCombinableItemOffers"}, dependsOnGroups = { "offerUsedForPricing"})
    @Transactional
    public void testOfferNotCombinableItemOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id == " + sku1, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, "discreteOrderItem.sku.id == " + sku1, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id != " + sku1, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, "discreteOrderItem.sku.id != " + sku1, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(300D)));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOffer"}, dependsOnGroups = { "testOfferNotCombinableItemOffers"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOffer() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
       
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, true, false, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(180D)));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOfferAndInformation"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOfferAndInformation() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        OfferCode offerCode1 = offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, true, true, 1);
        OfferCode offerCode2 = offerUtil.createOfferCode("30 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 30, null, true, false, 1);

        order.addOfferCode(offerCode1);
        order.addOfferCode(offerCode2);

        OfferInfo info1 = offerDao.createOfferInfo();
        info1.getFieldValues().put("key1", "value1");
        order.getAdditionalOfferInformation().put(offerCode1.getOffer(), info1);
        OfferInfo info2 = offerDao.createOfferInfo();
        info2.getFieldValues().put("key2", "value2");
        order.getAdditionalOfferInformation().put(offerCode2.getOffer(), info2);
        
        order = orderService.save(order, true);

        assert (order.getSubTotal().equals(new Money(180D)));

        order = orderService.findOrderById(order.getId());
        assert(order.getAdditionalOfferInformation().get(offerCode1.getOffer()).equals(info1));
    }

    @Test(groups =  {"testOfferLowerSalePriceWithNotCombinableOffer2"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer"})
    @Transactional
    public void testOfferLowerSalePriceWithNotCombinableOffer2() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, 50D, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, 50D, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("25 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 25, null, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("35 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 35, null, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("45 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 45, null, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, null, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        // The $45 offer got applied to all 4 items. The $30 order offer is also applied as an adjustment but since that would
        // push the order into negative ($20 total - $30 == -$10) then only $20 of that is actually applied to the total.
        // Important to note that this extra $20 order adjustment is not included in the subtotal (which is just the sum of
        // all of the order items) but is applied in to the full Total via the TotalAcivity in the pricing workflow
        assert order.getSubTotal().equals(new Money("20"));
        assert order.getOrderAdjustmentsValue().equals(new Money("20"));
    }

    @Test(groups =  {"testOfferNotStackableOrderOffers"}, dependsOnGroups = { "testOfferLowerSalePriceWithNotCombinableOffer2"})
    @Transactional
    public void testOfferNotStackableOrderOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Order Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF, 20, "order.subTotal.getAmount() >= 400", true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, "order.subTotal.getAmount() >= 400", false, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("100 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 100, "order.subTotal.getAmount() >= 400", false, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, "order.subTotal.getAmount() < 400", false, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        //     assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(240D));
    }

    @Test(groups =  {"testOfferNotCombinableOrderOffers"}, dependsOnGroups = { "testOfferNotStackableOrderOffers"})
    @Transactional
    public void testOfferNotCombinableOrderOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Order Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF, 20, null, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("30 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 30, null, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, null, true, false, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(290D));
    }

    @Test(groups =  {"testOfferNotCombinableOrderOffersWithItemOffer"}, dependsOnGroups = { "testOfferNotCombinableOrderOffers"})
    @Transactional
    public void testOfferNotCombinableOrderOffersWithItemOffer() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 100D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 2, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, null, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("10 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 10, null, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("15 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 15, null, true, true, 1));
        order.addOfferCode(offerUtil.createOfferCode("90 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 90, null, true, false, 1));
        order.addOfferCode(offerUtil.createOfferCode("50 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 50, null, true, true, 1));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        // Item offers (10+15 are combinable resulting in $100 off for the item.   Plus $90 as the best offer for the order).
        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(210D));
    }

    @Test(groups =  {"testGlobalOffers"}, dependsOnGroups = { "testOfferNotCombinableOrderOffersWithItemOffer"})
    @Transactional
    public void testGlobalOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        
        Offer offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
        offer.setAutomaticallyAdded(true);
        offer = offerService.save(offer);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testCustomerAssociatedOffers"}, dependsOnGroups = { "testGlobalOffers"})
    @Transactional
    public void testCustomerAssociatedOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id == " + sku1, true, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("3 Dollars Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.AMOUNT_OFF, 3, "discreteOrderItem.sku.id != " + sku1, true, true, 10));
        
        Offer offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
        offer = offerService.save(offer);
        CustomerOffer customerOffer = new CustomerOfferImpl();
        customerOffer.setCustomer(order.getCustomer());
        customerOffer.setOffer(offer);
        customerOffer = customerOfferDao.save(customerOffer);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert order.getSubTotal().subtract(order.getOrderAdjustmentsValue()).equals(new Money(31.80D));
    }

    @Test(groups =  {"testCustomerAssociatedOffers2"}, dependsOnGroups = { "testCustomerAssociatedOffers"})
    @Transactional
    public void testCustomerAssociatedOffers2() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 20D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(offerUtil.createOfferCode("15%OFF", "15 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 15, null, false, true, 0, null));

        Offer offer1 = offerUtil.createOffer("20 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 20, "discreteOrderItem.sku.id == " + sku1, false, true, 0, null);
        offerDao.save(offer1);
        CustomerOffer customerOffer1 = new CustomerOfferImpl();
        customerOffer1.setCustomer(order.getCustomer());
        customerOffer1.setOffer(offer1);
        customerOfferDao.save(customerOffer1);

        Offer offer2 = offerUtil.createOffer("10 Percent Off Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 10, "discreteOrderItem.sku.id == " + sku2, false, true, 0, null);
        offerDao.save(offer2);
        CustomerOffer customerOffer2 = new CustomerOfferImpl();
        customerOffer2.setCustomer(order.getCustomer());
        customerOffer2.setOffer(offer2);
        customerOfferDao.save(customerOffer2);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert (order.getSubTotal().equals(new Money(33D)));
    }

    @Test(groups =  {"testFulfillmentGroupOffers"}, dependsOnGroups = { "testCustomerAssociatedOffers2"})
    @Transactional
    public void testFulfillmentGroupOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        order.setFulfillmentGroups(orderUtil.createFulfillmentGroups(option, 5D, order));
        orderService.save(order, false);
        
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 10D, null, true, 2, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 20D, null, true, 1, order));

        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Item Offer", OfferType.FULFILLMENT_GROUP, OfferDiscountType.PERCENT_OFF, 20, null, true, true, 10));
        order.addOfferCode(offerUtil.createOfferCode("3 Dollars Off Item Offer", OfferType.FULFILLMENT_GROUP, OfferDiscountType.AMOUNT_OFF, 3, null, true, true, 10));
        
        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);
        offerService.applyAndSaveFulfillmentGroupOffersToOrder(offers, order);

        assert (order.getFulfillmentGroups().get(0).getShippingPrice().equals(new Money(1.6D)));
    }

    @Test(groups =  {"testOfferDelete"}, dependsOnGroups = { "testFulfillmentGroupOffers"})
    @Transactional
    public void testOfferDelete() throws Exception {
        CustomerOffer customerOffer = customerOfferDao.create();
        Customer customer = createCustomer();
        Long customerId = customer.getId();
        customerOffer.setCustomer(customerService.saveCustomer(customer));

        Offer offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
        offer = offerService.save(offer);
        Long offerId = offer.getId();
        offerDao.delete(offer);
        Offer deletedOffer = offerDao.readOfferById(offerId);
        assert ((OfferImpl) deletedOffer).getArchived() == 'Y';

        offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
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
        Offer offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
        offer = offerService.save(offer);
        List<Offer> allOffers = offerService.findAllOffers();
        assert allOffers != null && allOffers.isEmpty() == false;
    }

    @Test(groups =  {"testOfferCodeDao"}, dependsOnGroups = { "testReadAllOffers"})
    @Transactional
    public void testOfferCodeDao() throws Exception {
        String offerCodeString = "AJ's Code";
        OfferCode offerCode = offerUtil.createOfferCode("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10);
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
        Offer offer = offerUtil.createOffer("1.20 Dollars Off Order Offer", OfferType.ORDER, OfferDiscountType.AMOUNT_OFF, 1.20, null, true, true, 10, null);
        CustomerOffer customerOffer = new CustomerOfferImpl();
        customerOffer.setCustomer(order.getCustomer());
        customerOffer.setOffer(offer);
        customerOffer = customerOfferDao.save(customerOffer);
        CustomerOffer customerOfferTest = customerOfferDao.readCustomerOfferById(customerOffer.getId());

        assert (customerOffer.getId().equals(customerOfferTest.getId()));
    }

}
