/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.easymock.classextension.EasyMock;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * 
 * @author jfischer
 *
 */
public class OrderOfferProcessorTest extends TestCase {

    protected OfferDao offerDaoMock;
    protected OrderOfferProcessorImpl orderProcessor;
    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;
    
    @Override
    protected void setUp() throws Exception {
        offerDaoMock = EasyMock.createMock(OfferDao.class);
        offerTimeZoneProcessorMock = EasyMock.createMock(OfferTimeZoneProcessor.class);
        orderProcessor = new OrderOfferProcessorImpl();
        orderProcessor.setOfferDao(offerDaoMock);
        orderProcessor.setOfferTimeZoneProcessor(offerTimeZoneProcessorMock);
        orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
    }
    
    public void replay() {
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();
        EasyMock.replay(offerDaoMock);
        EasyMock.replay(offerTimeZoneProcessorMock);
    }
    
    public void verify() {
        EasyMock.verify(offerDaoMock);
        EasyMock.verify(offerTimeZoneProcessorMock);
    }
    
    public void testFilterOffers() throws Exception {
        replay();
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createCustomerBasedOffer("customer.registered==true", dataProvider.yesterday(), dataProvider.yesterday(), OfferDiscountType.PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        //confirm out-of-date orders are filtered out
        assertTrue(offers.size() == 0);
        
        offers = dataProvider.createCustomerBasedOffer("customer.registered==true", dataProvider.yesterday(), dataProvider.tomorrow(), OfferDiscountType.PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        //confirm valid customer offer is retained
        assertTrue(offers.size() == 1);
        
        offers = dataProvider.createCustomerBasedOffer("customer.registered==false", dataProvider.yesterday(), dataProvider.tomorrow(), OfferDiscountType.PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        //confirm invalid customer offer is culled
        assertTrue(offers.size() == 0);
        
        verify();
    }
    
    public void testFilterOrderLevelOffer() throws Exception {
        replay();
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<PromotableCandidateOrderOffer> qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF);
        
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        
        //test that the valid order offer is included
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)));
        
        qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        
        //test that the valid order offer is included
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offers.get(0))) ;
         
        qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([5,6] contains discreteOrderItem.category.id.intValue())");
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        
        //test that the invalid order offer is excluded
        assertTrue(qualifiedOffers.size() == 0) ;
        
        verify();
    }
    
    public void testCouldOfferApplyToOrder() throws Exception {
        replay();
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF);
        boolean couldApply = orderProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        //test that the valid order offer is included
        assertTrue(couldApply);
        
        offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()==0", OfferDiscountType.PERCENT_OFF);
        couldApply = orderProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        //test that the invalid order offer is excluded
        assertFalse(couldApply);
        
        verify();
    }
    
    public void testCouldOrderItemMeetOfferRequirement() throws Exception {
        replay();
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        OfferQualifyingCriteriaXref xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        boolean couldApply = orderProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        //test that the valid order offer is included
        assertTrue(couldApply);
        
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        couldApply = orderProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        //test that the invalid order offer is excluded
        assertFalse(couldApply);
        
        verify();
    }
    
    public void testCouldOfferApplyToOrderItems() throws Exception {
        replay();
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the valid order offer is included
        assertTrue(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);
        
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the invalid order offer is excluded because there are no qualifying items
        assertFalse(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);
        
        verify();
    }
}
