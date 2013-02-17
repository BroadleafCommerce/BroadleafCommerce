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

package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OfferServiceImpl;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderMultishipOption;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author jfischer
 *
 */
public class ItemOfferProcessorTest extends TestCase {

    private OfferDao offerDaoMock;
    private OrderService orderServiceMock;
    private OfferServiceImpl offerService;
    private OrderItemService orderItemServiceMock;
    private FulfillmentGroupItemDao fgItemDaoMock;
    private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    private FulfillmentGroupService fgServiceMock;
    private OrderMultishipOptionService multishipOptionServiceMock;

    private ItemOfferProcessorImpl itemProcessor;

    @Override
    protected void setUp() throws Exception {

        CustomerOfferDao customerOfferDaoMock = EasyMock.createMock(CustomerOfferDao.class);
        OfferCodeDao offerCodeDaoMock = EasyMock.createMock(OfferCodeDao.class);
        offerDaoMock = EasyMock.createMock(OfferDao.class);
        orderServiceMock = EasyMock.createMock(OrderService.class);
        orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
        fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
        fgServiceMock = EasyMock.createMock(FulfillmentGroupService.class);
        multishipOptionServiceMock = EasyMock.createMock(OrderMultishipOptionService.class);

        itemProcessor = new ItemOfferProcessorImpl();
        itemProcessor.setOfferDao(offerDaoMock);
        itemProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());

        offerService = new OfferServiceImpl();

