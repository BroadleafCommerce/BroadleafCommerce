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

package org.broadleafcommerce.core.order.domain;

import junit.framework.TestCase;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.common.money.Money;

/**
 * 
 * @author jfischer
 *
 */
public class OrderItemTest extends TestCase {

    private PromotableOrderItem orderItem1;
    private Offer offer;
    
    @Override
    protected void setUp() throws Exception {
        DiscreteOrderItemImpl discreteOrderItem1 = new DiscreteOrderItemImpl();
        discreteOrderItem1.setName("test1");
        discreteOrderItem1.setOrderItemType(OrderItemType.DISCRETE);
        discreteOrderItem1.setQuantity(2);
        discreteOrderItem1.setRetailPrice(new Money(19.99D));
        discreteOrderItem1.setPrice(new Money(19.99D));
        orderItem1 = new PromotableOrderItemImpl(discreteOrderItem1, null, new PromotableItemFactoryImpl());
        
        OfferDataItemProvider dataProvider = new OfferDataItemProvider();
        
        offer = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20", 
            OfferDiscountType.PERCENT_OFF, 
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
    }

    public void testGetQuantityAvailableToBeUsedAsQualifier() throws Exception {
        int quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //no previous qualifiers, so all quantity is available
        assertTrue(quantity == 2);
        
        PromotionDiscount discount = new PromotionDiscount();
        discount.setPromotion(offer);
        discount.setQuantity(1);
        orderItem1.getPromotionDiscounts().add(discount);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //items that have already received this promotion cannot get it again
        assertTrue(quantity==1);
        
        Offer testOffer = new OfferImpl();
        testOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.NONE);
        testOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.NONE);
        
        discount.setPromotion(testOffer);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //this item received a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        assertTrue(quantity==1);
        
        testOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER);
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //this item received a different promotion, but the restriction rule is QUALIFIER, so this item can be a qualifier for this promotion
        assertTrue(quantity==2);
        
        orderItem1.getPromotionDiscounts().clear();
        
        PromotionQualifier qualifier = new PromotionQualifier();
        qualifier.setPromotion(offer);
        qualifier.setQuantity(1);
        orderItem1.getPromotionQualifiers().add(qualifier);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //items that have already qualified for this promotion cannot qualify again
        assertTrue(quantity==1);
        
        qualifier.setPromotion(testOffer);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //this item qualified for a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        assertTrue(quantity==1);
        
        testOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsQualifier(offer);
        //this item qualified for a different promotion, but the restriction rule is QUALIFIER, so this item can be a qualifier for this promotion
        assertTrue(quantity==2);
    }
    
    public void testGetQuantityAvailableToBeUsedAsTarget() throws Exception {
        int quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //no previous qualifiers, so all quantity is available
        assertTrue(quantity == 2);
        
        PromotionDiscount discount = new PromotionDiscount();
        discount.setPromotion(offer);
        discount.setQuantity(1);
        orderItem1.getPromotionDiscounts().add(discount);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //items that have already received this promotion cannot get it again
        assertTrue(quantity==1);
        
        Offer tempOffer = new OfferImpl();
        tempOffer.setStackable(true);
        tempOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.NONE);
        tempOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.NONE);
        
        discount.setPromotion(tempOffer);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //this item received a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        assertTrue(quantity==1);
        
        tempOffer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.TARGET);
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //this item received a different promotion, but the restriction rule is QUALIFIER, so this item can be a qualifier for this promotion
        assertTrue(quantity==2);
        
        orderItem1.getPromotionDiscounts().clear();
        
        PromotionQualifier qualifier = new PromotionQualifier();
        qualifier.setPromotion(offer);
        qualifier.setQuantity(1);
        orderItem1.getPromotionQualifiers().add(qualifier);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //items that have already qualified for this promotion cannot qualify again
        assertTrue(quantity==1);
        
        qualifier.setPromotion(tempOffer);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //this item qualified for a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        assertTrue(quantity==1);
        
        tempOffer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.TARGET);
        
        quantity = orderItem1.getQuantityAvailableToBeUsedAsTarget(offer);
        //this item qualified for a different promotion, but the restriction rule is QUALIFIER, so this item can be a qualifier for this promotion
        assertTrue(quantity==2);
    }
}
