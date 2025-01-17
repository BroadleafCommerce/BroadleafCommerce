/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.service.type.OfferAdjustmentType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.type.FulfillmentType;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;

/**
 * @author Chad Harchar (charchar)
 */
public class FutureCreditOfferTest extends CommonSetupBaseTest {

    private CreateOfferUtility offerUtil;
    private CreateOrderEntityUtility orderUtil;

    @Resource
    private OfferService offerService;

    @Resource
    private OfferDao offerDao;

    @Resource
    private OfferCodeDao offerCodeDao;

    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;

    protected final OfferDataItemProvider dataProvider = new OfferDataItemProvider();


    private long sku1;
    private long sku2;

    @Test(groups = { "offerCreateSku1" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class)
    @Rollback(false)
    public void createSku1(Sku sku) {
        offerUtil = new CreateOfferUtility(offerDao, offerCodeDao, offerService);
        orderUtil = new CreateOrderEntityUtility(catalogService, orderItemService, isoService, countryService);
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
    public void testTwoFutureCreditPercentOffOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Item Offer", 
                "10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF, 
                10, null, false, true, 10, null, 
                OfferAdjustmentType.FUTURE_CREDIT));
        order.addOfferCode(offerUtil.createOfferCode("15 Percent Off Item Sku2 Offer",
                "15 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM,  OfferDiscountType.PERCENT_OFF, 
                15, "discreteOrderItem.sku.id == " + sku2, false, true, 10, 
                null, OfferAdjustmentType.FUTURE_CREDIT));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1100D) ));
        assert ( order.getTotalAdjustmentsValue().equals(new Money(0D)));
        assert ( order.getAllFutureCreditAdjustments().size() == 2);
        assert ( order.getTotalFutureCreditAdjustmentsValue().equals(new Money(115D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testFutureCreditAndOrderDiscountPercentOffOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Item Offer",
                "10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF,
                10, null, false, true, 10, null,
                OfferAdjustmentType.FUTURE_CREDIT));
        order.addOfferCode(offerUtil.createOfferCode("15 Percent Off Item Sku2 Offer",
                "15 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM,  OfferDiscountType.PERCENT_OFF,
                15, "discreteOrderItem.sku.id == " + sku2, false, true, 10,
                null, OfferAdjustmentType.ORDER_DISCOUNT));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1085D) ));
        assert ( order.getTotalAdjustmentsValue().equals(new Money(15D)));
        assert ( order.getAllFutureCreditAdjustments().size() == 1);
        assert ( order.getTotalFutureCreditAdjustmentsValue().equals(new Money(100D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testFutureCreditAndOrderDiscountOrderAndOrderItemOffers() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Offer",
                "10 Percent Off All Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF,
                10, null, false, true, 10, null,
                OfferAdjustmentType.FUTURE_CREDIT));
        order.addOfferCode(offerUtil.createOfferCode("15 Percent Off Item Sku2 Offer",
                "15 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM,  OfferDiscountType.PERCENT_OFF,
                15, "discreteOrderItem.sku.id == " + sku2, false, true, 10,
                null, OfferAdjustmentType.ORDER_DISCOUNT));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1085D) ));
        assert ( order.getTotalAdjustmentsValue().equals(new Money(15D)));
        assert ( order.getAllFutureCreditAdjustments().size() == 1);
        assert ( order.getTotalFutureCreditAdjustmentsValue().equals(new Money(108.5D) ));
    }

    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1", "offerCreateSku2" })
    @Transactional
    public void testFutureCreditAllOfferTypes() throws Exception {
        Order order = orderService.createNewCartForCustomer(createCustomer());
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        option.setFulfillmentType(FulfillmentType.PHYSICAL_SHIP);
        List<FulfillmentGroup> fulfillmentGroups = orderUtil.createFulfillmentGroups(option, 5D, order);
        fulfillmentGroups.add(orderUtil.createFulfillmentGroup2(option, 4D, order));
        order.setFulfillmentGroups(fulfillmentGroups);
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku1, 1000D, null, true, 1, order));
        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku2, 100D, null, true, 1, order));
        order.addOfferCode(offerUtil.createOfferCode("10 Percent Off All Offer",
                "10 Percent Off All Offer", OfferType.ORDER, OfferDiscountType.PERCENT_OFF,
                10, null, false, true, 10, null,
                OfferAdjustmentType.FUTURE_CREDIT));
        order.addOfferCode(offerUtil.createOfferCode("15 Percent Off Item Sku2 Offer",
                "15 Percent Off Item Sku2 Offer", OfferType.ORDER_ITEM,  OfferDiscountType.PERCENT_OFF,
                15, "discreteOrderItem.sku.id == " + sku2, false, true, 10,
                null, OfferAdjustmentType.FUTURE_CREDIT));
        order.addOfferCode(offerUtil.createOfferCode("20 Percent Off Shipping Offer",
                "20 Percent Off Shipping Offer", OfferType.FULFILLMENT_GROUP,  OfferDiscountType.PERCENT_OFF,
                20, null, false, true, 10,
                null, OfferAdjustmentType.FUTURE_CREDIT));

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);
        offerService.applyAndSaveFulfillmentGroupOffersToOrder(offers, order);

        assert ( order.getSubTotal().equals(new Money(1100D) ));
        assert ( order.getTotalFulfillmentCharges().equals(new Money(9D) ));
        assert ( order.getTotalAdjustmentsValue().equals(new Money(0D)));
        assert ( order.getAllFutureCreditAdjustments().size() == 4);
        assert ( order.getTotalFutureCreditAdjustmentsValue().equals(new Money(125.3D) ));
    }
}
