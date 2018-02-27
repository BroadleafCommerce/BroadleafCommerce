/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.domain;

import junit.framework.TestCase;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtility;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtilityImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetailImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.broadleafcommerce.core.order.service.type.OrderItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class CandidateItemOfferTest extends TestCase {
    
    private PromotableCandidateItemOffer promotableCandidate;
    private Offer offer;
    private PromotableCandidateItemOffer candidateOffer;
    private PromotableOrderItem promotableOrderItem;
    private PromotableOrder promotableOrder;
    private PromotableOrderItemPriceDetail priceDetail;
    private PromotableOfferUtility promotableOfferUtility;

    @Override
    protected void setUp() throws Exception {
        promotableOfferUtility = new PromotableOfferUtilityImpl();
        OfferDataItemProvider dataProvider = new OfferDataItemProvider();
        
        CandidateItemOfferImpl candidate = new CandidateItemOfferImpl();
        
        Category category1 = new CategoryImpl();
        category1.setName("test1");
        category1.setId(1L);
        
        Product product1 = new ProductImpl();
        
        Sku sku1 = new SkuImpl();
        sku1.setName("test1");
        sku1.setDiscountable(true);
        sku1.setRetailPrice(new Money(19.99D));
        product1.setDefaultSku(sku1);

        CategoryProductXref xref1 = new CategoryProductXrefImpl();
        xref1.setProduct(product1);
        xref1.setCategory(category1);
        
        category1.getAllProductXrefs().add(xref1);

        Category category2 = new CategoryImpl();
        category2.setName("test2");
        category2.setId(2L);
        
        Product product2 = new ProductImpl();
        
        Sku sku2 = new SkuImpl();
        sku2.setName("test2");
        sku2.setDiscountable(true);
        sku2.setRetailPrice(new Money(29.99D));
        product2.setDefaultSku(sku2);

        CategoryProductXref xref2 = new CategoryProductXrefImpl();
        xref2.setProduct(product2);
        xref2.setCategory(category2);

        category2.getAllProductXrefs().add(xref2);
        
        DiscreteOrderItemImpl orderItem1 = new DiscreteOrderItemImpl();
        orderItem1.setCategory(category1);
        orderItem1.setName("test1");
        orderItem1.setOrderItemType(OrderItemType.DISCRETE);
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);
        orderItem1.setSku(sku1);
        
        Order order = new OrderImpl();
        orderItem1.setOrder(order);
        
        promotableOrder = new PromotableOrderImpl(order, new PromotableItemFactoryImpl(promotableOfferUtility), false);
        offer = dataProvider.createItemBasedOfferWithItemCriteria(
                "order.subTotal.getAmount()>20",
                OfferDiscountType.PERCENT_OFF,
                "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
                "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
                ).get(0);
        candidateOffer = new PromotableCandidateItemOfferImpl(promotableOrder, offer);
        
        promotableOrderItem = new PromotableOrderItemImpl(orderItem1, null, new PromotableItemFactoryImpl(promotableOfferUtility), false);
        OrderItemPriceDetail pdetail = new OrderItemPriceDetailImpl();
        pdetail.setOrderItem(orderItem1);
        pdetail.setQuantity(2);
        priceDetail = new PromotableOrderItemPriceDetailImpl(promotableOrderItem, 2);
        
        List<PromotableOrderItem> items = new ArrayList<PromotableOrderItem>();
        items.add(promotableOrderItem);
        
        promotableCandidate = new PromotableCandidateItemOfferImpl(promotableOrder, offer);
        
        OfferTargetCriteriaXref xref = offer.getTargetItemCriteriaXref().iterator().next();
        promotableCandidate.getCandidateTargetsMap().put(xref.getOfferItemCriteria(), items);
    }
    
    public void testCalculateSavingsForOrderItem() throws Exception {
        Money savings = promotableOfferUtility.calculateSavingsForOrderItem(promotableCandidate, promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(2D)));
        
        offer.setDiscountType(OfferDiscountType.AMOUNT_OFF);
        savings = promotableOfferUtility.calculateSavingsForOrderItem(promotableCandidate, promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(10D)));
        
        offer.setDiscountType(OfferDiscountType.FIX_PRICE);
        savings = promotableOfferUtility.calculateSavingsForOrderItem(promotableCandidate, promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(19.99D - 10D)));
    }
    
    public void testCalculateMaximumNumberOfUses() throws Exception {
        int maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        assertTrue(maxOfferUses == 2);
        
        offer.setMaxUsesPerOrder(1);
        maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        assertTrue(maxOfferUses == 1);
    }
    
    public void testCalculateMaxUsesForItemCriteria() throws Exception {
        int maxItemCriteriaUses = 9999;
        for (OfferTargetCriteriaXref targetXref : offer.getTargetItemCriteriaXref()) {
            int temp = promotableCandidate.calculateMaxUsesForItemCriteria(targetXref.getOfferItemCriteria(), offer);
            maxItemCriteriaUses = Math.min(maxItemCriteriaUses, temp);
        }
        assertTrue(maxItemCriteriaUses == 2);
    }
}
