/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.offer.service.processor;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.dao.FulfillmentGroupItemDao;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.money.Money;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;

/**
 * 
 * @author jfischer
 *
 */
public class ItemOfferProcessorTest extends TestCase {

	private OfferDao offerDaoMock;
	private CartService cartServiceMock;
	private OrderItemService orderItemServiceMock;
	private FulfillmentGroupItemDao fgItemDaoMock;
	private ItemOfferProcessorImpl itemProcessor;
	private OfferDataItemProvider dataProvider = new OfferDataItemProvider();
	
	@Override
	protected void setUp() throws Exception {
		offerDaoMock = EasyMock.createMock(OfferDao.class);
		cartServiceMock = EasyMock.createMock(CartService.class);
		orderItemServiceMock = EasyMock.createMock(OrderItemService.class);
		fgItemDaoMock = EasyMock.createMock(FulfillmentGroupItemDao.class);
		itemProcessor = new ItemOfferProcessorImpl();
		itemProcessor.setOfferDao(offerDaoMock);
		itemProcessor.setCartService(cartServiceMock);
		itemProcessor.setFulfillmentGroupItemDao(fgItemDaoMock);
		itemProcessor.setOrderItemService(orderItemServiceMock);
	}
	
	public void replay() {
		EasyMock.replay(offerDaoMock);
		EasyMock.replay(cartServiceMock);
		EasyMock.replay(orderItemServiceMock);
		EasyMock.replay(fgItemDaoMock);
	}
	
	public void verify() {
		EasyMock.verify(offerDaoMock);
		EasyMock.verify(cartServiceMock);
		EasyMock.verify(orderItemServiceMock);
		EasyMock.verify(fgItemDaoMock);
	}
	
