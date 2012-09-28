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

import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.OrderItemMergeService;
import org.broadleafcommerce.core.offer.service.OrderItemMergeServiceImpl;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
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

/**
 * 
 * @author jfischer
 *
 */
public class ItemOfferProcessorTest extends TestCase {

	private OfferDao offerDaoMock;
	private OrderService orderServiceMock;
	private OrderItemService orderItemServiceMock;
	private FulfillmentGroupItemDao fgItemDaoMock;
	private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
	private FulfillmentGroupService fgServiceMock;
    private OrderMultishipOptionService multishipOptionServiceMock;

	private ItemOfferProcessorImpl itemProcessor;

	@Override
	protected void setUp() throws Exception {
		offerDaoMock = EasyMock.createMock(OfferDao.class);
		orderServiceMock = EasyMock.createMock(OrderService.class);
		orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
		fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
		fgServiceMock = EasyMock.createMock(FulfillmentGroupService.class);
        multishipOptionServiceMock = EasyMock.createMock(OrderMultishipOptionService.class);

        OrderItemMergeService orderItemMergeService = new OrderItemMergeServiceImpl();

        orderItemMergeService.setOrderService(orderServiceMock);
        orderItemMergeService.setFulfillmentGroupItemDao(fgItemDaoMock);
        orderItemMergeService.setFulfillmentGroupService(fgServiceMock);
        orderItemMergeService.setOrderItemService(orderItemServiceMock);
        orderItemMergeService.setOrderMultishipOptionService(multishipOptionServiceMock);
        orderItemMergeService.setPromotableItemFactory(new PromotableItemFactoryImpl());

		itemProcessor = new ItemOfferProcessorImpl();
		itemProcessor.setOfferDao(offerDaoMock);
		itemProcessor.setPromotableItemFactory(new PromotableItemFactoryImpl());
		itemProcessor.setOrderItemMergeService(orderItemMergeService);
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
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andReturn(new CandidateItemOfferImpl()).times(4);

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

		boolean couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableDiscreteOrderItems().get(0), order.getFulfillmentGroups().get(0));
		//test that the valid order item offer is included
		assertTrue(couldApply);

		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()==0",
			OfferDiscountType.PERCENT_OFF,
			null,
			null
		);
		couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableDiscreteOrderItems().get(0), order.getFulfillmentGroups().get(0));
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

		boolean couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().iterator().next(), order.getDiscountableDiscreteOrderItems().get(0));
		//test that the valid order item offer is included
		assertTrue(couldApply);

		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20",
			OfferDiscountType.PERCENT_OFF,
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))",
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().iterator().next(), order.getDiscountableDiscreteOrderItems().get(0));
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
		for (PromotableOrderItem orderItem : order.getDiscountableDiscreteOrderItems()) {
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

	public void testApplyAllItemOffers() throws Exception {
		Answer answer = new Answer();
		Answer2 answer2 = new Answer2();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(4);

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

		List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer1);

		boolean applied = itemProcessor.applyAllItemOffers(qualifiedOffers, order);

		assertTrue(applied);

		order = dataProvider.createBasicOrder();

		qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
        offer1.setApplyDiscountToSalePrice(false);
        order.getDiscreteOrderItems().get(0).getDelegate().setSalePrice(new Money(1D));
        order.getDiscreteOrderItems().get(1).getDelegate().setSalePrice(new Money(1D));
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer1);



		applied = itemProcessor.applyAllItemOffers(qualifiedOffers, order);

		assertFalse(applied);

		verify();
	}

	public void testApplyAdjustments() throws Exception {
		Answer answer = new Answer();
		Answer2 answer2 = new Answer2();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(7);

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

		List<PromotableCandidateItemOffer> qualifiedOffers = new ArrayList<PromotableCandidateItemOffer>();
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer1);
		assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offer1) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, offer2);
		assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(1).getOffer().equals(offer2) && qualifiedOffers.get(1).getCandidateQualifiersMap().size() == 1);

		int appliedCount = itemProcessor.applyAdjustments(order, 0, qualifiedOffers.get(0), 0);
		assertTrue(appliedCount == 1);

		appliedCount = itemProcessor.applyAdjustments(order, appliedCount, qualifiedOffers.get(1), appliedCount);

		//the first offer is not combinable, the a new adjustment will not be created for the second offer
		assertTrue(appliedCount == 1);

		order.removeAllAdjustments();

		offer1.setCombinableWithOtherOffers(true);
		appliedCount = itemProcessor.applyAdjustments(order, 0, qualifiedOffers.get(0), 0);
		appliedCount = itemProcessor.applyAdjustments(order, appliedCount, qualifiedOffers.get(1), appliedCount);

		//the first offer is now combinable, so both offer may be applied
		assertTrue(appliedCount == 2);

		order.removeAllAdjustments();

		offer1.setCombinableWithOtherOffers(false);
        offer1.setApplyDiscountToSalePrice(false);
		order.getDiscreteOrderItems().get(1).getDelegate().setSalePrice(new Money(10D));
		appliedCount = itemProcessor.applyAdjustments(order, 0, qualifiedOffers.get(0), 0);

		//the first offer is not combinable and the discount is less than the sale price
		assertTrue(appliedCount == 0);

		appliedCount = itemProcessor.applyAdjustments(order, appliedCount, qualifiedOffers.get(1), appliedCount);

		//since the non-combinable offer was removed, the second offer is now available to be applied
		assertTrue(appliedCount == 1);

		order.removeAllAdjustments();

		offer1.setCombinableWithOtherOffers(true);
		order.getDiscreteOrderItems().get(1).getDelegate().setSalePrice(null);
		offer2.setStackable(false);
		appliedCount = itemProcessor.applyAdjustments(order, 0, qualifiedOffers.get(0), 0);

		assertTrue(appliedCount == 1);

		appliedCount = itemProcessor.applyAdjustments(order, appliedCount, qualifiedOffers.get(1), appliedCount);

		assertTrue(appliedCount == 2);

		verify();
	}

	public void testApplyItemQualifiersAndTargets() throws Exception {
		Answer answer = new Answer();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(3);

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

		List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
		for (PromotableOrderItem orderItem : order.getDiscountableDiscreteOrderItems()) {
			orderItems.add(orderItem);
		}
		int qualCount = 0;
		int targetCount = 0;
		for (PromotableOrderItem orderItem : orderItems) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		assertTrue(qualCount == 0 && targetCount == 4);
		assertTrue(order.getAllSplitItems().size() == 3);

		itemProcessor.applyItemQualifiersAndTargets(qualifiedOffers.get(0), order);

		qualCount = 0;
		targetCount = 0;
		for (PromotableOrderItem orderItem : orderItems) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		assertTrue(qualCount == 1 && targetCount == 5);
		assertTrue(order.getSplitItems().size() == 2 && order.getSplitItems().get(0).getSplitItems().size() == 2 && order.getSplitItems().get(1).getSplitItems().size() == 2);

		itemProcessor.applyItemQualifiersAndTargets(qualifiedOffers.get(2), order);

		qualCount = 0;
		targetCount = 0;
		for (PromotableOrderItem orderItem : orderItems) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		int promoCount = 0;
		List<PromotableOrderItem> allSplitItems = order.getAllSplitItems();
		for (PromotableOrderItem item : allSplitItems) {
			promoCount += item.getPromotionDiscounts().size();
		}
		/*
		 * There's not enough qualifier spaces left
		 */
		assertTrue(qualCount == 1 && targetCount == 5);
		assertTrue(order.getSplitItems().size() == 2 && order.getSplitItems().get(0).getSplitItems().size() == 2 && order.getSplitItems().get(1).getSplitItems().size() == 2);
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
