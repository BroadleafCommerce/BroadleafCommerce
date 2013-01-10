/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.domain;

import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
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
    private PromotableOrderItem promotableOrderItem;

    @Override
    protected void setUp() throws Exception {
        OfferDataItemProvider dataProvider = new OfferDataItemProvider();
        
        offer = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20", 
            OfferDiscountType.PERCENT_OFF, 
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        
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
        
        category1.getAllProducts().add(product1);
        
        Category category2 = new CategoryImpl();
        category2.setName("test2");
        category2.setId(2L);
        
        Product product2 = new ProductImpl();
        
        Sku sku2 = new SkuImpl();
        sku2.setName("test2");
        sku2.setDiscountable(true);
        sku2.setRetailPrice(new Money(29.99D));
        product2.setDefaultSku(sku2);
        
        category2.getAllProducts().add(product2);
        
        DiscreteOrderItemImpl orderItem1 = new DiscreteOrderItemImpl();
        orderItem1.setCategory(category1);
        orderItem1.setName("test1");
        orderItem1.setOrderItemType(OrderItemType.DISCRETE);
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);
        orderItem1.setSku(sku1);
        orderItem1.setRetailPrice(new Money(19.99D));
        orderItem1.setPrice(new Money(19.99D));
        
        promotableOrderItem = new PromotableOrderItemImpl(orderItem1, null, new PromotableItemFactoryImpl());
        
        List<PromotableOrderItem> items = new ArrayList<PromotableOrderItem>();
        items.add(promotableOrderItem);
        
        promotableCandidate = new PromotableCandidateItemOfferImpl(candidate);
        
        promotableCandidate.getCandidateTargets().addAll(items);
        promotableCandidate.setOffer(offer);
    }
    
    public void testCalculatePotentialSavings() throws Exception {
        Money savings = promotableCandidate.calculatePotentialSavings();
        assertTrue(savings.equals(new Money(4D)));
    }
    
    public void testCalculateSavingsForOrderItem() throws Exception {
        Money savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(2D)));
        
        offer.setDiscountType(OfferDiscountType.AMOUNT_OFF);
        savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(10D)));
        
        offer.setDiscountType(OfferDiscountType.FIX_PRICE);
        savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        assertTrue(savings.equals(new Money(19.99D - 10D)));
    }
    
    public void testCalculateMaximumNumberOfUses() throws Exception {
        int maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        assertTrue(maxOfferUses == 2);
        
        offer.setMaxUses(1);
        maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        assertTrue(maxOfferUses == 1);
    }
    
    public void testCalculateMaxUsesForItemCriteria() throws Exception {
        int maxItemCriteriaUses = 9999;
        for (OfferItemCriteria targetCriteria : offer.getTargetItemCriteria()) {
            int temp = promotableCandidate.calculateMaxUsesForItemCriteria(targetCriteria, offer);
            maxItemCriteriaUses = Math.min(maxItemCriteriaUses, temp);
        }
        assertTrue(maxItemCriteriaUses == 2);
        
        PromotionQualifier qualifier = new PromotionQualifier();
        qualifier.setPromotion(offer);
        qualifier.setQuantity(1);
        promotableOrderItem.getPromotionQualifiers().add(qualifier);

        maxItemCriteriaUses = 9999;
        for (OfferItemCriteria targetCriteria : offer.getTargetItemCriteria()) {
            int temp = promotableCandidate.calculateMaxUsesForItemCriteria(targetCriteria, offer);
            maxItemCriteriaUses = Math.min(maxItemCriteriaUses, temp);
        }
        assertTrue(maxItemCriteriaUses == 1);
        
        PromotionDiscount discount = new PromotionDiscount();
        discount.setPromotion(offer);
        discount.setQuantity(1);
        promotableOrderItem.getPromotionDiscounts().add(discount);

        maxItemCriteriaUses = 9999;
        for (OfferItemCriteria targetCriteria : offer.getTargetItemCriteria()) {
            int temp = promotableCandidate.calculateMaxUsesForItemCriteria(targetCriteria, offer);
            maxItemCriteriaUses = Math.min(maxItemCriteriaUses, temp);
        }
        assertTrue(maxItemCriteriaUses == 0);
    }
}