	public void testFilterItemLevelOffer() {
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andReturn(new CandidateItemOfferImpl()).times(3);
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<CandidateItemOffer> qualifiedOffers = new ArrayList<CandidateItemOffer>();
		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF,
			null, 
			null
		);
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offers.get(0));
		
		//test that the valid order item offer is included - legacy format - no qualifier
		//since there's no qualifier, both items can apply
		assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(1).getOffer().equals(offers.get(0)));
		
		qualifiedOffers = new ArrayList<CandidateItemOffer>();
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offers.get(0));
		
		//test that the valid order item offer is included
		//there is a qualifier and the item qualifying criteria requires only 1, therefore there will be only one qualifier in the qualifiers map
		//we don't know the targets yet, so there's only one CandidateItemOffer for now
		assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offers.get(0)) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);
		 
		qualifiedOffers = new ArrayList<CandidateItemOffer>();
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offers.get(0));
		
		//test that the invalid order item offer is excluded
		assertTrue(qualifiedOffers.size() == 0);
		
		verify();
	}
	
	public void testCouldOfferApplyToOrder() {
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			null, 
			null
		);
		boolean couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getOrderItems().get(0), order.getFulfillmentGroups().get(0));
		//test that the valid order item offer is included
		assertTrue(couldApply);
		
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()==0", 
			OfferDiscountType.PERCENT_OFF, 
			null, 
			null
		);
		couldApply = itemProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getOrderItems().get(0), order.getFulfillmentGroups().get(0));
		//test that the invalid order item offer is excluded
		assertFalse(couldApply);
		
		verify();
	}
	
	public void testCouldOrderItemMeetOfferRequirement() {
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		boolean couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().get(0), order.getOrderItems().get(0));
		//test that the valid order item offer is included
		assertTrue(couldApply);
		
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		couldApply = itemProcessor.couldOrderItemMeetOfferRequirement(offers.get(0).getQualifyingItemCriteria().get(0), order.getOrderItems().get(0));
		//test that the invalid order item offer is excluded
		assertFalse(couldApply);
		
		verify();
	}
	
	public void testCouldOfferApplyToOrderItems() {
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<Offer> offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		CandidatePromotionItems candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), order.getDiscountableDiscreteOrderItems());
		//test that the valid order item offer is included
		//both cart items are valid for qualification and target
		assertTrue(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1 && candidates.getCandidateQualifiersMap().values().iterator().next().size() == 2 && candidates.isMatchedTarget() && candidates.getCandidateTargets().size() == 2);
		
		offers = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		);
		candidates = itemProcessor.couldOfferApplyToOrderItems(offers.get(0), order.getDiscountableDiscreteOrderItems());
		//test that the invalid order item offer is excluded because there are no qualifying items
		assertFalse(candidates.isMatchedQualifier() && candidates.getCandidateQualifiersMap().size() == 1);
		
		verify();
	}
	
	public void testApplyAllItemOffers() throws Exception {
		Answer answer = new Answer();
		Answer2 answer2 = new Answer2();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(4);
		
		EasyMock.expect(cartServiceMock.addItemToFulfillmentGroup(EasyMock.isA(OrderItem.class), EasyMock.isA(FulfillmentGroup.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddItemToFulfillmentGroupAnswer()).anyTimes();
		EasyMock.expect(cartServiceMock.addOrderItemToOrder(EasyMock.isA(Order.class), EasyMock.isA(OrderItem.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getAddOrderItemToOrderAnswer()).anyTimes();
		EasyMock.expect(cartServiceMock.removeItemFromOrder(EasyMock.isA(Order.class), EasyMock.isA(OrderItem.class), EasyMock.eq(false))).andAnswer(OfferDataItemProvider.getRemoveItemFromOrderAnswer()).anyTimes();
		EasyMock.expect(orderItemServiceMock.saveOrderItem(EasyMock.isA(OrderItem.class))).andAnswer(OfferDataItemProvider.getSaveOrderItemAnswer()).anyTimes();
		EasyMock.expect(fgItemDaoMock.save(EasyMock.isA(FulfillmentGroupItem.class))).andAnswer(OfferDataItemProvider.getSaveFulfillmentGroupItemAnswer()).anyTimes();
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		
		Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		).get(0);
		offer1.setId(1L);
		
		List<CandidateItemOffer> qualifiedOffers = new ArrayList<CandidateItemOffer>();
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer1);
		
		boolean applied = itemProcessor.applyAllItemOffers(qualifiedOffers, order.getDiscountableDiscreteOrderItems(), order);
		
		assertTrue(applied);
		
		order = dataProvider.createBasicOrder();
		
		qualifiedOffers = new ArrayList<CandidateItemOffer>();
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer1);
		
		order.getOrderItems().get(0).setSalePrice(new Money(1D));
		order.getOrderItems().get(1).setSalePrice(new Money(1D));
		
		applied = itemProcessor.applyAllItemOffers(qualifiedOffers, order.getDiscountableDiscreteOrderItems(), order);
		
		assertFalse(applied);
		
		verify();
	}
	
	public void testApplyAdjustments() throws Exception {
		Answer answer = new Answer();
		Answer2 answer2 = new Answer2();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(2);
		EasyMock.expect(offerDaoMock.createOrderItemAdjustment()).andAnswer(answer2).times(8);
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		
		Offer offer1 = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		).get(0);
		offer1.setId(1L);
		offer1.getTargetItemCriteria().setQuantity(2);
		offer1.setCombinableWithOtherOffers(false);
		Offer offer2 = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		).get(0);
		offer2.setId(2L);
		
		List<CandidateItemOffer> qualifiedOffers = new ArrayList<CandidateItemOffer>();
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer1);		
		assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offer1) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer2);		
		assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(1).getOffer().equals(offer2) && qualifiedOffers.get(1).getCandidateQualifiersMap().size() == 1);
		
		int appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, 0, qualifiedOffers.get(0), 0);
		assertTrue(appliedCount == 1);
		
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, appliedCount, qualifiedOffers.get(1), appliedCount);
		
		//the first offer is not combinable, the a new adjustment will not be created for the second offer
		assertTrue(appliedCount == 1);
		
		order.removeAllAdjustments();
		
		offer1.setCombinableWithOtherOffers(true);
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, 0, qualifiedOffers.get(0), 0);
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, appliedCount, qualifiedOffers.get(1), appliedCount);
		
		//the first offer is now combinable, so both offer may be applied
		assertTrue(appliedCount == 2);
		
		order.removeAllAdjustments();
		
		offer1.setCombinableWithOtherOffers(false);
		order.getOrderItems().get(1).setSalePrice(new Money(10D));
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, 0, qualifiedOffers.get(0), 0);
		
		//the first offer is not combinable and the discount is less than the sale price
		assertTrue(appliedCount == 0);
		
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, appliedCount, qualifiedOffers.get(1), appliedCount);
		
		//since the non-combinable offer was removed, the second offer is now available to be applied
		assertTrue(appliedCount == 1);
		
		order.removeAllAdjustments();
		
		offer1.setCombinableWithOtherOffers(true);
		order.getOrderItems().get(1).setSalePrice(null);
		offer2.setStackable(false);
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, 0, qualifiedOffers.get(0), 0);
		
		assertTrue(appliedCount == 1);
		
		appliedCount = itemProcessor.applyAdjustments(order.getDiscountableDiscreteOrderItems(), order, appliedCount, qualifiedOffers.get(1), appliedCount);
		
		assertTrue(appliedCount == 2);
		
		verify();
	}
	
	public void testApplyItemQualifiersAndTargets() throws Exception {
		Answer answer = new Answer();
		EasyMock.expect(offerDaoMock.createCandidateItemOffer()).andAnswer(answer).times(3);
		
		replay();
		
		Order order = dataProvider.createBasicOrder();
		List<CandidateItemOffer> qualifiedOffers = new ArrayList<CandidateItemOffer>();
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
		offer2.getTargetItemCriteria().setQuantity(4);
		offer2.getQualifyingItemCriteria().clear();
		offer2.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.TARGET);
		Offer offer3 = dataProvider.createItemBasedOfferWithItemCriteria(
			"order.subTotal.getAmount()>20", 
			OfferDiscountType.PERCENT_OFF, 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))", 
			"([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))"
		).get(0);
		
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer1);		
		assertTrue(qualifiedOffers.size() == 1 && qualifiedOffers.get(0).getOffer().equals(offer1) && qualifiedOffers.get(0).getCandidateQualifiersMap().size() == 1);
		
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer2);		
		assertTrue(qualifiedOffers.size() == 2 && qualifiedOffers.get(1).getOffer().equals(offer2) && qualifiedOffers.get(1).getCandidateQualifiersMap().size() == 0);
		
		itemProcessor.filterItemLevelOffer(order, qualifiedOffers, order.getDiscountableDiscreteOrderItems(), offer3);		
		assertTrue(qualifiedOffers.size() == 3 && qualifiedOffers.get(2).getOffer().equals(offer3) && qualifiedOffers.get(2).getCandidateQualifiersMap().size() == 1);
		
		itemProcessor.applyItemQualifiersAndTargets(order.getDiscountableDiscreteOrderItems(), qualifiedOffers.get(1), order);
		
		int qualCount = 0;
		int targetCount = 0;
		for (OrderItem orderItem : order.getDiscountableDiscreteOrderItems()) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		assertTrue(qualCount == 0 && targetCount == 4);
		assertTrue(itemProcessor.getAllSplitItems(order).size() == 3);
		
		itemProcessor.applyItemQualifiersAndTargets(order.getDiscountableDiscreteOrderItems(), qualifiedOffers.get(0), order);
		
		qualCount = 0;
		targetCount = 0;
		for (OrderItem orderItem : order.getDiscountableDiscreteOrderItems()) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		assertTrue(qualCount == 1 && targetCount == 5);
		assertTrue(order.getSplitItems().size() == 2 && order.getSplitItems().get(0).getSplitItems().size() == 2 && order.getSplitItems().get(1).getSplitItems().size() == 2);
		
		itemProcessor.applyItemQualifiersAndTargets(order.getDiscountableDiscreteOrderItems(), qualifiedOffers.get(2), order);
		
		qualCount = 0;
		targetCount = 0;
		for (OrderItem orderItem : order.getDiscountableDiscreteOrderItems()) {
			for (PromotionDiscount discount : orderItem.getPromotionDiscounts()) {
				targetCount += discount.getQuantity();
			}
			for (PromotionQualifier qual : orderItem.getPromotionQualifiers()) {
				qualCount += qual.getQuantity();
			}
		}
		int promoCount = 0;
		List<OrderItem> allSplitItems = itemProcessor.getAllSplitItems(order);
		for (OrderItem item : allSplitItems) {
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
