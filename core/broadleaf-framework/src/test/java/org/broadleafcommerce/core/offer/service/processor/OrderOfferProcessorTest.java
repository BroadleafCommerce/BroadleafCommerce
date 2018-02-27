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
package org.broadleafcommerce.core.offer.service.processor;

import junit.framework.TestCase;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXrefImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtility;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOfferUtilityImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.easymock.classextension.EasyMock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
    protected PromotableOfferUtility promotableOfferUtility;
    
    @Override
    protected void setUp() throws Exception {
        offerDaoMock = EasyMock.createMock(OfferDao.class);
        offerTimeZoneProcessorMock = EasyMock.createMock(OfferTimeZoneProcessor.class);
        promotableOfferUtility = new PromotableOfferUtilityImpl();
        orderProcessor = new OrderOfferProcessorImpl(promotableOfferUtility);
        orderProcessor.setOfferDao(offerDaoMock);
        orderProcessor.setOfferTimeZoneProcessor(offerTimeZoneProcessorMock);
        orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl(promotableOfferUtility));
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
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);
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
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);
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
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);
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
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);
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
        
        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);
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

    public void testQualifyingQuantity() throws Exception {
        replay();

        PromotableOrder order = dataProvider.createBasicPromotableOrder(promotableOfferUtility);

        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", OfferDiscountType.PERCENT_OFF);

        Offer firstOffer = offers.get(0);

        OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
        int originalQuantityOnOrder = 5;
        qualCriteria.setQuantity(originalQuantityOnOrder + 1);
        qualCriteria.setMatchRule("([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        Set<OfferQualifyingCriteriaXref> criterias = new HashSet<OfferQualifyingCriteriaXref>();
        OfferQualifyingCriteriaXref xref = new OfferQualifyingCriteriaXrefImpl();
        xref.setOffer(firstOffer);
        xref.setOfferItemCriteria(qualCriteria);
        criterias.add(xref);

        firstOffer.setQualifyingItemCriteriaXref(criterias);

        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the valid order offer is not included
        assertTrue(!candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);

        int quantity = orderItems.get(0).getOrderItem().getQuantity();
        orderItems.get(0).getOrderItem().setQuantity(quantity + 1);

        candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the valid order offer is included
        assertTrue(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);

        verify();
    }
}