        OrderOfferProcessor orderProcessor = new OrderOfferProcessorImpl();
        orderProcessor.setOfferDao(offerDaoMock);
        orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());

        offerService.setCustomerOfferDao(customerOfferDaoMock);
        offerService.setOfferCodeDao(offerCodeDaoMock);
        offerService.setOfferDao(offerDaoMock);
        offerService.setOrderOfferProcessor(orderProcessor);
        offerService.setItemOfferProcessor(itemProcessor);
        offerService.setPromotableItemFactory(new PromotableItemFactoryImpl());
        offerService.setOrderService(orderServiceMock);
    }

    public void replay() {
        EasyMock.replay(offerDaoMock);
        EasyMock.replay(orderServiceMock);
        EasyMock.replay(orderItemServiceMock);
        EasyMock.replay(fgItemDaoMock);
        EasyMock.replay(fgServiceMock);
        EasyMock.replay(multishipOptionServiceMock);
    }

    public void verify() {
        EasyMock.verify(offerDaoMock);
        EasyMock.verify(orderServiceMock);
        EasyMock.verify(orderItemServiceMock);
        EasyMock.verify(fgItemDaoMock);
        EasyMock.verify(fgServiceMock);
        EasyMock.verify(multishipOptionServiceMock);
    }

    public void testFilterItemLevelOffer() {
        replay();

        PromotableOrder order = dataProvider.createBasicOrder();
        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
                OfferDiscountType.PERCENT_OFF,
            null,
            null
        );

        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));

        //test that the valid order item offer is included - legacy format - no qualifier
        //since there's no qualifier, both items can apply
        assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(1).getOffer().equals(offers.get(0)));

        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));

        //test that the valid order item offer is included
        //there is a qualifier and the item qualifying criteria requires only 1, therefore there will be only one qualifier in the qualifiers map
        //we don't know the targets yet, so there's only one CandidateItemOffer for now
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);

        // Add a subtotal requirement that will be met by the item offer.
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );

        offers.get(0).setQualifyingItemSubTotal(new Money(1));
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));

        //test that the valid order item offer is included
        //there is a qualifier and the item qualifying criteria requires only 1, therefore there will be only one qualifier in the qualifiers map
        //we don't know the targets yet, so there's only one CandidateItemOffer for now
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);


        // Add a subtotal requirement that will not be met by the item offer.
        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );

        offers.get(0).setQualifyingItemSubTotal(new Money(99999));
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));

        // Since the item qualification subTotal is not met, the qualified offer size should
        // be zero.
        assertTrue(qualifiedOffers.size() == 0);


        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offers.get(0));

        //test that the invalid order item offer is excluded
        assertTrue(qualifiedOffers.size() == 0);

        verify();
    }

    public void testCouldOfferApplyToOrder() {
        replay();

        PromotableOrder order = dataProvider.createBasicOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            null,
            null
        );

        boolean couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        //test that the valid order item offer is included
        assertTrue(couldApply);

        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()==0",
            OfferDiscountType.PERCENT_OFF,
            null,
            null
        );
        couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        //test that the invalid order item offer is excluded
        assertFalse(couldApply);

        verify();
    }

    public void testCouldOrderItemMeetOfferRequirement() {
        replay();

        PromotableOrder order = dataProvider.createBasicOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );

        boolean couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().iterator().next(), order.getDiscountableOrderItems().get(0));
        //test that the valid order item offer is included
        assertTrue(couldApply);

        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().iterator().next(), order.getDiscountableOrderItems().get(0));
        //test that the invalid order item offer is excluded
        assertFalse(couldApply);

        verify();
    }

    public void testCouldOfferApplyToOrderItems() {
        replay();

        PromotableOrder order = dataProvider.createBasicOrder();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the valid order item offer is included
        //both cart items are valid for qualification and target
        assertTrue(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1 && candidates.getCandidateQualifiersMap().values().iterator().next().size() == 2 && candidates.isMatchedTarget() && candidates.getCandidateTargets().size() == 2);

        offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        );
        candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        //test that the invalid order item offer is excluded because there are no qualifying items
        assertFalse(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);

        verify();
    }

    private int checkOrderItemOfferAppliedCount(Order order) {
        int count = 0;
        for (OrderItem item : order.getOrderItems()) {
            for (OrderItemPriceDetail detail : item.getOrderItemPriceDetails()) {
                count = count + detail.getOrderItemPriceDetailAdjustments().size();
            }
        }
        return count;
    }

    public void testApplyAllItemOffers() throws Exception {
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();

        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class),EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();

        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();

        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();

        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {
            @Override
            public List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<OrderMultishipOption>();
            }
        }).anyTimes();

        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();

        replay();

        PromotableOrder order = dataProvider.createBasicOrder();

        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer1.setId(1L);

        List<Offer> offers = new ArrayList<Offer>();
        offers.add(offer1);


        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offerService.applyOffersToOrder(offers, order.getOrder());

        assertTrue(order.getOrder().getTotalAdjustmentsValue().getAmount().doubleValue() > 0);

        order = dataProvider.createBasicOrder();

        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offer1.setApplyDiscountToSalePrice(false);
        order.getDiscountableOrderItems().get(0).getOrderItem().setSalePrice(new Money(1D));
        order.getDiscountableOrderItems().get(1).getOrderItem().setSalePrice(new Money(1D));
        offerService.applyOffersToOrder(offers, order.getOrder());

        assertTrue(order.getOrder().getTotalAdjustmentsValue().getAmount().doubleValue() == 0);

        verify();
    }

    public void testApplyAdjustments() throws Exception {
        EasyMock.expect(offerDaoMock.createOrderItemPriceDetailAdjustment()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAdjustmentAnswer()).anyTimes();

        EasyMock.expect(fgServiceMock.addItemToFulfillmentGroup(EasyMock.isA(FulfillmentGroupItemRequest.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.removeItem(EasyMock.isA(Long.class), EasyMock.isA(Long.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
        EasyMock.expect(orderServiceMock.save(EasyMock.isA(Order.class), EasyMock.isA(Boolean.class))).andAnswer(OfferDataItemProvider.getSaveOrderAnswer()).anyTimes();

        EasyMock.expect(orderServiceMock.getAutomaticallyMergeLikeItems()).andReturn(true).anyTimes();

        EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();

        EasyMock.expect(multishipOptionServiceMock.findOrderMultishipOptions(EasyMock.isA(Long.class))).andAnswer(new IAnswer<List<OrderMultishipOption>>() {

            @Override
            public List<OrderMultishipOption> answer() throws Throwable {
                return new ArrayList<OrderMultishipOption>();
            }
        }).anyTimes();

        multishipOptionServiceMock.deleteAllOrderMultishipOptions(EasyMock.isA(Order.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(fgServiceMock.collapseToOneFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();

        replay();

        PromotableOrder order = dataProvider.createBasicOrder();

        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer1.setId(1L);
        offer1.getTargetItemCriteria().iterator().next().setQuantity(2);
        offer1.setCombinableWithOtherOffers(false);
        Offer offer2 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer2.setId(2L);

        List<Offer> offerListWithOneOffer = new ArrayList<Offer>();
        offerListWithOneOffer.add(offer1);

        List<Offer> offerListWithTwoOffers = new ArrayList<Offer>();
        offerListWithTwoOffers.add(offer1);
        offerListWithTwoOffers.add(offer2);

        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offerService.applyOffersToOrder(offerListWithOneOffer, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 1);

        // Add the second offer.   The first was nonCombinable so it should still be 1
        order = dataProvider.createBasicOrder();
        offerService.applyOffersToOrder(offerListWithTwoOffers, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 1);


        // Reset offer1 to combinable.   Now both should be applied.
        offer1.setCombinableWithOtherOffers(true);
        order = dataProvider.createBasicOrder();
        offerService.applyOffersToOrder(offerListWithTwoOffers, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 2);

        // Offer 1 back to nonCombinable but don't allow discount to the sale price
        // and make the sale price a better overall offer
        offer1.setCombinableWithOtherOffers(false);
        offer1.setApplyDiscountToSalePrice(false);
        order = dataProvider.createBasicOrder();
        order.getOrder().getOrderItems().get(1).setSalePrice(new Money(10D));
        offerService.applyOffersToOrder(offerListWithOneOffer, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 0);

        // Try again with two offers.   The second should be applied.    
        offerService.applyOffersToOrder(offerListWithTwoOffers, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 1);

        // Trying with 2nd offer as nonCombinable.
        offer1.setCombinableWithOtherOffers(true);
        order.getOrder().getOrderItems().get(1).setSalePrice(null);        
        offer2.setCombinableWithOtherOffers(false);

        offerService.applyOffersToOrder(offerListWithOneOffer, order.getOrder());
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 1);

        offerService.applyOffersToOrder(offerListWithTwoOffers, order.getOrder());
        // BCP:   Changed this test.   2nd offer is notCombinable so only one gets applied. 
        // Old logic had it applying anyway.
        assertTrue(checkOrderItemOfferAppliedCount(order.getOrder()) == 1);
        verify();
    }

    public void testApplyItemQualifiersAndTargets() throws Exception {
        replay();

        PromotableOrder order = dataProvider.createBasicOrder();
        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer1.setId(1L);
        Offer offer2 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer2.setId(2L);
        offer2.getTargetItemCriteria().iterator().next().setQuantity(4);
        offer2.getQualifyingItemCriteria().clear();
        offer2.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.TARGET);
        Offer offer3 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);

        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer1);
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offer1) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);

        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer2);
        assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(1).getOffer().equals(offer2) && qualifiedOffers.get(1).getCandidateQualifiersMap().size() == 0);

        itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer3);
        assertTrue(qualifiedOffers.size() == 3 && qualifiedOffers.get(2).getOffer().equals(offer3) && qualifiedOffers.get(2).getCandidateQualifiersMap().size() == 1);

        itemProcessor.applyItemQualifiersAndTargets(qualifiedOffers.get(1), order);

        int qualCount = 0;
        int targetCount = 0;
        for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
            for (PromotionDiscount discount : detail.getPromotionDiscounts()) {
                targetCount += discount.getQuantity();
            }
            for (PromotionQualifier qual : detail.getPromotionQualifiers()) {
                qualCount += qual.getQuantity();
            }
        }
        assertTrue(qualCount == 0 && targetCount == 4);
        assertTrue(order.getAllPromotableOrderItemPriceDetails().size() == 3);

        itemProcessor.applyItemQualifiersAndTargets(qualifiedOffers.get(0), order);

        qualCount = 0;
        targetCount = 0;
        for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
            for (PromotionDiscount discount : detail.getPromotionDiscounts()) {
                targetCount += discount.getQuantity();
            }
            for (PromotionQualifier qual : detail.getPromotionQualifiers()) {
                qualCount += qual.getQuantity();
            }
        }
        assertTrue(qualCount == 1 && targetCount == 5);
        assertTrue(order.getAllPromotableOrderItemPriceDetails().size() == 2);

        itemProcessor.applyItemQualifiersAndTargets(qualifiedOffers.get(2), order);

        qualCount = 0;
        targetCount = 0;
        for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
            for (PromotionDiscount discount : detail.getPromotionDiscounts()) {
                targetCount += discount.getQuantity();
            }
            for (PromotionQualifier qual : detail.getPromotionQualifiers()) {
                qualCount += qual.getQuantity();
            }
        }
        int promoCount = 0;
        for (PromotableOrderItemPriceDetail detail : order.getAllPromotableOrderItemPriceDetails()) {
            promoCount += detail.getPromotionDiscounts().size();
        }
        /*
         * There's not enough qualifier spaces left
         */
        assertTrue(qualCount == 1 && targetCount == 5);
        assertTrue(order.getAllPromotableOrderItemPriceDetails().size() == 2);
        assertTrue(promoCount == 4);

        verify();
    }

    public class Answer implements IAnswer<CandidateItemOffer> {

        public CandidateItemOffer answer() throws Throwable {
            return new CandidateItemOfferImpl();
        }

    }

    public class Answer2 implements IAnswer<OrderItemAdjustment> {

        public OrderItemAdjustment answer() throws Throwable {
            return new OrderItemAdjustmentImpl();
        }

    }
}
