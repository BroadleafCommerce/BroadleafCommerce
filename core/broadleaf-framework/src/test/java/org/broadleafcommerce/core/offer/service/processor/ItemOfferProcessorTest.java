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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.CustomerOfferDao;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OfferServiceImpl;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.dao.OrderItemDao;
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
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * 
 * @author jfischer
 *
 */
public class ItemOfferProcessorTest extends TestCase {

    protected OfferDao offerDaoMock;
    protected OrderItemDao orderItemDaoMock;
    protected OrderService orderServiceMock;
    protected OfferServiceImpl offerService;
    protected OrderItemService orderItemServiceMock;
    protected FulfillmentGroupItemDao fgItemDaoMock;
    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();
    protected FulfillmentGroupService fgServiceMock;
    protected OrderMultishipOptionService multishipOptionServiceMock;
    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    protected ItemOfferProcessorImpl itemProcessor;

    @Override
    protected void setUp() throws Exception {

        CustomerOfferDao customerOfferDaoMock = EasyMock.createMock(CustomerOfferDao.class);
        OfferCodeDao offerCodeDaoMock = EasyMock.createMock(OfferCodeDao.class);
        offerDaoMock = EasyMock.createMock(OfferDao.class);
        orderItemDaoMock = EasyMock.createMock(OrderItemDao.class);
        orderServiceMock = EasyMock.createMock(OrderService.class);
        orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
        fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
        fgServiceMock = EasyMock.createMock(FulfillmentGroupService.class);
        multishipOptionServiceMock = EasyMock.createMock(OrderMultishipOptionService.class);
        offerTimeZoneProcessorMock = EasyMock.createMock(OfferTimeZoneProcessor.class);

        itemProcessor = new ItemOfferProcessorImpl();
        itemProcessor.setOfferDao(offerDaoMock);
        itemProcessor.setOrderItemDao(orderItemDaoMock);
        itemProcessor.setOfferTimeZoneProcessor(offerTimeZoneProcessorMock);
        itemProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());

        offerService = new OfferServiceImpl();

        OrderOfferProcessorImpl orderProcessor = new OrderOfferProcessorImpl();
        orderProcessor.setOfferDao(offerDaoMock);
        orderProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
        orderProcessor.setOfferTimeZoneProcessor(offerTimeZoneProcessorMock);
        orderProcessor.setOrderItemDao(orderItemDaoMock);

