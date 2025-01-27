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
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.FixedPriceFulfillmentOptionImpl;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import jakarta.annotation.Resource;

/**
 * @author Chad Harchar (charchar)
 */
public class OfferAuditTest extends CommonSetupBaseTest {

    private CreateOfferUtility offerUtil;
    private CreateOrderEntityUtility orderUtil;

    @Resource
    private OfferService offerService;

    @Resource
    private OfferDao offerDao;

    @Resource
    private OfferCodeDao offerCodeDao;

    @Resource
    private OfferAuditDao offerAuditDao;

    @Resource
    private OfferAuditService offerAuditService;

    @Resource(name = "blOrderItemService")
    private OrderItemService orderItemService;
    
    @Resource
    private CheckoutService checkoutService;

    protected final OfferDataItemProvider dataProvider = new OfferDataItemProvider();


    private long sku;

    @Test(groups = { "offerCreateSku1" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class)
    @Rollback(false)
    public void createSku(Sku sku) {
        offerUtil = new CreateOfferUtility(offerDao, offerCodeDao, offerService);
        orderUtil = new CreateOrderEntityUtility(catalogService, orderItemService, isoService, countryService);
        sku.setSalePrice(new Money(BigDecimal.valueOf(10.0)));
        sku.setRetailPrice(new Money(BigDecimal.valueOf(15.0)));
        sku.setName("test1");
        assert sku.getId() == null;
        sku = catalogService.saveSku(sku);
        assert sku.getId() != null;
        this.sku = sku.getId();
    }


    @Test(groups =  {"testPercentageOffOffer"}, dependsOnGroups = { "offerCreateSku1" })
    @Transactional
    public void testMinimumDaysPerUsageAudit() throws Exception {

        Offer offer = offerUtil.createOffer(
                "10 Percent Off All Item Offer", OfferType.ORDER_ITEM, OfferDiscountType.PERCENT_OFF,
                10, null, false, true, 10, null);

        offer.setMaxUsesPerCustomer(2L);
        offer.setMinimumDaysPerUsage(1L);

        OfferCode offerCode = offerUtil.createOfferCode("10 Percent Off All Item Offer Code", offer);


        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.MINUTE, -5);

        Customer customer = createCustomer();

        OfferAudit offerAudit = offerAuditDao.create();
        
        offerAudit.setId(1L);
        offerAudit.setCustomerId(customer.getId());
        offerAudit.setOfferId(offer.getId());
        offerAudit.setOrderId(null);
        offerAudit.setRedeemedDate(currentDate.getTime());

        offerAuditService.save(offerAudit);

        OfferAudit offerAudit2 = offerAuditDao.create();

        offerAudit2.setId(2L);
        offerAudit2.setCustomerId(customer.getId());
        offerAudit2.setOfferId(offer.getId());
        offerAudit2.setOrderId(null);
        offerAudit2.setRedeemedDate(currentDate.getTime());

        offerAuditService.save(offerAudit2);


        Order order = orderService.createNewCartForCustomer(customer);
        FixedPriceFulfillmentOption option = new FixedPriceFulfillmentOptionImpl();
        option.setPrice(new Money(0));
        orderService.save(order, false);

        order.addOrderItem(orderUtil.createDiscreteOrderItem(sku, 100D, null, true, 1, order));
        order.addOfferCode(offerCode);

        List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyAndSaveOffersToOrder(offers, order);
        
        order.setTotal(order.getSubTotal());
        
        boolean maxUsesExceeded = false;
        try {
            checkoutService.performCheckout(order);
        } catch (CheckoutException exception) {
            if (exception.getCause() instanceof OfferMaxUseExceededException) {
                maxUsesExceeded = true;
            }
        }
        
        assert (maxUsesExceeded);
        
        currentDate.add(Calendar.DAY_OF_YEAR, -2);
        offerAudit.setRedeemedDate(currentDate.getTime());
        offerAuditService.delete(offerAudit);
        offerAuditService.save(offerAudit);

        offerAudit2.setRedeemedDate(currentDate.getTime());
        offerAuditService.delete(offerAudit2);
        offerAuditService.save(offerAudit2);

        maxUsesExceeded = false;
        try {
            checkoutService.performCheckout(order);
        } catch (CheckoutException exception) {
            if (exception.getCause() instanceof OfferMaxUseExceededException) {
                maxUsesExceeded = true;
            }
        }

        assert (!maxUsesExceeded);

        assert ( order.getSubTotal().equals(new Money(90D) ));
        assert ( order.getTotalAdjustmentsValue().equals(new Money(10D)));
    }
    
    
}