        offerService.setCustomerOfferDao(customerOfferDaoMock);
        offerService.setOfferCodeDao(offerCodeDaoMock);
        offerService.setOfferDao(offerDaoMock);
        offerService.setOrderOfferProcessor(orderProcessor);
        offerService.setItemOfferProcessor(itemProcessor);
        offerService.setPromotableItemFactory(new PromotableItemFactoryImpl());
        offerService.setOrderService(orderServiceMock);
    }

    public void replay() throws Exception {
        EasyMock.expect(orderItemDaoMock.createOrderItemPriceDetail()).andAnswer(OfferDataItemProvider.getCreateOrderItemPriceDetailAnswer()).anyTimes();
        EasyMock.expect(orderItemDaoMock.createOrderItemQualifier()).andAnswer(OfferDataItemProvider.getCreateOrderItemQualifierAnswer()).anyTimes();
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
        EasyMock.expect(fgServiceMock.collapseToOneShippableFulfillmentGroup(EasyMock.isA(Order.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getSameOrderAnswer()).anyTimes();
        EasyMock.expect(fgItemDaoMock.create()).andAnswer(OfferDataItemProvider.getCreateFulfillmentGroupItemAnswer()).anyTimes();
        fgItemDaoMock.delete(EasyMock.isA(FulfillmentGroupItem.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(offerTimeZoneProcessorMock.getTimeZone(EasyMock.isA(OfferImpl.class))).andReturn(TimeZone.getTimeZone("CST")).anyTimes();

        EasyMock.replay(offerDaoMock);
        EasyMock.replay(orderItemDaoMock);
        EasyMock.replay(orderServiceMock);
        EasyMock.replay(orderItemServiceMock);
        EasyMock.replay(fgItemDaoMock);
        EasyMock.replay(fgServiceMock);
        EasyMock.replay(multishipOptionServiceMock);
        EasyMock.replay(offerTimeZoneProcessorMock);
    }

    public void verify() {
        EasyMock.verify(offerDaoMock);
        EasyMock.verify(orderItemDaoMock);
        EasyMock.verify(orderServiceMock);
        EasyMock.verify(orderItemServiceMock);
        EasyMock.verify(fgItemDaoMock);
        EasyMock.verify(fgServiceMock);
        EasyMock.verify(multishipOptionServiceMock);
        EasyMock.verify(offerTimeZoneProcessorMock);
    }

    public void testFilterItemLevelOffer() throws Exception {
        replay();

        List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
                OfferDiscountType.PERCENT_OFF,
            null,
            null
        );

        PromotableOrder order = dataProvider.createBasicPromotableOrder();
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

    public void testCouldOfferApplyToOrder() throws Exception {
        replay();

        PromotableOrder order = dataProvider.createBasicPromotableOrder();
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

    public void testCouldOrderItemMeetOfferRequirement() throws Exception {
        replay();

        PromotableOrder order = dataProvider.createBasicPromotableOrder();
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

    public void testCouldOfferApplyToOrderItems() throws Exception {
        replay();

        PromotableOrder order = dataProvider.createBasicPromotableOrder();
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
        assertTrue(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1 &&
                candidates.getCandidateQualifiersMap().values().iterator().next().size() == 2 &&
                candidates.isMatchedTarget() && candidates.getCandidateTargetsMap().size() == 1 &&
                candidates.getCandidateTargetsMap().values().iterator().next().size() == 2);

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

    private int checkOrderItemOfferAppliedQuantity(Order order, Offer offer) {
        int count = 0;
        for (OrderItem item : order.getOrderItems()) {
            for (OrderItemPriceDetail detail : item.getOrderItemPriceDetails()) {
                for (OrderItemPriceDetailAdjustment adjustment : detail.getOrderItemPriceDetailAdjustments()) {
                    if (adjustment.getOffer().getId().equals(offer.getId())) {
                        count += detail.getQuantity();
                    }
                }
            }
        }
        return count;
    }

    private int countPriceDetails(Order order) {
        int count = 0;
        for (OrderItem item : order.getOrderItems()) {
            count = count + item.getOrderItemPriceDetails().size();
        }
        return count;
    }


    public void testApplyAllItemOffers() throws Exception {


        replay();

        Order order = dataProvider.createBasicOrder();

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
        offerService.applyOffersToOrder(offers, order);

        assertTrue(order.getTotalAdjustmentsValue().getAmount().doubleValue() > 0);

        order = dataProvider.createBasicOrder();

        qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offer1.setApplyDiscountToSalePrice(false);
        order.getOrderItems().get(0).setSalePrice(new Money(1D));
        order.getOrderItems().get(1).setSalePrice(new Money(1D));
        offerService.applyOffersToOrder(offers, order);

        assertTrue(order.getTotalAdjustmentsValue().getAmount().doubleValue() == 0);

        verify();
    }

    public void testApplyAdjustments() throws Exception {
        replay();

        Order order = dataProvider.createBasicOrder();

        Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
            "order.subTotal.getAmount()>20",
            OfferDiscountType.PERCENT_OFF,
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
            "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
        ).get(0);
        offer1.setId(1L);
        offer1.getQualifyingItemCriteria().iterator().next().setQuantity(2);
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

        offerService.applyOffersToOrder(offerListWithOneOffer, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 1);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 1);

        // Add the second offer.   The first was nonCombinable so it should still be 1
        order = dataProvider.createBasicOrder();
        offerService.applyOffersToOrder(offerListWithTwoOffers, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 0);


        // Reset offer1 to combinable.   Now both should be applied.
        offer1.setCombinableWithOtherOffers(true);
        order = dataProvider.createBasicOrder();
        offerService.applyOffersToOrder(offerListWithTwoOffers, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 0);

        // Offer 1 back to nonCombinable but don't allow discount to the sale price
        // and make the sale price a better overall offer
        offer1.setCombinableWithOtherOffers(false);
        offer1.setApplyDiscountToSalePrice(false);
        order = dataProvider.createBasicOrder();
        order.getOrderItems().get(1).setSalePrice(new Money(10D));
        offerService.applyOffersToOrder(offerListWithOneOffer, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 0);

        // Try again with two offers.   The second should be applied.   
        offerService.applyOffersToOrder(offerListWithTwoOffers, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 2);

        // Trying with 2nd offer as nonCombinable.
        offer1.setCombinableWithOtherOffers(true);
        order.getOrderItems().get(1).setSalePrice(null);
        offer2.setCombinableWithOtherOffers(false);

        offerService.applyOffersToOrder(offerListWithOneOffer, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 1);

        offerService.applyOffersToOrder(offerListWithTwoOffers, order);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 0);

        // Set qualifying criteria quantity to 1
        // Set qualifying target criteria to 2 
        order = dataProvider.createBasicOrder();
        offer1.getQualifyingItemCriteria().iterator().next().setQuantity(1);
        offer1.getTargetItemCriteria().iterator().next().setQuantity(2);
        offerService.applyOffersToOrder(offerListWithOneOffer, order);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 2);

        // Reset both offers to combinable and the qualifiers as allowing duplicate QUALIFIERs
        // and Targets
        offer1.setCombinableWithOtherOffers(true);
        offer2.setCombinableWithOtherOffers(true);
        offer1.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        offer1.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        offer2.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        offer2.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        order = dataProvider.createBasicOrder();
        offerService.applyOffersToOrder(offerListWithTwoOffers, order);
        assertTrue(checkOrderItemOfferAppliedCount(order) == 4);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 2);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer1) == 2);

        verify();
    }

    public void testApplyItemQualifiersAndTargets() throws Exception {
        replay();

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

        PromotableOrder promotableOrder = dataProvider.createBasicPromotableOrder();
        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer1);
        assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offer1) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);

        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer2);
        assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(1).getOffer().equals(offer2) && qualifiedOffers.get(1).getCandidateQualifiersMap().size() == 0);

        itemProcessor.filterItemLevelOffer(promotableOrder, qualifiedOffers, offer3);
        assertTrue(qualifiedOffers.size() == 3 && qualifiedOffers.get(2).getOffer().equals(offer3) && qualifiedOffers.get(2).getCandidateQualifiersMap().size() == 1);

        // Try with just the second offer.   Expect to get 4 targets based on the offer having no qualifiers required
        // and targeting category test1 or test2 and that the offer requires 4 target criteria.
        Order order = dataProvider.createBasicOrder();
        List<Offer> offerList = new ArrayList<Offer>();
        offerList.add(offer2);
        offerService.applyOffersToOrder(offerList, order);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 4);
        assertTrue(countPriceDetails(order) == 3);

        // Now try with both offers.   Since the targets can be reused, we expect to have 4 targets on offer2 
        // and 1 target on offer1
        order = dataProvider.createBasicOrder();
        offerList.add(offer1); // add in second offer (which happens to be offer1)

        offerService.applyOffersToOrder(offerList, order);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer2) == 4);
        assertTrue(countPriceDetails(order) == 3);

        // All three offers - offer 3 is now higher priority so the best offer (offer 2) won't be applied
        order = dataProvider.createBasicOrder();
        offerList.add(offer3); // add in second offer (which happens to be offer1)  
        offer3.setPriority(-1);
        offerService.applyOffersToOrder(offerList, order);
        assertTrue(checkOrderItemOfferAppliedQuantity(order, offer3) == 2);
        assertTrue(countPriceDetails(order) == 4);

        verify();
    }

    public class Answer implements IAnswer<CandidateItemOffer> {

        @Override
        public CandidateItemOffer answer() throws Throwable {
            return new CandidateItemOfferImpl();
        }

    }

    public class Answer2 implements IAnswer<OrderItemAdjustment> {

        @Override
        public OrderItemAdjustment answer() throws Throwable {
            return new OrderItemAdjustmentImpl();
        }

    }
}
